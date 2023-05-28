package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingUnknownStateException;
import ru.practicum.shareit.booking.exception.IncorrectBookingParameterException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDto getBooking(Integer bookerId, Integer id) {
        Optional<Booking> bookingOption = bookingRepository.findById(id);
        if (bookingOption.isPresent()) {
            Booking booking = bookingOption.get();
            if (booking.getBooker().getId().equals(bookerId) || booking.getItem().getOwner().getId().equals(bookerId)) {
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new BookingNotFoundException("Неверные параметры");
            }
        } else {
            throw new BookingNotFoundException("Брони с таким id нет");
        }
    }

    @Override
    public BookingDto booking(Integer bookerId, BookingDto bookingDto) {
        checkDates(bookingDto);

        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            throw new IncorrectParameterException("Вещи с таким id нет");
        } else if (item.get().getIsAvailable()) {
            Integer ownerId = item.get().getOwner().getId();
            if (ownerId.equals(bookerId)) {
                throw new IncorrectParameterException("Неверные параметры");
            }

            Booking booking = new Booking();
            Optional<User> user = userRepository.findById(bookerId);
            if (user.isPresent()) {
                booking.setBooker(user.get());
            } else {
                throw new UserNotFoundException("Пользователь не найден");
            }
            booking.setDateBegin(bookingDto.getStart());
            booking.setDateEnd(bookingDto.getEnd());
            booking.setItem(item.get());
            booking.setStatus("WAITING");

            bookingRepository.save(booking);

            return BookingMapper.toBookingDto(booking);

        } else {
            throw new IncorrectBookingParameterException("Вещь недоступна");
        }
    }

    @Override
    public BookingDto aprove(Integer ownerId, Integer bookingId, boolean approved) {
        Optional<Booking> bookingOption = bookingRepository.findById(bookingId);
        if (bookingOption.isPresent()) {
            log.info("booking: " + bookingOption.get());
            Item item = bookingOption.get().getItem();
            User owner = item.getOwner();
            if (owner.getId().equals(ownerId)) {
                Booking booking = bookingOption.get();
                if (booking.getStatus().equals("APPROVED")) {
                    throw new IncorrectBookingParameterException("Неверные параметры");
                }
                String status;
                if (approved) {
                    status = "APPROVED";
                } else {
                    status = "REJECTED";
                }
                booking.setStatus(status);

                bookingRepository.save(booking);
                return BookingMapper.toBookingDto(booking);
            } else {
                throw new BookingNotFoundException("Неверные параметры");
            }
        } else {
            throw new BookingNotFoundException("Брони с такой ID нет");
        }
    }

    private void checkDates(BookingDto bookingDto) {
        if (bookingDto.getEnd() == null || bookingDto.getStart() == null
                || bookingDto.getEnd().equals(bookingDto.getStart())
                || bookingDto.getEnd().isBefore(bookingDto.getStart())
                || bookingDto.getEnd().isBefore(LocalDateTime.now())
                || bookingDto.getStart().isBefore(LocalDateTime.now())
        ) {
            throw new IncorrectBookingParameterException("Неверные параметры");
        }
    }

    @Override
    public List<BookingDto> getBooking(String state, Integer bookerId, Integer from, Integer size) {
        Optional<User> user = userRepository.findById(bookerId);
        if (user.isPresent()) {
            List<Booking> bookingList = new ArrayList<>();
            if (from == null && size == null) {
                bookingList = bookingRepository.findByBooker(user.get());
            } else if (from >= 0 && size > 0) {
                Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "dateBegin"));
                bookingList = bookingRepository.findAllByBooker(user.get(), pageable).getContent();
            } else {
                throw new IncorrectBookingParameterException("Неверные параметры");
            }

            List<Booking> list = getBookingListByStatus(state, bookingList);
            return BookingMapper.toBookingDtoList(list);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    private List<Booking> getBookingListByStatus(String state, List<Booking> bookingList) {
        log.info("+++++" + state + "+++++");
        List<Booking> list = new ArrayList<>();
        switch (state) {
            case "PAST":
                list = bookingList.stream()
                        .filter(booking -> booking.getDateBegin().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getDateEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                break;
            case "FUTURE":
                list = bookingList.stream()
                        .filter(booking -> booking.getDateBegin().isAfter(LocalDateTime.now()) && booking.getDateEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                list = bookingList.stream()
                        .filter(booking -> booking.getDateBegin().isBefore(LocalDateTime.now()))
                        .filter(booking -> booking.getDateEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                break;
            case "WAITING":
                list = bookingList.stream()
                        .filter(booking -> booking.getStatus().equals("WAITING"))
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                break;
            case "REJECTED":
                list = bookingList.stream()
                        .filter(booking -> booking.getStatus().equals("REJECTED"))
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                break;
            case "ALL":
                list = bookingList.stream()
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                break;
            default:
                throw new BookingUnknownStateException(state);
        }
        return list;
    }

    @Override
    public List<BookingDto> ownerItemsBookingLists(String state, Integer ownerId, Integer from, Integer size) {
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isPresent()) {
            List<Item> ownerItemList = itemRepository.findByOwner(user.get());
            List<Booking> bookingList = new ArrayList<>();
            ownerItemList.forEach(item -> {
                        List<Booking> itemBookingList = new ArrayList<>();
                        if (from == null && size == null) {
                            itemBookingList = bookingRepository.findByItem(item);
                        } else if (from >= 0 && size > 0) {
                            itemBookingList = bookingRepository.findByItemByLimits(item.getId(), from, size);
                        } else {
                            throw new IncorrectBookingParameterException("Неверные параметры");
                        }
                        bookingList.addAll(itemBookingList);
                    }
            );
            List<Booking> list = getBookingListByStatus(state, bookingList);
            return BookingMapper.toBookingDtoList(list);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}

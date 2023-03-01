package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.IncorrectBookingParameterException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
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
        if (item.isPresent() && item.get().getIsAvailable()) {
            if (item.get().getOwner().getId().equals(bookerId)){
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

        } else if (!item.isPresent()) {
            throw new IncorrectParameterException("Вещи с таким id нет");
        } else {
            throw new IncorrectBookingParameterException("Вещь недоступна");
        }
    }

    @Override
    public BookingDto aprove(Integer ownerId, Integer bookingId, boolean approved) {
        Optional<Booking> bookingOption = bookingRepository.findById(bookingId);
        if (bookingOption.isPresent()) {
            Item item = bookingOption.get().getItem();
            User owner = item.getOwner();
            if (owner.getId().equals(ownerId)) {
                Booking booking = bookingOption.get();
                if (booking.getStatus().equals("APPROVED")){
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
        if (bookingDto.getEnd().before(bookingDto.getStart())
                || bookingDto.getEnd().before(new Date())
                || bookingDto.getStart().before(new Date())
        ) {
            throw new IncorrectBookingParameterException("Неверные параметры");
        }
    }

    @Override
    public List<BookingDto> getBooking(String state, Integer bookerId) {
        Optional<User> user = userRepository.findById(bookerId);
        if (user.isPresent()) {
            List<Booking> bookingList = bookingRepository.findByBooker(user.get());
            log.info("*******>>> bookingList:{}",bookingList);
            List<Booking> list = bookingList.stream()
                    .filter(
                            booking ->state.equals("ALL")
                                    || (state.equals("CURRENT") && booking.getDateBegin().before(new Date()) && booking.getDateEnd().after(new Date()))
                                    || (state.equals("FUTURE") && booking.getDateBegin().after(new Date()) && booking.getDateEnd().after(new Date()))
                                    || (state.equals("PAST") && booking.getDateBegin().before(new Date()) && booking.getDateEnd().before(new Date()))
                                    || (state.equals("WAITING") && booking.getStatus().equals("WAITING"))
                                    || (state.equals("REJECTED") && booking.getStatus().equals("REJECTED")) )
                    .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                    .collect(Collectors.toList());
            log.info("*******>>> sorted list :{}",list );
            return BookingMapper.toBookingDtoList(list);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    @Override
    public List<BookingDto> ownerItemsBooking(String state, Integer ownerId) {
        log.info("************* ownerItemsBooking ************");
        Optional<User> user = userRepository.findById(ownerId);
        if (user.isPresent()) {
            List<Item> ownerItemList = itemRepository.findByOwner(user.get());
            List<Booking> bookingList = new ArrayList<>();
            ownerItemList.stream().forEach(item -> {
                        List<Booking> itemBookingList = bookingRepository.findByItem(item);
                        bookingList.addAll(itemBookingList);
                    }
            );

            log.info(">>>>>>. bookingList:{}",bookingList);

            List<Booking> list = bookingList.stream()
                    .filter(
                            booking ->state.equals("ALL")
                                    || (state.equals("CURRENT") && booking.getDateBegin().before(new Date()) && booking.getDateEnd().after(new Date()))
                                    || (state.equals("FUTURE") && booking.getDateBegin().after(new Date()) && booking.getDateEnd().after(new Date()))
                                    || (state.equals("PAST") && booking.getDateBegin().before(new Date()) && booking.getDateEnd().before(new Date()))
                                    || (state.equals("WAITING") && booking.getStatus().equals("WAITING"))
                                    || (state.equals("REJECTED") && booking.getStatus().equals("REJECTED")) )
                    .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                    .collect(Collectors.toList());

            log.info(">>>>>> newList:{}",list);
            return BookingMapper.toBookingDtoList(list);
        } else {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}

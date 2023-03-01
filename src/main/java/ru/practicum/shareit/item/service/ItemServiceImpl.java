package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.IncorrectItemParameterException;
import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(Integer ownerId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new IncorrectItemParameterException("Статус не может быть пустой");
        } else if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new IncorrectItemParameterException("Название не может быть пустой");
        } else if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new IncorrectItemParameterException("Описание не может быть пустой");
        } else {
            checkOwner(ownerId);

            Item item = ItemMapper.toItem(itemDto);
            if (itemDto.getRequestId() != null) {
                item.setRequestId(itemDto.getRequestId());
            }

            item.setIsAvailable(itemDto.getAvailable());
            item.setDescription(itemDto.getDescription());
            item.setName(itemDto.getName());

            Optional<User> optionalUser = userRepository.findById(ownerId);
            item.setOwner(optionalUser.orElse(null));

            Item newItem = itemRepository.save(item);
            return ItemMapper.toItemDto(newItem);
        }
    }

    @Override
    public ItemDto update(Integer ownerId, Integer itemId, ItemDto itemDto) {
        log.info("ownerId:{}", ownerId);
        checkOwner(ownerId);

        Optional<Item> oldItemOptional = itemRepository.findById(itemId);
        Item oldItem = oldItemOptional.orElse(null);

        log.info("ownerId:{} oldItem :{}", ownerId, oldItem);

        if (oldItem != null && oldItem.getOwner().getId().equals(ownerId)) {
            Item item = itemRepository.findById(itemId).get();

            Optional<User> user = userRepository.findById(ownerId);
            if (user.isPresent()) {
                item.setOwner(user.get());
            }
            if (itemDto.getAvailable() != null) {
                item.setIsAvailable(itemDto.getAvailable());
            }
            if (itemDto.getName() != null) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getRequestId() != null) {
                item.setRequestId(itemDto.getRequestId());
            }
            Item newItem = itemRepository.save(item);
            return ItemMapper.toItemDto(newItem);
        } else {
            throw new IncorrectParameterException("Пользователь не найден");
        }
    }

    @Override
    public ItemDto getItem(Integer ownerId, Integer itemId) {
        checkOwner(ownerId);

        Optional<Item> newItem = itemRepository.findById(itemId);
        if (newItem.isPresent()) {
            Item item = newItem.get();
            ItemDto itemDto = ItemMapper.toItemDto(item);
            if (item.getOwner().getId().equals(ownerId)) {
                List<Booking> itemBookingList = bookingRepository.findByItem(item).stream()
                        .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                        .collect(Collectors.toList());
                if (itemBookingList.size() > 0) {
                    itemDto.setLastBooking(BookingMapper.toBookingDto(itemBookingList.get(itemBookingList.size() - 1)));
                    itemDto.setNextBooking(BookingMapper.toBookingDto(itemBookingList.get(itemBookingList.size() - 2)));
                }
            }
            return itemDto;
        } else {
            throw new IncorrectParameterException("Item не найден");
        }
    }

    @Override
    public List<ItemDto> getItems(Integer ownerId) {
        checkOwner(ownerId);
        Optional<User> owner = userRepository.findById(ownerId);
        List<Item> itemList = itemRepository.findByOwner(owner.get());

        List<ItemDto> itemDtoList = new ArrayList<>();
        itemList.stream().forEach(item -> {
            ItemDto itemDto = ItemMapper.toItemDto(item);
            List<Booking> itemBookingList = bookingRepository.findByItem(item).stream()
                    .sorted(Comparator.comparing(Booking::getDateBegin).reversed())
                    .collect(Collectors.toList());
            if (itemBookingList.size() > 0) {
                itemDto.setLastBooking(BookingMapper.toBookingDto(itemBookingList.get(itemBookingList.size() - 1)));
                itemDto.setNextBooking(BookingMapper.toBookingDto(itemBookingList.get(itemBookingList.size() - 2)));

            }
            itemDtoList.add(itemDto);
        });

        return itemDtoList;
    }

    @Override
    public List<ItemDto> getItems(String text) {
        List<Item> list = new ArrayList<>();
        if (!text.isEmpty()) {
            list = itemRepository.findItemsLike(text.toUpperCase());
        }
        return ItemMapper.toItemDtoList(list);
    }

    private void checkOwner(Integer ownerId) {
        User user = new User();
        if (ownerId != null) {
            Optional<User> optionalUser = userRepository.findById(ownerId);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            } else {
                throw new UserNotFoundException("Пользователь не найден");
            }
        }
        if (user == null) {
            throw new IncorrectParameterException("Неверные параметры");
        }
    }
}

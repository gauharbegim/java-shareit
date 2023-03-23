package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemRepository;

import ru.practicum.shareit.item.exception.IncorrectParameterException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addItemRequest(Integer userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        User user = getRequestorUser(userId);

        itemRequest.setRequestor(user);

        itemRequest.setCreated(new Date());
        if (itemRequestDto.getDescription() == null) {
            throw new ItemRequestNotFoundException("Описание не может быть пустым");
        }
        itemRequest.setDescription(itemRequestDto.getDescription());
        requestRepository.save(itemRequest);

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private User getRequestorUser(Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("Пользователь не найден");
        }

        User user = optionalUser.get();

        return user;
    }

    @Override
    public ItemRequestDto getItemRequest(Integer userId, Integer id) {
        User requestor = getRequestorUser(userId);

        Optional<ItemRequest> itemRequestOptional = requestRepository.findById(id);
        if (itemRequestOptional.isPresent()) {
            ItemRequest itemRequest = itemRequestOptional.get();

            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            List<Item> itemList = itemRepository.findByRequestId(itemRequestDto.getId());
            itemRequestDto.setItems(ItemMapper.toItemDtoList(itemList));

            return itemRequestDto;
        } else {
            log.info("-------------Запрос не найден--------");
            throw new IncorrectParameterException("Запрос не найден");
        }
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Integer userId) {
        User requestor = getRequestorUser(userId);
        List<ItemRequest> itemRequestList = requestRepository.findByRequestor(requestor);
        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(itemRequestList);
        addItems(itemRequestDtoList);

        itemRequestDtoList = sortItemRequestList(itemRequestDtoList);

        return itemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getItemRequests(Integer from, Integer size) {
        List<ItemRequest> itemRequestList = new ArrayList<>();
        if (from == null || size == null) {
            return ItemRequestMapper.toItemRequestDtoList(itemRequestList);
        } else if (from < 0 || size < 0) {
            throw new ItemRequestNotFoundException("Неверные параметры");
        } else {
            Sort sortById = Sort.by(Sort.Direction.ASC, "id");
            Pageable page = PageRequest.of(from, size, sortById);
            Page<ItemRequest> itemRequestPage = requestRepository.findAll(page);
            List<ItemRequestDto> itemRequestDtoList = null;
            if (itemRequestPage != null) {
                itemRequestList = itemRequestPage.getContent();
                itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(itemRequestList);
                addItems(itemRequestDtoList);
                itemRequestDtoList = sortItemRequestList(itemRequestDtoList);
            }
            return itemRequestDtoList;
        }

    }

    private List<ItemRequestDto> sortItemRequestList(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList = itemRequestDtoList.stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
        return itemRequestDtoList;
    }

    private List<ItemRequestDto> addItems(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList.stream()
                .forEach(requestDto -> {
                    List<Item> itemList = itemRepository.findByRequestId(requestDto.getId());
                    requestDto.setItems(ItemMapper.toItemDtoList(itemList));
                });
        return itemRequestDtoList;
    }
}

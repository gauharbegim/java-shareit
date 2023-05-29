package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.request.exception.IncorrectPageParametrException;
import ru.practicum.shareit.request.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
        return optionalUser.orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
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

    public List<ItemRequestDto> getItemRequests(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Не найден пользователь с ИД" + userId);
        }
        Pageable pageable = PageRequest.of(from / size, size,
                Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = requestRepository.findAllByRequestorIdNot(userId, pageable).getContent();
        List<ItemRequestDto> itemRequestDtoList = ItemRequestMapper.toItemRequestDtoList(requests);
        addItems(itemRequestDtoList);
        itemRequestDtoList = sortItemRequestList(itemRequestDtoList);
        return itemRequestDtoList;
    }

    private List<ItemRequestDto> sortItemRequestList(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList = itemRequestDtoList.stream()
                .sorted(Comparator.comparing(ItemRequestDto::getCreated))
                .collect(Collectors.toList());
        return itemRequestDtoList;
    }

    private List<ItemRequestDto> addItems(List<ItemRequestDto> itemRequestDtoList) {
        itemRequestDtoList.forEach(requestDto -> {
            List<Item> itemList = itemRepository.findByRequestId(requestDto.getId());
            requestDto.setItems(ItemMapper.toItemDtoList(itemList));
        });
        return itemRequestDtoList;
    }
}

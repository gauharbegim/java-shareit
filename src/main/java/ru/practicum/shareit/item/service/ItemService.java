package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Integer ownerId, ItemDto itemDto);

    ItemDto update(Integer ownerId, Integer itemId, ItemDto itemDto);

    ItemDto getItem(Integer itemId);

    List<ItemDto> getItems(Integer ownerId);

    List<ItemDto> getItems(String text);
}

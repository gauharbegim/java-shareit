package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Integer ownerId, ItemDto itemDto);

    ItemDto update(Integer ownerId, Integer itemId, ItemDto itemDto);

    ItemDto getItem(Integer itemId);

    Collection<ItemDto> getItems(Integer ownerId);

    Collection<ItemDto> getItems(String text);
}

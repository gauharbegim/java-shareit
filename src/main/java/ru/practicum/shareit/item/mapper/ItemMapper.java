package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getRequestId());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(null, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId(), null);
    }

    public static Collection<ItemDto> toItemDtoCollection(Map<Integer, Item> itemMap) {
        Collection<ItemDto> newCollection = new ArrayList<>();
        for (Item item : itemMap.values()) {
            ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getRequestId());

            newCollection.add(itemDto);
        }
        return newCollection;
    }

}

package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getRequestId());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(null, itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), itemDto.getRequestId(), null);
    }

    public static List<ItemDto> toItemDtoList(List<Item> itemList) {
        List<ItemDto> newList = new ArrayList<>();
        for (Item item : itemList) {
            ItemDto itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getIsAvailable(), item.getRequestId());

            newList.add(itemDto);
        }
        return newList;
    }

}

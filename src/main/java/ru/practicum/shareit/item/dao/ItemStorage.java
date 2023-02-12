package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Map;

public interface ItemStorage {
    Map<Integer, Item> getItems(Integer ownerId);

    Map<Integer, Item> getItems(String text);

    Item getItem(Integer id);

    Item addItem(Integer ownerId, Item item);

    Item updateUser(Integer ownerId, Integer id, Item item);
}

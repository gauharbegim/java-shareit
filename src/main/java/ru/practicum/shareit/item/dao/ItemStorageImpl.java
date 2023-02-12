package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {
    private final UserStorage userStorage;

    private HashMap<Integer, Item> items = new HashMap<>();
    private int counterId = 1;


    @Override
    public Map<Integer, Item> getItems(Integer ownerId) {
        Map<Integer, Item> res = new HashMap<>();

        User user = userStorage.getUserById(ownerId);

        for (Item item : items.values()) {
            if (item.getOwner().equals(user)) {
                res.put(item.getId(), item);
            }
        }

        return res;
    }

    @Override
    public Map<Integer, Item> getItems(String text) {
        Map<Integer, Item> res = new HashMap<>();

        for (Item item : items.values()) {
            if (item.getIsAvailable()
                    && !text.isBlank()
                    && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))) {
                res.put(item.getId(), item);
            }
        }

        return res;
    }

    @Override
    public Item getItem(Integer id) {
        Item item = items.get(id);
        return item;
    }

    @Override
    public Item addItem(Integer ownerId, Item item) {
        item.setId(counterId);

        User user = userStorage.getUserById(ownerId);
        item.setOwner(user);

        items.put(counterId, item);

        counterId++;

        return item;
    }

    @Override
    public Item updateUser(Integer ownerId, Integer id, Item item) {
        User user = userStorage.getUserById(ownerId);

        Item newItem = items.get(id);
        if (newItem.getOwner().equals(user)) {
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                newItem.setDescription(item.getDescription());
            }

            if (item.getName() != null && !item.getName().isBlank()) {
                newItem.setName(item.getName());
            }

            if (item.getIsAvailable() != null) {
                newItem.setIsAvailable(item.getIsAvailable());
            }
            items.put(id, newItem);
        }
        return newItem;
    }
}

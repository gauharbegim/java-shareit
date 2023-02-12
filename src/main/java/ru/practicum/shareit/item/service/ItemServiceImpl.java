package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.IncorrectItemParameterException;
import ru.practicum.shareit.item.exception.IncorrectOwnerParameterException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(Integer ownerId, ItemDto itemDto) {
        checkOwner(ownerId);
        if (itemDto.getName()==null || itemDto.getName().isBlank()) {
            throw new IncorrectItemParameterException("Название не может быть пустой");
        } else if (itemDto.getDescription()==null || itemDto.getDescription().isBlank()) {
            throw new IncorrectItemParameterException("Описание не может быть пустой");
        } else if (itemDto.getAvailable()==null) {
            throw new IncorrectItemParameterException("Статус не может быть пустой");
        } else {
            Item item = ItemMapper.toItem(itemDto);

            Item newItem = itemStorage.addItem(ownerId, item);
            return ItemMapper.toItemDto(newItem);
        }
    }

    @Override
    public ItemDto update(Integer ownerId, Integer itemId, ItemDto itemDto) {
        checkOwner(ownerId);

        Item oldItem = itemStorage.getItem(itemId);
        if (oldItem.getOwner().getId()==ownerId) {
            Item item = ItemMapper.toItem(itemDto);
            Item newItem = itemStorage.updateUser(ownerId, itemId, item);
            return ItemMapper.toItemDto(newItem);
        } else {
            throw new IncorrectOwnerParameterException("Пользователь не найден");
        }
    }

    @Override
    public ItemDto getItem(Integer itemId) {
        Item newItem = itemStorage.getItem(itemId);
        return ItemMapper.toItemDto(newItem);
    }

    @Override
    public Collection<ItemDto> getItems(Integer ownerId) {
        checkOwner(ownerId);
        return ItemMapper.toItemDtoCollection(itemStorage.getItems(ownerId));
    }

    @Override
    public Collection<ItemDto> getItems(String text) {
        return ItemMapper.toItemDtoCollection(itemStorage.getItems(text));
    }

    private void checkOwner(Integer ownerId) {
        User user = userStorage.getUserById(ownerId);
        if (user==null) {
            throw new IncorrectOwnerParameterException("Пользователь не найден");
        }
    }
}

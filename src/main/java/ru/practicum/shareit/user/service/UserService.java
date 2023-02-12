package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto addUser(UserDto user);

    void deleteUser(Integer userId);

    UserDto updateUser(Integer id, UserDto user);

    Collection<UserDto> getUsersList();

    UserDto getUser(Integer id);
}

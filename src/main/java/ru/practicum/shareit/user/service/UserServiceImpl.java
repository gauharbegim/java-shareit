package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.IncorrectParameterException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserStorage userStorage;

    public UserDto addUser(UserDto userDto) {
        boolean isEmailExists = userStorage.isEmailExists(userDto.getEmail());
        if (!isEmailExists) {
            User user = UserMapper.toUserModel(userDto);
            User newUser = userStorage.addUser(user);
            return UserMapper.toUserDto(newUser);
        }else{
            throw new IncorrectParameterException("Такой email уже существует");
        }
    }

    public void deleteUser(Integer userId) {
        if (userId < 1) {
            throw new UserNotFoundException("Id пользователя должно быть больше 0");
        }
        userStorage.deleteUser(userId);
    }

    public UserDto updateUser(Integer id, UserDto userDto) {
        boolean isEmailExists = userStorage.isEmailExists(userDto.getEmail());
        if (!isEmailExists) {
            User user = UserMapper.toUserModel(userDto);
            userStorage.updateUser(id, user);

            User newUser = userStorage.getUserById(id);
            return UserMapper.toUserDto(newUser);
        }else{
            throw new IncorrectParameterException("Такой email уже существует");
        }
    }

    public Collection<UserDto> getUsersList() {
        return UserMapper.toUserDtoCollection(userStorage.getUsers());
    }

    public UserDto getUserById(Integer id) {
        User user = userStorage.getUserById(id);
        return UserMapper.toUserDto(user);
    }
}

package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    public static User toUserModel(UserDto userDto) {
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static List<UserDto> toUserDtoList(List<User> userList) {
        List<UserDto> newList = new ArrayList<>();
        for (User user : userList) {
            UserDto userDto = new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );

            newList.add(userDto);
        }
        return newList;
    }
}

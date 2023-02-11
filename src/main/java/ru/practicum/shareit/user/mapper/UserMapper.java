package ru.practicum.shareit.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class UserMapper {

    public static User toUserModel(UserDto userDto){
        return new User(
                userDto.getId(),
                userDto.getName(),
                userDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user){
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static Collection<UserDto> toUserDtoCollection(Map<Integer, User> userMap){
        Collection<UserDto> newCollection = new ArrayList<>();
        for (User user : userMap.values()){
            UserDto userDto = new UserDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail()
            );

            newCollection.add(userDto);
        }
        return newCollection;
    }
}

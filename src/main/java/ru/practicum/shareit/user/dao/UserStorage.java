package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Map;

public interface UserStorage {

    Map<Integer, User> getUsers();

    User getUserByEmail(String email);

    User addUser(User user);

    void deleteUser(Integer id);

    void updateUser(Integer id, User user);

    User getUserById(Integer id);
}

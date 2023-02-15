package ru.practicum.shareit.user.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int counterId = 1;

    @Override
    public Map<Integer, User> getUsers() {
        return users;
    }

    @Override
    public User getUserByEmail(String email) {
        User res = null;
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                res = user;
            }
        }
        return res;
    }

    @Override
    public User getUserById(Integer id) {
        if (users.containsKey(id)) {
            User existsUser = users.get(id);
            return existsUser;
        } else {
            return null;
        }
    }

    @Override
    public User addUser(User user) {
        user.setId(counterId);
        users.put(user.getId(), user);
        counterId++;
        return user;
    }

    @Override
    public void deleteUser(Integer id) {
        if (users.containsKey(id)) {
            users.remove(id);
        }
    }

    @Override
    public void updateUser(Integer id, User user) {
        if (users.containsKey(id)) {
            User existsUser = users.get(id);

            if (user.getEmail() != null) {
                existsUser.setEmail(user.getEmail());
            }

            if (user.getName() != null) {
                existsUser.setName(user.getName());
            }

            users.put(id, existsUser);
        }
    }
}

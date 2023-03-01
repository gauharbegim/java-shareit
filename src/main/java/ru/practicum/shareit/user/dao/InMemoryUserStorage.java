//package ru.practicum.shareit.user.dao;
//
//import org.springframework.stereotype.Repository;
//import ru.practicum.shareit.user.model.User;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//public class InMemoryUserStorage implements UserStorage {
//    private Map<Integer, User> users = new HashMap<>();
//    private int counterId = 1;
//
//    @Override
//    public List<User> getUsers() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public User getUserByEmail(String email) {
//        return users.values().stream()
//                .filter(user -> user.getEmail().equals(email))
//                .findAny()
//                .orElse(null);
//    }
//
//    @Override
//    public User getUserById(Integer id) {
//        return users.getOrDefault(id, null);
//    }
//
//    @Override
//    public User addUser(User user) {
//        user.setId(counterId);
//        users.put(user.getId(), user);
//        counterId++;
//        return user;
//    }
//
//    @Override
//    public void deleteUser(Integer id) {
//        if (users.containsKey(id)) {
//            users.remove(id);
//        }
//    }
//
//    @Override
//    public void updateUser(Integer id, User user) {
//        if (users.containsKey(id)) {
//            User existsUser = users.get(id);
//
//            if (user.getEmail() != null) {
//                existsUser.setEmail(user.getEmail());
//            }
//
//            if (user.getName() != null) {
//                existsUser.setName(user.getName());
//            }
//
//            users.put(id, existsUser);
//        }
//    }
//}

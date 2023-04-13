package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        return userStorage.getFriends(userId);
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }
}

package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Component("userDbStorage")
public class UserDbStorage implements UserStorage {
    @Override
    public User create(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        return null;
    }

    @Override
    public User getUserById(Integer userId) {
        return null;
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        return null;
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        return null;
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        return null;
    }
}

package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendsService friendsService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, FriendsService friendsService) {
        this.userStorage = userStorage;
        this.friendsService = friendsService;
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
        User user = userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return user;
    }

    @Transactional
    public void addFriend(Integer userId, Integer friendId) {
        friendsService.addFriend(userId, friendId);
    }

    @Transactional
    public void deleteFriend(Integer userId, Integer friendId) {
        friendsService.deleteFriend(userId, friendId);
    }

    @Transactional
    public Collection<User> getFriendsByUserId(Integer userId) {
        return friendsService.getFriendsByUserId(userId);
    }

    @Transactional
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        return friendsService.getCommonFriends(userId, otherId);
    }
}

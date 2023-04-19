package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

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
        checkUserNameNotEmpty(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        checkUserNameNotEmpty(user);
        userStorage.getUserById(user.getId()).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", user.getId())));
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll().stream()
                .sorted(Comparator.comparing(User::getId))
                .collect(Collectors.toList());
    }

    public User getUserById(Integer userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        user.setFriendIds(userStorage.getFriendsIds(user.getId()));
        return user;
    }

    public void deleteUserById(Integer userId) {
        User user = userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        userStorage.deleteUserById(userId);
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        userStorage.getUserById(friendId).orElseThrow(() ->
                new NotFoundException(String.format("Друг пользователя с id %d не найден", userId)));
        if (Objects.equals(userId, friendId)) {
            log.error("Ошибка добавления в список друзей. Id пользователей не должны совпадать.");
            throw new ValidationException("Id пользователей не должны совпадать.");
        }
        Collection<User> friend = friendsService.getFriend(userId, friendId);
        if (friend.isEmpty()) {
            friendsService.addFriend(userId, friendId);
        }
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        userStorage.getUserById(friendId).orElseThrow(() ->
                new NotFoundException(String.format("Друг пользователя с id %d не найден", userId)));
        friendsService.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriendsByUserId(Integer userId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        return friendsService.getFriendsByUserId(userId);
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        userStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        userStorage.getUserById(otherId).orElseThrow(() ->
                new NotFoundException(String.format("Друг пользователя с id %d не найден", userId)));
        return friendsService.getCommonFriends(userId, otherId);
    }

    private void checkUserNameNotEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

}

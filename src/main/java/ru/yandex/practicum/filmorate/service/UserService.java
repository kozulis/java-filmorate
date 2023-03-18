package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public User getUserById(Integer userId) {
        return userStorage.getUserById(userId);
    }

    public User addFriend(Integer userId, Integer friendId) {
        userValidation(userId, friendId);
        getUserById(userId).getFriendIds().add(friendId);
        getUserById(friendId).getFriendIds().add(userId);
        return getUserById(friendId);
    }

    public User deleteFriend(Integer userId, Integer friendId) {
        userValidation(userId, friendId);
        getUserById(userId).getFriendIds().remove(friendId);
        getUserById(friendId).getFriendIds().remove(userId);
        return getUserById(friendId);
    }

    public Collection<User> getFriends(Integer userId) {
        if (!userStorage.getAll().contains(getUserById(userId))) {
            log.error("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
        return getUserById(userId).getFriendIds().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
//
//        List<User> friends = new ArrayList<>();
//        Set<Integer> friendsIds = getUserById(userId).getFriends();
//        for (Integer friendId : friendsIds) {
//            User friend = getUserById(friendId);
//            friends.add(friend);
//        }
//        return friends;
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        userValidation(userId, otherId);
        Set<Integer> userFriends = getUserById(userId).getFriendIds();
        Set<Integer> otherFriends = getUserById(otherId).getFriendIds();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public void userValidation(Integer userId, Integer friendId) {
        if (!userStorage.getAll().contains(getUserById(userId)) ||
                !userStorage.getAll().contains(getUserById(friendId))) {
            log.error("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }
}

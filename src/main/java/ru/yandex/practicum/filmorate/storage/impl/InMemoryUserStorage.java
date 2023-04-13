package ru.yandex.practicum.filmorate.storage.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private int userId = 1;

    @Getter
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        checkValidation(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        checkNotFound(user.getId());
        checkValidation(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getUserById(Integer userId) {
        checkNotFound(userId);
        return users.get(userId);
    }

    @Override
    public User addFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);
        return getUserById(friendId);
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);
        return getUserById(friendId);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        User user = getUserById(userId);
        return user.getFriendIds().stream()
                .map(this::getUserById)
//                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        User user = getUserById(userId);
        User otherUser = getUserById(otherId);
        Set<Integer> userFriends = user.getFriendIds();
        Set<Integer> otherFriends = otherUser.getFriendIds();
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map(this::getUserById)
//                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }


    private void checkValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Адрес электронной почты не может быть пустым.");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Адрес электронной почты указан в неверном формате.");
            throw new ValidationException("Адрес электронной почты указан в неверном формате.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не должен быть пустым или содержать пробелы");
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть указана позже текущей даты.");
            throw new ValidationException("Дата рождения не может быть указана позже текущей даты.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void checkNotFound(Integer userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }
}

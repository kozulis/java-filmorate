package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> getAll();

    User getUserById(Integer userId);

    User addFriend(Integer userId, Integer friendId);

    User deleteFriend(Integer userId, Integer friendId);

    Collection<User> getFriends(Integer userId);

    Collection<User> getCommonFriends(Integer userId, Integer otherId);
}

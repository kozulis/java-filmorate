package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> getAll();

    Set<Integer> getFriendsIds(Integer userId);

    Optional<User> getUserById(Integer userId);

    void deleteUserById(Integer UserId);
}

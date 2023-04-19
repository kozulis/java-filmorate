package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface FriendsDao {

    void addFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> getFriendsByUserId(Integer userId);

    Collection<User> getFriend(Integer userId, Integer friendId);

    Collection<User> getCommonFriends(Integer userId, Integer otherId);

}

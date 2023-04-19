package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;

import java.util.Collection;

@Slf4j
@Service
public class FriendsService {

    private final FriendsDao friendsDao;

    @Autowired
    public FriendsService(FriendsDao friendsDao) {
        this.friendsDao = friendsDao;
    }

    public void addFriend(Integer userId, Integer friendId) {
        friendsDao.addFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        friendsDao.deleteFriend(userId, friendId);
    }

    public Collection<User> getFriendsByUserId(Integer userId) {
        return friendsDao.getFriendsByUserId(userId);
    }

    public Collection<User> getFriend(Integer userId, Integer friendId) {
        return friendsDao.getFriend(userId, friendId);
    }

    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        return friendsDao.getCommonFriends(userId, otherId);
    }

}

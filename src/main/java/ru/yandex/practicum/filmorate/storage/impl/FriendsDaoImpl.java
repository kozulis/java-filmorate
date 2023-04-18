package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDaoImpl implements FriendsDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        checkUserNotFound(userId);
        checkUserNotFound(friendId);
        if (Objects.equals(userId, friendId)) {
            log.error("Ошибка добавления в список друзей. Id пользователей не должны совпадать.");
            throw new ValidationException("Id пользователей не должны совпадать.");
        }
        jdbcTemplate.update(Constants.INSERT_FRIEND, userId, friendId);
    }

    public Set<Integer> getFriendsIds(Integer userId) {
        List<Integer> ids = jdbcTemplate.queryForList(Constants.SELECT_FRIENDS_ID, Integer.class, userId);
        return new HashSet<>(ids);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        checkUserNotFound(userId);
        checkUserNotFound(friendId);
        jdbcTemplate.update(Constants.DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        checkUserNotFound(userId);
        return jdbcTemplate.query(Constants.SELECT_FRIENDS, this::mapRowToUser, userId);
    }

    public Collection<User> getFriend(Integer userId, Integer friendId) {
        return jdbcTemplate.query(Constants.SELECT_FRIEND, this::mapRowToUser, userId, friendId);
    }

    ;

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        checkUserNotFound(userId);
        checkUserNotFound(otherId);
        return jdbcTemplate.query(Constants.SELECT_COMMON_FRIENDS, this::mapRowToUser, userId, otherId);
    }

    private void checkUserNotFound(int userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        if (Objects.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId), false)) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getInt("user_id"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getDate("birthday").toLocalDate());
    }

}

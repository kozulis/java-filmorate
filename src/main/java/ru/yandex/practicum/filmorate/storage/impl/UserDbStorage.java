package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User create(User user) {
        checkUserNameNotEmpty(user);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(Constants.INSERT_USER, new String[]{"user_id"});
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return user;
    }

    @Override
    public User update(User user) {
        checkUserNotFound(user.getId());
        checkUserNameNotEmpty(user);
        int updateUser = jdbcTemplate.update(Constants.UPDATE_USER, user.getLogin(), user.getName(), user.getEmail(),
                user.getBirthday(), user.getId());
        if (updateUser == 0) {
            log.error("Данные пользователя с id {} не обновлены.", user.getId());
            throw new NotFoundException(String.format("Данные пользователя с id %d не обновлены.", user.getId()));
        }
        return user;
    }

    @Override
    public Collection<User> getAll() {
        List<User> users = jdbcTemplate.query(Constants.SELECT_ALL_USERS, this::mapRowToUser);
        for (User user : users) {
            user.setFriendIds(getFriendsIds(user.getId()));

        }
        return users;
    }

    @Override
    public User getUserById(Integer userId) {
        checkUserNotFound(userId);
        User user = jdbcTemplate.queryForObject(Constants.SELECT_USER_BY_ID, this::mapRowToUser, userId);
        user.setFriendIds(getFriendsIds(user.getId()));
        return user;
    }

    public Set<Integer> getFriendsIds(Integer userId) {
        List<Integer> ids = jdbcTemplate.queryForList(Constants.SELECT_FRIENDS_ID, Integer.class, userId);
        return new HashSet<>(ids);
    }


    @Override
    public User addFriend(Integer userId, Integer friendId) {
        checkUserNotFound(userId);
        checkUserNotFound(friendId);
        if (Objects.equals(userId, friendId)) {
            log.error("Ошибка добавления в список друзей. Id пользователей не должны совпадать.");
            throw new ValidationException("Id пользователей не должны совпадать.");
        }
        jdbcTemplate.update(Constants.INSERT_FRIEND, userId, friendId);
        return getUserById(friendId);
    }

    @Override
    public User deleteFriend(Integer userId, Integer friendId) {
        checkUserNotFound(userId);
        checkUserNotFound(friendId);
        jdbcTemplate.update(Constants.DELETE_FRIEND, userId, friendId);
        return getUserById(friendId);
    }

    @Override
    public Collection<User> getFriends(Integer userId) {
        checkUserNotFound(userId);
        return jdbcTemplate.query(Constants.SELECT_FRIENDS, this::mapRowToUser, userId);
    }

    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        checkUserNotFound(userId);
        checkUserNotFound(otherId);
        return jdbcTemplate.query(Constants.SELECT_COMMON_FRIENDS, this::mapRowToUser, userId, otherId);
    }

    public void deleteUser(Integer userId) {
        checkUserNotFound(userId);
        String sql = "DELETE FROM users WHERE user_id = ?;";
        jdbcTemplate.update(sql, userId);
        log.debug("Пользователь с id {} удален.", userId);
    }

    private void checkUserNameNotEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
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

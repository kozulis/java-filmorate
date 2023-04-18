package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDaoImpl implements FriendsDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(Constants.INSERT_FRIEND, userId, friendId);
    }


    //TODO использовать или удалить
    public Set<Integer> getFriendsIds(Integer userId) {
        List<Integer> ids = jdbcTemplate.queryForList(Constants.SELECT_FRIENDS_ID, Integer.class, userId);
        return new HashSet<>(ids);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        jdbcTemplate.update(Constants.DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Collection<User> getFriendsByUserId(Integer userId) {
        return jdbcTemplate.query(Constants.SELECT_FRIENDS, this::mapRowToUser, userId);
    }

    public Collection<User> getFriend(Integer userId, Integer friendId) {
        return jdbcTemplate.query(Constants.SELECT_FRIEND, this::mapRowToUser, userId, friendId);
    }


    @Override
    public Collection<User> getCommonFriends(Integer userId, Integer otherId) {
        return jdbcTemplate.query(Constants.SELECT_COMMON_FRIENDS, this::mapRowToUser, userId, otherId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return new User(resultSet.getInt("user_id"),
                resultSet.getString("login"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getDate("birthday").toLocalDate());
    }

}

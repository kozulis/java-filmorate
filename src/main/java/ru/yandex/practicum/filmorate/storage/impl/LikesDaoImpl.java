package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.storage.LikesDao;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

@Repository
@RequiredArgsConstructor
public class LikesDaoImpl implements LikesDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(Constants.INSERT_LIKE_TO_FILM, filmId, userId);
    }

    @Override
    public void deleteLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(Constants.DELETE_LIKE_FILM, filmId, userId);
    }

    @Override
    public Integer getFilmLikes(Integer filmId) {
        return jdbcTemplate.queryForObject(Constants.SELECT_LIKE_FILM, Integer.class, filmId);
    }
}

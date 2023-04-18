package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Integer genreId) {
        checkGenreNotFound(genreId);
        return jdbcTemplate.queryForObject(Constants.SELECT_GENRE_BY_ID, this::genreRowMapper, genreId);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(Constants.SELECT_GENRES, this::genreRowMapper);
    }

    @Override
    public Optional<List<Genre>> findByFilmId(Integer filmId) {
        try {
            List<Genre> genreList = jdbcTemplate.query(Constants.SELECT_GENRE_BY_FILM, this::genreRowMapper, filmId);
            return Optional.of(genreList);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateFilmGenres(Integer filmId, Integer genreId) {
        jdbcTemplate.update(Constants.INSERT_FILM_GENRES, filmId, genreId);
    }

    @Override
    public void deleteGenresByFilmId(Integer filmId) {
        jdbcTemplate.update(Constants.DELETE_FILM_GENRES, filmId);
    }

    private void checkGenreNotFound(int genreId) {
        if (Objects.equals(jdbcTemplate.queryForObject(Constants.SELECT_GENRE_EXIST, Boolean.class, genreId), false)) {
            log.error("Жанр с id {} не найден.", genreId);
            throw new NotFoundException(String.format("Жанр с id %d не найден", genreId));
        }
    }

    private Genre genreRowMapper(ResultSet resultSet, int rowMapper) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }
}

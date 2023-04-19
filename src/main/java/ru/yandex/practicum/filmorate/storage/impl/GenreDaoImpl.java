package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<Genre> getGenreById(Integer genreId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(Constants.SELECT_GENRE_BY_ID, this::genreRowMapper, genreId));
        } catch (EmptyResultDataAccessException e) {
            log.error("Жанр с id {} не найден.", genreId);
            return Optional.empty();
        }
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return jdbcTemplate.query(Constants.SELECT_GENRES, this::genreRowMapper);
    }

    @Override
    public Optional<List<Genre>> getByFilmId(Integer filmId) {
        try {
            List<Genre> genreList = jdbcTemplate.query(Constants.SELECT_GENRE_BY_FILM, this::genreRowMapper, filmId);
            return Optional.of(genreList);
        } catch (EmptyResultDataAccessException e) {
            log.error("У фильма с id {} не определены жанры.", filmId);
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

    private Genre genreRowMapper(ResultSet resultSet, int rowMapper) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }
}

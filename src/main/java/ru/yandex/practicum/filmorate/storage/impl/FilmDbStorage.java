package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(Constants.INSERT_FILM, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return film;
    }

    @Override
    public Film update(Film film) {
        int updateFilm = jdbcTemplate.update(Constants.UPDATE_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (updateFilm == 0) {
            log.error("Данные пользователя с id {} не обновлены.", film.getId());
            throw new NotFoundException(String.format("Данные фильма с id %d не обновлены.", film.getId()));
        }
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films = jdbcTemplate.query(Constants.SELECT_ALL_FILMS, this::mapRowToFilm);//        }
        return films;
    }

    @Override
    public Optional<Film> getFilmById(Integer filmId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(Constants.SELECT_FILM_BY_ID, this::mapRowToFilm, filmId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        List<Film> films = jdbcTemplate.query(Constants.SELECT_POPULAR_FILMS, this::mapRowToFilm, count);
        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
            film.setLikes(getFilmLikes(film.getId()));
        }
        return films;
    }

    //TODO добавлен такой же метод в genreDao, этот удалить после рефакторинга
    private Collection<Genre> getFilmGenres(Integer filmId) {
        return jdbcTemplate.query(Constants.SELECT_GENRE_BY_FILM, this::mapRowToGenre, filmId);
    }

    //TODO такой метод добавлен в лайксДао. Удалить после рефакторинга
    public Integer getFilmLikes(Integer filmId) {
        return jdbcTemplate.queryForObject(Constants.SELECT_LIKE_FILM, Integer.class, filmId);
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        return new Film(resultSet.getInt("film_id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getDate("release_date").toLocalDate(),
                resultSet.getLong("duration"),
                new Mpa(resultSet.getInt("mpa_rating_id"),
                        resultSet.getString("mpa_name"),
                        resultSet.getString("mpa_description")));
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"), resultSet.getString("name"));
    }
}

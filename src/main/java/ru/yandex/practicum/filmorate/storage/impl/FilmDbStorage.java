package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    private static final LocalDate BIRTHDAY_MOVIES = LocalDate.of(1895, 12, 28);

    @Override
    public Film create(Film film) {
        checkFilmDate(film);
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

        if (film.getGenres() != null) {
            addGenresToFilm(film);
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        checkFilmNotFound(film.getId());
        checkFilmDate(film);
        int updateFilm = jdbcTemplate.update(Constants.UPDATE_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            deleteGenresFromFilm(film);
        } else {
            film.setGenres(new LinkedHashSet<>(film.getGenres()));
            updateGenreOfFilm(film);
        }
        if (updateFilm == 0) {
            log.error("Данные пользователя с id {} не обновлены.", film.getId());
            throw new NotFoundException(String.format("Данные фильма с id %d не обновлены.", film.getId()));
        }
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        List<Film> films = jdbcTemplate.query(Constants.SELECT_ALL_FILMS, this::mapRowToFilm);
        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
            film.setLikes(getFilmLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        checkFilmNotFound(filmId);
        Film film = jdbcTemplate.queryForObject(Constants.SELECT_FILM_BY_ID, this::mapRowToFilm, filmId);
        film.setGenres(getFilmGenres(filmId));
        film.setLikes(getFilmLikes(filmId));
        return film;
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

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        checkUserNotFound(userId);
        checkFilmNotFound(filmId);
        jdbcTemplate.update(Constants.INSERT_LIKE_TO_FILM, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        checkUserNotFound(userId);
        checkFilmNotFound(filmId);
        jdbcTemplate.update(Constants.DELETE_LIKE_FILM, filmId, userId);
        return getFilmById(filmId);
    }

    public Set<Integer> getFilmLikes(Integer filmId) {
        checkFilmNotFound(filmId);
        List<Integer> filmLikes = jdbcTemplate.queryForList(Constants.SELECT_LIKE_FILM, Integer.class, filmId);
        return new HashSet<>(filmLikes);
    }

    public void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(Constants.INSERT_FILM_GENRE, film.getId(), genre.getId());
        }
    }

    public void deleteGenresFromFilm(Film film) {
        jdbcTemplate.update(Constants.DELETE_GENRE_FILM, film.getId());
    }

    public void updateGenreOfFilm(Film film) {
        deleteGenresFromFilm(film);
        addGenresToFilm(film);
    }

    private Collection<Genre> getFilmGenres(Integer filmId) {
        return jdbcTemplate.query(Constants.INSERT_FILM_GENRES, this::mapRowToGenre, filmId);
    }

    private void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше {} ", BIRTHDAY_MOVIES);
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");
        }
    }

    private void checkFilmNotFound(int filmId) {
        if (Objects.equals(jdbcTemplate.queryForObject(Constants.SELECT_FILM_EXIST, Boolean.class, filmId), false)) {
            log.error("Фильм с id {} не найден.", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
    }

    private void checkUserNotFound(int userId) {
        if (Objects.equals(jdbcTemplate.queryForObject(Constants.SELECT_USER_EXIST, Boolean.class, userId), false)) {
            log.error("Пользователь с id {} не найден.", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
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

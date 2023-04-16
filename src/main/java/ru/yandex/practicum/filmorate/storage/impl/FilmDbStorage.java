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
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        checkFilmDate(film);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
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
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?," +
                " duration = ?, mpa_id = ? WHERE film_id = ?";
        int updateFilm = jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
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
        String sql = "SELECT f.*, m.mpa_rating_id, m.name AS mpa_name, " +
                "m.description AS mpa_description " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_rating_id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm);
        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
            film.setLikes(getFilmLikes(film.getId()));
        }
        return films;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        checkFilmNotFound(filmId);
        String sql = "SELECT f.*, m.mpa_rating_id, m.name AS mpa_name, " +
                "m.description AS mpa_description " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_rating_id " +
                "WHERE film_id = ?";
        Film film = jdbcTemplate.queryForObject(sql, this::mapRowToFilm, filmId);
        film.setGenres(getFilmGenres(filmId));
        film.setLikes(getFilmLikes(filmId));
        return film;
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        String sql = "SELECT f.*, m.mpa_rating_id, m.name AS mpa_name, " +
                "m.description AS mpa_description " +
                "FROM films AS f " +
                "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_rating_id " +
                "LEFT JOIN film_user_likes as l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.user_id) DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, count);
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
        String sql = "INSERT INTO film_user_likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmById(filmId);
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        checkUserNotFound(userId);
        checkFilmNotFound(filmId);
        String sql = "DELETE FROM film_user_likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        return getFilmById(filmId);
    }

    public Set<Integer> getFilmLikes(Integer filmId) {
        checkFilmNotFound(filmId);
        String sql = "SELECT user_id FROM film_user_likes WHERE film_id = ?";
        List<Integer> filmLikes = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        return new HashSet<>(filmLikes);
    }

    public void addGenresToFilm(Film film) {
        for (Genre genre : film.getGenres()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, film.getId(), genre.getId());
        }
    }

    public void deleteGenresFromFilm(Film film) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getId());
    }

    public void updateGenreOfFilm(Film film) {
        deleteGenresFromFilm(film);
        addGenresToFilm(film);
    }

    private Collection<Genre> getFilmGenres(Integer filmId) {
        String sql = "SELECT * FROM genres AS g " +
                "JOIN film_genres AS fg ON g.genre_id = fg.genre_id " +
                "WHERE film_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    private void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше {} ", BIRTHDAY_MOVIES);
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");
        }
    }

    private void checkFilmNotFound(int filmId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        if (Objects.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId), false)) {
            log.error("Фильм с id {} не найден.", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
    }

    private void checkUserNotFound(int userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        if (Objects.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId), false)) {
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

package ru.yandex.practicum.filmorate.storage.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final UserStorage userStorage;

    @Autowired
    public InMemoryFilmStorage(@Qualifier("inMemoryUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private int filmId = 1;
    private static final LocalDate BIRTHDAY_MOVIES = LocalDate.of(1895, 12, 28);

    @Getter
    private final Map<Integer, Film> films = new HashMap<>();


    @Override
    public Film create(Film film) {
        checkValidation(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        checkNotFound(film.getId());
        checkValidation(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        checkNotFound(filmId);
        return films.get(filmId);
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        return getAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        return film;

    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().remove(user.getId());
        return film;
    }

    private void checkValidation(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма не должно превышать 200 символов.");
            throw new ValidationException("Описание фильма не должно превышать 200 символов.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не должно быть пустым.");
            throw new ValidationException("Название фильма не должно быть пустым.");
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше {} ", BIRTHDAY_MOVIES);
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма не может быть отрицательным числом.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным числом.");
        }
    }

    private void checkNotFound(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильм с id {} не существует", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
    }
}

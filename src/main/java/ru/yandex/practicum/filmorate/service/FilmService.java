package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film addLike(Integer filmId, Integer userId) {
        userAndFilmValidation(filmId, userId);
        getFilmById(filmId).getLikes().add(userId);
        return getFilmById(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        userAndFilmValidation(filmId, userId);
        getFilmById(filmId).getLikes().remove(userId);
        return getFilmById(filmId);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getAll().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public void userAndFilmValidation(Integer filmId, Integer userId) {
        if (filmStorage.getAll().contains(getFilmById(filmId))) {
            log.error("Фильм с id {} не найден", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        if (!userStorage.getAll().contains(userStorage.getUserById(userId))) {
            log.error("Пользователь с id {} не найден", userId);
            throw new NotFoundException(String.format("Пользователь с id %d не найден", userId));
        }
    }
}

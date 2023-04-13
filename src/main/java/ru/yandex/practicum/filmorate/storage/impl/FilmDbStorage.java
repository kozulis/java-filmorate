package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@Component("filmDbStorage")
public class FilmDbStorage implements FilmStorage {
    @Override
    public Film create(Film film) {
        return null;
    }

    @Override
    public Film update(Film film) {
        return null;
    }

    @Override
    public Collection<Film> getAll() {
        return null;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        return null;
    }

    @Override
    public Collection<Film> getMostPopularFilms(Integer count) {
        return null;
    }

    @Override
    public Film addLike(Integer filmId, Integer userId) {
        return null;
    }

    @Override
    public Film deleteLike(Integer filmId, Integer userId) {
        return null;
    }
}

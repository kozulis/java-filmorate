package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Collection<Film> getAll();

    Film getFilmById(Integer filmId);

    Collection<Film> getMostPopularFilms(Integer count);

    Film addLike(Integer filmId, Integer userId);

    Film deleteLike(Integer filmId, Integer userId);
}

package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {

    Genre getGenreById(Integer genreId);

    Collection<Genre> getAllGenres();
}

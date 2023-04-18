package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GenreDao {

    Genre getGenreById(Integer genreId);

    Collection<Genre> getAllGenres();

    Optional<List<Genre>> findByFilmId(Integer filmId);

    void updateFilmGenres(Integer filmId, Integer genreId);

    void deleteGenresByFilmId(Integer filmId);
}

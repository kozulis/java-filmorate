package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreDao genreDao;


    private static final LocalDate BIRTHDAY_MOVIES = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, GenreDao genreDao) {
        this.filmStorage = filmStorage;
        this.genreDao = genreDao;
    }


    public Film create(Film film) {
        checkFilmDate(film);
        Film addedFilm = filmStorage.create(film);
        if (film.getGenres() != null) {
            Collection<Genre> genres = film.getGenres();
            genreDao.deleteGenresByFilmId(film.getId());
            genres.forEach(genre -> genreDao.updateFilmGenres(film.getId(), genre.getId()));
            genreDao.findByFilmId(film.getId())
                    .ifPresent(genres1 -> addedFilm.setGenres(new HashSet<>(genres1)));
        }
        return addedFilm;
    }

    public Film update(Film film) {
        checkFilmDate(film);
        Film addedFilm = filmStorage.create(film);



        return addedFilm;
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getFilmById(Integer filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        return filmStorage.getMostPopularFilms(count);
    }

    public Film addLike(Integer filmId, Integer userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        return filmStorage.deleteLike(filmId, userId);
    }

    private void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше {} ", BIRTHDAY_MOVIES);
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");
        }
    }
}

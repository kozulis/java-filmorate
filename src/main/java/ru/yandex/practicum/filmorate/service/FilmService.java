package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreDao genreDao;

    private final LikesService likesService;


    private static final LocalDate BIRTHDAY_MOVIES = LocalDate.of(1895, 12, 28);


    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, GenreDao genreDao, LikesService likesService) {
        this.filmStorage = filmStorage;
        this.genreDao = genreDao;
        this.likesService = likesService;
    }


    public Film create(Film film) {
        checkFilmDate(film);
        Film addedFilm = filmStorage.create(film);
        if (film.getGenres() != null) {
            Collection<Genre> genres = film.getGenres();
            genreDao.deleteGenresByFilmId(film.getId());
            genres.forEach(genre -> genreDao.updateFilmGenres(film.getId(), genre.getId()));
            genreDao.getByFilmId(film.getId())
                    .ifPresent(genres1 -> addedFilm.setGenres(new HashSet<>(genres1)));
        }
        return addedFilm;
    }

    public Film update(Film film) {
        checkFilmDate(film);
        filmStorage.getFilmById(film.getId()).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id %d не найден", film.getId())));
        Film addedFilm = filmStorage.update(film);
        if (film.getGenres() != null) {
            Collection<Genre> genres = new HashSet<>(film.getGenres());
            genreDao.deleteGenresByFilmId(film.getId());
            genres.forEach(genre -> genreDao.updateFilmGenres(film.getId(), genre.getId()));
            genreDao.getByFilmId(film.getId())
                    .ifPresent(genres1 -> addedFilm.setGenres(new LinkedHashSet<>(genres1)));
        } else {
            genreDao.deleteGenresByFilmId(film.getId());
        }
        return addedFilm;
    }

    public Collection<Film> getAll() {
        Collection<Film> films = filmStorage.getAll();
        films.forEach(this::addLikesAndGenresToFilm);
        return films;
    }

    public Film getFilmById(Integer filmId) {
        Film film = filmStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id %d не найден", filmId)));
        addLikesAndGenresToFilm(film);
        return film;
    }

    public Collection<Film> getMostPopularFilms(Integer count) {
        List<Film> films = getAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
        return films;
    }

    private void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше {} ", BIRTHDAY_MOVIES);
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");
        }
    }

    private void addLikesAndGenresToFilm(Film film) {
        film.setLikes(likesService.getFilmLikes(film.getId()));
        genreDao.getByFilmId(film.getId()).ifPresent(genres -> film.setGenres(new HashSet<>(genres)));
    }
}

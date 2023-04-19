package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.LikesDao;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

@Slf4j
@Service
public class LikesService {
    private final LikesDao likesDao;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    public LikesService(LikesDao likesDao, FilmDbStorage filmDbStorage, UserDbStorage userDbStorage) {
        this.likesDao = likesDao;
        this.filmDbStorage = filmDbStorage;
        this.userDbStorage = userDbStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        filmDbStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id %d не найден", filmId)));
        userDbStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        likesDao.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmDbStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id %d не найден", filmId)));
        userDbStorage.getUserById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id %d не найден", userId)));
        likesDao.deleteLike(filmId, userId);
    }

    public Integer getFilmLikes(Integer filmId) {
        filmDbStorage.getFilmById(filmId).orElseThrow(() ->
                new NotFoundException(String.format("Фильм с id %d не найден", filmId)));
        return likesDao.getFilmLikes(filmId);
    }
}

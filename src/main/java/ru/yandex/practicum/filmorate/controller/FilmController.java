package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Добавление фильма {} ", film);
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Обновление фильма {} ", film);
        return filmStorage.update(film);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("Получаем список фильмов.");
        return filmStorage.getAll();
    }

    @GetMapping("/{id}")
    public Film getUserById(@PathVariable Integer id) {
        log.debug("Получаем фильм по id {}.", id);
        return filmStorage.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug("Пользователь с id {} ставит лайк фильму с id {}", userId, id);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug("Пользователь с id {} удаляет лайк фильму с id {}", userId, id);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("Получаем список популярных фильмов.");
        return filmService.getMostPopularFilms(count);
    }
}

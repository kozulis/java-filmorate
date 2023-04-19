package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikesService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final LikesService likesService;

    @Autowired
    public FilmController(FilmService filmService, LikesService likesService) {
        this.filmService = filmService;
        this.likesService = likesService;
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Добавление фильма {} ", film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Обновление фильма {} ", film);
        return filmService.update(film);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("Получаем список фильмов.");
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getUserById(@PathVariable Integer id) {
        log.debug("Получаем фильм по id {}.", id);
        return filmService.getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug("Пользователь с id {} ставит лайк фильму с id {}", userId, id);
        likesService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        log.debug("Пользователь с id {} удаляет лайк фильму с id {}", userId, id);
        likesService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count) {
        log.debug("Получаем список популярных фильмов.");
        return filmService.getMostPopularFilms(count);
    }
}

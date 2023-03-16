package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.debug("Добавление фильма {} ", film);
        return inMemoryFilmStorage.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Обновление фильма {} ", film);
        return inMemoryFilmStorage.update(film);
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.debug("Получаем список фильмов.");
        return inMemoryFilmStorage.getAll();
    }
}

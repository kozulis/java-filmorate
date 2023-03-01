package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private int filmId = 1;
    private static final LocalDate BIRTHDAY_MOVIES = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("Добавление фильма {} ", film);
        checkFilmDate(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновление фильма {} ", film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не существует", film.getId());
            throw new ValidationException("Фильм не найден, невозможно обновить данные фильма.");
        }
        checkFilmDate(film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> getFilmList() {
        return films.values();
    }

    public void checkFilmDate(Film film) {
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше 1895.12.28");
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");

        }
    }
}

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
        checkFilm(film);
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
        checkFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> getFilmList() {
        return films.values();
    }

    public void checkFilm(Film film) {
        if (film.getDescription().length() > 200) {
            log.error("Описание фильма не должно превышать 200 символов.");
            throw new ValidationException("Описание фильма не должно превышать 200 символов.");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название фильма не должно быть пустым.");
            throw new ValidationException("Название фильма не должно быть пустым.");
        }
        if (film.getReleaseDate().isBefore(BIRTHDAY_MOVIES)) {
            log.error("Дата релиза фильма не может быть раньше {} ", BIRTHDAY_MOVIES);
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895.12.28");
        }
        if (film.getDuration() < 0) {
            log.error("Продолжительность фильма не может быть отрицательным числом.");
            throw new ValidationException("Продолжительность фильма не может быть отрицательным числом.");
        }
    }
}

package ru.yandex.practicum.filmorate.storage;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{

    private int filmId = 1;
    private static final LocalDate BIRTHDAY_MOVIES = LocalDate.of(1895, 12, 28);

    @Getter
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        check(film);
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id {} не найден.", film.getId());
            throw new NotFoundException("Фильм не найден, невозможно обновить данные фильма.");
        }
        check(film);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Integer filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильм с id {} не существует", filmId);
            throw new NotFoundException(String.format("Фильм с id %d не найден", filmId));
        }
        return films.get(filmId);
    }

    public void check(Film film) {
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

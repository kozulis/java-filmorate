package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    FilmController filmController;
    Film film;

    @BeforeEach
    void before() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    void shouldAddFilm() {
        film = new Film("Кин-Дза-Дза", "Инопланетные путешествия",
                LocalDate.of(1985, 12, 5), 120L);
        filmController.create(film);
        assertEquals(1, filmController.getAll().size(), "Список фильмов пуст");

        film = new Film("Кин-Дза-Дза", "Инопланетные путешествия",
                LocalDate.of(1895, 12, 28), 120L);
        filmController.create(film);
        assertEquals(2, filmController.getAll().size(), "Список фильмов пуст");
    }

    @Test
    void shouldNotAddFilmWithEmptyOrNullName() {
        film = new Film(null, "Инопланетные путешествия",
                LocalDate.of(1985, 12, 5), 120L);
        assertThrows(ValidationException.class, () -> filmController.create(film));
        film = new Film("", "Инопланетные путешествия",
                LocalDate.of(1985, 12, 5), 120L);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldNotAddFilmWithLongDescription() {
        film = new Film("Кин-Дза-Дза", "Два знакомых человека встречают на улице прохожего, " +
                "который предлагает нажать +" +
                "на кнопку на неизвестном устройстве. Один из героев фильма нажимает на кнопку, и оба героя " +
                "оказываются на другой планете, покрытой песками. Герои пытаются найти обратный путь на свою " +
                "планету, попадая в череду загадочных и необъяснимых событий...",
                LocalDate.of(1985, 12, 5), 120L);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldNotAddFilmWithWrongReleaseDate() {
        film = new Film("Кин-Дза-Дза", "Инопланетные путешествия",
                LocalDate.of(1895, 12, 27), 120L);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldNotAddFilmWithNegativeDuration() {
        film = new Film("Кин-Дза-Дза", "Инопланетные путешествия",
                LocalDate.of(1985, 12, 5), -5L);
        assertThrows(ValidationException.class, () -> filmController.create(film));
    }

    @Test
    void shouldUpdateFilm() {
        film = new Film("Кин-Дза-Дза", "Инопланетные путешествия",
                LocalDate.of(1985, 12, 5), 120L);
        filmController.create(film);
        Film film1 = new Film("Кин-Дза-Дза Update", "Инопланетные путешествия",
                LocalDate.of(1985, 12, 5), 120L);
        film1.setId(film.getId());
        filmController.update(film1);
        assertEquals(1, filmController.getAll().size(), "Список пустой.");
        assertTrue(filmController.getAll().contains(film1), "Фильм не обновился.");
    }
}
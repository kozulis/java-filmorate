package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.util.Collection;

@Slf4j
@Service
public class GenreService {

    private final GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre getGenreById(Integer genreId) {
        Genre genre = genreDao.getGenreById(genreId).orElseThrow(() -> new NotFoundException(String.format
                ("Жанр с id %d не найден", genreId)));
        return genre;
    }

    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}

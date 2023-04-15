package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Repository
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre getGenreById(Integer genreId) {
        checkGenreNotFound(genreId);
        String sql = "SELECT * FROM genres WHERE genre_id = ?";
        return jdbcTemplate.queryForObject(sql, this::genreRowMapper, genreId);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::genreRowMapper);
    }

    private void checkGenreNotFound(int genreId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM genres WHERE genre_id = ?)";
        if (Objects.equals(jdbcTemplate.queryForObject(sql, Boolean.class, genreId), false)) {
            log.error("Жанр с id {} не найден.", genreId);
            throw new NotFoundException(String.format("Жанр с id %d не найден", genreId));
        }
    }

    private Genre genreRowMapper(ResultSet resultSet, int rowMapper) throws SQLException {
        return new Genre(resultSet.getInt("genre_id"),
                resultSet.getString("name"));
    }
}

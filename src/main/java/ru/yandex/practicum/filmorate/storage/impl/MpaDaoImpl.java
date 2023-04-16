package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDao;
import ru.yandex.practicum.filmorate.storage.impl.sql.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa getMpaById(Integer mpaId) {
        checkMpaNotFound(mpaId);
        return jdbcTemplate.queryForObject(Constants.SELECT_MPA_BY_ID, this::mpaRowMapper, mpaId);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return jdbcTemplate.query(Constants.SELECT_MPA, this::mpaRowMapper);
    }

    private void checkMpaNotFound(int mpaId) {
        if (Objects.equals(jdbcTemplate.queryForObject(Constants.CHECK_MPA_EXIST, Boolean.class, mpaId), false)) {
            log.error("Рейтинг MPA с id {} не найден.", mpaId);
            throw new NotFoundException(String.format("Рейтинг с id %d не найден", mpaId));
        }
    }

    private Mpa mpaRowMapper(ResultSet resultSet, int rowNum) throws SQLException {
        return new Mpa(resultSet.getInt("mpa_rating_id"),
                resultSet.getString("name"),
                resultSet.getString("description"));
    }
}

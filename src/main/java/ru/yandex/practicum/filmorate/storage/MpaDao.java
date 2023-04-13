package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {

    Mpa getMpaById(Integer mpaId);

    Collection<Mpa> getAllMpa();
}

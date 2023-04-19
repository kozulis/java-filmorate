package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaDao;

import java.util.Collection;

@Slf4j
@Service
public class MpaService {

    private final MpaDao mpaDao;

    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Collection<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    public Mpa getMpaById(Integer id) {
        return mpaDao.getMpaById(id);
    }
}

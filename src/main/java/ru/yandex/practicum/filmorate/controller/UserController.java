package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    InMemoryUserStorage inMemoryUserStorage = new InMemoryUserStorage();

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.debug("Добавление пользователя {} ", user);
        return inMemoryUserStorage.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Обновление данных пользователя {} ", user);
        return inMemoryUserStorage.update(user);
    }

    @GetMapping
    public Collection<User> getAll() {
        log.debug("Получаем список пользователей.");
        return inMemoryUserStorage.getAll();
    }
}

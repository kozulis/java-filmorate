package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private int userId = 1;
    private final Map<Integer, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("Добавление пользователя {} ", user);
        checkUser(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Обновление данных пользователя {} ", user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь {} не найден", user);
            throw new ValidationException("Пользователь с таким id не найден.");
        }
        checkUser(user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
    }

    public void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Адрес электронной почты не может быть пустым.");
            throw new ValidationException("Адрес электронной почты не может быть пустым.");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Адрес электронной почты указан в неверном формате.");
            throw new ValidationException("Адрес электронной почты указан в неверном формате.");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не должен быть пустым или содержать пробелы");
            throw new ValidationException("Логин не должен быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть указана позже текущей даты.");
            throw new ValidationException("Дата рождения не может быть указана позже текущей даты.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

    }
}

package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void shouldAddUser() {
        user = new User("somewhere@something.com", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        userController.create(user);
        assertEquals(1, userController.getAll().size());
    }

    @Test
    void shouldNotAddUserWithoutEmail() {
        user = new User(null, "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));
        user = new User("", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldNotAddUserWithWrongEmailStructure() {
        user = new User("somewheresomething.com", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldNotAddUserWithEmptyLogin() {
        user = new User("somewhere@something.com", null, "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));
        assertFalse(userController.getAll().contains(user));
    }

    @Test
    void shouldNotAddUserWithBlankInLogin() {
        user = new User("somewhere@something.com", "", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));

        user = new User("somewhere@something.com", "goga goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldAddUserWithEmptyName() {
        user = new User("somewhere@something.com", "goga", null,
                LocalDate.of(1995, 5, 3));
        userController.create(user);
        assertEquals(1, userController.getAll().size());
        assertTrue(userController.getAll().contains(user));
    }


    @Test
    void shouldNotAddUserWithFutureBirthday() {
        user = new User("somewhere@something.com", "goga", "Гоша",
                LocalDate.of(2995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.create(user));
    }

    @Test
    void shouldUpdateUser() {
        user = new User("somewhere@something.com", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        userController.create(user);

        User user1 = new User("somewhere@something.com", "gogaUpdate", "Гоша",
                LocalDate.of(1995, 5, 3));
        user1.setId(user.getId());
        userController.update(user1);
        assertEquals(1, userController.getAll().size(), "Список пустой.");
        assertTrue(userController.getAll().contains(user1), "Фильм не обновился.");
    }
}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    UserController userController;
    User user;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController();
    }

    @Test
    void shouldAddUser() {
        user = new User("somewhere@something.com", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        userController.addUser(user);
        assertEquals(1, userController.getUsers().size());
    }

    @Test
    void shouldNotAddUserWithoutEmail() {
        user = new User(null, "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
        user = new User("", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldNotAddUserWithWrongEmailStructure() {
        user = new User("somewheresomething.com", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldNotAddUserWithEmptyLogin() {
        user = new User("somewhere@something.com", null, "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
        assertFalse(userController.getUsers().contains(user));
    }

    @Test
    void shouldNotAddUserWithBlankInLogin() {
        user = new User("somewhere@something.com", "", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));

        user = new User("somewhere@something.com", "goga goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldAddUserWithEmptyName() {
        user = new User("somewhere@something.com", "goga", null,
                LocalDate.of(1995, 5, 3));
        userController.addUser(user);
        assertEquals(1, userController.getUsers().size());
        assertTrue(userController.getUsers().contains(user));
    }


    @Test
    void shouldNotAddUserWithFutureBirthday() {
        user = new User("somewhere@something.com", "goga", "Гоша",
                LocalDate.of(2995, 5, 3));
        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    void shouldUpdateUser() {
        user = new User("somewhere@something.com", "goga", "Гоша",
                LocalDate.of(1995, 5, 3));
        userController.addUser(user);

        User user1 = new User("somewhere@something.com", "gogaUpdate", "Гоша",
                LocalDate.of(1995, 5, 3));
        user1.setId(user.getId());
        userController.update(user1);
        assertEquals(1, userController.getUsers().size(), "Список пустой.");
        assertTrue(userController.getUsers().contains(user1), "Фильм не обновился.");
    }
}
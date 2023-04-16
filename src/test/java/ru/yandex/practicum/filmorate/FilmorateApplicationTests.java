package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final GenreDaoImpl genreDaoImpl;
    private final MpaDaoImpl mpaDaoImpl;

    User user = User.builder()
            .login("login6")
            .name("name6")
            .email("email6")
            .birthday(LocalDate.of(2006, 6, 6))
            .friendIds(Collections.emptySet())
            .build();


    User updateUser = User.builder()
            .id(6)
            .login("login6update")
            .name("name6update")
            .email("email6update")
            .birthday(LocalDate.of(2006, 6, 6))
            .friendIds(Collections.emptySet())
            .build();

    Film film = Film.builder()
            .name("film6")
            .description("description6")
            .releaseDate(LocalDate.of(2010, 1, 16))
            .duration(60L)
            .mpa(new Mpa(1))
            .build();

    Film updateFilm = Film.builder()
            .id(6)
            .name("film6update")
            .description("description6update")
            .releaseDate(LocalDate.of(2010, 1, 16))
            .duration(60L)
            .mpa(new Mpa(1))
            .build();

    //Test userDbStorage

    @Test
    public void ShouldCreateUser() {
        userDbStorage.create(user);
        assertThat(userDbStorage.getUserById(user.getId())).isEqualTo(user);
    }

    @Test
    public void ShouldUpdateUser() {
        userDbStorage.update(updateUser);
        assertThat(userDbStorage.getUserById(updateUser.getId()).getName())
                .isEqualTo(updateUser.getName());
    }

    @Test
    public void ShouldGetAllUsers() {
        Collection<User> users = userDbStorage.getAll();
        assertThat(users).isNotEmpty();
        assertThat(new ArrayList<>(users).get(2).getName()).isEqualTo("name3");
    }

    @Test
    public void ShouldGetUserById() {
        User user1 = userDbStorage.getUserById(1);
        assertThat(user1.getId()).isEqualTo(1);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 1);
        assertThat(user1).hasFieldOrPropertyWithValue("login", "login1");
    }

    @Test
    public void ShouldAddFriend() {
        userDbStorage.addFriend(3, 2);
        Collection<User> friends = userDbStorage.getFriends(3);
        assertThat(friends).isNotEmpty().hasSize(2);
    }

    @Test
    public void ShouldDeleteFriend() {
        userDbStorage.deleteFriend(4, 2);
        Collection<User> friends = userDbStorage.getFriends(4);
        assertThat(friends).hasSize(0);
    }

    @Test
    public void ShouldGetFriends() {
        Collection<User> friends = userDbStorage.getFriends(1);
        assertThat(friends).hasSize(2);
    }

    @Test
    public void ShouldGetCommonFriends() {
        Collection<User> commonFriends = userDbStorage.getCommonFriends(1, 2);
        assertThat(commonFriends).hasSize(1);
    }

    //Test filmDbStorage

    @Test
    void create() {
        filmDbStorage.create(film);
        assertThat(filmDbStorage.getFilmById(film.getId()))
                .hasFieldOrPropertyWithValue("name", "film6");
    }

    //
    @Test
    void update() {
        filmDbStorage.update(updateFilm);
        assertThat(filmDbStorage.getFilmById(updateFilm.getId()).getName())
                .isEqualTo(updateFilm.getName());
        assertThat(filmDbStorage.getFilmById(updateFilm.getId()))
                .hasFieldOrPropertyWithValue("name", "film6update");
    }


    @Test
    void getAll() {
        Collection<Film> films = filmDbStorage.getAll();
        assertThat(films).isNotEmpty();
        assertThat(new ArrayList<>(films).get(2)
                .getDescription()).isEqualTo("description3");
    }

    @Test
    void getFilmById() {
        Film film1 = filmDbStorage.getFilmById(1);
        assertThat(film1.getId()).isEqualTo(1);
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1);
        assertThat(film1).hasFieldOrPropertyWithValue("name", "film1");
    }

    @Test
    void getMostPopularFilms() {
        Collection<Film> popularFilms = filmDbStorage.getMostPopularFilms(1);
        assertThat(popularFilms.size()).isEqualTo(1);
        assertThat(new ArrayList<>(popularFilms).get(0))
                .hasFieldOrPropertyWithValue("name", "film1");
    }

    @Test
    void addLike() {
        Film film1 = filmDbStorage.addLike(2, 3);
        assertThat(film1.getLikes().size()).isEqualTo(2);
    }

    @Test
    void deleteLike() {
        Film film1 = filmDbStorage.deleteLike(3, 3);
        assertThat(film1.getLikes().size()).isEqualTo(0);
    }

    //Test MpaDaoImpl

    @Test
    void getMpaById() {
        mpaDaoImpl.getMpaById(3);
        assertThat(mpaDaoImpl.getMpaById(3))
                .hasFieldOrPropertyWithValue("description", "Детям до 13 лет просмотр не желателен");
    }

    @Test
    void getAllMpa() {
        Collection<Mpa> mpaList = mpaDaoImpl.getAllMpa();
        assertThat(mpaList.size()).isEqualTo(5);
    }

    //Test GenreDaoImpl

    @Test
    void getGenreById() {
        assertThat(genreDaoImpl.getGenreById(4))
                .hasFieldOrPropertyWithValue("name", "Триллер");
    }

    @Test
    void getAllGenres() {
        Collection<Genre> genresList = genreDaoImpl.getAllGenres();
        assertThat(genresList.size()).isEqualTo(6);
    }
}

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
class FilmorateApplicationTests {
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
            .id(1)
            .login("login1update")
            .name("name1update")
            .email("email1update")
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
            .id(1)
            .name("film1update")
            .description("description1update")
            .releaseDate(LocalDate.of(2010, 1, 16))
            .duration(60L)
            .mpa(new Mpa(1))
            .build();

    //Test userDbStorage

    @Test
    public void shouldCreateUser() {
        userDbStorage.create(user);
        assertThat(userDbStorage.getUserById(user.getId())).isEqualTo(user);
    }
//
//    @Test
//    public void shouldUpdateUser() {
//        userDbStorage.update(updateUser);
//        assertThat(userDbStorage.getUserById(updateUser.getId()).getName())
//                .isEqualTo(updateUser.getName());
//    }

    @Test
    public void shouldGetAllUsers() {
        Collection<User> users = userDbStorage.getAll();
        assertThat(users).isNotEmpty();
        assertThat(new ArrayList<>(users).get(2).getName()).isEqualTo("name3");
    }

//    @Test
//    public void shouldGetUserById() {
//        User user1 = userDbStorage.getUserById(2);
//        assertThat(user1.getId()).isEqualTo(2);
//        assertThat(user1).hasFieldOrPropertyWithValue("id", 2);
//        assertThat(user1).hasFieldOrPropertyWithValue("login", "login2");
//    }

    @Test
    public void shouldAddFriend() {
        userDbStorage.addFriend(3, 2);
        Collection<User> friends = userDbStorage.getFriends(3);
        assertThat(friends).isNotEmpty().hasSize(2);
    }

    @Test
    public void shouldDeleteFriend() {
        userDbStorage.deleteFriend(4, 2);
        Collection<User> friends = userDbStorage.getFriends(4);
        assertThat(friends).hasSize(0);
    }

    @Test
    public void shouldGetFriends() {
        Collection<User> friends = userDbStorage.getFriends(1);
        assertThat(friends).hasSize(2);
    }

    @Test
    public void shouldGetCommonFriends() {
        Collection<User> commonFriends = userDbStorage.getCommonFriends(1, 2);
        assertThat(commonFriends).hasSize(1);
    }

    //Test filmDbStorage

//    @Test
//    void shouldCreateFilm() {
//        filmDbStorage.create(film);
//        assertThat(filmDbStorage.getFilmById(film.getId()))
//                .hasFieldOrPropertyWithValue("name", "film6");
//    }
//
//    //
//    @Test
//    void shouldUpdateFilm() {
//        filmDbStorage.update(updateFilm);
//        assertThat(filmDbStorage.getFilmById(updateFilm.getId()).getName())
//                .isEqualTo(updateFilm.getName());
//        assertThat(filmDbStorage.getFilmById(updateFilm.getId()))
//                .hasFieldOrPropertyWithValue("name", "film1update");
//    }


    @Test
    void shouldGetAllFilms() {
        Collection<Film> films = filmDbStorage.getAll();
        assertThat(films).isNotEmpty();
        assertThat(new ArrayList<>(films).get(2)
                .getDescription()).isEqualTo("description3");
    }

//    @Test
//    void shouldGetFilmById() {
//        Film film1 = filmDbStorage.getFilmById(1);
//        assertThat(film1.getId()).isEqualTo(1);
//        assertThat(film1).hasFieldOrPropertyWithValue("id", 1);
//        assertThat(film1).hasFieldOrPropertyWithValue("name", "film1update");
//    }

    @Test
    void shouldGetMostPopularFilms() {
        Collection<Film> popularFilms = filmDbStorage.getMostPopularFilms(1);
        assertThat(popularFilms.size()).isEqualTo(1);
        assertThat(new ArrayList<>(popularFilms).get(0))
                .hasFieldOrPropertyWithValue("name", "film1update");
    }

//    @Test
//    void shouldAddLikeToFilm() {
//        Film film1 = filmDbStorage.addLike(2, 3);
//        assertThat(film1.getLikes().size()).isEqualTo(2);
//    }

//    @Test
//    void shouldDeleteLike() {
//        Film film1 = filmDbStorage.deleteLike(3, 3);
//        assertThat(film1.getLikes().size()).isEqualTo(0);
//    }

    //Test MpaDaoImpl

    @Test
    void shouldGetMpaById() {
        mpaDaoImpl.getMpaById(3);
        assertThat(mpaDaoImpl.getMpaById(3))
                .hasFieldOrPropertyWithValue("description", "Детям до 13 лет просмотр не желателен");
    }

    @Test
    void shouldGetAllMpa() {
        Collection<Mpa> mpaList = mpaDaoImpl.getAllMpa();
        assertThat(mpaList.size()).isEqualTo(5);
    }

    //Test GenreDaoImpl

    @Test
    void shouldGetGenreFilmById() {
        assertThat(genreDaoImpl.getGenreById(4))
                .hasFieldOrPropertyWithValue("name", "Триллер");
    }

    @Test
    void getAllGenresOfFilm() {
        Collection<Genre> genresList = genreDaoImpl.getAllGenres();
        assertThat(genresList.size()).isEqualTo(6);
    }
}

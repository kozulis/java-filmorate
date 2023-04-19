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
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FriendsDao;
import ru.yandex.practicum.filmorate.storage.LikesDao;
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
    private final FriendsDao friendsDao;
    private final LikesDao likesDao;
    private final FilmService filmService;

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
            .mpa(new Mpa(1, "G", "У фильма нет возрастных ограничений"))
            .build();

    Film updateFilm = Film.builder()
            .id(1)
            .name("film1update")
            .description("description1update")
            .releaseDate(LocalDate.of(2010, 1, 16))
            .duration(60L)
            .mpa(new Mpa(1, "G", "У фильма нет возрастных ограничений"))
            .build();

    /**
     * Test userDbStorage
     */

    @Test
    public void shouldCreateUser() {
        userDbStorage.create(user);
        User user1 = userDbStorage.getUserById(6).orElse(new User());
        assertThat(user1).isEqualTo(user);

    }

    @Test
    public void shouldUpdateUser() {
        userDbStorage.update(updateUser);
        User user1 = userDbStorage.getUserById(updateUser.getId()).orElse(new User());
        assertThat(user1.getName())
                .isEqualTo(updateUser.getName());
    }

    @Test
    public void shouldGetAllUsers() {
        Collection<User> users = userDbStorage.getAll();
        assertThat(users).isNotEmpty();
        assertThat(new ArrayList<>(users).get(2).getName()).isEqualTo("name3");
    }

    @Test
    public void shouldGetUserById() {
        User user1 = userDbStorage.getUserById(2).orElse(new User());
        assertThat(user1.getId()).isEqualTo(2);
        assertThat(user1).hasFieldOrPropertyWithValue("id", 2);
        assertThat(user1).hasFieldOrPropertyWithValue("login", "login2");
    }

    @Test
    public void shouldAddFriend() {
        friendsDao.addFriend(3, 2);
        Collection<User> friends = friendsDao.getFriendsByUserId(3);
        assertThat(friends).isNotEmpty().hasSize(2);
    }

    @Test
    public void shouldDeleteFriend() {
        friendsDao.deleteFriend(4, 2);
        Collection<User> friends = friendsDao.getFriendsByUserId(4);
        assertThat(friends).hasSize(0);
    }

    @Test
    public void shouldGetFriends() {
        Collection<User> friends = friendsDao.getFriendsByUserId((1));
        assertThat(friends).hasSize(2);
    }

    @Test
    public void shouldGetCommonFriends() {
        Collection<User> commonFriends = friendsDao.getCommonFriends(1, 2);
        assertThat(commonFriends).hasSize(1);
    }

    /**
     * Test filmDbStorage
     */

    @Test
    void shouldCreateFilm() {
        filmDbStorage.create(film);
        Film film1 = filmDbStorage.getFilmById(film.getId()).orElse(new Film());
        assertThat(film1).isEqualTo(film);
    }

    //
    @Test
    void shouldUpdateFilm() {
        filmDbStorage.update(updateFilm);
        Film film1 = filmDbStorage.getFilmById(updateFilm.getId()).orElse(new Film());
        assertThat(film1).isEqualTo(updateFilm);
    }


    @Test
    void shouldGetAllFilms() {
        Collection<Film> films = filmDbStorage.getAll();
        assertThat(films).isNotEmpty();
        assertThat(new ArrayList<>(films).get(2)
                .getDescription()).isEqualTo("description3");
    }

    @Test
    void shouldGetFilmById() {
        Film film1 = filmDbStorage.getFilmById(1).orElse(new Film());
        assertThat(film1).hasFieldOrPropertyWithValue("id", 1);
        assertThat(film1).hasFieldOrPropertyWithValue("name", "film1update");
    }

    @Test
    void shouldGetMostPopularFilms() {
        Collection<Film> popularFilms = filmService.getMostPopularFilms(1);
        assertThat(popularFilms.size()).isEqualTo(1);
        assertThat(new ArrayList<>(popularFilms).get(0))
                .hasFieldOrPropertyWithValue("name", "film1update");
    }

    @Test
    void shouldAddLikeToFilm() {
        likesDao.addLike(2, 3);
        Film film1 = filmService.getFilmById(1);
        assertThat(film1.getLikes()).isEqualTo(2);
    }

    @Test
    void shouldDeleteLike() {
        Film film1 = filmService.getFilmById(3);
        assertThat(film1.getLikes()).isEqualTo(1);
        likesDao.deleteLike(3, 3);
        Film film2 = filmService.getFilmById(3);
        assertThat(film2.getLikes()).isEqualTo(0);
    }

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

    /**
     *Test GenreDaoImpl
     */

    @Test
    void shouldGetGenreFilmById() {
        Genre genre = genreDaoImpl.getGenreById(4).orElse(new Genre());
        assertThat(genre.getName()).isEqualTo("Триллер");
    }

    @Test
    void getAllGenresOfFilm() {
        Collection<Genre> genresList = genreDaoImpl.getAllGenres();
        assertThat(genresList.size()).isEqualTo(6);
    }
}

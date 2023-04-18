package ru.yandex.practicum.filmorate.storage.impl.sql;

public class Constants {

    /**
     * Запросы для UserDao
     */

    public static final String INSERT_USER = "INSERT INTO users (login, name, email, birthday) VALUES (?, ?, ?, ?)";
    public static final String INSERT_FRIEND = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
    public static final String SELECT_ALL_USERS = "SELECT * FROM users";
    public static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    public static final String SELECT_FRIENDS_ID = "SELECT friend_id FROM friends WHERE user_id = ? AND status_id = 1";
    public static final String SELECT_FRIENDS = "SELECT * FROM users WHERE user_id IN " +
            "(SELECT friend_id FROM friends WHERE user_id = ? AND status_id = 1)";
    public static final String SELECT_FRIEND = "SELECT * FROM users AS u " +
            "RIGHT JOIN friends AS fr ON u.user_id = fr.friend_id WHERE fr.user_id = ? AND fr.friend_id = ?";
    public static final String SELECT_COMMON_FRIENDS = "SELECT u.* FROM friends AS fr " +
            "JOIN users AS u ON fr.friend_id = u.user_id " +
            "WHERE fr.user_id = ? AND fr.friend_id IN " +
            "(SELECT friend_id FROM friends WHERE user_id = ?)";
    public static final String SELECT_USER_EXIST = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";

    public static final String UPDATE_USER = "UPDATE users SET login = ?, name = ?, email = ?, birthday = ? WHERE user_id = ?";
    public static final String DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND friend_id = ? AND status_id = 1";

    /**
     * Запросы для FilmDao
     */
    public static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
            "VALUES (?, ?, ?, ?, ?)";
    public static final String INSERT_LIKE_TO_FILM = "INSERT INTO film_user_likes (film_id, user_id) VALUES (?, ?)";
    public static final String INSERT_GENRE_TO_FILM = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    public static final String SELECT_ALL_FILMS = "SELECT f.*, m.mpa_rating_id, m.name AS mpa_name, " +
            "m.description AS mpa_description " +
            "FROM films AS f " +
            "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_rating_id";
    public static final String SELECT_LIKE_FILM = "SELECT COUNT(user_id) FROM film_user_likes WHERE film_id = ?";
    public static final String SELECT_FILM_BY_ID = "SELECT f.*, m.mpa_rating_id, m.name AS mpa_name, " +
            "m.description AS mpa_description " +
            "FROM films AS f " +
            "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_rating_id " +
            "WHERE film_id = ?";
    public static final String SELECT_POPULAR_FILMS = "SELECT f.*, m.mpa_rating_id, m.name AS mpa_name, " +
            "m.description AS mpa_description " +
            "FROM films AS f " +
            "JOIN mpa_ratings AS m ON f.mpa_id = m.mpa_rating_id " +
            "LEFT JOIN film_user_likes as l ON f.film_id = l.film_id " +
            "GROUP BY f.film_id " +
            "ORDER BY COUNT(l.user_id) DESC " +
            "LIMIT ?";
    public static final String SELECT_FILM_EXIST = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
    public static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?," +
            " duration = ?, mpa_id = ? WHERE film_id = ?";
    public static final String DELETE_LIKE_FILM = "DELETE FROM film_user_likes WHERE film_id = ? AND user_id = ?";

    /**
     * Запросы для GenreDao
     */

    public static final String SELECT_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?";
    public static final String SELECT_GENRE_BY_FILM = "SELECT * FROM genres AS g " +
            "JOIN film_genres AS fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";
    public static final String INSERT_FILM_GENRES = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    public static final String DELETE_FILM_GENRES = "DELETE FROM film_genres WHERE film_id = ?";
    public static final String SELECT_GENRES = "SELECT * FROM genres";
    public static final String SELECT_GENRE_EXIST = "SELECT EXISTS(SELECT 1 FROM genres WHERE genre_id = ?)";

    /**
     * Запросы для MpaDao
     */

    public static final String SELECT_MPA_BY_ID = "SELECT * FROM mpa_ratings WHERE mpa_rating_id = ?";
    public static final String SELECT_MPA = "SELECT * FROM mpa_ratings";
    public static final String CHECK_MPA_EXIST = "SELECT EXISTS(SELECT 1 FROM mpa_ratings WHERE mpa_rating_id = ?)";
}


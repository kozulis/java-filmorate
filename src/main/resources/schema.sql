DROP TABLE IF EXISTS users, mpa_ratings, films, genres, film_genres, film_user_likes, friendship_statuses, friends;

CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER generated by default AS IDENTITY primary key,
    login varchar(255) NOT NULL,
    name varchar(255),
    email varchar(50) NOT NULL,
    birthday DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS mpa_ratings (
    mpa_rating_id INTEGER PRIMARY KEY NOT NULL,
    name varchar(100) NOT NULL,
    description varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    film_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    name varchar(100) NOT NULL ,
    description varchar(200),
    release_date DATE NOT NULL,
    duration INTEGER CHECK(duration > 0),
    mpa_id INTEGER REFERENCES mpa_ratings (mpa_rating_id)
);


CREATE TABLE IF NOT EXISTS genres (
    genre_id INTEGER PRIMARY KEY NOT NULL ,
    name varchar(25) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id INTEGER REFERENCES films (film_id),
    genre_id INTEGER REFERENCES genres (genre_id),
    PRIMARY KEY (film_id, genre_id)
);


CREATE TABLE IF NOT EXISTS film_user_likes (
    film_id INTEGER REFERENCES users (user_id),
    user_id INTEGER REFERENCES films (film_id),
    PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS friendship_statuses (
    status_id INTEGER PRIMARY KEY NOT NULL,
    name varchar(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS friends (
    user_id INTEGER REFERENCES users (user_id),
    friend_id INTEGER REFERENCES users (user_id),
    status_id INTEGER DEFAULT 1 REFERENCES friendship_statuses (status_id),
    PRIMARY KEY (user_id, friend_id)
);
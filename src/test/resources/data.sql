INSERT INTO users (login, name, email, birthday)
VALUES ('login1', 'name1', 'email1@yandex.ru', '2001-01-01'),
        ('login2', 'name2', 'email2@yandex.ru', '2002-02-02'),
        ('login3', 'name3', 'email3@yandex.ru', '2003-03-03'),
        ('login4', 'name4', 'email4@yandex.ru', '2004-04-04'),
        ('login5', 'name5', 'email5@yandex.ru', '2005-05-05');

INSERT INTO friends (user_id, friend_id, status_id)
VALUES (1, 2, 1),
        (1, 3, 1),
        (2, 3, 1),
        (3, 4, 1),
        (4, 2, 1);

INSERT INTO films (name, description, release_date, duration, mpa_id)
VALUES ('film1', 'description1', '2010-01-11', 10, 1),
       ('film2', 'description2', '2010-01-12', 20, 2),
       ('film3', 'description3', '2010-01-13', 30, 3),
       ('film4', 'description4', '2010-01-14', 40, 4),
       ('film5', 'description5', '2010-01-15', 50, 5);

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);

INSERT INTO film_user_likes (film_id, user_id)
VALUES (1, 1),
       (1, 2),
       (2, 2),
       (3, 3),
       (4, 4),
       (5, 5);
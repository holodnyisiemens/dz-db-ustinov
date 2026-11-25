-- Схема БД «Питомник собак». Создаётся Flyway, Hibernate работает в режиме validate.

CREATE SEQUENCE dog_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE litter_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE passport_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE exhibition_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE participation_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE litters (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    birth_date  DATE         NOT NULL
);

CREATE TABLE dogs (
    id          BIGINT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    breed       VARCHAR(100) NOT NULL,
    gender      VARCHAR(10)  NOT NULL,
    birth_date  DATE         NOT NULL,
    litter_id   BIGINT REFERENCES litters (id),
    version     BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE veterinary_passports (
    id                 BIGINT PRIMARY KEY,
    passport_number    VARCHAR(50)  NOT NULL UNIQUE,
    issue_date         DATE         NOT NULL,
    veterinarian_name  VARCHAR(120),
    dog_id             BIGINT       NOT NULL UNIQUE REFERENCES dogs (id)
);

CREATE TABLE exhibitions (
    id          BIGINT PRIMARY KEY,
    title       VARCHAR(150) NOT NULL,
    city        VARCHAR(100) NOT NULL,
    event_date  DATE         NOT NULL
);

-- Связка N:M «собака ↔ выставка» с собственными атрибутами (оценка, место)
CREATE TABLE participations (
    id             BIGINT PRIMARY KEY,
    dog_id         BIGINT  NOT NULL REFERENCES dogs (id),
    exhibition_id  BIGINT  NOT NULL REFERENCES exhibitions (id),
    score          INTEGER NOT NULL,
    place          INTEGER NOT NULL,
    version        BIGINT  NOT NULL DEFAULT 0,
    CONSTRAINT uq_participation_dog_exhibition UNIQUE (dog_id, exhibition_id)
);

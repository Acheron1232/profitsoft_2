-- --liquibase formatted sql

--changeset artem:1
CREATE TABLE IF NOT EXISTS author
(
    id           uuid primary key default gen_random_uuid(),
    first_name   varchar(255) not null,
    last_name    varchar(255) not null,
    contact_info varchar(600)
);

--changeset artem:2
CREATE TABLE IF NOT EXISTS book
(
    id           uuid primary key      default gen_random_uuid(),
    title        varchar(600) not null,
    isbn         varchar(13)  not null unique,
    publish_date timestamp    not null default now(),
    author_id    uuid         not null references author (id)
);

--changeset artem:3
CREATE INDEX IF NOT EXISTS idx_book_author_id
    ON book (author_id);

CREATE INDEX IF NOT EXISTS idx_book_publish_date
    ON book (publish_date);
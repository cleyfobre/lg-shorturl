DROP TABLE IF EXISTS Member;

create table Member
(
    id integer not null,
    name varchar(255) not null,
    primary key (id)
);

CREATE TABLE url (
    short_url VARCHAR(255) PRIMARY KEY,
    original_url TEXT
);
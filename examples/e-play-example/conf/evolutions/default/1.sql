# --- !Ups

CREATE TABLE people(
    id      BIGSERIAL   PRIMARY KEY,
    name    TEXT        NOT NULL,
    age     SMALLINT    NOT NULL
);

# --- !Downs

DROP TABLE people;

-- Uitvoeren als familytree_app (maakt de tabellen opnieuw aan, bestaande data verdwijnt):
--   psql -U familytree_app -p 5433 -d familytree -f db/01_schema.sql
DROP TABLE IF EXISTS pets, persons CASCADE;

CREATE TABLE persons (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        TEXT    NOT NULL,
    middle_name TEXT    NOT NULL DEFAULT '',
    last_name   TEXT    NOT NULL,
    sex         CHAR(1) NOT NULL CHECK (sex IN ('M', 'F')),
    age         INT     NOT NULL DEFAULT 0 CHECK (age >= 0), -- 0 = onbekend
    mother_id   BIGINT  REFERENCES persons (id) ON DELETE SET NULL,
    father_id   BIGINT  REFERENCES persons (id) ON DELETE SET NULL,
    partner_id  BIGINT  REFERENCES persons (id) ON DELETE SET NULL
);

CREATE TABLE pets (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name     TEXT NOT NULL,
    age      INT  NOT NULL DEFAULT 0 CHECK (age >= 0),
    species  TEXT NOT NULL,
    owner_id BIGINT REFERENCES persons (id) ON DELETE CASCADE
);

CREATE INDEX persons_mother_idx ON persons (mother_id);
CREATE INDEX persons_father_idx ON persons (father_id);
CREATE INDEX pets_owner_idx ON pets (owner_id);

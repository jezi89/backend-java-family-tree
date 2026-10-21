-- PGCLIENTENCODING=UTF8 psql -U familytree_app -p 5433 -d familytree -f db/02_seed.sql
INSERT INTO persons (name, last_name, sex) VALUES
    ('Marga',  'Hibma',         'F'),
    ('Otto',   'van der Zijde', 'M'),
    ('Judith', 'Luijendijk',    'F');

INSERT INTO persons (name, last_name, sex, mother_id, father_id)
SELECT 'Jerôme', 'van der Zijde', 'M',
       (SELECT id FROM persons WHERE name = 'Marga'),
       (SELECT id FROM persons WHERE name = 'Otto');

-- Otto en Judith zijn elkaars partner (wederzijds)
UPDATE persons SET partner_id = (SELECT id FROM persons WHERE name = 'Judith') WHERE name = 'Otto';
UPDATE persons SET partner_id = (SELECT id FROM persons WHERE name = 'Otto') WHERE name = 'Judith';

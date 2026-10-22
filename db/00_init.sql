-- Uitvoeren als superuser, verbonden met database "postgres":
--   psql -U postgres -p 5433 -v app_password=KIES_EEN_WACHTWOORD -f db/00_init.sql
-- Gebruik hetzelfde wachtwoord als omgevingsvariabele DB_PASSWORD wanneer je de API start.
CREATE ROLE familytree_app LOGIN PASSWORD :'app_password';
CREATE DATABASE familytree OWNER familytree_app;

-- Uitvoeren als superuser, verbonden met database "postgres":
--   psql -U postgres -p 5433 -f db/00_init.sql
CREATE ROLE familytree_app LOGIN PASSWORD 'CHANGE_ME';
CREATE DATABASE familytree OWNER familytree_app;

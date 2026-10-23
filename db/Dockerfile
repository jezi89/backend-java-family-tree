# Database-image met de startscripts erin. Bij een externe Docker-engine (zoals de dind-engine van Jenkins) werken
# bind-mounts van bestanden niet, omdat die engine jouw mappen niet ziet; daarom kopiëren we de scripts in het image.
FROM postgres:18-alpine
# Scripts in deze map draaien alleen de eerste keer, als de database nog leeg is.
COPY 01_schema.sql 02_seed.sql /docker-entrypoint-initdb.d/

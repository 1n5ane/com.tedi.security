FROM postgres:alpine3.19

USER postgres

COPY ./security-db-schema.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 5432
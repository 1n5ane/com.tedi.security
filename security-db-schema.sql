-- Database generated with pgModeler (PostgreSQL Database Modeler).
-- pgModeler version: 0.9.4
-- PostgreSQL version: 13.0
-- Project Site: pgmodeler.io
-- Model Author: ---

-- Database creation must be performed outside a multi lined SQL file. 
-- These commands were put in this file only as a convenience.
-- 
-- object: main | type: DATABASE --
-- DROP DATABASE IF EXISTS main;
CREATE DATABASE main;
-- ddl-end --


-- object: public.users_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.users_id_seq CASCADE;
CREATE SEQUENCE public.users_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public.users | type: TABLE --
-- DROP TABLE IF EXISTS public.users CASCADE;
CREATE TABLE public.users (
	id integer NOT NULL DEFAULT nextval('public.users_id_seq'::regclass),
	username varchar(25),
	email varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	first_name varchar(255) NOT NULL,
	last_name varchar(255) NOT NULL,
	locked boolean NOT NULL DEFAULT FALSE,
	created_at timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at timestamp,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT uniqueness UNIQUE (email,username)
);
-- ddl-end --
ALTER TABLE public.users OWNER TO postgres;
-- ddl-end --

-- object: public.roles_id_seq | type: SEQUENCE --
-- DROP SEQUENCE IF EXISTS public.roles_id_seq CASCADE;
CREATE SEQUENCE public.roles_id_seq
	INCREMENT BY 1
	MINVALUE 0
	MAXVALUE 2147483647
	START WITH 0
	CACHE 1
	NO CYCLE
	OWNED BY NONE;

-- ddl-end --

-- object: public.roles | type: TABLE --
-- DROP TABLE IF EXISTS public.roles CASCADE;
CREATE TABLE public.roles (
	id integer NOT NULL DEFAULT nextval('public.roles_id_seq'::regclass),
	name varchar(20) NOT NULL,
	CONSTRAINT roles_pk PRIMARY KEY (id)
);
-- ddl-end --
ALTER TABLE public.roles OWNER TO postgres;
-- ddl-end --

-- object: public.many_users_has_many_roles | type: TABLE --
-- DROP TABLE IF EXISTS public.many_users_has_many_roles CASCADE;
CREATE TABLE public.many_users_has_many_roles (
	id_users integer NOT NULL,
	id_roles integer NOT NULL,
	CONSTRAINT many_users_has_many_roles_pk PRIMARY KEY (id_users,id_roles)
);
-- ddl-end --

-- object: users_fk | type: CONSTRAINT --
-- ALTER TABLE public.many_users_has_many_roles DROP CONSTRAINT IF EXISTS users_fk CASCADE;
ALTER TABLE public.many_users_has_many_roles ADD CONSTRAINT users_fk FOREIGN KEY (id_users)
REFERENCES public.users (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: roles_fk | type: CONSTRAINT --
-- ALTER TABLE public.many_users_has_many_roles DROP CONSTRAINT IF EXISTS roles_fk CASCADE;
ALTER TABLE public.many_users_has_many_roles ADD CONSTRAINT roles_fk FOREIGN KEY (id_roles)
REFERENCES public.roles (id) MATCH FULL
ON DELETE RESTRICT ON UPDATE CASCADE;
-- ddl-end --

-- object: username_indexes | type: INDEX --
-- DROP INDEX IF EXISTS public.username_indexes CASCADE;
CREATE INDEX username_indexes ON public.users
USING btree
(
	username
);
-- ddl-end --

insert into roles (name) values ('ROLE_USER'),('ROLE_ADMIN');
SET TIME ZONE 'UTC-3';


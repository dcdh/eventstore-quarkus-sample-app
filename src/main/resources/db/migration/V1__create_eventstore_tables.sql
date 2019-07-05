--
-- PostgreSQL database dump
--

-- Dumped from database version 10.5 (Debian 10.5-2.pgdg90+1)
-- Dumped by pg_dump version 10.5 (Debian 10.5-2.pgdg90+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: aggregaterootprojection; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.aggregaterootprojection (
    aggregaterootid character varying(255) NOT NULL,
    aggregateroottype character varying(255) NOT NULL,
    aggregateroot jsonb,
    version bigint
);


ALTER TABLE public.aggregaterootprojection OWNER TO postgres;

--
-- Name: event; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.event (
    eventid uuid NOT NULL,
    aggregaterootid character varying(255),
    aggregateroottype character varying(255),
    creationdate timestamp without time zone,
    eventtype character varying(255),
    metadata jsonb,
    payload jsonb,
    version bigint
);


ALTER TABLE public.event OWNER TO postgres;

--
-- Name: aggregaterootprojection aggregaterootprojection_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.aggregaterootprojection
    ADD CONSTRAINT aggregaterootprojection_pkey PRIMARY KEY (aggregaterootid, aggregateroottype);


--
-- Name: event event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.event
    ADD CONSTRAINT event_pkey PRIMARY KEY (eventid);


--
-- PostgreSQL database dump complete
--


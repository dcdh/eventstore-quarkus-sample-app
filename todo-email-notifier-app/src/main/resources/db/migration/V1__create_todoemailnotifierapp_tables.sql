--
-- PostgreSQL database dump
--

-- Dumped from database version 11.4
-- Dumped by pg_dump version 11.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: kafkaevententity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.kafkaevententity (
    eventid uuid NOT NULL,
    consumedat timestamp without time zone NOT NULL
);


ALTER TABLE public.kafkaevententity OWNER TO postgres;

--
-- Name: todoentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.todoentity (
    todoid character varying(255) NOT NULL,
    description character varying(255),
    todostatus character varying(255),
    version bigint
);


ALTER TABLE public.todoentity OWNER TO postgres;


--
-- Name: kafkaevententity kafkaevententity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.kafkaevententity
    ADD CONSTRAINT kafkaevententity_pkey PRIMARY KEY (eventid);


--
-- Name: todoentity todoentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todoentity
    ADD CONSTRAINT todoentity_pkey PRIMARY KEY (todoid);


--
-- PostgreSQL database dump complete
--

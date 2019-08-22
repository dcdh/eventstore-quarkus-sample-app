--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;


--
-- Name: revinfo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.revinfo (
    rev integer NOT NULL,
    revtstmp bigint
);


ALTER TABLE public.revinfo OWNER TO postgres;

--
-- Name: todoentity_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.todoentity_aud (
    todoid character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    description character varying(255),
    todostatus character varying(255),
    currenteventid character varying(255),
    version bigint
);


ALTER TABLE public.todoentity_aud OWNER TO postgres;



--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('public.hibernate_sequence', 1, false);

--
-- Name: revinfo revinfo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.revinfo
    ADD CONSTRAINT revinfo_pkey PRIMARY KEY (rev);


--
-- Name: todoentity_aud todoentity_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todoentity_aud
    ADD CONSTRAINT todoentity_aud_pkey PRIMARY KEY (todoid, rev);


--
-- Name: todoentity_aud fkh1taenfalvox24026cffi419i; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todoentity_aud
    ADD CONSTRAINT fkh1taenfalvox24026cffi419i FOREIGN KEY (rev) REFERENCES public.revinfo(rev);



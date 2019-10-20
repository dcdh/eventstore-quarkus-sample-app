--
-- Name: todoentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.todoentity (
    todoid character varying(255) NOT NULL,
    description character varying(255),
    todostatus character varying(255),
    currenteventid character varying(255),
    version bigint
);

--
-- Name: todoentity todoentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.todoentity
    ADD CONSTRAINT todoentity_pkey PRIMARY KEY (todoid);

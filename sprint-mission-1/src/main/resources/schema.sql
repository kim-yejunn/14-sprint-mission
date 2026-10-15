CREATE TABLE public.binary_content (
    created_at timestamp(6) with time zone,
    size bigint,
    id uuid NOT NULL,
    content_type character varying(255),
    file_name character varying(255)
);


--
-- Name: channel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.channel (
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    id uuid NOT NULL,
    description character varying(255),
    name character varying(255),
    type character varying(255),
    CONSTRAINT channel_type_check CHECK (((type)::text = ANY ((ARRAY['PUBLIC'::character varying, 'PRIVATE'::character varying])::text[])))
);


--
-- Name: message; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.message (
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    author_id uuid,
    channel_id uuid,
    id uuid NOT NULL,
    content character varying(255)
);


--
-- Name: message_attachments; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.message_attachments (
    attachment_id uuid NOT NULL,
    message_id uuid NOT NULL
);


--
-- Name: read_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.read_status (
    created_at timestamp(6) with time zone,
    last_read_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    channel_id uuid,
    id uuid NOT NULL,
    user_id uuid
);


--
-- Name: user_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.user_status (
    created_at timestamp(6) with time zone,
    last_active_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    id uuid NOT NULL,
    user_id uuid NOT NULL
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    created_at timestamp(6) with time zone,
    updated_at timestamp(6) with time zone,
    id uuid NOT NULL,
    profile_id uuid,
    email character varying(255),
    password character varying(255),
    user_name character varying(255)
);


--
-- Name: binary_content binary_content_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.binary_content
    ADD CONSTRAINT binary_content_pkey PRIMARY KEY (id);


--
-- Name: channel channel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.channel
    ADD CONSTRAINT channel_pkey PRIMARY KEY (id);


--
-- Name: message_attachments message_attachments_attachment_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_attachments
    ADD CONSTRAINT message_attachments_attachment_id_key UNIQUE (attachment_id);


--
-- Name: message message_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT message_pkey PRIMARY KEY (id);


--
-- Name: read_status read_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.read_status
    ADD CONSTRAINT read_status_pkey PRIMARY KEY (id);


--
-- Name: read_status read_status_user_id_channel_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.read_status
    ADD CONSTRAINT read_status_user_id_channel_id_key UNIQUE (user_id, channel_id);


--
-- Name: read_status uks2dq41xxk6kq89ov394a93cqi; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.read_status
    ADD CONSTRAINT uks2dq41xxk6kq89ov394a93cqi UNIQUE (user_id, channel_id);


--
-- Name: user_status user_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_status
    ADD CONSTRAINT user_status_pkey PRIMARY KEY (id);


--
-- Name: user_status user_status_user_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_status
    ADD CONSTRAINT user_status_user_id_key UNIQUE (user_id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_profile_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_profile_id_key UNIQUE (profile_id);


--
-- Name: idx_message_channel_created_at; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_message_channel_created_at ON public.message USING btree (channel_id, created_at);


--
-- Name: read_status fk1fhc90wntoj7ur7ygpumffh9m; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.read_status
    ADD CONSTRAINT fk1fhc90wntoj7ur7ygpumffh9m FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: users fk2d4rnl8m6uag6xtxq7635ai3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT fk2d4rnl8m6uag6xtxq7635ai3 FOREIGN KEY (profile_id) REFERENCES public.binary_content(id);


--
-- Name: message_attachments fk4pvabosa3fbbcsr02govk5p8a; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_attachments
    ADD CONSTRAINT fk4pvabosa3fbbcsr02govk5p8a FOREIGN KEY (message_id) REFERENCES public.message(id);


--
-- Name: read_status fkd1q4o8lh6qh3vq8wi6rile7kl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.read_status
    ADD CONSTRAINT fkd1q4o8lh6qh3vq8wi6rile7kl FOREIGN KEY (channel_id) REFERENCES public.channel(id);


--
-- Name: user_status fki21pi0g9yogah1kwedmuhnt5s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_status
    ADD CONSTRAINT fki21pi0g9yogah1kwedmuhnt5s FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: message fkiimr93ytmcuira5le0sldvvma; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT fkiimr93ytmcuira5le0sldvvma FOREIGN KEY (channel_id) REFERENCES public.channel(id);


--
-- Name: message fklqr4fixlintqc9di9s3u665r4; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message
    ADD CONSTRAINT fklqr4fixlintqc9di9s3u665r4 FOREIGN KEY (author_id) REFERENCES public.users(id);


--
-- Name: message_attachments fkr5w564g5lgkf753tn0pjvc0dr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_attachments
    ADD CONSTRAINT fkr5w564g5lgkf753tn0pjvc0dr FOREIGN KEY (attachment_id) REFERENCES public.binary_content(id);

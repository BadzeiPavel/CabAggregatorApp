-- Table: public.passenger

-- DROP TABLE IF EXISTS public.passenger;

CREATE TABLE IF NOT EXISTS public.passenger
(
    id uuid NOT NULL,
    username character varying(50) COLLATE pg_catalog."default" NOT NULL,
    first_name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(50) COLLATE pg_catalog."default" NOT NULL,
    email character varying(50) COLLATE pg_catalog."default" NOT NULL,
    birth_date date NOT NULL,
    phone character varying(50) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp(6) without time zone NOT NULL,
    last_update_at timestamp without time zone,
    is_deleted boolean NOT NULL,
    CONSTRAINT passenger_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.passenger
    OWNER to root;
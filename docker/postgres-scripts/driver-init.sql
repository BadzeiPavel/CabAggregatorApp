-- Step 1: Create the tables without foreign keys
CREATE TABLE IF NOT EXISTS public.driver
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
    car_id uuid,
    status smallint NOT NULL,
    CONSTRAINT driver_pkey PRIMARY KEY (id),
    CONSTRAINT driver_car_id_key UNIQUE (car_id)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.driver
    OWNER to root;

CREATE TABLE IF NOT EXISTS public.car
(
    id uuid NOT NULL,
    "number" character varying(50) COLLATE pg_catalog."default" NOT NULL,
    seats_count smallint NOT NULL,
    color character varying(20) COLLATE pg_catalog."default" NOT NULL,
    brand character varying(50) COLLATE pg_catalog."default" NOT NULL,
    model character varying(50) COLLATE pg_catalog."default" NOT NULL,
    created_at timestamp without time zone NOT NULL,
    last_update_at timestamp without time zone,
    is_deleted boolean NOT NULL,
    driver_id uuid NOT NULL,
    car_category smallint NOT NULL,
    CONSTRAINT car_pkey PRIMARY KEY (id),
    CONSTRAINT car_driver_id_key UNIQUE (driver_id)
)
TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.car
    OWNER to root;

-- Step 2: Add foreign keys after both tables are created
ALTER TABLE IF EXISTS public.car
    ADD CONSTRAINT fk_car_driver FOREIGN KEY (driver_id)
    REFERENCES public.driver (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE IF EXISTS public.driver
    ADD CONSTRAINT fk_driver_car FOREIGN KEY (car_id)
    REFERENCES public.car (id) MATCH SIMPLE
    ON UPDATE RESTRICT
    ON DELETE RESTRICT;
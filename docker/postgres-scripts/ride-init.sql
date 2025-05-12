-- Table: public.driver_notification

-- DROP TABLE IF EXISTS public.driver_notification;

-- Table: public.ride

-- DROP TABLE IF EXISTS public.ride;

CREATE TABLE IF NOT EXISTS public.ride
(
    id uuid NOT NULL,
    passenger_id uuid NOT NULL,
    driver_id uuid,
    origin_address character varying(100) COLLATE pg_catalog."default" NOT NULL,
    destination_address character varying(100) COLLATE pg_catalog."default" NOT NULL,
    cost money NOT NULL,
    status smallint NOT NULL DEFAULT '0'::smallint,
    payment_method smallint NOT NULL DEFAULT '0'::smallint,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    created_at timestamp without time zone NOT NULL,
    last_update_at timestamp without time zone,
    seats_count smallint NOT NULL,
    car_category smallint NOT NULL DEFAULT '0'::smallint,
    origin_longitude double precision NOT NULL,
    origin_latitude double precision NOT NULL,
    destination_longitude double precision NOT NULL,
    destination_latitude double precision NOT NULL,
    distance double precision NOT NULL,
    CONSTRAINT ride_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.ride
    OWNER to root;

CREATE TABLE IF NOT EXISTS public.driver_notification
(
    id uuid NOT NULL,
    ride_id uuid NOT NULL,
    status smallint NOT NULL DEFAULT '0'::smallint,
    driver_id uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    passenger_rating double precision,
    CONSTRAINT ride_notification_pkey PRIMARY KEY (id),
    CONSTRAINT "FK_driver_notification_ride" FOREIGN KEY (ride_id)
        REFERENCES public.ride (id) MATCH SIMPLE
        ON UPDATE RESTRICT
        ON DELETE RESTRICT
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.driver_notification
    OWNER to root;

-- Table: public.passenger_notification

-- DROP TABLE IF EXISTS public.passenger_notification;

CREATE TABLE IF NOT EXISTS public.passenger_notification
(
    id uuid NOT NULL,
    message character varying(100) COLLATE pg_catalog."default" NOT NULL,
    status smallint NOT NULL DEFAULT '0'::smallint,
    passenger_id uuid NOT NULL,
    created_at timestamp without time zone NOT NULL,
    driver_rating double precision,
    CONSTRAINT passenger_notification_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.passenger_notification
    OWNER to root;

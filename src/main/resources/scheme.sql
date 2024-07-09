/*
drop table if exists public.users cascade;
drop table if exists public.locations cascade;
drop table if exists public.sessions cascade;
*/
create table public.users
(
    id       int          not null generated always as identity,
    login    varchar(128) not null,
    password varchar(128) not null,
    constraint pk_users primary key (id)
);

create table public.locations
(
    id        int            not null generated always as identity,
    name      text           not null,
    user_id   int            not null,
    latitude  numeric(10, 6) not null,
    longitude numeric(10, 6) not null,
    constraint pk_locations primary key (id),
    constraint fk_locations_user_id foreign key (user_id) references public.users (id)
);

create table public.sessions
(
    id         uuid                        not null,
    user_id    int                         not null,
    expires_at timestamp without time zone not null,
    constraint pk_sessions primary key (id),
    constraint fk_sessions_user_id foreign key (user_id) references public.users (id)
);

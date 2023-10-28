create table if not exists persons.persons(
    id bigserial not null,
    first_name text not null,
    last_name text not null,
    description text,
    constraint persons$pk primary key (id)
);

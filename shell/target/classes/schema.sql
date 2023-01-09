-- poi_type -----------------------------------------------
drop table if exists poi_type cascade;
create table poi_type
(
    id          serial primary key,
    code        varchar (32),
    title       varchar (256),
    description varchar(1024)
);
create unique index idx_poi_type on poi_type (code);
-----------------------------------------------------------

-- poi ----------------------------------------------------
drop table if exists poi cascade;
create table poi
(
    id          serial primary key,
    osm_id      bigint,
    type_id     integer not null,
    address     varchar(512),
    description varchar(1024),
    geo_lat     numeric (9, 6),
    geo_lon     numeric (9, 6),
    foreign key (type_id) references poi_type (id)
);
create index idx_poi_type_osm_id on poi (type_id, osm_id);
create index idx_type_id_address on poi (type_id, address);
create unique index idxu_type_osm_description on poi (type_id, description);
-----------------------------------------------------------

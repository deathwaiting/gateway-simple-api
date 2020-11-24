create table IF NOT EXISTS gateway(
     serial varchar PRIMARY KEY,
     name varchar,
     ip4_address varchar
);


create table IF NOT EXISTS peripheral_device(
     uid varchar  PRIMARY KEY,
     vendor varchar,
     status integer,
     gateway_serial varchar,
     date_created timestamp
);
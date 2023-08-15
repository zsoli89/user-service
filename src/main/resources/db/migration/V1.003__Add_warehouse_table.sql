create sequence if not exists warehouse_seq start with 1 increment by 50;
create table warehouse (creation_date date, creation_time time(6), last_modified_date date, last_modified_time time(6), id bigint not null, product_id bigint unique, quantity bigint, creation_username varchar(255), last_modified_username varchar(255), primary key (id));

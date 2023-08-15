create sequence if not exists address_seq start with 1 increment by 50;
create sequence if not exists order_product_seq start with 1 increment by 50;
create sequence if not exists webshop_order_seq start with 1 increment by 50;
create table address (id bigint not null, city varchar(255), door varchar(255), floor varchar(255), house_number varchar(255), street varchar(255), zip varchar(255), primary key (id));
create table webshop_order (order_status varchar(255), address_id bigint, id bigint not null, username varchar(255), primary key (id));
create table order_product (price float(53), amount bigint, id bigint not null, order_id_id bigint, product_id bigint, quantity bigint, primary key (id));
alter table if exists webshop_order add constraint FK_order_to_address foreign key (address_id) references address;
alter table if exists order_product add constraint FK_order_to_webshop_order foreign key (order_id_id) references webshop_order;

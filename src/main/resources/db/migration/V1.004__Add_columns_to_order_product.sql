alter table if exists order_product add column brand varchar(255);
alter table if exists order_product add column product_name varchar(255);
alter table if exists order_product add column amount_units varchar(255);
alter table if exists order_product add column description varchar(255);
alter table if exists order_product add column color varchar(255);

alter table if exists order_product add column size bigint;
alter table if exists order_product add column product_pcs_quantity bigint;




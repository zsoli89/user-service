create sequence if not exists app_user_seq start with 1 increment by 50;
create table app_user (birthdate date, id bigint not null, facebook_id varchar(255), name varchar(255), password varchar(255), username varchar(255), primary key (id));

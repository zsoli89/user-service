create sequence if not exists responsibility_app_user_seq start with 1 increment by 50;
create table responsibility_app_user (id bigint not null, username varchar(255), role varchar(255), primary key (id));

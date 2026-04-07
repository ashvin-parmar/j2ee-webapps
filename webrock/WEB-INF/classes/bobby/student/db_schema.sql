-- sudo systemctl start mysql
-- sudo systemctl status mysql

create database webrock_db;
create user 'webrockuser1' identified by 'webrock#User1';
grant all privileges on webrock_db.* to 'webrockuser1';
use webrock_db;



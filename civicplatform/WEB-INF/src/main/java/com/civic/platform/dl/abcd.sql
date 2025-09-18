create database civicplatform;
create user 'civicuser1'@'localhost' identified by 'civic#User1';
grant all privileges on civicplatform.* to 'civicuser1'@'localhost';
flush privileges;


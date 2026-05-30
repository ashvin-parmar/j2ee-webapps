create database school_db;
create user schooluser1 identified by 'school#User1';
grant all privileges on school_db.* to schooluser1;
use school_db;

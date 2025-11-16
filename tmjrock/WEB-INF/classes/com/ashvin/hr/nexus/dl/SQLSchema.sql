create table designation
(
code int primary key auto_increment,
title char(35) not null unique
);

create table employee
(
id int primary key auto_increment=10000000,
name char(50) not null,
designation_code int not null,
date_of_birth date,
gender char(1),
is_indian boolean ,
basic_salary decimal(10,2),
pan_number char(15) not null unique,
aadhar_card_number char(15) not null unique,
foreign key (designation_code) references designation(code);
);

create table administrator
(
uname char(15) primary key,
pwd char(15) not null
);

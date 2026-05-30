create table course
(
code int primary key auto_increment,
title char(35)
);

create table student
(
roll_number int primary key,
first_name char(20) not null,
last_name char(20) not null,
aadhar_card_number char(20) not null unique,
course_code int not null,
gender char(1) not null,
date_of_birth date not null,
foreign key (course_code) references course(code)
);


create table employee
(
id int primary key auto_increment=100001,
name char(50) not null,
designation_code int ,
date_of_birth date not null,
gender char(1),
is_indian bool,
basic_salary decimal(10,2),
pan_number char(15) not null unique,
aadhar_card_number char(15) not null unique,
foreign key (designation_code) references designation(code)
)

package com.ashvin.hr.nexus.dl;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.math.*;

public class EmployeeDAO 
{
List<EmployeeDTO> getAll() throws DAOException
{
List<EmployeeDTO> employees;
employees=new LinkedList<>();
try
{
Connection connection=DAOConnection.getConnection();
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select * from employee");
EmployeeDTO employee;
int employeeId=0;
String name="";
int designationCode=0;
java.util.Date dateOfBirth;
char gender=' ';
boolean isIndian=false;
BigDecimal basicSalary=new BigDecimal("0");
String panNumber="";
String aadharCardNumber="";
while(resultSet.next())
{
employeeId=resultSet.getInt("id");
name=resultSet.getString("name").trim();
designationCode=resultSet.getInt("designation_code");
dateOfBirth=resultSet.getDate("date_of_birth");
gender=resultSet.getString("gender").trim().charAt(0);
isIndian=resultSet.getBoolean("is_indian");
basicSalary=new BigDecimal(resultSet.getString("basic_salary").trim());
panNumber=resultSet.getString("pan_number").trim();
aadharCardNumber=resultSet.getString("aadhar_card_number").trim();
employee=new EmployeeDTO();
employee.setEmployeeId(employeeId);
employee.setName(name);
employee.setDesignationCode(designationCode);
employee.setDateOfBirth(dateOfBirth);
employee.setGender(gender);
employee.setIsIndian(isIndian);
employee.setBasicSalary(basicSalary);
employee.setPanNumber(panNumber);
employee.setAadharCardNumber(aadharCardNumber);
employees.add(employee);
}
resultSet.close();
statement.close();
connection.close();
return employees;
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
}//getAll function ends
}//class ends

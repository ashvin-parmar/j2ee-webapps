package com.ashvin.hr.nexus.dl;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.math.*;

public class EmployeeDAO 
{
public void addEmployee(EmployeeDTO employeeDTO) throws DAOException
{
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select code from designation where code=?");
preparedStatement.setInt(1,employeeDTO.getDesignationCode());
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid designation code: "+employeeDTO.getDesignationCode());
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select gender from employee where pan_number=?");
preparedStatement.setString(1,employeeDTO.getPANNumber());
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==true)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("PAN Number "+employeeDTO.getPANNumber()+" exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select gender from employee where aadhar_card_number=?");
preparedStatement.setString(1,employeeDTO.getAadharCardNumber());
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Aadhar card number "+employeeDTO.getAadharCardNumber()+" exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("insert into employee (name,designation_code,gender,date_of_birth,is_indian,basic_salary,pan_number,aadhar_card_number) values(?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
preparedStatement.setString(1,employeeDTO.getName());
preparedStatement.setInt(2,employeeDTO.getDesignationCode());
preparedStatement.setString(3,String.valueOf(employeeDTO.getGender()));
java.util.Date date=employeeDTO.getDateOfBirth();
preparedStatement.setDate(4,new java.sql.Date(date.getYear(),date.getMonth(),date.getDate()));
preparedStatement.setBoolean(5,employeeDTO.getIsIndian());
preparedStatement.setBigDecimal(6,employeeDTO.getBasicSalary());
preparedStatement.setString(7,employeeDTO.getPANNumber());
preparedStatement.setString(8,employeeDTO.getAadharCardNumber());
preparedStatement.executeUpdate();
resultSet=preparedStatement.getGeneratedKeys();
resultSet.next();
employeeDTO.setEmployeeId("A"+resultSet.getInt(1));
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
}
public boolean isPANNumberExists(String panNumber) throws DAOException
{
if(panNumber==null) return false;
panNumber=panNumber.trim();
if(panNumber.length()==0) return false;
boolean exists=false;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select gender from employee where pan_number=?");
preparedStatement.setString(1,panNumber);
ResultSet resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
return exists;
}
public boolean isAadharCardNumberExists(String aadharCardNumber) throws DAOException
{
if(aadharCardNumber==null) return false;
aadharCardNumber=aadharCardNumber.trim();
if(aadharCardNumber.length()==0) return false;
boolean exists=false;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select gender from employee where aadhar_card_number=?");
preparedStatement.setString(1,aadharCardNumber);
ResultSet resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
return exists;
}
public List<EmployeeDTO> getAll() throws DAOException
{
List<EmployeeDTO> employees;
employees=new LinkedList<>();
try
{
Connection connection=DAOConnection.getConnection();
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select employee.id,employee.name,employee.designation_code,designation.title,employee.date_of_birth,employee.gender,employee.is_indian,employee.basic_salary,employee.pan_number,employee.aadhar_card_number from employee inner join designation on employee.designation_code=designation.code order by employee.name");
EmployeeDTO employee;
int employeeId=0;
String name="";
int designationCode=0;
String designation="";
java.sql.Date dateOfBirth;
char gender=' ';
boolean isIndian=false;
BigDecimal basicSalary=null;
String panNumber="";
String aadharCardNumber="";
while(resultSet.next())
{
employeeId=resultSet.getInt("id");
name=resultSet.getString("name").trim();
designationCode=resultSet.getInt("designation_code");
designation=resultSet.getString("title").trim();
dateOfBirth=resultSet.getDate("date_of_birth");
gender=resultSet.getString("gender").charAt(0);
isIndian=resultSet.getBoolean("is_indian");
basicSalary=resultSet.getBigDecimal("basic_salary");
panNumber=resultSet.getString("pan_number").trim();
aadharCardNumber=resultSet.getString("aadhar_card_number").trim();
employee=new EmployeeDTO();
employee.setEmployeeId("A"+employeeId);
employee.setName(name);
employee.setDesignationCode(designationCode);
employee.setDesignation(designation);
employee.setDateOfBirth(dateOfBirth);
employee.setGender(gender);
employee.setIsIndian(isIndian);
employee.setBasicSalary(basicSalary);
employee.setPANNumber(panNumber);
employee.setAadharCardNumber(aadharCardNumber);
employees.add(employee);
}
resultSet.close();
statement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
return employees;
}//getAll function ends
}//class ends

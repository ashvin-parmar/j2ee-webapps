package com.ashvin.hr.nexus.dl;

import java.util.*;
import java.sql.*;
import java.io.*;
import java.math.*;
import java.text.*;
public class EmployeeDAO 
{
public void update(EmployeeDTO employeeDTO) throws DAOException
{
if(employeeDTO==null) throw new DAOException("Employee information required");
String employeeId=employeeDTO.getEmployeeId();
if(employeeId==null) throw new DAOException("Employee id required");
employeeId=employeeId.trim();
if(employeeId.length()==0 || employeeId.length()<2) throw new DAOException("Employee id required");
int id=0;
try
{
id=Integer.parseInt(employeeId.substring(1));		// to prevent exception here
}catch(NumberFormatException nfe)
{
throw new DAOException("Invalid employee id: "+employeeId);
}
String name=employeeDTO.getName();
if(name==null) throw new DAOException("Name required");
name=name.trim();
if(name.length()==0) throw new DAOException("Name required");
int designationCode=employeeDTO.getDesignation().getCode();
if(designationCode<=0) throw new DAOException("Invalid designation code: "+designationCode);
if(!(new DesignationDAO()).isCodeExists(designationCode))
{
throw new DAOException("Invalid designation code: "+designationCode);
}
Boolean isIndian=employeeDTO.getIsIndian();
String gender=employeeDTO.getGender();
if(gender==null) throw new DAOException("Gender required");
if(!gender.equals("M") && !gender.equals("F")) throw new DAOException("Invalid gender :"+gender);
java.util.Date dateOfBirth=employeeDTO.getDateOfBirth();
if(dateOfBirth==null) throw new DAOException("Date of birth required");
BigDecimal basicSalary=employeeDTO.getBasicSalary();
if(basicSalary==null) throw new DAOException("Basic salary required");
String panNumber=employeeDTO.getPANNumber();
if(panNumber==null) throw new DAOException("PAN number required");
panNumber=panNumber.trim();
if(panNumber.length()<=0 || panNumber.length()>15) throw new DAOException("Invalid PAN number: "+panNumber);
String aadharCardNumber=employeeDTO.getAadharCardNumber();
if(aadharCardNumber==null) throw new DAOException("Aadhar number required");
aadharCardNumber=aadharCardNumber.trim();
if(aadharCardNumber.length()<=0 || aadharCardNumber.length()>15) throw new DAOException("Invalid aadhar card number: "+aadharCardNumber);
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
ResultSet resultSet;
preparedStatement=connection.prepareStatement("select gender from employee where id=?");
preparedStatement.setInt(1,id);
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid employee id: "+employeeId);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select gender from employee where pan_number=? && id<>?");
preparedStatement.setString(1,panNumber);
preparedStatement.setInt(2,id);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("PAN Number "+panNumber+" exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select gender from employee where aadhar_card_number=? and id<>?");
preparedStatement.setString(1,aadharCardNumber);
preparedStatement.setInt(2,id);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Aadhar card number "+aadharCardNumber+" exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("update employee set name=?, designation_code=?, gender=?, is_indian=?, date_of_birth=?, basic_salary=?, pan_number=?, aadhar_card_number=? where id=?");
preparedStatement.setString(1,name);
preparedStatement.setInt(2,designationCode);
preparedStatement.setString(3,gender);
preparedStatement.setBoolean(4,isIndian);
preparedStatement.setDate(5,new java.sql.Date(dateOfBirth.getYear(),dateOfBirth.getMonth(),dateOfBirth.getDate()));
preparedStatement.setBigDecimal(6,basicSalary);
preparedStatement.setString(7,panNumber);
preparedStatement.setString(8,aadharCardNumber);
preparedStatement.setInt(9,id);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
}
public void add(EmployeeDTO employeeDTO) throws DAOException
{
if(employeeDTO==null) throw new DAOException("Employee information required");
String name=employeeDTO.getName();
if(name==null) throw new DAOException("Name required");
name=name.trim();
if(name.length()==0) throw new DAOException("Name required");
int designationCode=employeeDTO.getDesignation().getCode();
if(designationCode<=0) throw new DAOException("Invalid designation code: "+designationCode);
if(!(new DesignationDAO()).isCodeExists(designationCode))
{
throw new DAOException("Invalid designation code: "+designationCode);
}
Boolean isIndian=employeeDTO.getIsIndian();
String gender=employeeDTO.getGender();
if(gender==null) throw new DAOException("Gender required");
if(!gender.equals("M") && !gender.equals("F")) throw new DAOException("Invalid gender :"+gender);
java.util.Date dateOfBirth=employeeDTO.getDateOfBirth();
if(dateOfBirth==null) throw new DAOException("Date of birth required");
BigDecimal basicSalary=employeeDTO.getBasicSalary();
if(basicSalary==null) throw new DAOException("Basic salary required");
String panNumber=employeeDTO.getPANNumber();
if(panNumber==null) throw new DAOException("PAN number required");
panNumber=panNumber.trim();
if(panNumber.length()<=0 || panNumber.length()>15) throw new DAOException("Invalid PAN number: "+panNumber);
String aadharCardNumber=employeeDTO.getAadharCardNumber();
if(aadharCardNumber==null) throw new DAOException("Aadhar number required");
aadharCardNumber=aadharCardNumber.trim();
if(aadharCardNumber.length()<=0 || aadharCardNumber.length()>15) throw new DAOException("Invalid aadhar card number: "+aadharCardNumber);
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select code from designation where code=?");
preparedStatement.setInt(1,designationCode);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid designation code: "+designationCode);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select gender from employee where pan_number=?");
preparedStatement.setString(1,panNumber);
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==true)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("PAN Number "+panNumber+" exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select gender from employee where aadhar_card_number=?");
preparedStatement.setString(1,aadharCardNumber);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Aadhar card number "+aadharCardNumber+" exists");
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("insert into employee (name,designation_code,gender,date_of_birth,is_indian,basic_salary,pan_number,aadhar_card_number) values(?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
preparedStatement.setString(1,name);
preparedStatement.setInt(2,designationCode);
preparedStatement.setString(3,gender);
preparedStatement.setDate(4,new java.sql.Date(dateOfBirth.getYear(),dateOfBirth.getMonth(),dateOfBirth.getDate()));
preparedStatement.setBoolean(5,isIndian);
preparedStatement.setBigDecimal(6,basicSalary);
preparedStatement.setString(7,panNumber);
preparedStatement.setString(8,aadharCardNumber);
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
public EmployeeDTO getByEmployeeId(String employeeId) throws DAOException
{
if(employeeId==null) throw new DAOException("Employee id required");
employeeId=employeeId.trim();
if(employeeId.length()==0) throw new DAOException("Employee id required");
int id=0;
EmployeeDTO employeeDTO=null;
try
{
try
{
id=Integer.parseInt(employeeId.substring(1));
}catch(NumberFormatException nfe)
{
//Nothing
}
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select gender from employee where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid employee id: "+employeeId);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("select employee.id,employee.name,employee.designation_code,designation.title,employee.date_of_birth,employee.gender,employee.is_indian,employee.basic_salary,employee.pan_number,employee.aadhar_card_number from employee inner join designation on employee.designation_code=designation.code and id=?");
preparedStatement.setInt(1,id);
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid employee id: "+employeeId);
}
employeeDTO=new EmployeeDTO();
employeeDTO.setEmployeeId("A"+resultSet.getInt("id"));
employeeDTO.setName(resultSet.getString("name").trim());
DesignationDTO designation=new DesignationDTO();
designation.setCode(resultSet.getInt("designation_code"));
designation.setTitle(resultSet.getString("title").trim());
employeeDTO.setDesignation(designation);
employeeDTO.setDateOfBirth(resultSet.getDate("date_of_birth"));
employeeDTO.setGender(resultSet.getString("gender"));
employeeDTO.setIsIndian(resultSet.getBoolean("is_indian"));
employeeDTO.setBasicSalary(resultSet.getBigDecimal("basic_salary"));
employeeDTO.setPANNumber(resultSet.getString("pan_number").trim());
employeeDTO.setAadharCardNumber(resultSet.getString("aadhar_card_number").trim());
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
return employeeDTO;
}
public EmployeeDTO getByPANNumber(String panNumber) throws DAOException
{
if(panNumber==null) throw new DAOException("PAN number required");
panNumber=panNumber.trim();
if(panNumber.length()==0) throw new DAOException("PAN number required");
EmployeeDTO employeeDTO=null;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select employee.id,employee.name,employee.designation_code,designation.title,employee.date_of_birth,employee.gender,employee.is_indian,employee.basic_salary,employee.pan_number,employee.aadhar_card_number from employee inner join designation on employee.designation_code=designation.code and employee.pan_number=?");
ResultSet resultSet;
preparedStatement.setString(1,panNumber);
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("PAN number: "+panNumber+" does not exists");
}
employeeDTO=new EmployeeDTO();
employeeDTO.setEmployeeId("A"+resultSet.getInt("id"));
employeeDTO.setName(resultSet.getString("name").trim());
DesignationDTO designation=new DesignationDTO();
designation.setCode(resultSet.getInt("designation_code"));
designation.setTitle(resultSet.getString("title").trim());
employeeDTO.setDesignation(designation);
employeeDTO.setDateOfBirth(resultSet.getDate("date_of_birth"));
employeeDTO.setGender(resultSet.getString("gender"));
employeeDTO.setIsIndian(resultSet.getBoolean("is_indian"));
employeeDTO.setBasicSalary(resultSet.getBigDecimal("basic_salary"));
employeeDTO.setPANNumber(resultSet.getString("pan_number").trim());
employeeDTO.setAadharCardNumber(resultSet.getString("aadhar_card_number").trim());
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
return employeeDTO;
}
public EmployeeDTO getByAadharCardNumber(String aadharCardNumber) throws DAOException
{
if(aadharCardNumber==null) throw new DAOException("Aadhar card number required");
aadharCardNumber=aadharCardNumber.trim();
if(aadharCardNumber.length()==0) throw new DAOException("Aadhar card number required");
EmployeeDTO employeeDTO=null;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select employee.id,employee.name,employee.designation_code,designation.title,employee.date_of_birth,employee.gender,employee.is_indian,employee.basic_salary,employee.pan_number,employee.aadhar_card_number from employee inner join designation on employee.designation_code=designation.code and employee.aadhar_card_number=?");
preparedStatement.setString(1,aadharCardNumber);
ResultSet resultSet;
resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Aadhar card number: "+aadharCardNumber+" does not exists");
}
employeeDTO=new EmployeeDTO();
employeeDTO.setEmployeeId("A"+resultSet.getInt("id"));
employeeDTO.setName(resultSet.getString("name").trim());
DesignationDTO designation=new DesignationDTO();
designation.setCode(resultSet.getInt("designation_code"));
designation.setTitle(resultSet.getString("title").trim());
employeeDTO.setDesignation(designation);
employeeDTO.setDateOfBirth(resultSet.getDate("date_of_birth"));
employeeDTO.setGender(resultSet.getString("gender"));
employeeDTO.setIsIndian(resultSet.getBoolean("is_indian"));
employeeDTO.setBasicSalary(resultSet.getBigDecimal("basic_salary"));
employeeDTO.setPANNumber(resultSet.getString("pan_number").trim());
employeeDTO.setAadharCardNumber(resultSet.getString("aadhar_card_number").trim());
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
return employeeDTO;
}
public void deleteByEmployeeId(String employeeId) throws DAOException
{
if(employeeId==null) throw new DAOException("Employee id required");
employeeId=employeeId.trim();
if(employeeId.length()==0) throw new DAOException("Employee id required");
if(employeeId.charAt(0)!='A') throw new DAOException("Invalid employee id: "+employeeId);
int id=0;
try
{
try
{
id=Integer.parseInt(employeeId.substring(1));
}catch(NumberFormatException nfew)
{
//do nothing
}
if(id==0) throw new DAOException("Invalid employee id: "+employeeId);
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select gender from employee where id=?");
preparedStatement.setInt(1,id);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid employee id: "+employeeId);
}
resultSet.close();
preparedStatement.close();
preparedStatement=connection.prepareStatement("delete from employee where id=?");
preparedStatement.setInt(1,id);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException(sqlException.getMessage());
}
}
public boolean isEmployeeIdExists(String employeeId) throws DAOException
{
if(employeeId==null) return false;
employeeId=employeeId.trim();
if(employeeId.length()==0) return false;
if(employeeId.charAt(0)!='A') return false;
boolean exists=false;
int empId=0;
try
{
empId=Integer.parseInt(employeeId.substring(1));
}catch(NumberFormatException nfe)
{
return false;
}
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select gender from employee where id=?");
preparedStatement.setInt(1,empId);
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
public boolean isDesignationCodeAlloted(int designationCode)
{
if(designationCode<=0) return false;
boolean alloted=false;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select gender from employee where designation_code=?");
preparedStatement.setInt(1,designationCode);
ResultSet resultSet=preparedStatement.executeQuery();
alloted=resultSet.next();
resultSet.close();
preparedStatement.close();
connection.close();
return alloted;
}catch(DAOException daoException)
{
}catch(SQLException sqlException)
{
}
return false;
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
String title="";
java.sql.Date dateOfBirth;
String gender="";
boolean isIndian=false;
BigDecimal basicSalary=null;
String panNumber="";
String aadharCardNumber="";
while(resultSet.next())
{
employeeId=resultSet.getInt("id");
name=resultSet.getString("name").trim();
designationCode=resultSet.getInt("designation_code");
title=resultSet.getString("title").trim();
DesignationDTO designation=new DesignationDTO();
designation.setCode(designationCode);
designation.setTitle(title);
dateOfBirth=resultSet.getDate("date_of_birth");
gender=resultSet.getString("gender");
isIndian=resultSet.getBoolean("is_indian");
basicSalary=resultSet.getBigDecimal("basic_salary");
panNumber=resultSet.getString("pan_number").trim();
aadharCardNumber=resultSet.getString("aadhar_card_number").trim();
employee=new EmployeeDTO();
employee.setEmployeeId("A"+employeeId);
employee.setName(name);
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

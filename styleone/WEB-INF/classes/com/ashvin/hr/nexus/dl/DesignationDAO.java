package com.ashvin.hr.nexus.dl;

import java.util.*;
import java.sql.*;
public class DesignationDAO
{
public List<DesignationDTO> getAll() throws DAOException
{
List<DesignationDTO> designations;
designations=new LinkedList<>();
try
{
Connection connection=DAOConnection.getConnection();
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select * from designation");
int code;
String title;
DesignationDTO designationDTO;
while(resultSet.next())
{
code=resultSet.getInt("code");
title=resultSet.getString("title").trim();
designationDTO=new DesignationDTO();
designationDTO.setCode(code);
designationDTO.setTitle(title);
designations.add(designationDTO);
}
}
catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return designations;
}
}

package com.ashvin.hr.nexus.dl;

import java.sql.*;
import java.util.*;

public class DesignationDAO
{
public List<DesignationDTO> getAllDesignations() throws DAOException
{
List<DesignationDTO> designations=new LinkedList<>();
try
{
Connection connection=DAOConnection.getConnection();
Statement statement=connection.createStatement();
ResultSet resultSet=statement.executeQuery("select code,title from designation");
while(resultSet.next())
{
int code=resultSet.getInt("code");
String title=resultSet.getString("title").trim();
DesignationDTO designationDTO=new DesignationDTO();
designationDTO.setCode(code);
designationDTO.setTitle(title);
designations.add(designationDTO);
}
resultSet.close();
statement.close();
connection.close();
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return designations;
}
}

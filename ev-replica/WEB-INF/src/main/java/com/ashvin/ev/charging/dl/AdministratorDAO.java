package com.ashvin.ev.charging.dl;

import java.sql.*;
public class AdministratorDAO
{
public AdministratorDTO getByUsername(String username) throws DAOException
{
if(username==null) 
{
throw new DAOException("Username required");
}
username=username.trim();
if(username.length()==0)
{
throw new DAOException("Username required");
}

try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select uname as username, pwd as password from administrator where uname=?");
preparedStatement.setString(1,username);
ResultSet resultSet=preparedStatement.executeQuery();
if(resultSet.next()==false)
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid username: "+username);
}
resultSet.getString("username");
String password=resultSet.getString("password").trim();
AdministratorDTO administrator=new AdministratorDTO();
administrator.setUsername(username);
administrator.setPassword(password);
resultSet.close();
preparedStatement.close();
connection.close();
return administrator;
}
catch(DAOException dao)
{
// do nothing -> Because of connectivity issue
}
catch(Exception exception)
{
System.out.println(exception);		//Removed after testing
}
return null;
}
}

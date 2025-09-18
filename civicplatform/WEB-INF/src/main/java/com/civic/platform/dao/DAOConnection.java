package com.civic.platform.dao;
import java.sql.*;
public class DAOConnection
{
private DAOConnection(){}
public static Connection getConnection() throws Exception
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/civicplatform","civicuser1","civic#User1");
}catch(Exception exception)
{
throw new Exception(exception.getMessage());
}
return connection;
}
}


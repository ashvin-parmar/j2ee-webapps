package com.smart.carbonix.dbdl.dao;
import java.sql.*;
import com.smart.carbonix.dbdl.exceptions.*;
public class DAOConnection
{
private DAOConnection(){}
public static Connection getConnection() throws DAOException
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3060/smart_carbonix_db1","smartcarbonix1","SC#user1");
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return connection;
}
}


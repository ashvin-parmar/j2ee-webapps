package com.ashvin.hr.nexus.dl;

import java.sql.*;

public class DAOConnection
{
private DAOConnection(){}
static public Connection getConnection() throws DAOException
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/tmdb","tmdbuser","tmdb#User1");
}catch(Exception exception)
{
throw new DAOException(exception.getMessage());
}
return connection;
}
}

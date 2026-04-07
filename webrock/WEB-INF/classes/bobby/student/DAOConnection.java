package bobby.student;

import java.sql.*;

public class DAOConnection
{
private DAOConnection(){};
public static Connection getConnection()
{
Connection connection=null;
try
{
Class.forName("com.mysql.cj.jdbc.Driver");
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/webrock_db","webrockuser1","webrock#User1");
}catch(Exception exception)
{
}
return connection;
}
}

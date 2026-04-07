package bobby.student;

import com.ashvin.web.rock.annotations.*;
import java.util.*;
import java.sql.*;

@PATH("/StudentManager")
public class StudentDAO
{
@PATH("/add")
public void add(StudentDTO student) throws DAOException
{
if(student==null) throw new DAOException("Student required");
int rollNumber=student.getRollNumber();
String name=student.getName();
String gender=student.getGender();
if(name==null || name.isBlank()) throw new DAOException("name required: "+name);
if(gender==null || gender.isBlank()) throw new DAOException("gender required: "+gender);
gender=gender.toUpperCase();
if(!(gender.charAt(0)=='F' || gender.charAt(0)=='M')) throw new DAOException("Invalid gender");
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("insert into student (name,gender) values(?,?)",Statement.RETURN_GENERATED_KEYS);
preparedStatement.setString(1,name);
preparedStatement.setString(2,gender.toUpperCase().substring(0,1));

preparedStatement.executeUpdate();
ResultSet resultSet=preparedStatement.getGeneratedKeys();
resultSet.next();
rollNumber=resultSet.getInt(1);
System.out.println(rollNumber);
student.setRollNumber(rollNumber);
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
System.out.println(sqlException.getMessage());
throw new DAOException("Invalid data passed");
}
}
@PATH("/update")
public void update(StudentDTO student) throws DAOException
{
if(student==null) throw new DAOException("Student required");
int rollNumber=student.getRollNumber();
String name=student.getName();
String gender=student.getGender();
if(rollNumber<=0) throw new DAOException("Invalid roll number: "+rollNumber);
if(name==null || name.isBlank()) throw new DAOException("name required: "+name);
if(gender==null || gender.isBlank()) throw new DAOException("gender required: "+gender);
gender=gender.toUpperCase();
if(!(gender.charAt(0)=='F' || gender.charAt(0)=='M')) throw new DAOException("Invalid gender");
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
ResultSet resultSet;
preparedStatement=connection.prepareStatement("select gender from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid roll number: "+rollNumber);
}
resultSet.close();
preparedStatement.close();

preparedStatement=connection.prepareStatement("update student set name=? , gender=? where rollNumber=?");
preparedStatement.setString(1,name);
preparedStatement.setString(2,gender.toUpperCase().substring(0,1));
preparedStatement.setInt(3,rollNumber);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException("Invalid data passed");
}
}
@PATH("/delete")
public void delete(@RequestParameter int rollNumber) throws DAOException
{
if(rollNumber<=0) throw new DAOException("Invalid roll number: "+rollNumber);
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
ResultSet resultSet;
preparedStatement=connection.prepareStatement("select gender from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid roll number: "+rollNumber);
}
resultSet.close();
preparedStatement.close();

preparedStatement=connection.prepareStatement("delete from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
preparedStatement.executeUpdate();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException("Invalid roll number: "+rollNumber);
}
}
@PATH("/getByRollNumber")
public StudentDTO getByRollNumber(@RequestParameter int rollNumber) throws DAOException
{
if(rollNumber<=0) throw new DAOException("Invalid roll number: "+rollNumber);
StudentDTO student=null;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement;
ResultSet resultSet;
preparedStatement=connection.prepareStatement("select gender from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
connection.close();
throw new DAOException("Invalid roll number: "+rollNumber);
}
resultSet.close();
preparedStatement.close();

preparedStatement=connection.prepareStatement("select * from student where rollNumber=?");
preparedStatement.setInt(1,rollNumber);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
student=new StudentDTO();
student.setRollNumber(rollNumber);
student.setName(resultSet.getString("name").trim());
String gender=resultSet.getString("gender").trim();
if(gender.toUpperCase().equals("M"))
{
student.setGender("Male");
}
else
{
student.setGender("Female");
}
}
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException("Invalid roll number: "+rollNumber);
}
return student;
}
@PATH("/getAll")
public List<StudentDTO> getAll() throws DAOException
{
List<StudentDTO> students=new ArrayList<>();
StudentDTO student;
try
{
Connection connection=DAOConnection.getConnection();
PreparedStatement preparedStatement=connection.prepareStatement("select * from student");
ResultSet resultSet=preparedStatement.executeQuery();
while(resultSet.next())
{
student=new StudentDTO();
student.setRollNumber(resultSet.getInt("rollNumber"));
student.setName(resultSet.getString("name").trim());
String gender=resultSet.getString("gender").trim();
if(gender.toUpperCase().equals("M"))
{
student.setGender("Male");
}
else
{
student.setGender("Female");
}
students.add(student);
}
resultSet.close();
preparedStatement.close();
connection.close();
}catch(SQLException sqlException)
{
throw new DAOException("Unable to load all student list");
}
return students;
}
}

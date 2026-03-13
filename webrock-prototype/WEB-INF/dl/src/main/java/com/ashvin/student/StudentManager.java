package com.ashvin.student;

import com.ashvin.student.dto.*;
//import com.ashvin.web.rock.annotations.*;
import java.util.*;

//@Path("/student")
public class StudentManager
{
//@Path("/addStudent")
public void addStudent(Student student) throws ManagerException
{
int rollNumber;
String name;
try
{
rollNumber=student.getRollNumber();
name=student.getName();
if(name.equals("Rohan"))
{
throw new ManagerException("Unable to add Rohan, Already exists");
}
System.out.printf("Student added\n");

}catch(Exception exception)
{
throw new ManagerException(exception.getMessage());
}
}
public List<Student> getStudents() throws ManagerException
{
Student student=null;
List<Student> students=null;
try
{
students=new ArrayList<>(4);

student=new Student();
student.setName("Sunil Pal");
student.setRollNumber(10001);
students.add(student);

student=new Student();
student.setName("BalKrishna Patidar");
student.setRollNumber(10002);
students.add(student);

student=new Student();
student.setName("Rohit Solanki");
student.setRollNumber(10003);
students.add(student);

student=new Student();
student.setName("Rohan");
student.setRollNumber(10004);
students.add(student);

return students;
}catch(Exception exception)
{
throw new ManagerException(exception.getMessage());
}
}
}

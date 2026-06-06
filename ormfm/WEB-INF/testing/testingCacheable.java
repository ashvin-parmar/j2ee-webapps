import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.*;

import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

class testingCacheable {
public static void main(String[] args) 
{
try
{
DataManager dm=null;
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
dm=DataManager.getDataManager();
}catch(DataException de)
{
System.out.println("Problem: "+de.getMessage());
return;
}
try
{

dm.begin();
List<Student> students=(List<Student>)dm.queryDS(Student.class);
dm.end();
for(Student std:students)
{
System.out.print(std.getFirstName()+" ");
System.out.print(std.getRollNumber()+" ");
System.out.print(std.getLastName()+" ");
System.out.print(std.getGender()+" ");
System.out.print(std.getCourseCode()+" ");
System.out.print(std.getAadharCardNumber()+" ");
System.out.println(std.getDateOfBirth());
}
System.out.println();
System.out.println();

try
{
Student student=new Student();
student.setRollNumber(10132);
student.setFirstName("Sarvan");
student.setLastName("Kumar");
student.setGender("M");
student.setCourseCode(22);
student.setAadharCardNumber("UID98123");
student.setDateOfBirth(students.get(0).getDateOfBirth());
dm.begin();
dm.save(student);
System.out.println("Roll number of student added: "+student.getRollNumber());
}catch(DataException de)
{
de.printStackTrace();
System.out.println(de.getMessage());
}
System.out.println();
System.out.println();

dm.begin();
students=(List<Student>)dm.queryDS(Student.class);
dm.end();
for(Student std:students)
{
System.out.print(std.getFirstName()+" ");
System.out.print(std.getRollNumber()+" ");
System.out.print(std.getLastName()+" ");
System.out.print(std.getGender()+" ");
System.out.print(std.getCourseCode()+" ");
System.out.print(std.getAadharCardNumber()+" ");
System.out.println(std.getDateOfBirth());
}

System.out.println();
System.out.println();

try
{
Student student=new Student();
student.setRollNumber(10132);
student.setFirstName("Sarvan");
student.setLastName("P. Kumar");
student.setGender("M");
student.setCourseCode(20);
student.setAadharCardNumber("UID98123");
student.setDateOfBirth(students.get(0).getDateOfBirth());
dm.begin();
dm.update(student);
System.out.println("Roll number of student updated.");
}catch(DataException de)
{
//de.printStackTrace();
System.out.println(de.getMessage());
}
System.out.println();
System.out.println();

dm.begin();
students=(List<Student>)dm.queryDS(Student.class);
dm.end();
for(Student std:students)
{
System.out.print(std.getFirstName()+" ");
System.out.print(std.getRollNumber()+" ");
System.out.print(std.getLastName()+" ");
System.out.print(std.getGender()+" ");
System.out.print(std.getCourseCode()+" ");
System.out.print(std.getAadharCardNumber()+" ");
System.out.println(std.getDateOfBirth());
}

System.out.println();
System.out.println();

try
{
dm.begin();
dm.delete(Student.class,10132);
System.out.println("Roll number of student deleted.");
}catch(DataException de)
{
//de.printStackTrace();
System.out.println(de.getMessage());
}
System.out.println();
System.out.println();

dm.begin();
students=(List<Student>)dm.queryDS(Student.class);
dm.end();
for(Student std:students)
{
System.out.print(std.getFirstName()+" ");
System.out.print(std.getRollNumber()+" ");
System.out.print(std.getLastName()+" ");
System.out.print(std.getGender()+" ");
System.out.print(std.getCourseCode()+" ");
System.out.print(std.getAadharCardNumber()+" ");
System.out.println(std.getDateOfBirth());
}


}catch(DataException de)
{
System.out.println(de.getMessage());
}

}catch(Exception exception)
{
System.out.println(exception);
}
}
}


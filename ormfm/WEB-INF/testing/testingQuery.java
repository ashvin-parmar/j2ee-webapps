/**
 * @author Ashvin
 * @since 2026-06-02
 * Description: 
 */

import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.*;

import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

class testingQuery {
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
List<Student> students;
try
{
dm.begin();
students=(List<Student>)dm.query(Student.class).where("first_name").eq("Rohit").fire();
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
dm.end();
System.out.println("Statement1 Problem: "+de.getMessage());
}

try
{
dm.begin();
students=(List<Student>)dm.select(Student.class,new String[]{"first_name","last_name"}).where("first_name").eq("Rohit").fire();
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
dm.end();
System.out.println("Statement2 Problem: "+de.getMessage());
}

try
{
dm.begin();
students=(List<Student>)dm.select(Student.class,new String[]{"first_name","roll_number","last_name"}).where("first_name").eq("Rohit").and().where("course_code").eq(22).fire();
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
dm.end();
System.out.println("Statement3 Problem: "+de.getMessage());
}

try
{
dm.begin();
students=(List<Student>)dm.select(Student.class,new String[]{"first_name","roll_number","last_name"}).where("first_name").eq("Rohit").orderBy("last_name").fire();
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
dm.end();
System.out.println("Statement4 Problem: "+de.getMessage());
}

//Invalid statement tesing
try
{
dm.begin();
students=(List<Student>)dm.query(Student.class).where("first_name").eq("Rohit").fire();
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
dm.end();
System.out.println("Statement1 Problem: "+de.getMessage());
}

try
{
dm.begin();
students=(List<Student>)dm.select(Student.class,new String[]{"abcd","last_name"}).where("first_name").eq("Rohit").fire();
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
dm.end();
System.out.println("Statement2 Problem: "+de.getMessage());
}

try
{
dm.begin();
students=(List<Student>)dm.select(Student.class,new String[]{"first_name","roll_number","last_name"}).where("first_name").and().where("course_code").eq(22).fire();
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
dm.end();
System.out.println("Invalid Statement3 Problem: "+de.getMessage());
}

try
{
dm.begin();
students=(List<Student>)dm.select(Student.class,new String[]{"first_name","roll_number","last_name"}).where("first_name").eq("Rohit").orderBy("last_name").orderBy("first_name").fire();
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
dm.end();
System.out.println("Invalid Statement4 Problem: "+de.getMessage());
}


}catch(Exception exception)
{
System.out.println(exception);
}
}
}


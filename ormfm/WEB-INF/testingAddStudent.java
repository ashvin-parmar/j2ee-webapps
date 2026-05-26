import java.lang.*;

import java.util.*;
import java.text.*;
import java.io.*;

import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

public class testingAddStudent
{
public static void main(String args[])
{
DataManager dm=null;
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
dm=DataManager.getDataManager();
dm.begin();
if(args.length<6) 
{
System.out.println("[order: roll_number first_name last_name aadhar_card_number course_code gender date]");
return;
}
Student s=new Student();
//s.setRollNumber(10001);   //To test unique student roll number constraint
s.setRollNumber(Integer.parseInt(args[0]));
s.setFirstName(args[1]);
s.setLastName(args[2]);
s.setAadharCardNumber(args[3]);
s.setCourseCode(Integer.parseInt(args[4]));
s.setGender(args[5]);
SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yy");
Date date=new Date();
try
{
//date=sdf.parse("02/01/2001");
date=sdf.parse(args[6]);
}catch(Exception e)
{
System.out.println(e);
return;
}
//System.out.println("Date: "+date);
s.setDateOfBirth(date);

dm.save(s);
dm.end();
System.out.println("Student added");

dm.begin();
List<Student> students=(List<Student>)dm.query(Student.class).fire();
dm.end();

for(Student student:students)
{
System.out.println("----------");
System.out.println("Student rollNumber: "+student.getRollNumber());
System.out.println("First name: "+student.getFirstName());
System.out.println("Last name: "+student.getLastName());
System.out.println("Aadhar card number: "+student.getAadharCardNumber());
System.out.println("Course code: "+student.getCourseCode());
System.out.println("Date: "+student.getDateOfBirth());
System.out.println("----------");
}
}catch(DataException de)
{
dm.end();
System.out.println(de);
}
System.out.println();
System.out.println();

}
}


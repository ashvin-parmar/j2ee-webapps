import java.lang.*;

import java.util.*;
import java.text.*;

public class testingDeleteStudent
{
public static void main(String args[])
{
DataManager dm=DataManager.getDataManager();
try
{
dm.begin();
if(args.length<6) 
{
System.out.println("[order: roll_number]");
return;
}
dm.delete(Student.class,Integer.parseInt(args[0]));
dm.end();
System.out.println("Student deleted");

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
}
}

package bobby.test;

import java.util.*;
import com.ashvin.web.rock.annotations.*;

@PATH(value="/studentService")
public class StudentManager
{
@PATH(value="/add")
@POST
@FORWARD(value="/webrock/index.html")
public void addStudent(Student student)
{

}
@PATH(value="/getAll")
@GET
//@FORWARD(value="http://localhost:8080/stylethree/")
//@FORWARD(relativeValue="/isRollNumber")   //DEPRECATED [by me]
@FORWARD(value="/isRollNumber")  //Provide complete path while forward
public List<Student> getStudents()
{
List<Student> students=new ArrayList<>(2);
Student student;

student=new Student();
student.setName("Rohit");
student.setRollNumber(101);
students.add(student);

student=new Student();
student.setName("Suman");
student.setRollNumber(102);
students.add(student);

return students;
}
@PATH("/isRollNumber")
public boolean isRollNumberExists(int rollNumber)
{
return true;
}
}

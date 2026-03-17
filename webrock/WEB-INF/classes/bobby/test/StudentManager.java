package bobby.test;

import java.util.*;
import com.ashvin.web.rock.annotations.*;

@PATH(value="/studentService")
public class StudentManager
{
@PATH(value="/add")
public void addStudent(Student student)
{

}
@PATH(value="/getAll")
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

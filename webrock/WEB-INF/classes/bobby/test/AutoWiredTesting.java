package bobby.test;

import com.ashvin.web.rock.annotations.*;


@PATH("/autoWiredTesting")
public class AutoWiredTesting
{
@AutoWired(name="xyz")
private Student student;
public void setStudent(Student student)
{
this.student=student;
}
public Student getStudent()
{
return this.student;
}
@PATH("/testing1")
public void service1()
{
System.out.println("Over here");
System.out.println(this.student!=null);

if(this.student!=null)
{
System.out.println("Name: "+student.getName());
}
}
}

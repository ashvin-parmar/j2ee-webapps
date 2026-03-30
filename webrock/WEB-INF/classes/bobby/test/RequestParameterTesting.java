package bobby.test;

import com.ashvin.web.rock.annotations.*;


@PATH("/requestParameterTesting")
public class RequestParameterTesting
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
public void service1(Student student,@RequestParameter("name") String name,@RequestParameter("rollNumber")int rollNumber)
{
System.out.println("Over here");
System.out.println(this.student!=null);

if(this.student!=null)
{
student=this.student;
System.out.println("Name via getAttribute from any scope: "+student.getName());
}
System.out.println("Name via RequestParameter: "+name);
System.out.println("Roll number via request parameter: "+rollNumber);
}
@PATH("/testing2")
public void service2(@RequestParameter("longValue") Long longValue,
                     @RequestParameter("intValue")int intValue,
                     @RequestParameter("shortValue")short shortValue,
                     @RequestParameter("byteValue")byte byteValue,
                     @RequestParameter("floatValue")Float floatValue,
                     @RequestParameter("doubleValue")double doubleValue,
                     @RequestParameter("booleanValue")boolean booleanValue,
                     @RequestParameter("charValue")char charValue,
                     @RequestParameter("stringValue")String stringValue
)
{
System.out.println(longValue);
System.out.println(intValue);
System.out.println(shortValue);
System.out.println(byteValue);
System.out.println(doubleValue);
System.out.println(floatValue);
System.out.println(charValue);
System.out.println(stringValue);
System.out.println(booleanValue);
}
}

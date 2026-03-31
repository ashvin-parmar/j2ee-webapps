package bobby.test;

import com.ashvin.web.rock.annotations.*;
import com.ashvin.web.rock.pojo.*;


@PATH("/requestParameterTesting")
public class RequestParameterTesting
{
@AutoWired(name="student")
private Student student;
public void setStudent(Student student)
{
this.student=student;
}
public Student getStudent()
{
return this.student;
}
private int rollNumber;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
private String name;
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}

@PATH("/testing1")
public void service1(Student student,@RequestParameter("name") String name,@RequestParameter("rollNumber")int rollNumber,SessionScope sessionScope,ApplicationScope as,ApplicationDirectory ad,RequestScope rs,double xyz)
{
System.out.println("-------------Testing ---------");
if(student==null) System.out.println("Initially student is null");
System.out.println("Value of primitive data type if not operated: "+xyz); //default value assigned
if(sessionScope!=null)
{
System.out.println("Session scope is not null");
student=(Student)sessionScope.getAttribute("student");
if(student!=null)
{
System.out.println("Name via getAttribute from session scope: "+student.getName());
}
if(this.student!=null)
{
System.out.println("Name via @AutoWired from any scope: "+this.student.getName());
}
}
System.out.println("----------Testing --------");
System.out.println("Name via @RequestParameter: "+name);
System.out.println("Roll number via @RequestParameter: "+rollNumber);
System.out.println("----------Testing --------");
System.out.println("Name via @InjectRequestParameter: "+this.name);
System.out.println("Roll number via @InjectReqeustParameter: "+this.rollNumber);
System.out.println("----------Testing --------");
if(as!=null)
{
System.out.println("Application scope is not null");
}
if(ad!=null)
{
System.out.println("Application directory is not null");
}
if(rs!=null)
{
System.out.println("Request scope is not null");
}
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

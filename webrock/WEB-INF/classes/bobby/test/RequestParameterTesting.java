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
@InjectRequestParameter("rollNumber")
private int rollNumber;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
@InjectRequestParameter("name")
private String name;
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}

@AutoWired(name="Pqr")      //Testing failed [Done!]    //We can put any name
private double pqr;
public void setPqr(double pqr)
{
this.pqr=pqr;
}
public double getPqr()
{
return this.pqr;
}

@PATH("/testing1")
public void service1(@RequestParameter("name") String name,@RequestParameter("rollNumber")int rollNumber,SessionScope sessionScope,ApplicationScope as,ApplicationDirectory ad,RequestScope rs)
{
Student student=null;
System.out.println("-------------Testing ---------");
if(student==null) System.out.println("Initially student is null");
System.out.println("Value of @AutoWired primitive data type initially: "+this.pqr);
if(sessionScope!=null)
{
//-- Setting in sessionScope for pqr, Testing on next request arrival for same service 
//sessionScope.setAttribute("PQR","fasds"); 
sessionScope.setAttribute("Pqr",123123.13213);

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

@PATH("/testing3")
public String service3(Student student,@RequestParameter("name") String name,@RequestParameter("rollNumber")int rollNumber,SessionScope sessionScope,ApplicationScope as,ApplicationDirectory ad,RequestScope rs)
{
System.out.println("-------------Testing ---------");
if(student==null) System.out.println("Initially student is null");
else 
{
System.out.println("Student data is fetched from json data provided in request");
System.out.println("Student name: "+student.getName());
System.out.println("Student roll number: "+student.getRollNumber());
}

if(sessionScope!=null)
{
//-- Setting in sessionScope for pqr, Testing on next request arrival for same service 
//sessionScope.setAttribute("PQR","fasds"); 
sessionScope.setAttribute("Pqr",123123.13213);
System.out.println("Session scope is not null");
if(this.student!=null)
{
System.out.println("Name via @AutoWired from any scope: "+this.student.getName());
}
}
System.out.println("----------Testing --------");   //If not pass then defalt or null values assigned. 
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
String message="It's working";
return message;
}

@PATH("/testing4")      //@RequestParameter  are also not allowed, if json data arrived [only one non assiged parameter to set]
public String service4(Student student,SessionScope sessionScope,ApplicationScope as,ApplicationDirectory ad,RequestScope rs)
{
System.out.println("-------------Testing ---------");
if(student==null) System.out.println("Initially student is null");
else 
{
System.out.println("Student data is fetched from json data provided in request");
System.out.println("Student name: "+student.getName());
System.out.println("Student roll number: "+student.getRollNumber());
}
if(sessionScope!=null)
{
//-- Setting in sessionScope for pqr, Testing on next request arrival for same service 
//sessionScope.setAttribute("PQR","fasds"); 
sessionScope.setAttribute("Pqr",123123.13213);
System.out.println("Session scope is not null");
if(this.student!=null)
{
System.out.println("Name via @AutoWired from any scope: "+this.student.getName());
}
}
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
String message="It's working";
return message;
}
@PATH("/testing5")    //no arguments needed method
public String service5(SessionScope sessionScope,ApplicationScope as,ApplicationDirectory ad,RequestScope rs)
{
System.out.println("-------------Testing ---------");
if(sessionScope!=null)
{
//-- Setting in sessionScope for pqr, Testing on next request arrival for same service 
//sessionScope.setAttribute("PQR","fasds"); 
sessionScope.setAttribute("Pqr",123123.13213);
System.out.println("Session scope is not null");
if(this.student!=null)
{
System.out.println("Name via @AutoWired from any scope: "+this.student.getName());
}
}
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
String message="It's working";
return message;
}

}

package bobby.test;

import com.ashvin.web.rock.annotations.*;
import com.ashvin.web.rock.pojo.*;
import java.io.File;

@InjectApplicationScope
@InjectSessionScope
@InjectRequestScope
@InjectApplicationDirectory
@GET
@PATH("/injectTesting")
public class InjectionTesting
{
private ApplicationScope applicationScope;
private SessionScope sessionScope;
private RequestScope requestScope;
private ApplicationDirectory applicationDirectory;
public void setApplicationScope(ApplicationScope applicationScope)
{
this.applicationScope=applicationScope;
}
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}
public void setRequestScope(RequestScope requestScope)
{
this.requestScope=requestScope;
}
public void setApplicationDirectory(ApplicationDirectory applicationDirectory)
{
this.applicationDirectory=applicationDirectory;
}
@PATH("/testing1")
public void service1()
{
try
{
System.out.println("Here, Testing1 of Inject Session start");
sessionScope.setAttribute("key1","value1");
}catch(Exception exception)
{
System.out.println("problem: "+exception.getMessage());
}
}
@PATH("/testing2")
public void service2()
{
try
{
System.out.println("Here, Testing2 of Inject session ends");
System.out.println("Is sessionScope null: "+sessionScope==null);
System.out.println("Fetched data from session: "+(String)this.sessionScope.getAttribute("key1"));
}catch(Exception exception)
{
System.out.println("problem: "+exception.getMessage());
}
}
@PATH("/testing3")
@FORWARD("/testing4")
public void service3()
{
try
{
System.out.println("Here, Testing3 of Inject Request starts");
this.requestScope.setAttribute("key2","value2");
}catch(Exception exception)
{
System.out.println("problem: "+exception.getMessage());
}
}
@PATH("/testing4")
public void service4()
{
try
{
System.out.println("Here, Testing4 of injecet Request ends.");
System.out.println("Value from requestScope.getAttribute is: "+(String)requestScope.getAttribute("key2"));
}catch(Exception e)
{
System.out.println("problem: "+e.getMessage());
}
}
@PATH("/testing5")
public void service5()
{
try
{
System.out.println("Here, Testing5 of Inject Application scope start");
Student student=new Student();
student.setName("Ashvin");
student.setRollNumber(1001);
applicationScope.setAttribute("key3",student);
}catch(Exception exception)
{
System.out.println("problem: "+exception.getMessage());
}
}
@PATH("/testing6")
public void service6()
{
try
{
System.out.println("Here, Testing6 of Inject application scope ends");
System.out.println("Fetched data from Application Scope [Student]: "+((Student)this.applicationScope.getAttribute("key3")).getName());
}catch(Exception exception)
{
System.out.println("problem: "+exception.getMessage());
}
}

@PATH("/testing7")
public void service7()
{
try
{
System.out.println("Here, Testing7 of Inject Application Directory to fetch the directory File");
File file=this.applicationDirectory.getDirectory();
System.out.println(file.getAbsolutePath());
}catch(Exception exception)
{
System.out.println("problem: "+exception.getMessage());
}
}

}

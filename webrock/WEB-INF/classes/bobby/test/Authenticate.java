package bobby.test;

import com.ashvin.web.rock.annotations.*;
import com.ashvin.web.rock.pojo.*;

@PATH("/authenticate")
@InjectSessionScope
public class Authenticate
{
private SessionScope sessionScope;
public void setSessionScope(SessionScope sessionScope)
{
this.sessionScope=sessionScope;
}
@AutoWired(name="name")
private String name;
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
@PATH("/login")
public void login(SessionScope sessionScope,ApplicationScope applicationScope) throws com.ashvin.web.rock.exceptions.SecurityException
{
System.out.println(sessionScope!=null);
System.out.println(applicationScope!=null);
String name=(String)sessionScope.getAttribute("name");
System.out.println("login method called");
if(name!=null && name.equalsIgnoreCase("ASHVIN"))
{
System.out.println("Authenticate");
sessionScope.setAttribute("name","ashvin");
}
else
{
System.out.println("Not authenticate");
throw new com.ashvin.web.rock.exceptions.SecurityException("Invalid username/password");
}
try
{
Thread.sleep(5000);
}catch(Exception e)
{
}
}
/*
@PATH("/login2")
public void login(RequestScope requestScope)    //Testing: Same method name
{

}*/   //Just for testing

@PATH("/login3")
public void login3()
{
System.out.println("Name from AutoWired: "+this.name);
System.out.println("is sessionScope from inject: "+this.sessionScope!=null);
}
@PATH("/logout")
public void logout(SessionScope sessionScope,ApplicationScope applicationScope)
{
sessionScope.removeAttribute("name");
}
}

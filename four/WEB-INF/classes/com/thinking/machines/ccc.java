package com.thinking.machines;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class ccc extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name="";
String city="";
Cookie c1;
Cookie cookies[]=request.getCookies();
if(cookies!=null)
{
for(int i=0;i<cookies.length;i++)
{
c1=cookies[i];
if(c1.getName().equals("name"))
{
name=c1.getValue();
break;
}
}
if(name.length()!=0) name=URLDecoder.decode(name);
for(int i=0;i<cookies.length;i++)
{
c1=cookies[i];
if(c1.getName().equals("city"))
{
city=c1.getValue();
break;
}
}
if(city.length()!=0) city=URLDecoder.decode(city);
}
System.out.println("Name: "+name);
System.out.println("City: "+city);

PrintWriter pw=response.getWriter();
response.setContentType("text/html");


pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My Fourth Web Application</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("<form action='/four/index.html'>");
pw.println("Data Saved<br>");
pw.println("Name: "+name+"<br>");
pw.println("City: "+city+"<br>");
pw.println("<button type='submit'>OK</button>");
pw.println("</form>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}



}
}

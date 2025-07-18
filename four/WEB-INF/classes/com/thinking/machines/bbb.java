package com.thinking.machines;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class bbb extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name="";
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
}
String city=request.getParameter("city");
Cookie c2=new Cookie("city",URLEncoder.encode(city));
response.addCookie(c2);

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
pw.println("Name: "+name+"<br>");
pw.println("City: "+city+"<br>");
pw.println("<a href='/four/ccc'>Save</a>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}



}
}

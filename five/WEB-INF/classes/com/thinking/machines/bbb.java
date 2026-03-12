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
ServletContext sc=request.getServletContext();

String city=sc.getInitParameter("city");      //Over here the context from global context
System.out.println("City in bbb servlet: "+city);
String country=getInitParameter("country");   //Over here the init-parameters from local servlet context.
System.out.println("Country in bbb servlet: "+country);

HttpSession hs=request.getSession();
hs.setMaxInactiveInterval(30);		//30 Second time duration assigned to this session
String name=(String)hs.getAttribute("name");
 city=request.getParameter("city");
hs.setAttribute("city",city);

System.out.println("Name: "+name);
System.out.println("City: "+city);

PrintWriter pw=response.getWriter();
response.setContentType("text/html");

pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My Fifth Web Application</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("Name: "+name+"<br>");
pw.println("City: "+city+"<br>");
pw.println("<a href='/five/ccc'>Save</a>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}
}
}

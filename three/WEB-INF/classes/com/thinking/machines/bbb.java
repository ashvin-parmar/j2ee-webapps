package com.thinking.machines;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class bbb extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name=request.getParameter("name");
String city=request.getParameter("city");
PrintWriter pw=response.getWriter();
response.setContentType("text/html");


pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My Third Web Application</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("Name: "+name+"<br>");
pw.println("City: "+city+"<br>");
pw.println("<a href='/three/ccc?name="+name+"&city="+city+"'>Save</a>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}
}
}

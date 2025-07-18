package com.thinking.machines;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ccc extends HttpServlet
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
pw.println("<html>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My Third Web Application</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("Data Saved<br>");
pw.println("Name: "+name+"<br>");
pw.println("City: "+city+"<br>");
pw.println("<a href='/three/index.html'>OK</a>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}
}
}

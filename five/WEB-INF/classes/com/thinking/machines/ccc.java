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
HttpSession hs=request.getSession();
hs.setMaxInactiveInterval(30);
String name=(String)hs.getAttribute("name");
String city=(String)hs.getAttribute("city");
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
pw.println("<form action='/five/index.html'>");
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

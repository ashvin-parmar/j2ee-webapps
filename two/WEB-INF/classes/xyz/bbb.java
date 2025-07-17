package xyz;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class bbb extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name=request.getParameter("name");
String city=request.getParameter("city");
System.out.println("Name: "+name);
System.out.println("City : "+city);

PrintWriter pw=response.getWriter();
response.setContentType("text/html");
pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My second web application</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("<form action='/two/index.html'>");
pw.println("Data Saved<br>");
pw.println("Name: "+name+"<br>");
pw.println("City: "+city);
pw.println("<br>");
pw.println("<button type='submit'>OK</button>");
pw.println("</form>");
pw.println("</body>");
pw.println("<html>");


}catch(Exception exception)
{
System.out.println(exception);
}
}
}

package xyz;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class abc extends HttpServlet
{
public void doGet(HttpServletRequest rq,HttpServletResponse rs)
{
try
{
String nm=rq.getParameter("nm");
String ct=rq.getParameter("ct");
String gdr=rq.getParameter("gdr");

PrintWriter pw;
pw=rs.getWriter();
rs.setContentType("text/html");
pw.println("<!DOCTYPE html>");
pw.println("<html>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My First Web Application</title>");
pw.println("</head");
pw.println("<body>");
pw.println("<center>");
pw.println("<h3>Data Saved</h3>");
pw.println("<a href='/one/index.html'>OK</a>");
pw.println("</center>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}
}
}

package xyz;

import javax.servlet.*;			//
import javax.servlet.http.*; //HttpServlet,HttpServletRequest,HttpServletResponse
import java.io.*;

public class aaa extends HttpServlet
{
public void doGet(HttpServletRequest rq,HttpServletResponse rs)
{
try
{
String nm=rq.getParameter("nm");
String ct=rq.getParameter("ct");
String gdr=rq.getParameter("gdr");

System.out.println("Name: "+nm);
System.out.println("City: "+ct);
System.out.println("Gender: "+gdr);
PrintWriter pw;
pw=rs.getWriter();
rs.setContentType("text/html");
pw.println("<!DOCTYPE html>");
pw.println("<hmtl lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My First Web Application</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("<h3>Data Saved</h3>");
pw.println("<form action='/one/index.html'>");
pw.println("<button type='submit'>OK</button>");
pw.println("</form>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception exception)
{
System.out.println(exception);
}	
}
}

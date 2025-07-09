package xyz;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class vvv extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name=request.getParameter("nm");
String city=request.getParameter("ct");
String gender=request.getParameter("gdr");
System.out.println("Name: "+name);
System.out.println("City "+city);
System.out.println("Gender: "+gender);

PrintWriter pw=response.getWriter();
response.setContentType("text/html");
pw.println("<!DOCTYPE html>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My first web application</title>");
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

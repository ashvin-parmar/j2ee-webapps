package com.thinking.machines;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.net.*;

public class aaa extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name=request.getParameter("name");
Cookie c1=new Cookie("name",URLEncoder.encode(name));
System.out.println("Name: "+name);
response.addCookie(c1);
PrintWriter pw=response.getWriter();
response.setContentType("text/html");

pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My Fourth Web Application</title>");
pw.println("<script>");
pw.println("function validate(frm)");
pw.println("{");
pw.println("var city=frm.city.value.trim();");
pw.println("if(city.length==0)");
pw.println("{");
pw.println("alert('City Required');");
pw.println("frm.city.focus();");
pw.println("return false;");
pw.println("}");
pw.println("return true;");
pw.println("}");
pw.println("</script>");
pw.println("</head>");
pw.println("<body>");
pw.println("<form action='/four/bbb' onsubmit='return validate(this)'>");
pw.println("City");
pw.println("&nbsp;");
pw.println("<input type='text' id='city' name='city'><br>");
pw.println("<button type='submit'>Next</button>");
pw.println("</form>");
pw.println("</body>");
pw.println("</html>");
}catch(Exception e)
{
System.out.println(e);
}



}
}

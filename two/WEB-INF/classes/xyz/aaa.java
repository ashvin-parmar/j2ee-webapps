package xyz;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;

public class aaa extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
String name=request.getParameter("name");
System.out.println("Name: "+name);
PrintWriter pw;
pw=response.getWriter();
response.setContentType("text/html");

pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>My second web application</title>");
pw.println("<script>");
pw.println("function validate(frm)");
pw.println("{");
pw.println("var city=frm.city.value.trim();");
pw.println("if(city.length==0)");
pw.println("{");
pw.println("var cityErrorSection=document.getElementById('cityErrorSection');");
pw.println("cityErrorSection.innerHTML='City name required';");
pw.println("return false;");
pw.println("}");
pw.println("return true;");
pw.println("}");
pw.println("</script>");
pw.println("</head>");
pw.println("<body>");
pw.println("<form action='/two/bbb' onsubmit='return validate(this)'>");
pw.println("Name");
pw.println("<input type='text' id='name' name='name' value='"+name+"'><br>");
pw.println("City");
pw.println("<input type='text' id='city' name='city'>");
pw.println("&nbsp;&nbsp;");
pw.println("<span id='cityErrorSection'></span>");
pw.println("<br>");
pw.println("<button type='submit'>Save</button>");
pw.println("</form>");
pw.println("</body>");
pw.println("<html>");
}catch(Exception exception)
{
System.out.println(exception);
}
}
}

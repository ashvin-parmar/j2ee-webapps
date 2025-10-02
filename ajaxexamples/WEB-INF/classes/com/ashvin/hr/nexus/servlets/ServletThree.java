package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class ServletThree extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}catch(Exception exception)
{
//do nothing
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
String firstName=request.getParameter("firstName");
String lastName=request.getParameter("lastName");
int age=0;
try
{
age=Integer.parseInt(request.getParameter("age"));
}catch(NumberFormatException nfe)
{
age=0;
}
PrintWriter pw=response.getWriter();
response.setContentType("text/plain");
pw.print(firstName+","+lastName+","+age);
}catch(Exception exception)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{

}
}
}
}

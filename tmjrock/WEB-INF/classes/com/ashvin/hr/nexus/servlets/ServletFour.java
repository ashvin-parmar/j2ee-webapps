package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import com.google.gson.*;
public class ServletFour extends HttpServlet
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
int age=Integer.parseInt(request.getParameter("age"));
Gson gson=new Gson();
Customer customer=new Customer();
customer.firstName=firstName;
customer.lastName=lastName;
customer.age=age;
PrintWriter pw=response.getWriter();
response.setContentType("application/json");

pw.print(gson.toJson(customer));
pw.flush();
//pw.print(firstName+","+lastName+","+age);
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

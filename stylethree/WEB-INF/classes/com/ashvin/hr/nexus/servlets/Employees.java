package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

import com.ashvin.hr.nexus.dl.*;

public class Employees extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
doPost(request,response);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
HttpSession hs=request.getSession();
if(hs.getAttribute("username")==null)
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
return ;
}
PrintWriter pw=response.getWriter();
response.setContentType("application/json");

//ServletContext servletContext=getServletContext();	//HttpServlet method getServletContext available
List<EmployeeDTO> employees=(new EmployeeDAO()).getAll();
Gson gson=new Gson();
String jsonString=gson.toJson(employees);

pw.print(jsonString);
pw.flush();
}catch(Exception exception)
{
System.out.println(exception);
try
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/ErrorPage.jsp");
requestDispatcher.forward(request,response);
}catch(Exception e)
{
//do nothing
}
}
}
}

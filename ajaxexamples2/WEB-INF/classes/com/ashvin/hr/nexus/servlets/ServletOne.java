package com.ashvin.hr.nexus.servlets;

import com.ashvin.hr.nexus.dl.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import com.google.gson.*;

public class ServletOne extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);	//Error code: 405
}catch(Exception exception)
{

}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
List<DesignationDTO> designations;
DesignationDAO designationDAO=new DesignationDAO();
designations=designationDAO.getAll();
PrintWriter pw=response.getWriter();
response.setContentType("application/json");
int i=0;
String d="{}";
try
{
Gson gson=new Gson();
d=gson.toJson(designations);
}catch(Exception e)
{

}
pw.print(d);
pw.flush();
}catch(DAOException daoException)
{
//Nothing	--> Not encountered here, because we do not have thrown anything
System.out.println(daoException.getMessage());
}
catch(Exception exception)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);	//Error code: 500
}catch(Exception e)
{
//do nothing
}
}
}
}

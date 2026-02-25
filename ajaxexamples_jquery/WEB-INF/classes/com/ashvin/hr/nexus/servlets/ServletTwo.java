package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

import com.ashvin.hr.nexus.dl.*;
import com.google.gson.*;

public class ServletTwo extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}catch(Exception e)
{
//do nothing
}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
int code=0;
try
{
code=Integer.parseInt(request.getParameter("code"));
}catch(NumberFormatException nfe)
{
//do nothing;
}
DesignationDAO designationDAO=new DesignationDAO();
DesignationDTO designationDTO;
Gson gson=new Gson();
String designation;
try
{
PrintWriter pw=response.getWriter();
response.setContentType("application/json");
try
{
designationDTO=designationDAO.getByCode(code);
designation=gson.toJson(designationDTO);
System.out.println(designation);
pw.print(designation);
}catch(DAOException daoException)
{
pw.print("{}");
}
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

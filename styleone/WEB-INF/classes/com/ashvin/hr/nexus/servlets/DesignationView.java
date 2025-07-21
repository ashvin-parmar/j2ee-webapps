package com.ashvin.hr.nexus.servlets;

import com.ashvin.hr.nexus.dl.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.*;

public class DesignationView extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
DesignationDAO designationDAO;
designationDAO=new DesignationDAO();
List<DesignationDTO> designations;
designations=designationDAO.getAll();
PrintWriter pw=response.getWriter();
response.setContentType("text/html");

//Code from DesignationViewTemplate.html


}catch(DAOException daoException)
{
System.out.println(daoException.getMessage());		//505 error page[later on]
}
catch(Exception exception)
{
System.out.println(exception.getMessage());		//505 error page
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
}

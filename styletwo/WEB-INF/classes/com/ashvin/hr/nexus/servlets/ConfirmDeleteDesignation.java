package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;

public class ConfirmDeleteDesignation extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
int code=0;
try
{
try
{
code=Integer.parseInt(request.getParameter("code"));
}catch(NumberFormatException nfe)
{
// do nothing
}
DesignationDAO designationDAO=new DesignationDAO();
DesignationDTO designationDTO;
DesignationBean designationBean;
try
{
designationDTO=designationDAO.getByCode(code);
designationBean=new DesignationBean();
designationBean.setCode(designationDTO.getCode());
designationBean.setTitle(designationDTO.getTitle());
request.setAttribute("designationBean",designationBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/ConfirmDeleteDesignation.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Designations.jsp");
requestDispatcher.forward(request,response);
}

}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}

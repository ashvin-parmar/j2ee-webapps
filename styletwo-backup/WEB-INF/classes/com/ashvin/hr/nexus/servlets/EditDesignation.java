package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.beans.*;
import com.ashvin.hr.nexus.dl.*;
public class EditDesignation extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
int code=0;
try
{
code=Integer.parseInt(request.getParameter("code"));
}catch(NumberFormatException nfe)
{
}
DesignationBean designationBean;
DesignationDAO designationDAO=new DesignationDAO();
DesignationDTO designationDTO;
try
{
designationDTO=designationDAO.getByCode(code);
designationBean=new DesignationBean();
designationBean.setCode(code);
designationBean.setTitle(designationDTO.getTitle());
request.setAttribute("designationBean",designationBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/EditDesignationForm.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
/*
MessageBean messageBean=new MessageBean();
messageBean.setHeading("Designation (Edit module)");
messageBean.setMessage(daoException.getMessage());
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(false);
messageBean.setButtonOneText("OK");
messageBean.setButtonOneAction("/Designations.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
*/
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Designations.jsp");
requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
System.out.println("Problem: "+exception.getMessage());
}
}
}

package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
public class UpdateDesignation extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
HttpSession hs=request.getSession();
if(hs.getAttribute("username")==null)
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
return;
}
DesignationBean designationBean=(DesignationBean)request.getAttribute("designationBean");
DesignationDTO designationDTO;
DesignationDAO designationDAO=new DesignationDAO();
try
{
designationDTO=new DesignationDTO();
designationDTO.setCode(designationBean.getCode());
designationDTO.setTitle(designationBean.getTitle());
designationDAO.update(designationDTO);
MessageBean messageBean=new MessageBean();
messageBean.setHeading("Designation (Update module)");
messageBean.setMessage("Designation updated");
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(false);
messageBean.setButtonOneText("OK");
messageBean.setButtonOneAction("Designations.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
ErrorBean errorBean=new ErrorBean();
errorBean.setError(daoException.getMessage());
request.setAttribute("errorBean",errorBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/EditDesignationForm.jsp");
requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}


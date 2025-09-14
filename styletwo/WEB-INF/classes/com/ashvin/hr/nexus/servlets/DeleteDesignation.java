package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;

public class DeleteDesignation extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
HttpSession hs=request.getSession();
String formId=request.getParameter("formId");
RefreshBean refreshBean=(RefreshBean)hs.getAttribute("refreshBean");
String formIdFromSession;
if(refreshBean==null || formId==null || (formIdFromSession=refreshBean.getFormId())==null || !formId.equals(formIdFromSession))
{ 
MessageBean messageBean=new MessageBean();
messageBean.setHeading("Designation (Delete Module)");
messageBean.setMessage("Resubmission occured, do not resubmit.");
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(false);
messageBean.setButtonOneText("OK");
messageBean.setButtonOneAction("Designations.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher;
requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
requestDispatcher.forward(request,response);
}
refreshBean.setFormId("");
hs.setAttribute("refreshBean",refreshBean);
DesignationBean designationBean=(DesignationBean)request.getAttribute("designationBean");
String message="";
DesignationDAO designationDAO=new DesignationDAO();
try
{
designationDAO.deleteByCode(designationBean.getCode());
message="Designation: "+designationBean.getTitle()+" deleted";
}catch(DAOException daoException)
{
message=daoException.getMessage();
}
MessageBean messageBean=new MessageBean();
messageBean.setHeading("Designation (Delete Module)");
messageBean.setMessage(message);
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(false);
messageBean.setButtonOneText("OK");
messageBean.setButtonOneAction("Designations.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
requestDispatcher.forward(request,response);
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}

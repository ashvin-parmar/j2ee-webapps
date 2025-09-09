package com.ashvin.hr.nexus.servlets;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class AddDesignation extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
DesignationBean designationBean;
designationBean=(DesignationBean)request.getAttribute("designationBean");
DesignationDAO designationDAO;
DesignationDTO designationDTO;

try
{
designationDTO=new DesignationDTO();
designationDTO.setTitle(designationBean.getTitle());
designationDAO=new DesignationDAO();
System.out.println("Title: "+designationDTO.getTitle());
designationDAO.add(designationDTO);
designationBean.setCode(designationDTO.getCode());

MessageBean messageBean=new MessageBean();
messageBean.setHeading("Designation (Add Module)");
messageBean.setMessage("Designation added, add more?");
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(true);
messageBean.setButtonOneText("Yes");
messageBean.setButtonOneAction("AddDesignationForm.jsp");
messageBean.setButtonTwoText("No");
messageBean.setButtonTwoAction("/styletwo/Designations.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher;
requestDispatcher=request.getRequestDispatcher("Notification.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
ErrorBean errorBean=new ErrorBean();
errorBean.setError(daoException.getMessage());
request.setAttribute("errorBean",errorBean);
RequestDispatcher requestDispatcher;
requestDispatcher=request.getRequestDispatcher("AddDesignationForm.jsp");
requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}

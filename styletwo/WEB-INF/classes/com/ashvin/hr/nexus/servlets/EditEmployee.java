package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.text.*;
import java.math.*;
import java.util.*;
import com.ashvin.hr.nexus.beans.*;
import com.ashvin.hr.nexus.dl.*;

public class EditEmployee extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
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
String employeeId=request.getParameter("employeeId");
System.out.println("Employee Id: "+employeeId);
SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

EmployeeBean employeeBean;
EmployeeDTO employeeDTO;
EmployeeDAO employeeDAO=new EmployeeDAO();
try
{
employeeDTO=employeeDAO.getByEmployeeId(employeeId);
employeeBean=new EmployeeBean();
employeeBean.setEmployeeId(employeeDTO.getEmployeeId());
employeeBean.setName(employeeDTO.getName());
DesignationDTO designationDTO=employeeDTO.getDesignation();
employeeBean.setDesignationCode(designationDTO.getCode());
employeeBean.setDesignation(designationDTO.getTitle());
employeeBean.setDateOfBirth(simpleDateFormat.format(employeeDTO.getDateOfBirth()));
employeeBean.setGender(employeeDTO.getGender().equals("M")?"Male":"Female");
employeeBean.setIsIndian(employeeDTO.getIsIndian());
employeeBean.setBasicSalary(employeeDTO.getBasicSalary().toPlainString());
employeeBean.setPanNumber(employeeDTO.getPANNumber());
employeeBean.setAadharCardNumber(employeeDTO.getAadharCardNumber());


request.setAttribute("employeeBean",employeeBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/EditEmployeeForm.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
System.out.println(daoException.getMessage());
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
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Employees.jsp");
requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
System.out.println("Problem: "+exception.getMessage());
}
}
}

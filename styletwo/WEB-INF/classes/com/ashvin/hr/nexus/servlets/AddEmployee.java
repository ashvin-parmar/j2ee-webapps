package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.math.*;
import java.util.*;
import java.text.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;



public class AddEmployee extends HttpServlet
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
SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
EmployeeDTO employee=new EmployeeDTO();
EmployeeBean employeeBean=(EmployeeBean)request.getAttribute("employeeBean");
employee.setName(employeeBean.getName());
DesignationDTO designation=new DesignationDTO();
designation.setCode(employeeBean.getDesignationCode());
designation.setTitle(employeeBean.getDesignation());
employee.setDesignation(designation);
try
{
employee.setDateOfBirth(simpleDateFormat.parse(employeeBean.getDateOfBirth()));
}catch(ParseException pe)
{
employee.setDateOfBirth(new Date("2000-01-01"));
}

employee.setGender(employeeBean.getGender().equals("Male")?"M":"F");
employee.setIsIndian(employeeBean.getIsIndian());
employee.setBasicSalary(new BigDecimal(employeeBean.getBasicSalary()));
employee.setPANNumber(employeeBean.getPanNumber());
employee.setAadharCardNumber(employeeBean.getAadharCardNumber());

Boolean designationCodeExists=false;
Boolean panNumberExists=false;
Boolean aadharCardNumberExists=false; 


EmployeeDAO employeeDAO=new EmployeeDAO();
/*
designationCodeExists=(new DesignationDAO()).isCodeExists(employeeBean.getDesignationCode());
panNumberExists=employeeDAO.isPANNumberExists(employee.getPANNumber());
aadharCardNumberExists=employeeDAO.isAadharCardNumberExists(employee.getAadharCardNumber());
if(!designationCodeExists || panNumberExists || aadharCardNumberExists)
{

}
*/
try
{
employeeDAO.add(employee);
MessageBean messageBean=new MessageBean();
messageBean.setHeading("Employee (Add module)");
messageBean.setMessage("Employee: "+employee.getName()+" added.");
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(false);
messageBean.setButtonOneText("OK");
messageBean.setButtonOneAction("Employees.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
ErrorBean errorBean=new ErrorBean();
errorBean.setError(daoException.getMessage());
request.setAttribute("errorBean",errorBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/AddEmployeeForm.jsp");
requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
//do nothing
}
}
}

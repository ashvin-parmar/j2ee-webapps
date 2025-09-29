package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
import java.util.*;
import java.text.*;
import java.math.*;

public class UpdateEmployee extends HttpServlet
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
EmployeeBean employeeBean=(EmployeeBean)request.getAttribute("employeeBean");
EmployeeDTO employee=new EmployeeDTO();
employee.setEmployeeId(employeeBean.getEmployeeId());
employee.setName(employeeBean.getName());
DesignationDTO designation=new DesignationDTO();
designation.setCode(employeeBean.getDesignationCode());
designation.setTitle(employeeBean.getDesignation());
employee.setDesignation(designation);
Date date=null;
try
{
date=simpleDateFormat.parse("2001-01-01");
date=simpleDateFormat.parse(employeeBean.getDateOfBirth());
}catch(ParseException pe)
{
}
employee.setDateOfBirth(date);
employee.setGender(employeeBean.getGender().equals("Male")?"M":"F");
employee.setIsIndian(employeeBean.getIsIndian());
employee.setBasicSalary(new BigDecimal(employeeBean.getBasicSalary()));
employee.setPANNumber(employeeBean.getPanNumber());
employee.setAadharCardNumber(employeeBean.getAadharCardNumber());

EmployeeDAO employeeDAO=new EmployeeDAO();

/*
EmployeeDTO employeeDTO;
try
{
employeeDTO=employeeDAO.getByPANNumber(panNumber);
if(employeeDTO.getEmployeeId().equalsIgnoreCase(employeeId)==false)
{
panNumberExists=true;
}
}catch(DAOException d2)
{
panNumberExists=false;
}
try
{
employeeDTO=employeeDAO.getByAadharCardNumber(aadharCardNumber);
if(employeeDTO.getEmployeeId().equalsIgnoreCase(employeeId)==false)
{
aadharCardNumberExists=true;
}
}catch(DAOException d3)
{
aadharCardNumberExists=false;
}
try
{
designationCodeExists=designationDAO.isCodeExists(designationCode);
}catch(DAOException d4)
{
designationCodeExists=false;
}
*/

try
{
employeeDAO.update(employee);

MessageBean messageBean=new MessageBean();
messageBean.setHeading("Employee (Update module)");
messageBean.setMessage("Employee updated");
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
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/EditEmployeeForm.jsp");
requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}



package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
import java.text.*;
import java.math.*;
import java.util.*;

public class ConfirmDeleteEmployee extends HttpServlet
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
EmployeeDAO employeeDAO=new EmployeeDAO();
EmployeeDTO employeeDTO;
EmployeeBean employeeBean;
SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
try
{
employeeDTO=employeeDAO.getByEmployeeId(employeeId);
employeeBean=new EmployeeBean();
employeeBean.setEmployeeId(employeeId);
employeeBean.setName(employeeDTO.getName());
DesignationDTO designation=employeeDTO.getDesignation();
employeeBean.setDesignationCode(designation.getCode());
employeeBean.setDesignation(designation.getTitle());
employeeBean.setGender(employeeDTO.getGender().equals("M")?"Male":"Female");
employeeBean.setIsIndian(employeeDTO.getIsIndian());
employeeBean.setBasicSalary(employeeDTO.getBasicSalary().toPlainString());
employeeBean.setDateOfBirth(simpleDateFormat.format(employeeDTO.getDateOfBirth()));
employeeBean.setPanNumber(employeeDTO.getPANNumber());
employeeBean.setAadharCardNumber(employeeDTO.getAadharCardNumber());
request.setAttribute("employeeBean",employeeBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/ConfirmDeleteEmployee.jsp");
requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Employees.jsp");
requestDispatcher.forward(request,response);
}

}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}

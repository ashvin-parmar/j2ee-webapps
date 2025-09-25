package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import com.ashvin.hr.nexus.beans.*;
import com.ashvin.hr.nexus.bl.*;

public class Employees extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
doPost(request,response);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
HttpSession hs=request.getSession();
if(hs.getAttribute("username")==null)
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
return ;
}
PrintWriter pw=response.getWriter();
response.setContentType("text/javascript");

/*
Below given things are not to try --> Statically path given
File file=new File("/media/ashvin/code/tomcat9/webapps/styletwo/js/Employees.js");
*/
ServletContext servletContext=getServletContext();	//HttpServlet method getServletContext available
File file=new File(servletContext.getRealPath("")+File.separator+"WEB-INF"+File.separator+"js"+File.separator+"Employees.js");
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");		//We want to read only file Employees.js and fetch all data and println to client side 
while(randomAccessFile.getFilePointer()<randomAccessFile.length())
{
pw.println(randomAccessFile.readLine());
}
randomAccessFile.close();

pw.println("var employee;");
List<EmployeeBean> employees=(new EmployeeBL()).getAll();
int i=0;
for(EmployeeBean employee:employees)
{
pw.println("employee=new Employee();");
pw.println("employee.employeeId=\""+employee.getEmployeeId()+"\";");
pw.println("employee.name=\""+employee.getName()+"\";");
pw.println("employee.designationCode=\""+employee.getDesignationCode()+"\";");
pw.println("employee.designation=\""+employee.getDesignation()+"\";");
pw.println("employee.gender=\""+employee.getGender()+"\";");
pw.println("employee.isIndian=\""+employee.getIsIndian()+"\";");
pw.println("employee.dateOfBirth=\""+employee.getDateOfBirth()+"\";");
pw.println("employee.basicSalary=\""+employee.getBasicSalary()+"\";");
pw.println("employee.panNumber=\""+employee.getPanNumber()+"\";");
pw.println("employee.aadharCardNumber=\""+employee.getAadharCardNumber()+"\";");
pw.println("employees["+i+"]=employee;\n");
i++;
}

}catch(Exception exception)
{
System.out.println(exception);
try
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/ErrorPage.jsp");
requestDispatcher.forward(request,response);
}catch(Exception e)
{
//do nothing
}
}
}
}

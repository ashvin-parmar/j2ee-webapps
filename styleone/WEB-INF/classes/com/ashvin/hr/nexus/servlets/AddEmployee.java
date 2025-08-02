package com.ashvin.hr.nexus.servlets;

import com.ashvin.hr.nexus.dl.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.math.*;
import java.text.*;

public class AddEmployee extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
String name="";
int designationCode=0;
Date dateOfBirth=null;
String gender="";
String isIndian="";
BigDecimal basicSalary=null;
String panNumber="";
String aadharCardNumber="";
PrintWriter pw=null;
SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
try
{
pw=response.getWriter();
response.setContentType("text/html");
//Pending 
/*
	1) designation_code validate
	2) pan_number duplicacy check
	3) aadhar_card_number duplicacy check
*/
name=request.getParameter("name");
try
{
designationCode=Integer.parseInt(request.getParameter("designationCode"));
}catch(NumberFormatException nfe)
{
System.out.println(nfe.getMessage());
//sendBackView();
return;
}
try
{
dateOfBirth=simpleDateFormat.parse(request.getParameter("dateOfBirth"));
}catch(ParseException pe)
{
//sendBackView();
return ;
}

gender=request.getParameter("gender");

isIndian=request.getParameter("isIndian");
if(isIndian==null) isIndian="N";
basicSalary=new BigDecimal(request.getParameter("basicSalary"));

panNumber=request.getParameter("panNumber");

aadharCardNumber=request.getParameter("aadharCardNumber");


EmployeeDTO employeeDTO=new EmployeeDTO();
employeeDTO.setName(name);
employeeDTO.setDesignationCode(designationCode);
employeeDTO.setDateOfBirth(dateOfBirth);
employeeDTO.setGender(gender.charAt(0));
employeeDTO.setIsIndian(isIndian.equals("Y")?true:false);
employeeDTO.setBasicSalary(basicSalary);
employeeDTO.setPANNumber(panNumber);
employeeDTO.setAadharCardNumber(aadharCardNumber);

(new EmployeeDAO()).addEmployee(employeeDTO);

pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>Style one</title>");
pw.println("</head>");
pw.println("<body>");
pw.println("<!-- Main content start here -->");
pw.println("<div style='width:90hw;height:95vh;border:1px solid black'>");
pw.println("<!-- header start here -->");
pw.println("<div style='width:90hw;margin:5px;border:1px solid black'>");
pw.println("<a href='/styleone/index.html'><img src='/styleone/images/hr_nexus_logo.png' style='width:30px;float:left'></a>");
pw.println("<div style='margin:4px;font-size:15pt'>HR-Nexus</div>");
pw.println("</div>");
pw.println("<!-- header ends here -->");
pw.println("<!-- middle content start here -->");
pw.println("<div style='width:90hw;height:72vh;margin:5px;border:1px solid white'>");
pw.println("<!-- left panel start here -->");
pw.println("<div style='height:65vh;margin:5px;padding:5px;float:left;border:1px solid black'>");
pw.println("<a href='/styleone/designationView'>Designations</a><br>");
pw.println("<b>Employees</b><br><br>");
pw.println("<a href='/styleone/index.html'>Home</a>");
pw.println("</div>");
pw.println("<!-- left panel ends here -->");
pw.println("<!-- right panel start here -->");
pw.println("<div style='height:65vh;margin-left:110px;margin-right:5px;margin-bottom:5px;margin-top:5px;padding:5px;overflow:scroll;border:1px solid black'>");
pw.println("<h2>Notification</h2>");
pw.println("Employee "+name+" added<br><br>");
pw.println("<b>Add more?</b><br>");
pw.println("<table>");
pw.println("<tr>");
pw.println("<td>");
pw.println("<form action='/styleone/addEmployeeForm'>");
pw.println("<button type='submit'>Yes</button>");
pw.println("</form>");
pw.println("</td>");
pw.println("<td>");
pw.println("<form action='/styleone/employeeView'>");
pw.println("<button type='submit'>No</button>");
pw.println("</form>");
pw.println("</td>");
pw.println("</tr>");
pw.println("</table>");
pw.println("</div>");
pw.println("<!-- right panel ends here -->");
pw.println("</div>");
pw.println("<!-- middle content ends here -->");
pw.println("<!-- footer start here -->");
pw.println("<div style='text-align:center;margin:5px;font-size:10pt;border:1px solid white'>");
pw.println("&copy; HR-Nexus 2025");
pw.println("</div>");
pw.println("<!-- footer ends here -->");
pw.println("</div>");
pw.println("<!-- Main content ends here -->");
pw.println("</body>");
pw.println("</html>");

}catch(DAOException daoException)
{
System.out.println("Message here: "+daoException.getMessage());
//same add page with daoException.getMessage() page and previously updated data.
}catch(Exception exception)
{
System.out.println("exception: "+exception.getMessage());	//removed after testing
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
}

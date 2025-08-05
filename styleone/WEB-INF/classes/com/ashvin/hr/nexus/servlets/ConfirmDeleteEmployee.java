package com.ashvin.hr.nexus.servlets;

import com.ashvin.hr.nexus.dl.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;

public class ConfirmDeleteEmployee extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
//Declared and assigned such that use them in both -> try and catch blocks
PrintWriter pw=null;
String employeeId="";
try
{
SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
pw=response.getWriter();		//To be declared above everything, such that in every case pw.println works
response.setContentType("text/html");
employeeId=request.getParameter("employeeId");
EmployeeDTO employeeDTO=((new EmployeeDAO()).getByEmployeeId(employeeId));
employeeId=employeeDTO.getEmployeeId();
String name=employeeDTO.getName();
String designation=employeeDTO.getDesignation();
java.util.Date dateOfBirth=employeeDTO.getDateOfBirth();
String gender=employeeDTO.getGender();
boolean isIndian=employeeDTO.getIsIndian();
BigDecimal basicSalary=employeeDTO.getBasicSalary();
String panNumber=employeeDTO.getPANNumber();
String aadharCardNumber=employeeDTO.getAadharCardNumber();

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
pw.println("<b>Employee ID: </b> "+employeeId+"<br>");
pw.println("<b>Name: </b> "+name+"<br>");
pw.println("<b>Designation: </b>"+designation+"<br>");
pw.println("<b>Gender: </b>"+(gender.equals("M")?"Male":"Female")+"<br>");
pw.println("<b>Nationality: </b>"+(isIndian?"Indian<br>":"Not an Indian<br>"));
pw.println("<b>Date of Birth: </b>"+simpleDateFormat.format(dateOfBirth)+"<br>");
pw.println("<b>Basic salary: </b>"+basicSalary.toPlainString()+"<br>");
pw.println("<b>PAN number: </b>"+panNumber+"<br>");
pw.println("<b>Aadhar card number: </b>"+aadharCardNumber+"<br>");

pw.println("Are you sure, you want to delete employee '<b>"+name+"</b>'?<br>");
pw.println("<table>");
pw.println("<tr>");
pw.println("<td>");
pw.println("<form action='/styleone/deleteEmployee'>");
pw.println("<input type='hidden' id='employeeId' name='employeeId' value='"+employeeId+"'>");
pw.println("<input type='hidden' id='name' name='name' value='"+name+"'>");
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
sendBackView(response);
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
public void sendBackView(HttpServletResponse response)
{
try
{
DesignationDAO designationDAO;
designationDAO=new DesignationDAO();
List<DesignationDTO> designations;
designations=designationDAO.getAll();
PrintWriter pw=response.getWriter();
response.setContentType("text/html");

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
pw.println("<b>Designations</b><br>");
pw.println("<a href='/styleone/employeeView' style='float:left'>Employees</a><br><br>");
pw.println("<a href='/styleone/index.html'>Home</a>");
pw.println("</div>");
pw.println("<!-- left panel ends here -->");
pw.println("<!-- right panel start here -->");
pw.println("<div style='height:65vh;margin-left:110px;margin-right:5px;margin-bottom:5px;margin-top:5px;padding:5px;overflow:scroll;border:1px solid black'>");
pw.println("<h2>Designations</h2>");
pw.println("<table border='1'>");
pw.println("<thead>");
pw.println("<tr>");
pw.println("<th colspan='4' style='text-align:right'><a href='/styleone/AddDesignation.html'>Add new designation</a></th>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<th style='width:40px;text-align:center'>S.No.</th>");
pw.println("<th style='width:200px;text-align:center'>Designation</th>");
pw.println("<th style='width:80px;text-align:center'>Edit</th>");
pw.println("<th style='width:80px;text-align:center'>Delete</th>");
pw.println("</tr>");
pw.println("</thead>");
pw.println("<tbody>");

int sno,i;
DesignationDTO designationDTO;
int code;
String title;
for(i=0,sno=0;i<designations.size();i++)
{
sno++;
designationDTO=designations.get(i);
code=designationDTO.getCode();
title=designationDTO.getTitle();
pw.println("<tr>");
pw.println("<td style='text-align:right'>"+sno+".</td>");
pw.println("<td>"+title+"</td>");
pw.println("<td style='text-align:center'><a href='/styleone/editDesignation?code="+code+"'>edit</a></td>");
pw.println("<td style='text-align:center'><a href='/styleone/confirmDeleteDesignation?code="+code+"'>delete</a></td>");
pw.println("</tr>");
}
pw.println("</tbody>");
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
System.out.println(daoException.getMessage());		//505 error page[later on]
}
catch(Exception exception)
{
System.out.println(exception.getMessage());		//505 error page
}
}
}

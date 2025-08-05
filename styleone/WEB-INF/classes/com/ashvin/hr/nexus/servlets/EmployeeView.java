package com.ashvin.hr.nexus.servlets;

import com.ashvin.hr.nexus.dl.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.text.*;

public class EmployeeView extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
PrintWriter pw;
try
{
pw=response.getWriter();
response.setContentType("text/html");

SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
List<EmployeeDTO> employees=null;
employees=(new EmployeeDAO()).getAll();

pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>HR-Nexus Application</title>");
pw.println("<script>");
pw.println("function Employee()");
pw.println("{");
pw.println("this.employeeId=\"\";");
pw.println("this.name=\"\";");
pw.println("this.designationCode=0;");
pw.println("this.designation=\"\";");
pw.println("this.dateOfBirth=\"\";");
pw.println("this.gender=\"\";");
pw.println("this.isIndian=true;");
pw.println("this.basicSalary=0;");
pw.println("this.panNumber=\"\";");
pw.println("this.aadharCardNumber=\"\";");
pw.println("}");
pw.println("var employees=[];");
pw.println("var employee;");
int i=0;
for(EmployeeDTO employee:employees)
{
pw.println("employee=new Employee();");
pw.println("employee.employeeId=\""+employee.getEmployeeId()+"\";");
pw.println("employee.name=\""+employee.getName()+"\";");
pw.println("employee.designationCode="+employee.getDesignationCode()+";");
pw.println("employee.designation=\""+employee.getDesignation()+"\"");
pw.println("employee.dateOfBirth=\""+simpleDateFormat.format(employee.getDateOfBirth())+"\";");
pw.println("employee.gender=\""+((employee.getGender().equals("M"))?"Male":"Female")+"\";");
pw.println("employee.isIndian="+employee.getIsIndian()+";");
pw.println("employee.basicSalary="+employee.getBasicSalary()+";");
pw.println("employee.panNumber=\""+employee.getPANNumber()+"\";");
pw.println("employee.aadharCardNumber=\""+employee.getAadharCardNumber()+"\";");
pw.println("employees["+i+"]=employee;");
i++;
}
pw.println("var selectedRow=null;");
pw.println("function selectEmployee(row,employeeId)");
pw.println("{");
pw.println("if(selectedRow==row) return;");
pw.println("if(selectedRow!=null)");
pw.println("{");
pw.println("selectedRow.style.background=\"white\";");
pw.println("selectedRow.style.color=\"black\";");
pw.println("}");
pw.println("row.style.background=\"grey\";");
pw.println("row.style.color=\"white\";");
pw.println("selectedRow=row;");
pw.println("var i;");
pw.println("for(i=0;i<employees.length;i++)");
pw.println("{");
pw.println("if(employees[i].employeeId==employeeId)");
pw.println("{");
pw.println("break;");
pw.println("}");
pw.println("}");
pw.println("var emp=employees[i];");
pw.println("document.getElementById('detailsPanel_employeeId').innerHTML=emp.employeeId;");
pw.println("document.getElementById('detailsPanel_name').innerHTML=emp.name;");
pw.println("document.getElementById('detailsPanel_designation').innerHTML=emp.designation;");
pw.println("document.getElementById('detailsPanel_dateOfBirth').innerHTML=emp.dateOfBirth;");
pw.println("document.getElementById('detailsPanel_isIndian').innerHTML=emp.isIndian?\"Yes\":\"No\";");
pw.println("document.getElementById('detailsPanel_gender').innerHTML=emp.gender;");
pw.println("document.getElementById('detailsPanel_basicSalary').innerHTML=emp.basicSalary;");
pw.println("document.getElementById('detailsPanel_panNumber').innerHTML=emp.panNumber;");
pw.println("document.getElementById('detailsPanel_aadharCardNumber').innerHTML=emp.aadharCardNumber;");
pw.println("}");
pw.println("</script>");
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
pw.println("<a href='/styleone/designationView' style='float:left'>Designations</a><br>");
pw.println("<b>Employees</b><br><br>");
pw.println("<a href='/styleone/index.html'>Home</a>");
pw.println("</div>");
pw.println("<!-- left panel ends here -->");
pw.println("<!-- right panel start here -->");
pw.println("<div style='height:65vh;margin-left:110px;margin-right:5px;margin-bottom:5px;margin-top:5px;padding:5px;border:1px solid black'>");
pw.println("<b>Employees</b><br>");
pw.println("<!-- table division start here -->");
pw.println("<div style='height:35vh;margin:5px;border:1px solid black;overflow:scroll'>");
pw.println("<table border='1'>");
pw.println("<thead>");
pw.println("<tr>");
pw.println("<th colspan='6' style='text-align:right'><a href='/styleone/addEmployeeForm' style='margin-right:5px'>Add employee</a></th>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<th style='width:40px;text-align:center'>S.No.</th>");
pw.println("<th style='width:100px;text-align:center'>ID</th>");
pw.println("<th style='width:200px;text-align:center'>Name</th>");
pw.println("<th style='width:200px;text-align:center'>Designation</th>");
pw.println("<th style='width:60px;text-align:center'>Edit</th>");
pw.println("<th style='width:80px;text-align:center'>Delete</th>");
pw.println("</tr>");
pw.println("</thead>");
pw.println("<tbody>");
i=1;
for(EmployeeDTO employee:employees)
{
pw.println("<tr style='cursor:pointer' onclick='selectEmployee(this,\""+employee.getEmployeeId()+"\")'>");
pw.println("<td style='text-align:right'>"+i+".</td>");
pw.println("<td style='text-align:right'>"+employee.getEmployeeId()+"</td>");
pw.println("<td>"+employee.getName()+"</td>");
pw.println("<td>"+employee.getDesignation()+"</td>");
pw.println("<td style='text-align:center'><a href='/styleone/editEmployee?id="+employee.getEmployeeId()+"'>edit</a></td>");
pw.println("<td style='text-align:center'><a href='/styleone/confirmDeleteEmployee?id="+employee.getEmployeeId()+"'>delete</a></td>");
pw.println("</tr>");
i++;
}
pw.println("</tbody>");
pw.println("</table>");
pw.println("</div>		");
pw.println("<!-- table division ends here -->");
pw.println("<!-- data show panel start here -->");
pw.println("<div style='height:20vh;margin:5px;padding-left:5px;border:1px solid black'>");
pw.println("<label style='background:grey;color:white;padding-left:5px;padding-right:5px;'>Details</label>");
pw.println("<table border='0'  width='100%'>");
pw.println("<tr>");
pw.println("<td>ID: <span id='detailsPanel_employeeId' style='margin-right:30px'></span></td>");
pw.println("<td>Name: <span id='detailsPanel_name' style='margin-right:30px'></span></td>");
pw.println("<td>Designation: <span id='detailsPanel_designation'></span></td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Is Indian?: <span id='detailsPanel_isIndian'></span></td>");
pw.println("<td>Gender: <span id='detailsPanel_gender'></span></td>");
pw.println("<td>Date of Birth: <span id='detailsPanel_dateOfBirth'></span></td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Basic salary: <span id='detailsPanel_basicSalary'></span></td>");
pw.println("<td>PAN number: <span id='detailsPanel_panNumber'></span></td>");
pw.println("<td>Aadhar card number: <span id='detailsPanel_aadharCardNumber'></span></td>");
pw.println("</tr>");
pw.println("</table >");
pw.println("</div>");
pw.println("<!-- data show panel ends here -->");
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
System.out.println(daoException.getMessage());	// removed after testing --> mysql related problem
}catch(Exception exception)
{
System.out.println(exception.getMessage());	//removed after testing
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
}

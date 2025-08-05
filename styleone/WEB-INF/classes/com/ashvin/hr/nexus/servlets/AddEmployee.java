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
Boolean designationCodeExists=false;
Boolean panNumberExists=false;
Boolean aadharCardNumberExists=false; 
try
{
pw=response.getWriter();
response.setContentType("text/html");

name=request.getParameter("name");
try
{
designationCode=Integer.parseInt(request.getParameter("designationCode"));
}catch(NumberFormatException nfe)
{
System.out.println(nfe.getMessage());
//sendBackView(response);
return;
}
designationCodeExists=(new DesignationDAO()).isCodeExists(designationCode);
try
{
dateOfBirth=simpleDateFormat.parse(request.getParameter("dateOfBirth"));
}catch(ParseException pe)
{
//sendBackView(response);
return ;
}
gender=request.getParameter("gender");

isIndian=request.getParameter("isIndian");
if(isIndian==null) isIndian="N";
basicSalary=new BigDecimal(request.getParameter("basicSalary"));

panNumber=request.getParameter("panNumber");
aadharCardNumber=request.getParameter("aadharCardNumber");
EmployeeDAO employeeDAO=new EmployeeDAO();
panNumberExists=employeeDAO.isPANNumberExists(panNumber);
aadharCardNumberExists=employeeDAO.isAadharCardNumberExists(aadharCardNumber);
Boolean isValid=true;
if(!designationCodeExists || panNumberExists || aadharCardNumberExists) isValid=false;
if(isValid)
{
EmployeeDTO employeeDTO=new EmployeeDTO();
employeeDTO.setName(name);
employeeDTO.setDesignationCode(designationCode);
employeeDTO.setDateOfBirth(dateOfBirth);
employeeDTO.setGender(gender);
employeeDTO.setIsIndian(isIndian.equals("Y")?true:false);
employeeDTO.setBasicSalary(basicSalary);
employeeDTO.setPANNumber(panNumber);
employeeDTO.setAadharCardNumber(aadharCardNumber);

employeeDAO.addEmployee(employeeDTO);

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
pw.println("Employee <b>"+name+"</b> added<br><br>");
pw.println("<b>Would you like to add more employee?</b><br>");
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
}
else
{
pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>HR-Nexus</title>");
pw.println("<script>");
pw.println("function validateDesignation(frm)");
pw.println("{");
pw.println("var firstInvalidComponent=null;");
pw.println("var valid=true;");
pw.println("var name=frm.name.value.trim();");
pw.println("var nameErrorSection=document.getElementById('nameErrorSection');");
pw.println("nameErrorSection.innerHTML='';");
pw.println("if(name.length==0)");
pw.println("{");
pw.println("nameErrorSection.innerHTML='Required';");
pw.println("firstInvalidComponent=frm.name;");
pw.println("valid=false;");
pw.println("}");
pw.println("var designationCode=frm.designationCode.value;");
pw.println("var designationCodeErrorSection=document.getElementById('designationCodeErrorSection');");
pw.println("designationCodeErrorSection.innerHTML='';");
pw.println("if(designationCode==-1)");
pw.println("{");
pw.println("designationCodeErrorSection.innerHTML=\"Select designation\";");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.designationCode;");
pw.println("valid=false;");
pw.println("}");
pw.println("var dateOfBirth=frm.dateOfBirth.value;");
pw.println("var dateOfBirthErrorSection=document.getElementById('dateOfBirthErrorSection');");
pw.println("dateOfBirthErrorSection.innerHTML='';");
pw.println("if(dateOfBirth.length==0)");
pw.println("{");
pw.println("dateOfBirthErrorSection.innerHTML='Select date of birth';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.dateOfBirth;");
pw.println("valid=false;");
pw.println("}");
pw.println("var gender=frm.gender;");
pw.println("var genderErrorSection=document.getElementById('genderErrorSection');");
pw.println("genderErrorSection.innerHTML='';");
pw.println("if(gender[0].checked==false && gender[0].checked==false)");
pw.println("{");
pw.println("genderErrorSection.innerHTML='Select gender';");
pw.println("valid=false;");
pw.println("}");
pw.println("var basicSalary=frm.basicSalary.value;");
pw.println("var basicSalaryErrorSection=document.getElementById('basicSalaryErrorSection');");
pw.println("basicSalaryErrorSection.innerHTML='';");
pw.println("if(basicSalary<=0)");
pw.println("{");
pw.println("basicSalaryErrorSection.innerHTML='Invalid basic salary';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.basicSalary;");
pw.println("valid=false;");
pw.println("}");
pw.println("var panNumber=frm.panNumber.value.trim();");
pw.println("var panNumberErrorSection=document.getElementById('panNumberErrorSection');");
pw.println("panNumberErrorSection.innerHTML='';");
pw.println("if(panNumber.length==0)");
pw.println("{");
pw.println("panNumberErrorSection.innerHTML='PAN number required';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.panNumber;");
pw.println("valid=false;");
pw.println("}");
pw.println("var aadharCardNumber=frm.aadharCardNumber.value.trim();");
pw.println("var aadharCardNumberErrorSection=document.getElementById('aadharCardNumberErrorSection');");
pw.println("aadharCardNumberErrorSection.innerHTML='';");
pw.println("if(aadharCardNumber.length==0)");
pw.println("{");
pw.println("aadharCardNumberErrorSection.innerHTML='Aadhar card number required';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.aadharCardNumber;");
pw.println("valid=false;");
pw.println("}");
pw.println("if(!valid) firstInvalidComponent.focus();");
pw.println("return valid;");
pw.println("}");
pw.println("function cancelAddition()");
pw.println("{");
pw.println("document.getElementById('cancelAdditionForm').submit();");
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
pw.println("<b>Designations</b><br>");
pw.println("<a href='/styleone/employeeView' style='float:left'>Employees</a><br><br>");
pw.println("<a href='/styleone/index.html'>Home</a>");
pw.println("</div>");
pw.println("<!-- left panel ends here -->");
pw.println("<!-- right panel start here -->");
pw.println("<div style='height:65vh;margin-left:110px;margin-right:5px;margin-bottom:5px;margin-top:5px;padding:5px;overflow:scroll;border:1px solid black'>");
pw.println("<b>Employee (Add Module)</b><br>");
pw.println("<form method='post' action='/styleone/addEmployee' onsubmit='return validateDesignation(this)'>");
pw.println("<table>");
pw.println("<tr>");
pw.println("<td><b>Name: </b></td>");
pw.println("<td>");
pw.println("<input type='text' id='name' name='name' maxlength='50' size='51' value='"+name+"'>");
pw.println("<span id='nameErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("");
pw.println("<tr>");
pw.println("<td><b>Designation: </b></td>");
pw.println("<td>");
pw.println("<select id='designationCode' name='designationCode'>");
pw.println("<option value='-1'>&lt;Select designation&gt;</option>");
List<DesignationDTO> designations=(new DesignationDAO()).getAll();
for(DesignationDTO designation:designations)
{
if(designation.getCode()!=designationCode) pw.println("<option value='"+designation.getCode()+"'>"+designation.getTitle()+"</option>");
else pw.println("<option selected value='"+designation.getCode()+"'>"+designation.getTitle()+"</option>"); 
}
pw.println("</select>");
if(designationCodeExists) pw.println("&nbsp;<span id='designationCodeErrorSection' style='color:red'></span>");
else pw.println("&nbsp;<span  id='designationCodeErrorSection' style='color:red'>Select designation</span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Date of birth: </td>");
pw.println("<td><input type='date' id='dateOfBirth' name='dateOfBirth' value='"+simpleDateFormat.format(dateOfBirth)+"'>");	//format: 'yyyy-MM-dd'
pw.println("<span id='dateOfBirthErrorSection' style='color:red'></span></td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Gender: </td>");
pw.println("<td>");
if(gender.equals("M")) pw.println("<input checked type='radio' id='male' name='gender' value='M'>Male");
else pw.println("<input type='radio' id='male' name='gender' value='M'>Male");

pw.println("&nbsp;&nbsp;");
if(gender.equals("F")) pw.println("<input checked type='radio' id='female' name='gender' value='F'>Female");
else pw.println("<input type='radio' id='female' name='gender' value='F'>Female");
pw.println("<span id='genderErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Indian? : </td>");
pw.println("<td>");
if(isIndian.equals("Y")) pw.println("<input checked type='checkbox' id='isIndian' name='isIndian' value='Y'>");
else pw.println("<input type='checkbox' id='isIndian' name='isIndian' value='Y'>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Basic Salary: </td>");
pw.println("<td>");
pw.println("<input type='number' id='basicSalary' name='basicSalary' value='"+basicSalary.toPlainString()+"'>");
pw.println("<span id='basicSalaryErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("");
pw.println("<tr>");
pw.println("<td>PAN number: </td>");
pw.println("<td>");
if(!panNumberExists) 
{
pw.println("<input type='text' id='panNumber' name='panNumber' maxlength='15' size='16' value='"+panNumber+"'>");
pw.println("<span id='panNumberErrorSection' style='color:red'></span>");
}
else 
{
pw.println("<input type='text' autofocus id='panNumber' name='panNumber' maxlength='15' size='16' value='"+panNumber+"'>");
pw.println("<span id='panNumberErrorSection' style='color:red' >PAN Number already exists</span>");
}
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Aadhar card number: </td>");
pw.println("<td>");
if(!aadharCardNumberExists)
{
pw.println("<input type='text' id='aadharCardNumber' name='aadharCardNumber' maxlength='15' size='16' value='"+aadharCardNumber+"'>");
pw.println("<span id='aadharCardNumberErrorSection' style='color:red'></span>");
}
else 
{
pw.println("<input type='text' autofocus id='aadharCardNumber' name='aadharCardNumber' maxlength='15' size='16' value='"+aadharCardNumber+"'>");
pw.println("<span id='aadharCardNumberErrorSection' style='color:red'>Aadhar card number already exists</span>");
}
pw.println("</td>");
pw.println("</tr>");
pw.println("<td colspan='2'>");
pw.println("<button type='submit'>Add</button> &nbsp;&nbsp;");
pw.println("<button type='button' onclick='cancelAddition()'>Cancel</button>");
pw.println("</td>");
pw.println("</table>");
pw.println("</form>");
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
pw.println("<form id='cancelAdditionForm' action='/styleone/employeeView'>");
pw.println("</form>");
pw.println("</body>");
pw.println("</html>");
}
}catch(DAOException daoException)
{
pw.println("<!DOCTYPE HTML>");
pw.println("<html lang='en'>");
pw.println("<head>");
pw.println("<meta charset='utf-8'>");
pw.println("<title>HR-Nexus</title>");
pw.println("<script>");
pw.println("function validateDesignation(frm)");
pw.println("{");
pw.println("var firstInvalidComponent=null;");
pw.println("var valid=true;");
pw.println("var name=frm.name.value.trim();");
pw.println("var nameErrorSection=document.getElementById('nameErrorSection');");
pw.println("nameErrorSection.innerHTML='';");
pw.println("if(name.length==0)");
pw.println("{");
pw.println("nameErrorSection.innerHTML='Required';");
pw.println("firstInvalidComponent=frm.name;");
pw.println("valid=false;");
pw.println("}");
pw.println("var designationCode=frm.designationCode.value;");
pw.println("var designationCodeErrorSection=document.getElementById('designationCodeErrorSection');");
pw.println("designationCodeErrorSection.innerHTML='';");
pw.println("if(designationCode==-1)");
pw.println("{");
pw.println("designationCodeErrorSection.innerHTML=\"Select designation\";");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.designationCode;");
pw.println("valid=false;");
pw.println("}");
pw.println("var dateOfBirth=frm.dateOfBirth.value;");
pw.println("var dateOfBirthErrorSection=document.getElementById('dateOfBirthErrorSection');");
pw.println("dateOfBirthErrorSection.innerHTML='';");
pw.println("if(dateOfBirth.length==0)");
pw.println("{");
pw.println("dateOfBirthErrorSection.innerHTML='Select date of birth';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.dateOfBirth;");
pw.println("valid=false;");
pw.println("}");
pw.println("var gender=frm.gender;");
pw.println("var genderErrorSection=document.getElementById('genderErrorSection');");
pw.println("genderErrorSection.innerHTML='';");
pw.println("if(gender[0].checked==false && gender[0].checked==false)");
pw.println("{");
pw.println("genderErrorSection.innerHTML='Select gender';");
pw.println("valid=false;");
pw.println("}");
pw.println("var basicSalary=frm.basicSalary.value;");
pw.println("var basicSalaryErrorSection=document.getElementById('basicSalaryErrorSection');");
pw.println("basicSalaryErrorSection.innerHTML='';");
pw.println("if(basicSalary<=0)");
pw.println("{");
pw.println("basicSalaryErrorSection.innerHTML='Invalid basic salary';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.basicSalary;");
pw.println("valid=false;");
pw.println("}");
pw.println("var panNumber=frm.panNumber.value.trim();");
pw.println("var panNumberErrorSection=document.getElementById('panNumberErrorSection');");
pw.println("panNumberErrorSection.innerHTML='';");
pw.println("if(panNumber.length==0)");
pw.println("{");
pw.println("panNumberErrorSection.innerHTML='PAN number required';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.panNumber;");
pw.println("valid=false;");
pw.println("}");
pw.println("var aadharCardNumber=frm.aadharCardNumber.value.trim();");
pw.println("var aadharCardNumberErrorSection=document.getElementById('aadharCardNumberErrorSection');");
pw.println("aadharCardNumberErrorSection.innerHTML='';");
pw.println("if(aadharCardNumber.length==0)");
pw.println("{");
pw.println("aadharCardNumberErrorSection.innerHTML='Aadhar card number required';");
pw.println("if(firstInvalidComponent==null) firstInvalidComponent=frm.aadharCardNumber;");
pw.println("valid=false;");
pw.println("}");
pw.println("if(!valid) firstInvalidComponent.focus();");
pw.println("return valid;");
pw.println("}");
pw.println("function cancelAddition()");
pw.println("{");
pw.println("document.getElementById('cancelAdditionForm').submit();");
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
pw.println("<b>Designations</b><br>");
pw.println("<a href='/styleone/employeeView' style='float:left'>Employees</a><br><br>");
pw.println("<a href='/styleone/index.html'>Home</a>");
pw.println("</div>");
pw.println("<!-- left panel ends here -->");
pw.println("<!-- right panel start here -->");
pw.println("<div style='height:65vh;margin-left:110px;margin-right:5px;margin-bottom:5px;margin-top:5px;padding:5px;overflow:scroll;border:1px solid black'>");
pw.println("<b>Employee (Add Module)</b><br>");
pw.println("<span id='errorSection' style='color:red'>"+daoException.getMessage()+"</span>");
pw.println("<form method='post' action='/styleone/addEmployee' onsubmit='return validateDesignation(this)'>");
pw.println("<table>");
pw.println("<tr>");
pw.println("<td><b>Name: </b></td>");
pw.println("<td>");
pw.println("<input type='text' id='name' name='name' maxlength='50' size='51' value='"+name+"'>");
pw.println("<span id='nameErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("");
pw.println("<tr>");
pw.println("<td><b>Designation: </b></td>");
pw.println("<td>");
pw.println("<select id='designationCode' name='designationCode'>");
pw.println("<option value='-1'>&lt;Select designation&gt;</option>");
try
{
List<DesignationDTO> designations=(new DesignationDAO()).getAll();
for(DesignationDTO designation:designations)
{
if(designation.getCode()!=designationCode) pw.println("<option value='"+designation.getCode()+"'>"+designation.getTitle()+"</option>");
else pw.println("<option checked value='"+designation.getCode()+"'>"+designation.getTitle()+"</option>");
}
}catch(DAOException d)
{
//nothing
}
pw.println("</select>");
pw.println("&nbsp;<span id='designationCodeErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Date of birth: </td>");
pw.println("<td><input type='date' id='dateOfBirth' name='dateOfBirth' value='"+simpleDateFormat.format(dateOfBirth)+"'>");	//format: 'yyyy-MM-dd'
pw.println("<span id='dateOfBirthErrorSection' style='color:red'></span></td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Gender: </td>");
pw.println("<td>");
if(gender.equals("M"))
{
pw.println("<input checked type='radio' id='male' name='gender' value='M'>Male");
pw.println("&nbsp;&nbsp;");
pw.println("<input type='radio' id='female' name='gender' value='F'>Female");
}
else
{
pw.println("<input type='radio' id='male' name='gender' value='M'>Male");
pw.println("&nbsp;&nbsp;");
pw.println("<input checked type='radio' id='female' name='gender' value='F'>Female");
}
pw.println("<span id='genderErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Indian? : </td>");
pw.println("<td>");
if(isIndian.equals("Y")) pw.println("<input checked type='checkbox' id='isIndian' name='isIndian' value='Y'>");
else pw.println("<input type='checkbox' id='isIndian' name='isIndian' value='Y'>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Basic Salary: </td>");
pw.println("<td>");
pw.println("<input type='number' id='basicSalary' name='basicSalary' value='"+basicSalary.toPlainString()+"'>");
pw.println("<span id='basicSalaryErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("");
pw.println("<tr>");
pw.println("<td>PAN number: </td>");
pw.println("<td>");
pw.println("<input type='text' id='panNumber' name='panNumber' maxlength='15' size='16' value='"+panNumber+"'>");
pw.println("<span id='panNumberErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<tr>");
pw.println("<td>Aadhar card number: </td>");
pw.println("<td>");
pw.println("<input type='text' id='aadharCardNumber' name='aadharCardNumber' maxlength='15' size='16' value='"+aadharCardNumber+"'>");
pw.println("<span id='aadharCardNumberErrorSection' style='color:red'></span>");
pw.println("</td>");
pw.println("</tr>");
pw.println("<td colspan='2'>");
pw.println("<button type='submit'>Add</button> &nbsp;&nbsp;");
pw.println("<button type='button' onclick='cancelAddition()'>Cancel</button>");
pw.println("</td>");
pw.println("</table>");
pw.println("</form>");
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
pw.println("<form id='cancelAdditionForm' action='/styleone/employeeView'>");
pw.println("</form>");
pw.println("</body>");
pw.println("</html>");
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

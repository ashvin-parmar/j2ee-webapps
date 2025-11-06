package com.ashvin.hr.nexus.servlets;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*;
import java.io.*;

public class AddDesignation extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}catch(Exception exception)
{

}
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
return;
}
PrintWriter pw=response.getWriter();
response.setContentType("application/json");

BufferedReader bufferedReader=request.getReader();
StringBuilder sb=new StringBuilder();
String b;
Gson gson=new Gson();
while(true)
{
b=bufferedReader.readLine();
if(b==null) break;
sb.append(b);
}
DesignationBean designationBean;
designationBean=(DesignationBean)gson.fromJson(sb.toString(),DesignationBean.class);
DesignationDAO designationDAO;
DesignationDTO designationDTO;
try
{
designationDTO=new DesignationDTO();
designationDTO.setTitle(designationBean.getTitle());
designationDAO=new DesignationDAO();
designationDAO.add(designationDTO);
designationBean.setCode(designationDTO.getCode());
MessageBean message=new MessageBean();
message.setHeading("Designation (Add Module)");
message.setMessage("Designation "+designationBean.getTitle()+" added, add more?");
message.setHasToGenerateButtons(true);
message.setHasToGenerateTwoButtons(true);
message.setButtonOneText("Yes");
message.setButtonOneAction("AddDesignation.jsp");
message.setButtonTwoText("No");
message.setButtonTwoAction("Designations.jsp");
/*
pw.print("{\"designation\":");
pw.flush();
pw.print(gson.toJson(designationBean)+",");
pw.flush();
pw.print("\"message\":");
pw.flush();
pw.print(gson.toJson(message)+"}");
pw.flush();
*/
String jsonString=gson.toJson(message);
pw.print(jsonString);
pw.flush();

//request.setAttribute("messageBean",message);
//RequestDispatcher requestDispatcher;
//requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
//requestDispatcher.forward(request,response);
}catch(DAOException daoException)
{
ErrorBean error=new ErrorBean();
error.setError(daoException.getMessage());
pw.print(gson.toJson(error));
pw.flush();
//RequestDispatcher requestDispatcher;
//requestDispatcher=request.getRequestDispatcher("/AddDesignationForm.jsp");
//requestDispatcher.forward(request,response);
}
}catch(Exception exception)
{
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{

}
}
}
}

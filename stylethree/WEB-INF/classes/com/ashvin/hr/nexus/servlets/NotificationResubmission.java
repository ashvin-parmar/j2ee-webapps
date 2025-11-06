package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.beans.MessageBean;
import com.google.gson.*;
import java.io.*;

public class NotificationResubmission extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
doPost(request,response);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
PrintWriter pw;
try
{
HttpSession hs=request.getSession();
if(hs.getAttribute("username")==null)
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
return;
}
pw=response.getWriter();
response.setContentType("application/json");
MessageBean message=new MessageBean();
message.setHeading("Notification");
message.setMessage("Forms are not to be resubmitted.");
message.setHasToGenerateButtons(true);
message.setHasToGenerateTwoButtons(false);
message.setButtonOneText("OK");
message.setButtonOneAction("index.jsp");
//request.setAttribute("message",messageBean);
Gson gson=new Gson();
pw.print(gson.toJson(message));
pw.flush();
}catch(Exception exception)
{
System.out.println(exception.getMessage());
try
{
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}catch(Exception e)
{
}
}
}
}

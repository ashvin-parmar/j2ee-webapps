package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.beans.MessageBean;
public class NotifyFormResubmission extends HttpServlet
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
return;
}
MessageBean messageBean=new MessageBean();
messageBean.setHeading("Notification");
messageBean.setMessage("Forms are not to be resubmitted.");
messageBean.setHasToGenerateButtons(true);
messageBean.setHasToGenerateTwoButtons(false);
messageBean.setButtonOneText("OK");
messageBean.setButtonOneAction("index.jsp");
request.setAttribute("messageBean",messageBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/Notification.jsp");
requestDispatcher.forward(request,response);
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

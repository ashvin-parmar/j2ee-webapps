package com.ashvin.hr.nexus.servlets;

import javax.servlet.*;
import javax.servlet.http.*;

public class Logout extends HttpServlet
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
hs.removeAttribute("username");
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
}catch(Exception exception)
{
System.out.println(exception);		//removed after testing
}
}
}

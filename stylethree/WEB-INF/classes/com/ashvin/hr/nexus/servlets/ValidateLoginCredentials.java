package com.ashvin.hr.nexus.servlets;

import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.gson.*;
import java.io.*;

public class ValidateLoginCredentials extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
try
{

}catch(Exception exception)
{
try
{
response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
}catch(Exception e)
{

}
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
HttpSession httpSession=request.getSession();
String username=(String)httpSession.getAttribute("username");
response.setContentType("application/json");
PrintWriter pw=response.getWriter();
Gson gson=new Gson();
String jsonUsername=gson.toJson(username);
pw.print(jsonUsername);
pw.flush();
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

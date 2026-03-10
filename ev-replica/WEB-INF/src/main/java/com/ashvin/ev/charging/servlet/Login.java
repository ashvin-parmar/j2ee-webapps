package com.ashvin.ev.charging.servlet;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.ev.charging.dl.*;
import java.io.*;

public class Login extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
PrintWriter pw=null;
try
{
pw=response.getWriter();
response.setContentType("application/json");
String username=request.getParameter("username");
String password=request.getParameter("password");
//System.out.println(username+", "+password);
try
{
AdministratorDTO administratorDTO;
administratorDTO=(new AdministratorDAO()).getByUsername(username);
if(administratorDTO!=null && password!=null && password.equals(administratorDTO.getPassword()))
{
HttpSession hs=request.getSession();
hs.setAttribute("username",username);
pw.print("{\"message\":\"Forwarded to index.jsp\"}");
pw.flush();
}
else
{
String str="{\"error\":\"Invalid username/password\"}";
pw.print(str);
pw.flush();
}
}catch(DAOException daoException)
{
pw.print("{\"error\":\"Invalid username/password\"}");
pw.flush();
return;
}
}catch(Exception exception)
{
System.out.println(exception);	//remove after testing
try
{
pw.print("{\"error\":\"Invalid username/password\"}");
pw.flush();
}catch(Exception e)
{
// do nothing
}
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{

}
}

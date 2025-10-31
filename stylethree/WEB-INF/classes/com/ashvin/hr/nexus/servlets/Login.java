package com.ashvin.hr.nexus.servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.beans.*;
import com.ashvin.hr.nexus.dl.*;
import com.google.gson.*;
import java.io.*;

public class Login extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
PrintWriter pw=null;
try
{
pw=response.getWriter();
response.setContentType("application/json");
BufferedReader br=request.getReader();
StringBuilder sb=new StringBuilder();
String d;
while(true)
{
d=br.readLine();
if(d==null) break;
sb.append(d);
}
String data=sb.toString();
Gson gson=new Gson();
AdministratorDTO administratorDTOFromRequest=gson.fromJson(data,AdministratorDTO.class);
if(administratorDTOFromRequest==null) 
{
pw.print("{\"error\":\"Invalid username/password\"}");
pw.flush();
return;
}
String username=administratorDTOFromRequest.getUsername();
String password=administratorDTOFromRequest.getPassword();
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
//RequestDispatcher requestDispatcher=request.getRequestDispatcher("/index.jsp");
//requestDispatcher.forward(request,response);
}
else
{
String str="{\"error\":\"Invalid username/password\"}";
//ErrorBean errorBean=new ErrorBean();
//errorBean.setError("Invalid username/password");
//request.setAttribute("errorBean",errorBean);
//RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
//requestDispatcher.forward(request,response);
pw.print(str);
pw.flush();
}
}catch(DAOException daoException)
{
//ErrorBean errorBean=new ErrorBean();
//errorBean.setError(daoException.getMessage());
//request.setAttribute("errorBean",errorBean);
//RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
//requestDispatcher.forward(request,response);
pw.print("{\"error\":\"Invalid username/password\"}");
pw.flush();
return;
}
}catch(Exception exception)
{
System.out.println(exception);	//remove after testing
try
{
//RequestDispatcher requestDispatcher=request.getRequestDispatcher("/ErrorPage.jsp");
//requestDispatcher.forward(request,response);
pw.print("{\"error\":\"Invalid username/password\"}");
pw.flush();
}catch(Exception e)
{
// do nothing
}
}
}
}

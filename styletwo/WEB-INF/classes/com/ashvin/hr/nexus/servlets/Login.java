package com.ashvin.hr.nexus.servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.hr.nexus.beans.*;
import com.ashvin.hr.nexus.dl.*;

public class Login extends HttpServlet
{
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
try
{
AdministratorBean administratorBean=(AdministratorBean)request.getAttribute("administratorBean");
if(administratorBean!=null)
{
String username=administratorBean.getUsername();
String password=administratorBean.getPassword();
try
{
AdministratorDTO administratorDTO;
administratorDTO=(new AdministratorDAO()).getByUsername(username);
if(administratorDTO!=null && password!=null)
{
String pass=administratorDTO.getPassword();
if(pass.equals(password))
{
HttpSession hs=request.getSession();
hs.setAttribute("username",username);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/index.jsp");
requestDispatcher.forward(request,response);
}
}
}catch(DAOException daoException)
{
ErrorBean errorBean=new ErrorBean();
errorBean.setError(daoException.getMessage());
request.setAttribute("errorBean",errorBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
}
}
ErrorBean errorBean=new ErrorBean();
errorBean.setError("Invalid username/password");
request.setAttribute("errorBean",errorBean);
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/LoginPage.jsp");
requestDispatcher.forward(request,response);
}catch(Exception exception)
{
System.out.println(exception);	//remove after testing
try
{
RequestDispatcher requestDispatcher=request.getRequestDispatcher("/ErrorPage.jsp");
requestDispatcher.forward(request,response);
}catch(Exception e)
{
// do nothing
}
}
}
}

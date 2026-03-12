package com.thinking.machines;

import javax.servlet.*;
import javax.servlet.http.*;

public class pqr extends HttpServlet
{
public void init()
{
ServletContext sc=getServletContext();
System.out.println("-----------pqr Country servlet data loaded -------");
sc.setAttribute("country1","Indore");
sc.setAttribute("country2","Dewas");
sc.setAttribute("country3","Delhi");
sc.setAttribute("country4","Satna");

}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{

}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
}
}

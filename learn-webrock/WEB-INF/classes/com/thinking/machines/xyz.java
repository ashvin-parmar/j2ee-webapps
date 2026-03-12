package com.thinking.machines;

import javax.servlet.*;
import javax.servlet.http.*;

public class xyz extends HttpServlet
{
public void init()
{
ServletContext sc=getServletContext();
System.out.println("------------------City servlet data loaded-------------");
sc.setAttribute("city1","Indore");
sc.setAttribute("city2","Dewas");
sc.setAttribute("city3","Delhi");
sc.setAttribute("city4","Satna");

}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{

}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{

}
}

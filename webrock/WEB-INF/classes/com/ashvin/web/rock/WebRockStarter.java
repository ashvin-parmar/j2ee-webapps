package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;

public class WebRockStarter extends HttpServlet
{
public void init()
{
ServletContext sc=getServletContext();
String servicePackagePrefix=(String)sc.getInitParameter("SERVICE_PACKAGE_PREFIX");
System.out.println(servicePackagePrefix);



}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{

}
public void doPost(HttpServletRequest requese,HttpServletResponse response)
{

}
}

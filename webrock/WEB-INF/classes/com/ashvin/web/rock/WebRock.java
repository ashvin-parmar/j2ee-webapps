package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.web.rock.pojo.*;
import com.ashvin.web.rock.model.*;

public class WebRock extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)  //default method
{
System.out.println(request.getRequestURI());
String requestURI=request.getRequestURI();
String siteName=getServletContext().getInitParameter("SITE_NAME");
String fullPathToService=requestURI.substring(siteName.length()+1);
System.out.println(fullPathToService);
Service service=WebRockModel.getWebRockModel().getPathService(fullPathToService);
System.out.println(service);
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
String requestURI=request.getRequestURI();

}
}

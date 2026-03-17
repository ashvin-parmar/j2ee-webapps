package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;

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
System.out.println("Service: "+service);
if(service==null)
{
System.out.println("Null Area");
try
{
response.sendError(HttpServletResponse.SC_NOT_FOUND);
}catch(IOException ioException)
{
System.out.println("Some problem: "+ioException);
}
}
else
{
System.out.println("Not null area");
System.out.println("Service Path: "+service.getPath());
Class serviceClass=service.getServiceClass();
Method serviceMethod=service.getServiceMethod();
Object serviceClassObject;
Object result;
Class returnType;
String jsonString="ABCD";
Object[] parametersValue=null;
try
{
response.setContentType("application/json");
PrintWriter pw=response.getWriter();

com.google.gson.Gson g1=new com.google.gson.Gson();   //Now Working

try
{
serviceClassObject=serviceClass.newInstance();
returnType=serviceMethod.getReturnType();
System.out.println(returnType.getName());
result=serviceMethod.invoke(serviceClassObject,parametersValue);
jsonString=g1.toJson(result);
System.out.println(jsonString);
}catch(Exception exception)
{
System.out.println(exception);
}
pw.println(jsonString);
pw.flush();

}catch(IOException ioException)
{
System.out.println(ioException.getMessage());
}
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
String requestURI=request.getRequestURI();

}
}

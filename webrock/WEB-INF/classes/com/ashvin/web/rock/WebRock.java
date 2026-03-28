package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;

import com.ashvin.web.rock.pojo.*;
import com.ashvin.web.rock.model.*;

public class WebRock extends HttpServlet
{
private WebRockModel webRockModel;
public WebRock()
{
webRockModel=WebRockModel.getWebRockModel();
}
private void doIt(HttpServletRequest request,HttpServletResponse response,String type)
{
//System.out.println(request.getRequestURI());
String requestURI=request.getRequestURI();
System.out.println("URL: "+request.getRequestURL());
String siteName=getServletContext().getInitParameter("SITE_NAME");
String fullPathToService=requestURI.substring(siteName.length()+1);
System.out.println("Full service path: "+fullPathToService);
//System.out.println("Real path: "+getServletContext().getRealPath("/"));
//System.out.println(fullPathToService.substring(0,fullPathToService.lastIndexOf('/')));
String resourcePath=fullPathToService.substring(0,fullPathToService.lastIndexOf('/'));
Service service=webRockModel.getPathService(fullPathToService,type);
if(service==null)
{
System.out.println("No Service");
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
System.out.println("Service Path: "+service.getPath());
Class serviceClass=service.getServiceClass();
Method serviceMethod=service.getServiceMethod();
String forwardTo=service.getForwardTo();
boolean injectApplicationScope=service.getInjectApplicationScope();
boolean injectSessionScope=service.getInjectSessionScope();
boolean injectRequestScope=service.getInjectRequestScope();
boolean injectApplicationDirectory=service.getInjectApplicationDirectory();
Object serviceClassObject;
Object result;
Class returnType;
String jsonString="";
com.google.gson.Gson g1=new com.google.gson.Gson();   //Now Working
Object[] parametersValue=null;
try
{
serviceClassObject=serviceClass.newInstance();
if(injectApplicationScope)
{
Method method=serviceClass.getMethod("setApplicationScope",ApplicationScope.class);
if(method!=null)
{
try
{
//System.out.println("Setter method available");
ApplicationScope applicationScope=new ApplicationScope();
applicationScope.setServletContext(request.getServletContext());
method.invoke(serviceClassObject,applicationScope);
}catch(Exception e)
{
System.out.println("Problem : "+e.getMessage());
}
}
/*else
{
System.out.println("Method not available");
}*/
}
if(injectSessionScope)
{
Method method=serviceClass.getMethod("setSessionScope",SessionScope.class);
if(method!=null)
{
try
{
//System.out.println("Setter method available");
SessionScope sessionScope=new SessionScope();
sessionScope.setHttpSession(request.getSession());
Object[] ssParameters=new Object[1];
ssParameters[0]=sessionScope;
method.invoke(serviceClassObject,ssParameters);
}catch(Exception e)
{
System.out.println("Problem : "+e.getMessage());
}
}
/*else
{
System.out.println("Method not available");
}*/
}
if(injectRequestScope)
{
Method method=serviceClass.getMethod("setRequestScope",RequestScope.class);
if(method!=null)
{
try
{
//System.out.println("Setter method available");
RequestScope requestScope=new RequestScope();
requestScope.setHttpServletRequest(request);
method.invoke(serviceClassObject,requestScope);
}catch(Exception e)//has been ignored
{
System.out.println("Problem : "+e.getMessage());
}
}
/*else
{
System.out.println("Method not available");
}*/
}
if(injectApplicationDirectory)
{
Method method=serviceClass.getMethod("setApplicationDirectory",ApplicationDirectory.class);
if(method!=null)
{
try
{
File file=new File(getServletContext().getRealPath("/"));
//System.out.println("Path: "+file.getName()+" exists: "+file.exists());
ApplicationDirectory applicationDirectory=new ApplicationDirectory(file);
method.invoke(serviceClassObject,applicationDirectory);
}catch(Exception exception)//has been ignored
{
System.out.println("problem: "+exception.getMessage());
}
}
}

returnType=serviceMethod.getReturnType();
//System.out.println(returnType.getName());
if(returnType.getName().equals("void"))
{
serviceMethod.invoke(serviceClassObject,parametersValue);
}
else
{
result=serviceMethod.invoke(serviceClassObject,parametersValue);
jsonString=g1.toJson(result);
//System.out.println(jsonString);
}
}catch(Exception exception)
{
System.out.println("Exception: "+exception);
}
try
{
if(forwardTo!=null)
{
String forwardToPath=resourcePath+forwardTo;
if(webRockModel.getPathService(forwardToPath,type)!=null)
{
try
{
getServletContext().getRequestDispatcher(forwardToPath).forward(request,response);
}catch(ServletException se)
{
System.out.println("SE: "+se);
}
}
else
{
forwardToPath=forwardTo; //donedone
System.out.println(forwardToPath);
response.sendRedirect(forwardToPath);
}
}
else
{
response.setContentType("application/json");
PrintWriter pw=response.getWriter();
pw.println(jsonString);
pw.flush();
}
}catch(IOException ioException)
{
System.out.println("IOException: "+ioException.getMessage());
}
}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)  //default method
{
doIt(request,response,"GET");
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doIt(request,response,"POST");
}
}

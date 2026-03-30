package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

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
//System.out.println("Full service path: "+fullPathToService);
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

List<AutoWiredField> autoWiredFields=service.getAutoWiredFields();
List<RequestParameterOnMethod> requestParametersOnMethod=service.getRequestParametersOnMethod();

String name;
Object nameResult;
Field field;
String autoWiredSetMethodName;
Method autoWiredSetMethod;

Class parameterType;
String parameterValue;
int i=0;

Object serviceClassObject;
Object result;
Class returnType;
String jsonString="";
com.google.gson.Gson g1=new com.google.gson.Gson();   //Now Working
Object[] parametersValue=null;
try
{
serviceClassObject=serviceClass.newInstance();

//AutoWired feature implementation starts here
for(AutoWiredField autoWiredField:autoWiredFields)
{
nameResult=null;
name=autoWiredField.getName();
field=autoWiredField.getField();
//System.out.println(field.getName());
//System.out.println(field.getType());
autoWiredSetMethodName=field.getName();
autoWiredSetMethodName=autoWiredSetMethodName.substring(0,1).toUpperCase()+autoWiredSetMethodName.substring(1);
autoWiredSetMethodName="set"+autoWiredSetMethodName;
//System.out.println("Setter method name: "+autoWiredSetMethodName);
autoWiredSetMethod=serviceClass.getMethod(autoWiredSetMethodName,field.getType());
if(autoWiredSetMethod==null)
{
//System.out.println(autoWiredSetMethodName+" Method not found");
continue;
}

nameResult=request.getAttribute(name);
if(nameResult==null)
{
nameResult=request.getSession().getAttribute(name);
if(nameResult==null)
{
nameResult=request.getServletContext().getAttribute(name);
}
}
/*
if(nameResult==null)
{
field.set(serviceClassObject,nameResult);
}
else
{
System.out.println(field.getType().isInstance(nameResult));
System.out.println(field.getType().equals(nameResult.getClass()));
if(field.getType().isInstance(nameResult))
{
field.set(serviceClassObject,nameResult);
}
}
*/
if(nameResult!=null)
{
//System.out.println(name+" found");
//System.out.println("Is instanceof correct: "+field.getType().isInstance(nameResult));
//System.out.println("Is instanceof correct: "+field.getType().equals(nameResult.getClass()));
if(field.getType().isInstance(nameResult))
{
//System.out.println("Yes, instanceof "+field.getType());
autoWiredSetMethod.invoke(serviceClassObject,nameResult);
}
else
{
nameResult=null;
autoWiredSetMethod.invoke(serviceClassObject,nameResult);
}
}
else
{
//System.out.println(name+" not found");
autoWiredSetMethod.invoke(serviceClassObject,nameResult);   //null set
}
}
//AutoWired featuer implementatios ends here

// Request Parameter feature start here
parametersValue=new Object[requestParametersOnMethod.size()];
i=0;
for(RequestParameterOnMethod requestParameterOnMethod:requestParametersOnMethod)
{
if(requestParameterOnMethod!=null)
{
name=requestParameterOnMethod.getName();
parameterType=requestParameterOnMethod.getParameterType();
if(!name.isBlank() && parameterType!=null)
{
parameterValue=request.getParameter(name);
if(parameterValue==null) parameterValue="";
//System.out.println("Type: "+parameterType+" value: "+parameterValue);
nameResult=null;
if(parameterType.equals(Long.TYPE) || parameterType.equals(java.lang.Long.class))
{
try
{
nameResult=Long.parseLong(parameterValue);
}catch(NumberFormatException nfe)
{
nameResult=(long)0;
}
}
if(parameterType.equals(Integer.TYPE) || parameterType.equals(java.lang.Integer.class))
{
try
{
nameResult=Integer.parseInt(parameterValue);
}catch(NumberFormatException nfe)
{
nameResult=(int)0;
}
}
if(parameterType.equals(Short.TYPE) || parameterType.equals(java.lang.Short.class))
{
try
{
nameResult=Short.parseShort(parameterValue);
}catch(NumberFormatException nfe)
{
nameResult=(short)0;
}
}
if(parameterType.equals(Byte.TYPE) || parameterType.equals(java.lang.Byte.class))
{
try
{
nameResult=Byte.parseByte(parameterValue);
}catch(NumberFormatException nfe)
{
nameResult=(byte)0;
}
}
if(parameterType.equals(Double.TYPE) || parameterType.equals(java.lang.Double.class))
{
try
{
nameResult=Double.parseDouble(parameterValue);
}catch(NumberFormatException nfe)
{
nameResult=(double)0.0;
}
}
if(parameterType.equals(Float.TYPE) || parameterType.equals(java.lang.Float.class))
{
try
{
nameResult=Float.parseFloat(parameterValue);
}catch(NumberFormatException nfe)
{
nameResult=(float)0.0;
}
}
if(parameterType.equals(Boolean.TYPE) || parameterType.equals(java.lang.Boolean.class))
{
if(parameterValue.isBlank()==false) nameResult=Boolean.parseBoolean(parameterValue);
else nameResult=(boolean)false;
}
if(parameterType.equals(Character.TYPE) || parameterType.equals(java.lang.Character.class))
{
if(parameterValue.isBlank()==false) nameResult=parameterValue.charAt(0);
else nameResult=(char)' ';
}
if(parameterType.equals(java.lang.String.class))
{
System.out.println("String type parameter");
nameResult=parameterValue;
}
parametersValue[i++]=nameResult;
continue;
}
}
nameResult=null;
parametersValue[i++]=nameResult;
}
//Request parameter feautre ends here

//Injection feature implementation starts here
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
// Injection feature implementation ends here

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
//Over here, checkout for forwardService method parameter's type which needs to being matched with this service method returnType type. If that so, then we are proceed for the forwarding request, otherwise we are sending ServiceException with the message. Also sendError(500 error code) to client end.  
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
//System.out.println(forwardToPath);
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

package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import com.ashvin.web.rock.pojo.*;
import com.ashvin.web.rock.model.*;
import com.ashvin.web.rock.utils.*;
import com.ashvin.web.rock.exceptions.*;

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
List<RequestParameterOnField> injectRequestParameterOnFields=service.getInjectRequestParameterFields();
List<RequestParameterOnMethod> requestParametersOnMethod=service.getRequestParametersOnMethod();

String name;
Object nameResult;
Field field;
String autoWiredSetMethodName;
Method autoWiredSetMethod;
String injectRequestParameterSetMethodName;
Method injectRequestParameterSetMethod;


ApplicationScope applicationScope=null;
SessionScope sessionScope=null;
RequestScope requestScope=null;
ApplicationDirectory applicationDirectory=null;

Class parameterType=null;
Class parameterTypeNP=null;    //parameterType Non Primitive [from Boxer class's]
String parameterValue;
int i=0;

Object serviceClassObject;
Object result;
Class returnType;
String jsonString="";
Object[] parametersValue=null;
try
{
serviceClassObject=serviceClass.newInstance();

//AutoWired feature implementation starts here
for(AutoWiredField autoWiredField:autoWiredFields)
{
nameResult=null;
name=autoWiredField.getName();    //this name is form annotation
field=autoWiredField.getField();
//System.out.println(field.getName());
//System.out.println(field.getType());
autoWiredSetMethodName=field.getName();   //this name is from field itself
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
parameterType=field.getType();
parameterTypeNP=WebRockUtils.wrap(parameterType);
if(nameResult!=null)
{
//System.out.println(name+" found");

if(parameterTypeNP.isInstance(nameResult))
{
//System.out.println("Yes, instanceof "+field.getType());
autoWiredSetMethod.invoke(serviceClassObject,nameResult);
}
else
{
throw new ServiceException("Invalid arguments of type "+nameResult.getClass().getName()+" passed to method ["+autoWiredSetMethod.getName()+"] against @AutoWired annotation, Required "+parameterType.getName());
//nameResult=null;      //NOT TO DONE THIS, because it may be primitive at user end
//autoWiredSetMethod.invoke(serviceClassObject,nameResult);
}
}
else
{
//System.out.println(name+" not found"); //means want to set null, correct
nameResult=WebRockUtils.parseTo(parameterTypeNP,null);
autoWiredSetMethod.invoke(serviceClassObject,nameResult);   //null set
}
}
//AutoWired featuer implementatios ends here

//InjectRequestParameter feature implementation starts here
for(RequestParameterOnField requestParameterOnField:injectRequestParameterOnFields)
{
nameResult=null;
name=requestParameterOnField.getName();
field=requestParameterOnField.getField();
//System.out.println(field.getName());
//System.out.println(field.getType());
injectRequestParameterSetMethodName=field.getName();
injectRequestParameterSetMethodName=injectRequestParameterSetMethodName.substring(0,1).toUpperCase()+injectRequestParameterSetMethodName.substring(1);
injectRequestParameterSetMethodName="set"+injectRequestParameterSetMethodName;
injectRequestParameterSetMethod=serviceClass.getMethod(injectRequestParameterSetMethodName,field.getType());
if(injectRequestParameterSetMethod==null)
{
continue;
}
parameterValue=request.getParameter(name);
parameterType=field.getType();
nameResult=WebRockUtils.parseTo(parameterType,parameterValue);
parameterTypeNP=WebRockUtils.wrap(parameterType);
//System.out.println(parameterTypeNP.isInstance(nameResult));
//if(nameResult!=null) System.out.println(parameterTypeNP.equals(nameResult.getClass())+", "+nameResult.getClass());
if(nameResult!=null)
{
if(parameterTypeNP.isInstance(nameResult))
{
injectRequestParameterSetMethod.invoke(serviceClassObject,nameResult);
}
else
{
throw new ServiceException("Invalid arguments of type "+nameResult.getClass().getName()+" passed to method ["+injectRequestParameterSetMethod.getName()+"] against @InjectRequestParameter annotation, Required "+parameterType.getName());
//nameResult=null;
//injectRequestParameterSetMethod.invoke(serviceClassObject,nameResult);
}
}
else
{
//System.out.println(name+" not found");
//nameResult=WebRockUtils.parseTo(parameterTypeNP,null);  //over here, not needed because already outside of block praseTo called. 
injectRequestParameterSetMethod.invoke(serviceClassObject,nameResult);   //null set
}
}
//InjectRequestParmaeter featuer implementatios ends here

// Request Parameter feature start here
parametersValue=new Object[requestParametersOnMethod.size()];
i=0;
int count=0;
int index=-1;
for(RequestParameterOnMethod requestParameterOnMethod:requestParametersOnMethod)
{
name=requestParameterOnMethod.getName();
parameterType=requestParameterOnMethod.getParameterType();

if(name==null)
{
nameResult=null;
if(parameterType.equals(ApplicationScope.class)) 
{
if(applicationScope==null)
{
applicationScope=new ApplicationScope();
applicationScope.setServletContext(request.getServletContext());
}
nameResult=applicationScope;
}
if(parameterType.equals(SessionScope.class))
{
if(sessionScope==null)
{
sessionScope=new SessionScope();
sessionScope.setHttpSession(request.getSession());
}
nameResult=sessionScope;
}
else if(parameterType.equals(RequestScope.class))
{
if(requestScope==null)
{
requestScope=new RequestScope();
requestScope.setHttpServletRequest(request);
}
nameResult=requestScope;
}
else if(parameterType.equals(ApplicationDirectory.class))
{
if(applicationDirectory==null)
{
File file=new File(getServletContext().getRealPath("/"));
applicationDirectory=new ApplicationDirectory(file);
}
nameResult=applicationDirectory;
}
if(nameResult!=null) 
{
parametersValue[i++]=nameResult;
continue;
}
name="";
count++;
index=i;
}
parameterValue=request.getParameter(name);
nameResult=null;
if(parameterValue==null) parameterValue="";
//System.out.println("Type: "+parameterType+" value: "+parameterValue);
nameResult=WebRockUtils.parseTo(parameterType,parameterValue);
parametersValue[i++]=nameResult;      //There are no problem arrived, because invoke method manages wrap functionality from Primitive to Boxer classes
}
if(count==1)
{
RequestParameterOnMethod requestParameterOnMethod=requestParametersOnMethod.get(index);
nameResult=null;
BufferedReader br=request.getReader();
StringBuffer sb=new StringBuffer();
String d;
while(true)
{
d=br.readLine();
if(d==null) break;
sb.append(d);
}
parameterValue=sb.toString();
parameterType=requestParameterOnMethod.getParameterType();
if(parameterValue==null || parameterValue.isBlank()) parameterValue="{}";
nameResult=WebRockUtils.parseTo(parameterType,parameterValue,"JSON");
parametersValue[index]=nameResult;
}
else if(count>1)
{
System.out.println("Count: "+count+", index: "+index);
throw new ServiceException("Invalid argument passing for service: "+service.getPath());
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
if(applicationScope==null)
{
applicationScope=new ApplicationScope();
applicationScope.setServletContext(request.getServletContext());
}
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
if(sessionScope==null)
{
sessionScope=new SessionScope();
sessionScope.setHttpSession(request.getSession());
}
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
if(requestScope==null)
{
requestScope=new RequestScope();
requestScope.setHttpServletRequest(request);
}
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
if(applicationDirectory==null)
{
File file=new File(getServletContext().getRealPath("/"));
//System.out.println("Path: "+file.getName()+" exists: "+file.exists());
applicationDirectory=new ApplicationDirectory(file);
}
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
jsonString=WebRockUtils.toJSON(result);
//System.out.println(jsonString);
}
}catch(ServiceException se)
{
try
{
System.out.println("message: "+se.getMessage());
response.sendError(HttpServletResponse.SC_NOT_FOUND,se.getMessage());
}catch(Exception e)
{

}
}catch(Exception exception)
{
try
{
System.out.println(exception.getMessage());
response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
System.out.println("Exception: "+exception);
}catch(Exception e)
{

}
}
try
{
if(forwardTo!=null)
{
System.out.println("forwardTo: "+forwardTo);
String forwardToPath=resourcePath+forwardTo;
if(webRockModel.getPathService(forwardToPath,type)!=null)
{
//Over here, checkout for forwardService method parameter's type which needs to being matched with this service method returnType type. If that so, then we are proceed for the forwarding request, otherwise we are sending ServiceException with the message. Also sendError(500 error code) to client end.
/*
---------APPROUCH 1------------
save isForwardedRequest=true in forwardService object. then on arriving request, if isForwardedRequest is true then process it with the returnObject as parameter of this service if both are of same type or class. and again set the isForwardedRequest inside the block with false for current service.
otherwise, process as normal request. 
*/
/*
---------APPROUCH 2------------
set in requestScope against some key with unique id. and set that unique id in forwardService. 
On arriving the request, check for any key in service object, if found then process to fetch the data against that key from requestScope and invoke method, and set the key to null. 
otherwise process as normal request.
*/
//All below part are remain same. 
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

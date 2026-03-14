package com.ashvin.web.rock.services;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.student.dto.*;
import com.ashvin.student.*;
import java.util.*;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.*;

public class RockService extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
StudentManager studentManager=null;
Object obj=null;
Method method=null;
String className="com.ashvin.student.StudentManager";
String methodName="getStudents";
Class[] parametersType=null;
Object[] parametersValue=null;

ServletResponse servletResponse;
String responseString;
Object result;
PrintWriter pw;
Gson gson=new Gson();
try
{
response.setContentType("application/json");
pw=response.getWriter();
Class<?> c=Class.forName(className);
obj=c.newInstance();
studentManager=new StudentManager();
servletResponse=new ServletResponse();
try
{
method=c.getMethod(methodName,parametersType);
//result=studentManager.getStudents();
result=method.invoke(obj,parametersValue);
servletResponse.setIsSuccess(true);
servletResponse.setException(null);
servletResponse.setResult(result);
if(false)
{
throw new ManagerException("abcd");
}
}catch(ManagerException me)
{
servletResponse.setIsSuccess(false);
servletResponse.setException(me);
servletResponse.setResult(null);
}
responseString=gson.toJson(servletResponse);
pw.println(responseString);
pw.flush();
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
StringBuffer sb;
BufferedReader br;
Gson gson=new Gson();
String s;

Student student;
StudentManager studentManager;
ServletResponse servletResponse;
String responseString;
PrintWriter pw;

Object obj=null;
Method method=null;
String className="com.ashvin.student.StudentManager";
String methodName="addStudent";
Class[] parametersType={Student.class};
Object[] parametersValue=new Object[1];

try
{
response.setContentType("application/json");
pw=response.getWriter();

br=request.getReader();
sb=new StringBuffer();
while(true)
{
s=br.readLine();
if(s==null) break;
sb.append(s);
}
student=gson.fromJson(sb.toString(),Student.class);

Class<?> c=Class.forName(className);
obj=c.newInstance();
parametersValue[0]=student;
//studentManager=new StudentManager();
servletResponse=new ServletResponse();
try
{
//studentManager.addStudent(student);
method=c.getMethod(methodName,parametersType);
method.invoke(obj,parametersValue);
servletResponse.setResult(null);
servletResponse.setException(null);
servletResponse.setIsSuccess(true);
if(false)
{
throw new ManagerException("nothing");
}
}catch(ManagerException me)
{
servletResponse.setResult(null);
servletResponse.setException(me);
servletResponse.setIsSuccess(false);
}catch(InvocationTargetException ite)
{
Throwable me=ite.getCause();
servletResponse.setResult(null);
servletResponse.setException(me);
servletResponse.setIsSuccess(false);
}
responseString=gson.toJson(servletResponse);
pw.println(responseString);
pw.flush();
}catch(Exception exception)
{
System.out.println(exception.getMessage());
}
}
}


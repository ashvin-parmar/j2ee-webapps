package com.ashvin.web.rock.services;

import javax.servlet.*;
import javax.servlet.http.*;
import com.ashvin.student.dto.*;
import com.ashvin.student.*;
import java.util.*;
import com.google.gson.*;
import java.io.*;

public class RockService extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
StudentManager studentManager=null;
ServletResponse servletResponse;
String responseString;
Object result;
PrintWriter pw;
Gson gson=new Gson();
try
{
response.setContentType("application/json");
pw=response.getWriter();
studentManager=new StudentManager();
servletResponse=new ServletResponse();
try
{
result=studentManager.getStudents();
servletResponse.setIsSuccess(true);
servletResponse.setException(null);
servletResponse.setResult(result);
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

studentManager=new StudentManager();
servletResponse=new ServletResponse();
try
{
studentManager.addStudent(student);
servletResponse.setResult(null);
servletResponse.setException(null);
servletResponse.setIsSuccess(true);
}catch(ManagerException me)
{
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


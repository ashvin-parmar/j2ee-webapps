package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class JSFileLoading extends HttpServlet
{
public void doGet(HttpServletRequest request,HttpServletResponse response)
{
String fileName=request.getParameter("name");
PrintWriter pw=null;
ServletContext sc=request.getSession().getServletContext();
String jsFolderPath=sc.getRealPath("/WEB-INF/js/");
File file=new File(jsFolderPath+fileName);
try
{
response.setContentType("text/javascript");
pw=response.getWriter();
if(!file.exists())
{
pw.println("window.addEventListener(\"load\",()=>{ alert('"+fileName+" not found.')})");
pw.flush();
return;
}
RandomAccessFile randomAccessFile=new RandomAccessFile(file,"rw");
while(randomAccessFile.getFilePointer()<randomAccessFile.length())
{
pw.println(randomAccessFile.readLine());
pw.flush();
}
}catch(IOException ioException)
{
System.out.println("IOException: "+ioException);
pw.println("window.addEventListener(\"load\",()=>{ alert('"+fileName+" not found.')})");
pw.flush();
return;
}catch(Exception exception)
{
System.out.println("Exception: "+exception);
pw.println("window.addEventListener(\"load\",()=>{ alert('"+fileName+" not found.')})");
pw.flush();
return;
}
}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{
doGet(request,response);
}
}

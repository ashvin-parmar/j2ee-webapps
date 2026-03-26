package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.stream.*;
import java.nio.file.*;
import java.util.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import com.ashvin.web.rock.pojo.*;
import com.ashvin.web.rock.model.*;
import com.ashvin.web.rock.annotations.*;

public class WebRockStarter extends HttpServlet
{
public void init()
{
ServletContext sc=getServletContext();
String servicePackagePrefix=(String)sc.getInitParameter("SERVICE_PACKAGE_PREFIX");
//System.out.println(servicePackagePrefix);
String pathToClassFolder=sc.getRealPath("/WEB-INF/classes");
//System.out.println(pathToClassFolder);
File classesDir=new File(pathToClassFolder);
//System.out.println(classesDir.exists());
File[] folders=classesDir.listFiles();
for(File folder:folders)
{
if(folder.isDirectory() && folder.getName().startsWith(servicePackagePrefix))
{
//System.out.println(folder.getAbsolutePath());
//traverseToClassFiles(folder);
loadAllPathServices(folder);
}
}
}
private String getClassName(String rootDir, Path classPath) 
{
String relativePath = Paths.get(rootDir).relativize(classPath).toString();  //Remove rootPath from classPath
return relativePath.replace(File.separator, ".").replace(".class", "");
}
private List<Path> findClassFiles(Path rootPath) 
{
List<Path> filesList;
try (Stream<Path> paths = Files.walk(rootPath)) 
{
filesList = paths
              .filter(Files::isRegularFile)
              .filter(p -> p.getFileName().toString().endsWith(".class"))
              .collect(Collectors.toList());
}catch (IOException e) 
{
System.err.println("Error traversing directory: " + e.getMessage());
filesList = List.of();
}
return filesList;
}
private void loadAllPathServices(File folder)
{
try
{
String parentPath=folder.getParent();
List<Path> classPaths=findClassFiles(Paths.get(folder.getAbsolutePath()));
//Class<?> pathAnnotationClass=Class.forName("com.ashvin.web.rock.annotations.Path");
PATH pathAvailableOnClass=null;
PATH pathAvailableOnMethod=null;
GET getAvailableOnClass=null;
GET getAvailableOnMethod=null;
POST postAvailableOnClass=null;
POST postAvailableOnMethod=null;
FORWARD forwardAvailableOnMethod=null;

for(Path path:classPaths)
{
pathAvailableOnClass=null;
getAvailableOnClass=null;
postAvailableOnClass=null;

String className=getClassName(parentPath,path);
//Class<?> loadedClass=cl.loadClass(className);    //URLClassLoader
Class<?> loadedClass=Class.forName(className);
System.out.println("Class: "+loadedClass.getName());
Annotation[] annos=loadedClass.getDeclaredAnnotations();
for(Annotation anno:annos)
{
if(pathAvailableOnClass==null && anno instanceof PATH)
{    
pathAvailableOnClass=(PATH)anno;
}
if(getAvailableOnClass==null && (anno instanceof GET))
{
getAvailableOnClass=(GET)anno;
}
if(postAvailableOnClass==null && (anno instanceof POST))
{
postAvailableOnClass=(POST)anno;
}
}
if(pathAvailableOnClass==null) continue;
//System.out.println("-------------PATH ON CLASS AVAILABLE-------------");
Method methods[]=loadedClass.getDeclaredMethods();
for(Method method:methods)
{
pathAvailableOnMethod=null;
getAvailableOnMethod=null;
postAvailableOnMethod=null;
forwardAvailableOnMethod=null;

Annotation[] annos2=method.getDeclaredAnnotations();
for(Annotation anno2:annos2)
{
if(pathAvailableOnMethod==null && anno2 instanceof PATH)
{
pathAvailableOnMethod=(PATH)anno2;
}
if(getAvailableOnClass==null && getAvailableOnMethod==null && anno2 instanceof GET)
{
getAvailableOnMethod=(GET)anno2;
}
if(postAvailableOnClass==null && postAvailableOnMethod==null && anno2 instanceof POST)
{
postAvailableOnMethod=(POST)anno2;
}
if(forwardAvailableOnMethod==null && anno2 instanceof FORWARD)
{
forwardAvailableOnMethod=(FORWARD)anno2;
}
}
if(pathAvailableOnMethod==null) continue;
//System.out.println("-------------------PATH AVAILABLE ON METHOD ----------");
Service service=new Service();
String fullPath;
String path1=pathAvailableOnClass.value();
String path2=pathAvailableOnMethod.value();
fullPath=path1+path2;
System.out.println(fullPath);
service.setPath(fullPath);
service.setServiceMethod(method);
service.setServiceClass(loadedClass);
if(forwardAvailableOnMethod!=null)
{
String forwardToPath=forwardAvailableOnMethod.value();
if(forwardToPath.isBlank()==false)
{
service.setForwardTo(forwardToPath);
}
}
if(getAvailableOnClass!=null)
{
//System.out.println("GET on Class");
WebRockModel.getWebRockModel().setPathService(fullPath,service,"GET");
}
else if(postAvailableOnClass!=null)
{
//System.out.println("POST on class");
WebRockModel.getWebRockModel().setPathService(fullPath,service,"POST");
}
else if(getAvailableOnMethod!=null)
{
//System.out.println("GET on method");
WebRockModel.getWebRockModel().setPathService(fullPath,service,"GET");
}
else if(postAvailableOnMethod!=null)
{
//System.out.println("POST on method");
WebRockModel.getWebRockModel().setPathService(fullPath,service,"POST");
}
else
{
//System.out.println("NO GET, NO POST on METHOD and CLASS");
WebRockModel.getWebRockModel().setPathService(fullPath,service,"GET");
WebRockModel.getWebRockModel().setPathService(fullPath,service,"POST");
}
}
}
}catch(Exception ioException)
{
System.out.println("Exception: "+ioException.getMessage());
}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{

}
public void doPost(HttpServletRequest requese,HttpServletResponse response)
{

}
}

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

public class WebRockStarter extends HttpServlet
{
public void init()
{
ServletContext sc=getServletContext();
String servicePackagePrefix=(String)sc.getInitParameter("SERVICE_PACKAGE_PREFIX");
System.out.println(servicePackagePrefix);
String pathToClassFolder=sc.getRealPath("/WEB-INF/classes");
System.out.println(pathToClassFolder);
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
Class<?> pathAnnotationClass=Class.forName("com.ashvin.web.rock.annotations.Path");
for(Path path:classPaths)
{
String className=getClassName(parentPath,path);
//Class<?> loadedClass=cl.loadClass(className);    //URLClassLoader
Class<?> loadedClass=Class.forName(className);
System.out.println("Class: "+loadedClass.getName());

Annotation[] annos=loadedClass.getDeclaredAnnotations();
for(Annotation anno:annos)
{
if(anno instanceof com.ashvin.web.rock.annotations.Path p)
{
Method methods[]=loadedClass.getDeclaredMethods();
for(Method method:methods)
{
Annotation[] annos2=method.getDeclaredAnnotations();
for(Annotation anno2:annos2)
{
if(anno2 instanceof com.ashvin.web.rock.annotations.Path p2)
{
Service service=new Service();
String fullPath;
String path1=p.value();
String path2=p2.value();
fullPath=path1+path2;
System.out.println(fullPath);
service.setPath(fullPath);
service.setServiceMethod(method);
service.setServiceClass(loadedClass);
WebRockModel.getWebRockModel().setPathService(fullPath,service);
}
}
}
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

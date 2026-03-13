import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.lang.annotation.*;

class PrintAllAnnotations
{
public static void main(String args[])
{
try
{

File file=new File(args[0]);
if(!file.exists() || !file.isDirectory()) 
{
System.out.println("Directory required, Given path is not to a directory");
return;
}
System.out.println("Path to DIR of classes: "+file.getAbsolutePath());
URL[] urls={file.toURI().toURL()};

for(URL url:urls)
{
System.out.println(url.getPath());
}
URLClassLoader cl=new URLClassLoader(urls,PrintAllAnnotations.class.getClassLoader());

String className;
File[] childrens=file.listFiles();
System.out.println(childrens.length);
for(File child:childrens)
{
if(!child.getName().endsWith(".class")) continue;
className=child.getAbsolutePath();
className=child.getName().substring(0,child.getName().length()-6);
Class<?> c=cl.loadClass(className);
System.out.println("Class: "+c.getName());
Annotation annos[]=c.getAnnotations();
if(annos!=null)
{
int i=1;
for(Annotation anno:annos)
{
if(anno instanceof Path p)
{
System.out.println((i++)+": "+p.value());
}
else if(anno instanceof XYZAnnotation x)
{
System.out.println((i++)+": "+x.xyz());
}
}
}

}
}catch(Exception exception)
{
System.out.println("Error: "+exception.getMessage());
}

}
}

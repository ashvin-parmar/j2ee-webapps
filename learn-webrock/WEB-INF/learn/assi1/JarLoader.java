import java.lang.reflect.*;
import java.io.*;
import java.net.*;
import java.util.jar.*;
import java.util.*;

class JarLoader
{
public static void loadClassesFromJarFile(String pathToJar)
{
try
{
System.out.println("PATH + JAR file: "+pathToJar);
URL[] urls={new URL("jar:file:"+pathToJar+"!/")};

URLClassLoader cl=URLClassLoader.newInstance(urls,Thread.currentThread().getContextClassLoader());

JarFile jarFile=new JarFile(pathToJar);
JarEntry je;
String className;
Enumeration<JarEntry> e=jarFile.entries();
while(e.hasMoreElements())
{
je=e.nextElement();
if(je.isDirectory() || !je.getName().endsWith(".class")) continue;
className=je.getName().substring(0,je.getName().length()-6);
className=className.replace("/",".");
System.out.println(className);

//Class<?> c=cl.loadClass(className);   //Here, we get the class, now we can apply reflection on it.
//System.out.println("Loaded class: "+c.getName());
}

}catch(Exception exception)
{
System.out.println("Unable to load jar file: "+pathToJar);
}
}
public static void main(String args[])
{
System.out.println("Jar Loaded: "+args[0]);
File jarDir=new File(args[0]);
if(!jarDir.isDirectory())
{
if(jarDir.getName().endsWith(".jar")) loadClassesFromJarFile(args[0]);
}
else
{
File[] children=jarDir.listFiles();
if(children==null) return;
for(File child:children)
{
if(child.getName().endsWith(".jar"))  loadClassesFromJarFile(child.getAbsolutePath());
}
}
}
}

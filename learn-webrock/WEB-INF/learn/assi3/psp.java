import java.lang.reflect.*;
import java.util.*;
import java.io.*;


class psp
{
public static Class[] getParametersType(String[] parameters)
{
boolean flag=false;
int i=0;
Class[] parametersType=new Class[parameters.length];
for(String parameter:parameters)
{
flag=false;
if(!flag)   //For Interger
{
try
{
Integer value=Integer.parseInt(parameter);
parametersType[i]=Integer.class;
flag=true;
}catch(NumberFormatException nfe)
{
}
}

if(!flag)
{
try
{
Double value=Double.parseDouble(parameter);
parametersType[i]=Double.class;
flag=true;
}catch(NumberFormatException nfe)
{
}
}
if(!flag)
{
parametersType[i]=String.class;
flag=true;
}
i++;
}
return parametersType;
}
public static Object[] getParametersValue(String[] parameters)
{
boolean flag=false;
int i=0;
Object[] parametersValue=new Object[parameters.length];
for(String parameter:parameters)
{
flag=false;
if(!flag)   //For Interger
{
try
{
Integer value=Integer.parseInt(parameter);
parametersValue[i]=value;
flag=true;
}catch(NumberFormatException nfe)
{
}
}

if(!flag)
{
try
{
Double value=Double.parseDouble(parameter);
parametersValue[i]=value;
flag=true;
}catch(NumberFormatException nfe)
{
}
}
if(!flag)
{
parametersValue[i]=parameter;
flag=true;
}
i++;
}
return parametersValue;
}

public static void main(String args[])
{
String className=args[0];
String methodName=args[1];
//System.out.println(className+"."+methodName+"("+value+","+count+");");
Method method;
String[] subargs=Arrays.copyOfRange(args,2,args.length);
Class[] parametersType=getParametersType(subargs);
Object[] parametersValue=getParametersValue(subargs);
Object obj;

//System.out.println(parametersType.length);
try
{
Class<?> c=Class.forName(className);

method=c.getMethod(methodName,parametersType);
if(method==null)
{
System.out.println("Method: "+methodName+" not found in the class "+className);
return;
}
//System.out.println("Method found");
obj=c.newInstance();
Class returnType=method.getReturnType();
//System.out.println("Over here: "+returnType);
if(returnType.toString().equals("void"))
{
method.invoke(obj,parametersValue);
}
else
{
Object returnedData=method.invoke(obj,parametersValue);
System.out.println(returnedData.toString());
}
}catch(ClassNotFoundException cnfe)
{
System.out.println("Class "+className+" not available");
}catch(Exception e)
{
System.out.println("Error: "+e.getMessage());
}
}
}

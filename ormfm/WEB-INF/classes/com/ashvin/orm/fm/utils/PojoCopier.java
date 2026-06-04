package com.ashvin.orm.fm.utils;

import java.lang.reflect.*;


public class PojoCopier
{
public static void copy(Object target,Object source) throws Exception
{
Class ct=target.getClass();
Class cs=source.getClass();
if(!ct.equals(cs)) throw new Exception("Target and source instance are of different class");
try
{
Class objClass=ct;
Field fields[]=objClass.getDeclaredFields();
String fieldName;
String stdFieldName;
Class<?> fieldType;

Method setterMethod;
Method getterMethod;

for(Field field:fields)
{
try
{
fieldName=field.getName();
fieldType=field.getType();
stdFieldName=standardFieldName(fieldName);
if(stdFieldName.isBlank()) continue;
setterMethod=objClass.getMethod("set"+stdFieldName,fieldType);
if(setterMethod==null) continue;
getterMethod=objClass.getMethod("get"+stdFieldName);
if(getterMethod==null) continue;
}catch(Exception exception)
{
continue;
}
setterMethod.invoke(target,getterMethod.invoke(source));
}
}catch(Exception exception)
{
throw new Exception("Unable to copy source data to target");
}
}
private static String standardFieldName(String fieldName)
{
String stdFieldName="";
if(fieldName==null || fieldName.isBlank()) return stdFieldName;
fieldName=fieldName.trim();
stdFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
return stdFieldName;
}
}

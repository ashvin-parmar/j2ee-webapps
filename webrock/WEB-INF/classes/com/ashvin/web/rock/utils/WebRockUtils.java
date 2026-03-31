package com.ashvin.web.rock.utils;
import java.util.*;
public class WebRockUtils
{
private static final com.google.gson.Gson gson=new com.google.gson.Gson();
private static final Map<Class<?>,Class<?>> primitiveToWrapper=new HashMap<>();
static
{
primitiveToWrapper.put(long.class,Long.class);
primitiveToWrapper.put(int.class,Integer.class);
primitiveToWrapper.put(short.class,Short.class);
primitiveToWrapper.put(byte.class,Byte.class);
primitiveToWrapper.put(double.class,Double.class);
primitiveToWrapper.put(float.class,Float.class);
primitiveToWrapper.put(char.class,Character.class);
primitiveToWrapper.put(boolean.class,Boolean.class);
primitiveToWrapper.put(void.class,Void.class);
}
public static Class<?> wrap(Class<?> type)
{
if(type==null)
{
return null;
}
return type.isPrimitive()?primitiveToWrapper.get(type):type;
}
public static final Object parseTo(Class parameterType,String parameterValue)
{
return parseTo(parameterType,parameterValue,null);
}
public static final Object parseTo(Class parameterType,String parameterValue,String convertFrom)
{
Object result;
if(parameterType==null) return null;
if(parameterValue==null) parameterValue="";
//System.out.println("Type: "+parameterType+" value: "+parameterValue);
result=null;
if(parameterType.equals(Long.TYPE) || parameterType.equals(java.lang.Long.class))
{
try
{
result=Long.parseLong(parameterValue);
}catch(NumberFormatException nfe)
{
result=(long)0;
}
}
if(parameterType.equals(Integer.TYPE) || parameterType.equals(java.lang.Integer.class))
{
try
{
result=Integer.parseInt(parameterValue);
}catch(NumberFormatException nfe)
{
result=(int)0;
}
}
if(parameterType.equals(Short.TYPE) || parameterType.equals(java.lang.Short.class))
{
try
{
result=Short.parseShort(parameterValue);
}catch(NumberFormatException nfe)
{
result=(short)0;
}
}
if(parameterType.equals(Byte.TYPE) || parameterType.equals(java.lang.Byte.class))
{
try
{
result=Byte.parseByte(parameterValue);
}catch(NumberFormatException nfe)
{
result=(byte)0;
}
}
if(parameterType.equals(Double.TYPE) || parameterType.equals(java.lang.Double.class))
{
try
{
result=Double.parseDouble(parameterValue);
}catch(NumberFormatException nfe)
{
result=(double)0.0;
}
}
if(parameterType.equals(Float.TYPE) || parameterType.equals(java.lang.Float.class))
{
try
{
result=Float.parseFloat(parameterValue);
}catch(NumberFormatException nfe)
{
result=(float)0.0;
}
}
if(parameterType.equals(Boolean.TYPE) || parameterType.equals(java.lang.Boolean.class))
{
if(parameterValue.isBlank()==false) result=Boolean.parseBoolean(parameterValue);
else result=(boolean)false;
}
if(parameterType.equals(Character.TYPE) || parameterType.equals(java.lang.Character.class))
{
if(parameterValue.isBlank()==false) result=parameterValue.charAt(0);
else result=(char)' ';
}
if(parameterType.equals(java.lang.String.class))
{
result=parameterValue;
}
if(result==null)
{
if(convertFrom!=null && convertFrom.equalsIgnoreCase("JSON"))
{
try
{
result=gson.fromJson(parameterValue,parameterType);
}catch(Exception exception)
{
result=null;
}
}
}
return result;
}
public static final String toJSON(Object obj)
{
return gson.toJson(obj);
}
}

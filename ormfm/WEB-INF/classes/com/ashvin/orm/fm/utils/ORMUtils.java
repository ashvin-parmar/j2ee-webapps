package com.ashvin.orm.fm.utils;

import java.sql.*;
import java.util.*;

public class ORMUtils
{
private static final Map<JDBCType,Class<?>> sqlToJavaTypes=new HashMap<>();
private static final Map<Class<?>,Class<?>> primitiveToWrapper=new HashMap<>();
private static final com.google.gson.Gson gson=new com.google.gson.Gson();
static
{
sqlToJavaTypes.put(JDBCType.BIGINT,Long.class);
sqlToJavaTypes.put(JDBCType.INTEGER,Integer.class);
sqlToJavaTypes.put(JDBCType.SMALLINT,Short.class);
sqlToJavaTypes.put(JDBCType.TINYINT,Byte.class);
sqlToJavaTypes.put(JDBCType.FLOAT,Double.class);
sqlToJavaTypes.put(JDBCType.DOUBLE,Double.class);
sqlToJavaTypes.put(JDBCType.DECIMAL,java.math.BigDecimal.class);
sqlToJavaTypes.put(JDBCType.NUMERIC,java.math.BigDecimal.class);
sqlToJavaTypes.put(JDBCType.REAL,Float.class);
sqlToJavaTypes.put(JDBCType.BIT,Boolean.class);
sqlToJavaTypes.put(JDBCType.DATE,java.util.Date.class);
sqlToJavaTypes.put(JDBCType.CHAR,java.lang.String.class);
sqlToJavaTypes.put(JDBCType.VARCHAR,java.lang.String.class);
sqlToJavaTypes.put(JDBCType.LONGVARCHAR,java.lang.String.class);
}
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
public static Class<?> jdbcToJavaMappedType(JDBCType jdbcTypeCode)
{
return sqlToJavaTypes.get(jdbcTypeCode);
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

static int uniqueFieldCount=1;
public static String camelCaseRepresent(String field)
{
StringBuilder camelCaseField=new StringBuilder("");
int i=0;
while(i<field.length() && !Character.isLetter(field.charAt(i))) i++;
if(i==field.length()) return "tmp"+(uniqueFieldCount++);
char m=field.charAt(i);
if(Character.isUpperCase(m)) m=Character.toLowerCase(m);
camelCaseField.append(m);
i++;
while(i<field.length())
{
if(Character.isLetterOrDigit(field.charAt(i)))
{
camelCaseField.append(field.charAt(i));
}
else
{
while(i<field.length() && !Character.isLetterOrDigit(field.charAt(i))) i++;
if(i<field.length())
{
m=field.charAt(i);
if(Character.isLowerCase(m)) m=Character.toUpperCase(m);
//if(m>=97 && m<=122) m=(m-(char)32);
camelCaseField.append(m);
}
}
i++;
}
return camelCaseField.toString();
}
}

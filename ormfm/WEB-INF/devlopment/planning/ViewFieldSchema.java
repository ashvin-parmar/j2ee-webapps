package com.ashvin.orm.fm.model;

public class ViewFieldSchema
{
private String methodName;
private String columnName;
private Class<?> type;
public ViewFieldSchema(String methodName,String columnName,Class<?> type)
{
this.methodName=methodName;
this.columnName=columnName;
this.type=type;
}
public void setMethodName(String methodName)
{
this.methodName=methodName;
}
public String getMethodName()
{
return this.methodName;
}
public void setColumnName(String columnName)
{
this.columnName=columnName;
}
public String getColumnName()
{
return this.columnName;
}
public void setType(Class type)
{
this.type=type;
}
public Class getType()
{
return this.type;
}
public String toString()
{
return "ViewFieldSchema {"
+"methodName='"+methodName+"',"
+"columnName='"+columnName+"',"
+"type="+type.getName()
+"}";
}
}


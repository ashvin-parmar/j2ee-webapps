package com.ashvin.orm.fm.model;
import java.util.*;

public class ViewSchema implements Schema
{
private String viewName;
private List<FieldSchema> fields;
private Class<?> objectClass;
public ViewSchema(Class<?> objectClass,String viewName)
{
this.objectClass=objectClass;
this.viewName=viewName;
this.fields=new ArrayList<>();
}
public String getName()
{
return this.viewName;
}
public void addField(FieldSchema fieldSchema)
{
if(fieldSchema==null) return;
fields.add(fieldSchema);
}
public String getViewName()
{
return this.viewName;
}
public Class<?> getObjectClass()
{
return objectClass;
}
public List<FieldSchema> getAllFields()
{
return this.fields;
}
public FieldSchema getFieldByMethodName(String methodName)
{
for(FieldSchema fs:fields)
{
if(fs.getMethodName().equals(methodName)) return fs;
}
return null;
}
public FieldSchema getFieldByColumnName(String columnName)
{
for(FieldSchema fs:fields)
{
if(fs.getColumnName().equals(columnName)) return fs;
}
return null;
}
public String toString()
{
StringBuilder sb=new StringBuilder();
sb.append("ViewSchema { viewName='").append(viewName).append("'\n");
for(FieldSchema fs:fields)
{
sb.append("\t").append(fs.toString()).append("\n");
}
sb.append("}");
return sb.toString();
}
}

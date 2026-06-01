package com.ashvin.orm.fm.model;
import java.util.*;

public class ViewSchema
{
private String viewName;
private List<ViewFieldSchema> fields;
private Class<?> objectClass;
public ViewSchema(Class<?> objectClass,String viewName)
{
this.objectClass=objectClass;
this.viewName=viewName;
this.fields=new ArrayList<>();
}
public void addField(ViewFieldSchema fieldSchema)
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
public List<ViewFieldSchema> getAllFields()
{
return this.fields;
}
public ViewFieldSchema getFieldByMethodName(String methodName)
{
for(ViewFieldSchema fs:fields)
{
if(fs.getMethodName().equals(methodName)) return fs;
}
return null;
}
public ViewFieldSchema getFieldByColumnName(String columnName)
{
for(ViewFieldSchema fs:fields)
{
if(fs.getColumnName().equals(columnName)) return fs;
}
return null;
}
public String toString()
{
StringBuilder sb=new StringBuilder();
sb.append("ViewSchema { viewName='").append(viewName).append("'\n");
for(ViewFieldSchema fs:fields)
{
sb.append("\t").append(fs.toString()).append("\n");
}
sb.append("}");
return sb.toString();
}
}

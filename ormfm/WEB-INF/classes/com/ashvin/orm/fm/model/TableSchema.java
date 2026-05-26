package com.ashvin.orm.fm.model;
import java.util.*;

public class TableSchema
{
private String tableName;
private List<FieldSchema> fields;
private Class<?> objectClass;
public TableSchema(Class<?> objectClass,String tableName)
{
this.objectClass=objectClass;
this.tableName=tableName;
this.fields=new ArrayList<>();
}
public void addField(FieldSchema fieldSchema)
{
if(fieldSchema==null) return;
fields.add(fieldSchema);
}
public String getTableName()
{
return this.tableName;
}
public Class<?> getObjectClass()
{
return objectClass;
}
public List<FieldSchema> getAllFields()
{
return this.fields;
}
public List<FieldSchema> getPrimaryKeyFields()
{
List<FieldSchema> result=new ArrayList<>();
for(FieldSchema fs:fields)
{
if(fs.isPrimaryKey()) result.add(fs);
}
return result;
}
public List<FieldSchema> getForeignKeyFields()
{
List<FieldSchema> result=new ArrayList<>();
for(FieldSchema fs:fields)
{
if(fs.isForeignKey()) result.add(fs);
}
return result;
}
public List<FieldSchema> getAutoIncrementFields()
{
List<FieldSchema> result=new ArrayList<>();
for(FieldSchema fs:fields)
{
if(fs.isAutoIncrement()) result.add(fs);
}
return result;
}
public List<FieldSchema> getNonAutoIncrementFields()
{
List<FieldSchema> result=new ArrayList<>();
for(FieldSchema fs:fields)
{
if(fs.isAutoIncrement()==false) result.add(fs);
}
return result;
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
sb.append("TableSchema { tableName='").append(tableName).append("'\n");
for(FieldSchema fs:fields)
{
sb.append("\t").append(fs.toString()).append("\n");
}
sb.append("}");
return sb.toString();
}
}

package com.ashvin.orm.fm.model;
import java.util.*;

public interface Schema
{
public Class<?> getObjectClass();
public String getName();
public void addField(FieldSchema fieldSchema);
public List<FieldSchema> getAllFields();
public FieldSchema getFieldByMethodName(String methodName);
public FieldSchema getFieldByColumnName(String columnName);
}

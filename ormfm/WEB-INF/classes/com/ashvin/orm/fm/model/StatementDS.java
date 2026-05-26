package com.ashvin.orm.fm.model;
import java.util.*;
import java.lang.reflect.*;

public class StatementDS
{
private String statement;
private int paramsCount;
private List<Method> jdbcMethods;
private List<Method> classMethods;
private List<Integer> paramsType;
public void setStatement(String statement)
{
this.statement=statement;
}
public String getStatement()
{
return this.statement;
}
public void setParamsCount(int paramsCount)
{
this.paramsCount=paramsCount;
}
public int getParamsCount()
{
return this.paramsCount;
}
public void setJDBCMethods(List<Method> jdbcMethods)
{
this.jdbcMethods=jdbcMethods;
}
public List<Method> getJDBCMethods()
{
return this.jdbcMethods;
}
public void setClassMethods(List<Method> classMethods)
{
this.classMethods=classMethods;
}
public List<Method> getClassMethods()
{
return this.classMethods;
}
public void setParamsType(List<Integer> paramsType)
{
this.paramsType=paramsType;
}
public List<Integer> getParamsType()
{
return this.paramsType;
}
}

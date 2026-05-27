package com.ashvin.orm.fm.model;
import java.util.*;
import java.lang.reflect.*;

public class StatementDS
{
private StringBuilder statement;
private List<Method> jdbcMethods;
private List<Method> classMethods;
private List<Integer> paramsType;
public StatementDS()
{
statement=new StringBuilder();
jdbcMethods=new ArrayList<>();
classMethods=new ArrayList<>();
paramsType=new ArrayList<>();
}
public void clear()
{
statement=new StringBuilder();
jdbcMethods=new ArrayList<>();
classMethods=new ArrayList<>();
paramsType=new ArrayList<>();
}
public void setStatement(StringBuilder statement)
{
this.statement=statement;
}
public StringBuilder append(String data)
{
this.statement.append(data);
return this.statement;
}
public StringBuilder append(StringBuilder data)
{
this.statement.append(data.toString());
return this.statement;
}
public StringBuilder getStatement()
{
return this.statement;
}
public int getParamsCount()
{
return this.paramsType.size();
}
public void setJDBCMethods(List<Method> jdbcMethods)
{
this.jdbcMethods=jdbcMethods;
}
public List<Method> getJDBCMethods()
{
return this.jdbcMethods;
}
public void addJDBCMethod(Method jdbcMethod)
{
this.jdbcMethods.add(jdbcMethod);
}
public void addJDBCMethods(List<Method> jdbcMethods)
{
for(Method method:jdbcMethods)
{
this.jdbcMethods.add(method);
}
}
public void setClassMethods(List<Method> classMethods)
{
this.classMethods=classMethods;
}
public List<Method> getClassMethods()
{
return this.classMethods;
}
public void addClassMethod(Method classMethod)
{
this.classMethods.add(classMethod);
}
public void addClassMethods(List<Method> classMethods)
{
for(Method method:classMethods)
{
this.classMethods.add(method);
}
}
public void setParamsType(List<Integer> paramsType)
{
this.paramsType=paramsType;
}
public List<Integer> getParamsType()
{
return this.paramsType;
}
public void addParamType(int paramType)
{
this.paramsType.add(paramType);
}
public void addParamsType(List<Integer> paramsType)
{
for(Integer type:paramsType)
{
this.paramsType.add(type);
}
}
}

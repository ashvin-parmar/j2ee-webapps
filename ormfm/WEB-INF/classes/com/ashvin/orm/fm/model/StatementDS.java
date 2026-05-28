package com.ashvin.orm.fm.model;
import java.util.*;
import java.lang.reflect.*;

public class StatementDS
{
private StringBuilder statement;
private boolean isQuery;
private List<Method> jdbcSetterMethods;
private List<Method> classGetterMethods;
private List<Method> jdbcGetterMethods;
private List<Method> classSetterMethods;
private List<Integer> statementParamsType;
private List<Integer> resultParamsType;
public StatementDS()
{
statement=new StringBuilder();
jdbcSetterMethods=new ArrayList<>();
classGetterMethods=new ArrayList<>();
statementParamsType=new ArrayList<>();
isQuery=false;
}
public void clear()
{
statement=new StringBuilder();
jdbcSetterMethods=new ArrayList<>();
classGetterMethods=new ArrayList<>();
statementParamsType=new ArrayList<>();
isQuery=false;
}
public void setQuery(boolean isQuery)
{
if(isQuery==true && this.isQuery==false)
{
this.isQuery=true;
jdbcGetterMethods=new ArrayList<>();
classSetterMethods=new ArrayList<>();
resultParamsType=new ArrayList<>();
}
}
public boolean isQuery()
{
return this.isQuery;
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
public int getStatementParamsCount()
{
return this.statementParamsType.size();
}
public int getResultParamsCount()
{
return this.resultParamsType.size();
}
public void setJDBCSetterMethods(List<Method> jdbcSetterMethods)
{
this.jdbcSetterMethods=jdbcSetterMethods;
}
public List<Method> getJDBCSetterMethods()
{
return this.jdbcSetterMethods;
}
public void addJDBCSetterMethod(Method jdbcSetterMethod)
{
this.jdbcSetterMethods.add(jdbcSetterMethod);
}
public void addJDBCSetterMethods(List<Method> jdbcSetterMethods)
{
for(Method method:jdbcSetterMethods)
{
this.jdbcSetterMethods.add(method);
}
}
public void setJDBCGetterMethods(List<Method> jdbcGetterMethods)
{
if(this.isQuery) this.jdbcGetterMethods=jdbcGetterMethods;
}
public List<Method> getJDBCGetterMethods()
{
if(this.isQuery) return this.jdbcGetterMethods;
return null;
}
public void addJDBCGetterMethod(Method jdbcGetterMethod)
{
if(this.isQuery) this.jdbcGetterMethods.add(jdbcGetterMethod);
}
public void addJDBCGetterMethods(List<Method> jdbcGetterMethods)
{
if(!this.isQuery) return;
for(Method method:jdbcGetterMethods)
{
this.jdbcGetterMethods.add(method);
}
}
public void setClassSetterMethods(List<Method> classSetterMethods)
{
if(this.isQuery) this.classSetterMethods=classSetterMethods;
}
public List<Method> getClassSetterMethods()
{
if(this.isQuery) return this.classSetterMethods;
return null;
}
public void addClassSetterMethod(Method classSetterMethod)
{
if(this.isQuery) this.classSetterMethods.add(classSetterMethod);
}
public void addClassSetterMethods(List<Method> classSetterMethods)
{
if(!this.isQuery) return;
for(Method method:classSetterMethods)
{
this.classSetterMethods.add(method);
}
}
public void setClassGetterMethods(List<Method> classGetterMethods)
{
this.classGetterMethods=classGetterMethods;
}
public List<Method> getClassGetterMethods()
{
return this.classGetterMethods;
}
public void addClassGetterMethod(Method classGetterMethod)
{
this.classGetterMethods.add(classGetterMethod);
}
public void addClassGetterMethods(List<Method> classGetterMethods)
{
for(Method method:classGetterMethods)
{
this.classGetterMethods.add(method);
}
}
public void setStatementParamsType(List<Integer> statementParamsType)
{
this.statementParamsType=statementParamsType;
}
public List<Integer> getStatementParamsType()
{
return this.statementParamsType;
}
public void addStatementParamType(int statementParamType)
{
this.statementParamsType.add(statementParamType);
}
public void addStatementParamsType(List<Integer> statementParamsType)
{
for(Integer type:statementParamsType)
{
this.statementParamsType.add(type);
}
}

public void setResultParamsType(List<Integer> resultParamsType)
{
this.resultParamsType=resultParamsType;
}
public List<Integer> getResultParamsType()
{
return this.resultParamsType;
}
public void addResultParamType(int resultParamType)
{
this.resultParamsType.add(resultParamType);
}
public void addResultParamsType(List<Integer> resultParamsType)
{
for(Integer type:resultParamsType)
{
this.resultParamsType.add(type);
}
}
}

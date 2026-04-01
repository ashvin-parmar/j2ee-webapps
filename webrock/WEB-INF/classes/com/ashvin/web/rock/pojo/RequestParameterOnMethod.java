package com.ashvin.web.rock.pojo;

public class RequestParameterOnMethod
{
private String name;
private Class parameterType;
public RequestParameterOnMethod()
{
this.name=null;
this.parameterType=null;
}
public RequestParameterOnMethod(String name,Class parameterType)
{
this.name=name;
this.parameterType=parameterType;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
public void setParameterType(Class parameterType)
{
this.parameterType=parameterType;
}
public Class getParameterType()
{
return this.parameterType;
}
}

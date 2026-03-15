package com.ashvin.web.rock.pojo;

import java.lang.reflect.*;

public class Service implements java.io.Serializable
{
private Class serviceClass;
private String path;
private Method serviceMethod;
public void setServiceClass(Class serviceClass)
{
this.serviceClass=serviceClass;
}
public Class getServiceClass()
{
return this.serviceClass;
}
public void setPath(String path)
{
this.path=path;
}
public String getPath()
{
return this.path;
}
public void setServiceMethod(Method serviceMethod)
{
this.serviceMethod=serviceMethod;
}
public Method getServiceMethod()
{
return this.serviceMethod;
}
}

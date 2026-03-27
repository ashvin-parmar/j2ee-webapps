package com.ashvin.web.rock.pojo;

import java.lang.reflect.*;

public class Service implements java.io.Serializable
{
private Class serviceClass;
private String path;
private Method serviceMethod;
private String forwardTo;
private boolean isGetAllowed;
private boolean isPostAllowed;
private boolean runOnStartup;
private int priority;
public Service()
{
serviceClass=null;
path=null;
serviceMethod=null;
forwardTo=null;
isGetAllowed=false;
isPostAllowed=false;
runOnStartup=false;
priority=-1;    //lazy loading
}
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
public void setForwardTo(String forwardTo)
{
this.forwardTo=forwardTo;
}
public String getForwardTo()
{
return this.forwardTo;
}
public void setIsGetAllowed(boolean isGetAllowed)
{
this.isGetAllowed=isGetAllowed;
}
public boolean getIsGetAllowed()
{
return this.isGetAllowed;
}
public boolean isGetAllowed()
{
return this.isGetAllowed;
}
public void setIsPostAllowed(boolean isPostAllowed)
{
this.isPostAllowed=isPostAllowed;
}
public boolean getIsPostAllowed()
{
return this.isPostAllowed;
}
public boolean isPostAllowed()
{
return this.isPostAllowed;
}
public void setRunOnStartup(boolean runOnStartup)
{
this.runOnStartup=runOnStartup;
}
public boolean getRunOnStartup()
{
return this.runOnStartup;
}
public void setPriority(int priority)
{
this.priority=priority;
}
public int getPriority()
{
return this.priority;
}
}

package com.ashvin.web.rock.pojo;
import java.lang.reflect.*;

public class SecurityAccess implements java.io.Serializable
{
public Class checkPost;
public Method guard;
public String servicePath;
public void setCheckPost(Class checkPost)
{
this.checkPost=checkPost;
}
public Class getCheckPost()
{
return this.checkPost;
}
public void setGuard(Method guard)
{
this.guard=guard;
}
public Method getGuard()
{
return this.guard;
}
public void setServicePath(String servicePath)
{
this.servicePath=servicePath;
}
public String getServicePath()
{
return this.servicePath;
}
}

package com.ashvin.web.rock.pojo;

import javax.servlet.*;

public class ApplicationScope implements java.io.Serializable
{
private ServletContext servletContext;
public ApplicationScope()
{
}
public void setServletContext(ServletContext servletContext)
{
this.servletContext=servletContext;
}
/*
public ServletContext getServletContext()
{
return this.servletContext;
}
*/
public void setAttribute(String key,Object value)
{
servletContext.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return servletContext.getAttribute(key);
}
public void removeAttribute(String key)
{
this.servletContext.removeAttribute(key);
}
}

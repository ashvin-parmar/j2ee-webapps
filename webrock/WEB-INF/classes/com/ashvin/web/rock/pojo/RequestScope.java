package com.ashvin.web.rock.pojo;

import javax.servlet.http.*;

public class RequestScope
{
private HttpServletRequest httpServletRequest;
public RequestScope()
{
}
public void setHttpServletRequest(HttpServletRequest httpServletRequest)
{
this.httpServletRequest=httpServletRequest;
}
/*
public HttpServletRequest getHttpServletRequest()
{
return this.httpServletRequest;
}
*/
public void setAttribute(String key,Object value)
{
this.httpServletRequest.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.httpServletRequest.getAttribute(key);
}
}

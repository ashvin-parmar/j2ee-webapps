package com.ashvin.web.rock.pojo;
import javax.servlet.http.*;

public class SessionScope
{
private HttpSession httpSession;
public SessionScope()
{
}
public void setHttpSession(HttpSession httpSession)
{
this.httpSession=httpSession;
}
/*
public HttpSession getSessionScope()
{
return this.httpSession;
}
*/
public void setAttribute(String key,Object value)
{
this.httpSession.setAttribute(key,value);
}
public Object getAttribute(String key)
{
return this.httpSession.getAttribute(key);
}
public void removeAttribute(String key)
{
this.httpSession.removeAttribute(key);
}
}

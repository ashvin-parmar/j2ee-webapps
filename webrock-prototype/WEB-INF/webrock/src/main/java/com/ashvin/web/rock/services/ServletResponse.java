package com.ashvin.web.rock.services;

public class ServletResponse implements java.io.Serializable
{
private Object result;
private Object exception;
private boolean isSuccess;
public void setResult(Object result)
{
this.result=result;
}
public Object getResult()
{
return this.result;
}
public void setException(Object exception)
{
this.exception=exception;
}
public Object getException()
{
return this.exception;
}
public void setIsSuccess(boolean isSuccess)
{
this.isSuccess=isSuccess;
}
public boolean getIsSuccess()
{
return this.isSuccess;
}
public boolean isSuccess()
{
return this.isSuccess;
}
}

package com.smart.carbonix.beans.common;

public class ErrorBean implements java.io.Serializable
{
private String error;
public ErrorBean()
{
this.error=null;
}
public void setError(String error)
{
this.error=error;
}
public String getError()
{
return this.error;
}
}

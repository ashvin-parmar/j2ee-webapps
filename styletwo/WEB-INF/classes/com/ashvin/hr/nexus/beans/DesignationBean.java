package com.ashvin.hr.nexus.beans;

public class DesignationBean implements java.io.Serializable
{
private Integer code;
private String title;
public void setCode(int code)
{
this.code=code;
}
public Integer getCode()
{
return this.code;
}
public void setTitle(String title)
{
this.title=title;
}
public String getTitle()
{
return this.title;
}
}

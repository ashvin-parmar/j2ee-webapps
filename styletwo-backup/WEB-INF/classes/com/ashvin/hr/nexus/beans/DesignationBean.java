package com.ashvin.hr.nexus.beans;

public class DesignationBean implements java.io.Serializable
{
private int code;
private String title;
public void DesignationBean()
{
this.code=0;
this.title="";
}
public void setCode(int code)
{
this.code=code;
}
public int getCode()
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

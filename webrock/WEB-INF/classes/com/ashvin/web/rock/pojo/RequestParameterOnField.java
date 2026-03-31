package com.ashvin.web.rock.pojo;

import java.lang.reflect.*;
public class RequestParameterOnField
{
private String name;
private Field field;
public RequestParameterOnField()
{
this.name="";
this.field=null;
}
public RequestParameterOnField(String name,Field field)
{
this.name=name;
this.field=field;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
public void setField(Field field)
{
this.field=field;
}
public Field getField()
{
return this.field;
}
}

package com.ashvin.web.rock.pojo;

import java.lang.reflect.*;

public class AutoWiredField
{
private String name;
private Field field;
public AutoWiredField()
{
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

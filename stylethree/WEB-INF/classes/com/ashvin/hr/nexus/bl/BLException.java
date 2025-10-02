package com.ashvin.hr.nexus.bl;
import java.util.*;

public class BLException extends Exception
{
private String genericException;
private Map<String,String> propertyException;
public BLException()
{
this("");
}
public BLException(String genericException)
{
genericException=genericException;
propertyException=new HashMap<>();
}
public void setGenericException(String genericException)
{
this.genericException=genericException;
}
public String getGenericException()
{
return this.genericException;
}
public void setPropertyException(String key,String value)
{
if(key==null || value==null) return;
key=key.trim();
value=value.trim();
if(key.length()==0 || value.length()==0) return ;
this.propertyException.put(key,value);
}
public String getPropertyException(String key)
{
if(key==null) return "";
key=key.trim();
return this.propertyException.get(key);
}
public boolean hasException()
{
int count;
count=(this.genericException.trim().length()!=0)?1:0;
count+=propertyException.size();
return count!=0;
}
public boolean hasPropertyException()
{
return this.propertyException.size()!=0;
}
public boolean hasGenericException()
{
return this.genericException.trim().length()!=0;
}
public String getMessage()
{
return this.genericException;
}
public String getMessage(String key)
{
if(key==null) return "";
key=key.trim();
return this.propertyException.get(key);
}
public List<String> getProperties()
{
return new LinkedList<>(this.propertyException.keySet());
}
}

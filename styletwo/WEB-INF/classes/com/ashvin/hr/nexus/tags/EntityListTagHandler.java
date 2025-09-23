package com.ashvin.hr.nexus.tags;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

import java.lang.reflect.*;
import java.util.*;

public class EntityListTagHandler extends BodyTagSupport
{
private List<Class> list;
int index;
private String populateClass;
private String populateMethod;
private String name;
public void setPopulateClass(String populateClass)
{
this.populateClass=populateClass;
}
public String getPopulateClass()
{
return this.populateClass;
}
public void setPopulateMethod(String populateMethod)
{
this.populateMethod=populateMethod;
}
public String getPopulateMethod()
{
return this.populateMethod;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}
public EntityListTagHandler()
{
this.reset();
}
public void reset()
{
if(list!=null) list.clear();
list=null;
index=0;
populateClass=null;
populateMethod=null;
name=null;
}
public int doStartTag()
{
try
{
if(name==null || name.trim().length()==0) return super.SKIP_BODY;
Class c=Class.forName(this.populateClass);
Object obj=c.newInstance();
Class parameters[]=new Class[0]; 
Method method=c.getMethod(this.populateMethod,parameters);
Object listObject=method.invoke(obj);
list=(List)listObject;
}catch(NoSuchMethodException nsme)		//line 23
{
System.out.println("Exception raise because no such type of methods exist against respective class");
System.out.println(nsme);
return super.SKIP_BODY;
}
catch(ClassNotFoundException cnfe)
{
System.out.println(cnfe);
return super.SKIP_BODY;
}
catch(InstantiationException ie)	//invoke
{
System.out.println("Exception: "+ie);
return super.SKIP_BODY;
}catch(IllegalAccessException iae)	//line 43 private field access
{
System.out.println(iae);
return super.SKIP_BODY;
}
catch(InvocationTargetException ite)	// when exception raise on calling some methods should be arrived in ite wrapper accessed using 'getCause()' method
{
System.out.println(ite);
return super.SKIP_BODY;
}
if(list==null || list.size()==0) return super.SKIP_BODY;
index=0;
Object bean=list.get(index);
pageContext.setAttribute("serialNumber",(index+1),PageContext.REQUEST_SCOPE);
pageContext.setAttribute(this.name,bean,PageContext.REQUEST_SCOPE);
return super.EVAL_BODY_INCLUDE;
}
public int doAfterBody()
{
index++;
if(index==list.size()) return super.SKIP_BODY;
pageContext.setAttribute("serialNumber",(index+1),PageContext.REQUEST_SCOPE);
pageContext.setAttribute(this.name,list.get(index),PageContext.REQUEST_SCOPE);
return super.EVAL_BODY_AGAIN;
}
public int doEndTag()
{
this.reset();
return super.EVAL_PAGE;
}
}

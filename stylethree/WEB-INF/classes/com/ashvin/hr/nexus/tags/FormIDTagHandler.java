package com.ashvin.hr.nexus.tags;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import java.util.*;
import java.io.*;

public class FormIDTagHandler extends TagSupport
{
public FormIDTagHandler()
{
reset();
}
public void reset()
{
// do nothing;
}
public int doStartTag()
{
String formId=UUID.randomUUID().toString();
pageContext.setAttribute(formId,formId,PageContext.SESSION_SCOPE);
JspWriter jw=pageContext.getOut();
try
{
jw.print("<input type='hidden' id='formId' name='formId' value='"+formId+"'>");
}catch(IOException exception)
{
//do nothing
}
return super.SKIP_BODY;
}
public int doEndTag()
{
reset();
return super.EVAL_PAGE;
} 
} 

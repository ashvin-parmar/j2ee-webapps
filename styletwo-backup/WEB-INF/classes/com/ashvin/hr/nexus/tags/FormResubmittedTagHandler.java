package com.ashvin.hr.nexus.tags;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;
import javax.servlet.*;

public class FormResubmittedTagHandler extends TagSupport
{
public FormResubmittedTagHandler()
{
reset();
}
public void reset()
{
//do nothing
}
public int doStartTag()
{
HttpServletRequest request=(HttpServletRequest)pageContext.getRequest();
String formId=request.getParameter("formId");
if(formId==null)
{
return super.EVAL_BODY_INCLUDE;
}
String formIdFromSession=(String)pageContext.getAttribute(formId,PageContext.SESSION_SCOPE);
if(formIdFromSession==null)
{
return super.EVAL_BODY_INCLUDE;
}
pageContext.removeAttribute(formId,PageContext.SESSION_SCOPE);
if(formIdFromSession.equals(formId))
{
return super.SKIP_BODY;
}
else 
{
return super.EVAL_BODY_INCLUDE;
}
}
public int doEndTag()
{
reset();
return super.EVAL_PAGE;
}
}

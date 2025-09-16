package com.ashvin.hr.nexus.tags;

import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.*;

public class ValidateLoginTagHandler extends TagSupport
{
public ValidateLoginTagHandler()
{
this.reset();
}
public void reset()
{
// do nothing
}
public int doStartTag()
{
String username=(String)pageContext.getAttribute("username",PageContext.SESSION_SCOPE);
if(username==null)
{
return super.EVAL_BODY_INCLUDE;
}
return super.SKIP_BODY;
}
public int doEndTag()
{
reset();
return super.EVAL_PAGE;
}
}

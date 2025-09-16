package com.ashvin.hr.nexus.tags;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;
import java.util.*;

public class DesignationsTagHandler extends BodyTagSupport
{
private List<DesignationBean> designations;
private int index;
private DesignationBean designationBean;
public int DESIGNATION=1;
public int EMPLOYEE=2;
public int HOME=0;
public int module;
public DesignationsTagHandler()
{
reset();
}
public void setModule(int module)
{
this.module=module;
}
public int getModule()
{
return this.module;
}
public int doStartTag()
{
designations=new ArrayList<>();
List<DesignationDTO> dlDesignations=null;
try
{
dlDesignations=(new DesignationDAO()).getAll();
}catch(DAOException daoException)
{
System.out.println(daoException.getMessage());
return super.SKIP_BODY;
}
if(dlDesignations.size()==0) return super.SKIP_BODY;
for(DesignationDTO dlDesignation:dlDesignations)
{
designationBean=new DesignationBean();
designationBean.setCode(dlDesignation.getCode());
designationBean.setTitle(dlDesignation.getTitle());
designations.add(designationBean);
}
index=0;
designationBean=designations.get(index);
pageContext.setAttribute("designationBean",designationBean,PageContext.REQUEST_SCOPE);
pageContext.setAttribute("serialNumber",new Integer(index+1),PageContext.REQUEST_SCOPE);
return super.EVAL_BODY_INCLUDE;
}
public int doAfterBody()
{
index++;
if(index==designations.size()) return super.SKIP_BODY;
//DATA ALLOCATION
designationBean=designations.get(index);
pageContext.setAttribute("designationBean",designationBean,PageContext.REQUEST_SCOPE);
pageContext.setAttribute("serialNumber",index+1,PageContext.REQUEST_SCOPE);
return super.EVAL_BODY_AGAIN;
}
public int doEndtag()
{
reset();
return super.EVAL_PAGE;
}
public void reset()
{
this.index=0;
if(designations!=null)
{
designations.clear();
designations=null;
}
}
}

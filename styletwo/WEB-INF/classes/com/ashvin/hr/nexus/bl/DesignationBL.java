package com.ashvin.hr.nexus.bl;
import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.DesignationBean;
import java.util.*;

public class DesignationBL
{
public List<DesignationBean> getAll()
{
List<DesignationBean> designations=new LinkedList<>();
try
{
List<DesignationDTO> dlDesignations=(new DesignationDAO().getAll());
DesignationBean designation;
for(DesignationDTO dlDesignation:dlDesignations)
{
designation=new DesignationBean();
designation.setCode(dlDesignation.getCode());
designation.setTitle(dlDesignation.getTitle());
designations.add(designation);
}
}catch(DAOException daoException)
{
// do nothing
}
return designations;
}
}

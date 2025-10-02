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
public DesignationBean getByCode(int code) throws BLException
{
BLException blException=new BLException();
DesignationDAO designationDAO=new DesignationDAO();
if(code<=0) blException.setPropertyException("code","Code should not lesser than equals to zero");
else
{
boolean exists=designationDAO.isCodeExists(code);
if(exists==false) blException.setPropertyException("code","Invalid code");
}
if(blException.hasException()) throw blException;
DesignationBean designationBean=null;
try
{
DesignationDTO designationDTO=designationDAO.getByCode(code);
designationBean=new DesignationBean();
designationBean.setCode(designationDTO.getCode());
designationBean.setTitle(designationDTO.getTitle());
return designationBean;
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
throw  blException;
}
}
public void add(DesignationBean designationBean) throws BLException
{
int code=designationBean.getCode();
String title=designationBean.getTitle();
BLException blException=new BLException();
if(code!=0) blException.setPropertyException("code","Code should be zero");
if(title==null) blException.setPropertyException("title","Title required");
else 
{
title=title.trim();
if(title.length()==0) blException.setPropertyException("title","Title length is zero, title required");
}
DesignationDAO designationDAO=new DesignationDAO();
boolean titleExists=designationDAO.isTitleExists(title);
if(titleExists) blException.setPropertyException("title","Title already exists");
if(blException.hasException()) throw blException;
try
{
DesignationDTO designationDTO=new DesignationDTO();
designationDTO.setCode(0);
designationDTO.setTitle(title);
designationDAO.add(designationDTO);
designationBean.setCode(designationDTO.getCode());
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
throw blException;
}
}
public void update(DesignationBean designationBean) throws BLException
{
int code=designationBean.getCode();
String title=designationBean.getTitle();
BLException blException=new BLException();
if(code<=0) blException.setPropertyException("code","Code should not lesser than equals to zero");
if(title==null) blException.setPropertyException("title","Title required");
else 
{
title=title.trim();
if(title.length()==0) blException.setPropertyException("title","Title length is zero, title required");
}
if(blException.hasException()) throw blException;
DesignationDAO designationDAO=new DesignationDAO();
try
{
DesignationDTO designationDTO=null;
try
{
designationDTO=designationDAO.getByTitle(title);
if(designationDTO.getCode()!=code)
{
blException.setPropertyException("title","Title already exists");
throw blException;
}
}catch(DAOException daoException1)
{
// If here means title not exists
}
designationDTO=new DesignationDTO();
designationDTO.setCode(code);
designationDTO.setTitle(title);
designationDAO.update(designationDTO);
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
throw blException;
}
}
public void delete(int code) throws BLException
{
BLException blException=new BLException();
if(code<=0) blException.setPropertyException("code","Code should not lesser than equals to zero");
if(blException.hasException()) throw blException;
DesignationDAO designationDAO=new DesignationDAO();
try
{
//Over here everywhere dl method are called, unless use data-structures to maintain all data in it and respectively search and update in DS
designationDAO.delete(code);
}catch(DAOException daoException)
{
blException.setGenericException(daoException.getMessage());
throw blException;
}
}
}

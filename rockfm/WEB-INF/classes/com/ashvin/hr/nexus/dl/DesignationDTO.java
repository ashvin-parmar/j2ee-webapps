package com.ashvin.hr.nexus.dl;
public class DesignationDTO implements java.io.Serializable,Comparable<DesignationDTO>
{
private int code;
private String title;
public void setCode(int code)
{
this.code=code;
}
public int getCode()
{
return this.code;
}
public void setTitle(String title)
{
this.title=title;
}
public String getTitle()
{
return this.title;
}
public boolean equals(Object object)
{
if(!(object instanceof DesignationDTO)) return false;
DesignationDTO other=(DesignationDTO)object;
return this.code==other.code;
}
public int compareTo(DesignationDTO designationDTO)
{
return designationDTO.getTitle().compareToIgnoreCase(this.title);
}
public int hashCode()
{
return this.code;
}
}

package com.ashvin.hr.nexus.dl;

import java.util.*;
import java.math.*;

public class EmployeeDTO implements java.io.Serializable,Comparable<EmployeeDTO>
{
private String employeeId;
private String name;
private DesignationDTO designation;
private Date dateOfBirth;
private String gender;
private boolean isIndian;
private BigDecimal basicSalary;
private String panNumber;
private String aadharCardNumber;
public EmployeeDTO()
{
this.employeeId="";
this.name="";
this.designation=null;
this.dateOfBirth=null;
this.gender="";
this.isIndian=false;
this.basicSalary=null;
this.panNumber="";
this.aadharCardNumber="";
}
public void setEmployeeId(java.lang.String employeeId)
{
this.employeeId=employeeId;
}
public java.lang.String getEmployeeId()
{
return this.employeeId;
}
public void setName(java.lang.String name)
{
this.name=name;
}
public java.lang.String getName()
{
return this.name;
}
public void setDesignation(DesignationDTO designation)
{
this.designation=designation;
}
public DesignationDTO getDesignation()
{
return this.designation;
}
public void setDateOfBirth(java.util.Date dateOfBirth)
{
this.dateOfBirth=dateOfBirth;
}
public java.util.Date getDateOfBirth()
{
return this.dateOfBirth;
}
public void setGender(String gender)
{
this.gender=gender;
}
public String getGender()
{
return this.gender;
}
public void setIsIndian(boolean isIndian)
{
this.isIndian=isIndian;
}
public boolean getIsIndian()
{
return this.isIndian;
}
public void setBasicSalary(java.math.BigDecimal basicSalary)
{
this.basicSalary=basicSalary;
}
public java.math.BigDecimal getBasicSalary()
{
return this.basicSalary;
}
public void setPANNumber(java.lang.String panNumber)
{
this.panNumber=panNumber;
}
public java.lang.String getPANNumber()
{
return this.panNumber;
}
public void setAadharCardNumber(java.lang.String aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
public java.lang.String getAadharCardNumber()
{
return this.aadharCardNumber;
}
public boolean equals(Object other)
{
if(!(other instanceof EmployeeDTO)) return false;
EmployeeDTO employeeDTO=(EmployeeDTO)other;
return this.employeeId.equalsIgnoreCase(employeeDTO.employeeId);
}
public int compareTo(EmployeeDTO other)
{
return this.employeeId.compareTo(other.employeeId);
}
public int hashCode()
{
return this.employeeId.hashCode();
}
}

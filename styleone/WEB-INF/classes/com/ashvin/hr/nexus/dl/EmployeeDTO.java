package com.ashvin.hr.nexus.dl;

import java.util.*;
import java.math.*;

public class EmployeeDTO implements java.io.Serializable,Comparable<EmployeeDTO>
{
private int employeeId;
private String name;
private int designationCode;
private Date dateOfBirth;
private char gender;
private boolean isIndian;
private BigDecimal basicSalary;
private String panNumber;
private String aadharCardNumber;
public void setEmployeeId(int employeeId)
{
this.employeeId=employeeId;
}
public int getEmployeeId()
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
public void setDesignationCode(int designationCode)
{
this.designationCode=designationCode;
}
public int getDesignationCode()
{
return this.designationCode;
}
public void setDateOfBirth(java.util.Date dateOfBirth)
{
this.dateOfBirth=dateOfBirth;
}
public java.util.Date getDateOfBirth()
{
return this.dateOfBirth;
}
public void setGender(char gender)
{
this.gender=gender;
}
public char getGender()
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
public BigDecimal getBasicSalary()
{
return this.basicSalary;
}
public void setPanNumber(java.lang.String panNumber)
{
this.panNumber=panNumber;
}
public java.lang.String getPanNumber()
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
return employeeDTO.employeeId==this.employeeId;
}
public int compareTo(EmployeeDTO other)
{
return other.employeeId-this.employeeId;
}
public int hashCode()
{
return this.employeeId;
}
}

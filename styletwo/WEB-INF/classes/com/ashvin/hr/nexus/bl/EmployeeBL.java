package com.ashvin.hr.nexus.bl;

import com.ashvin.hr.nexus.dl.*;
import com.ashvin.hr.nexus.beans.*;

import java.util.*;
import java.text.*;
import java.math.*;

public class EmployeeBL
{
public List<EmployeeBean> getAll()
{
List<EmployeeBean> employees=new LinkedList<>();
EmployeeBean employee;
try
{
List<EmployeeDTO> dlEmployees=(new EmployeeDAO().getAll());
DesignationDTO designation=null;
SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
for(EmployeeDTO dlEmployee:dlEmployees)
{
employee=new EmployeeBean();
employee.setEmployeeId(dlEmployee.getEmployeeId());
employee.setName(dlEmployee.getName());
designation=dlEmployee.getDesignation();
employee.setDesignationCode(designation.getCode());
employee.setDesignation(designation.getTitle());
employee.setIsIndian(dlEmployee.getIsIndian());
employee.setGender((dlEmployee.getGender().equals("M")?"Male":"Female"));
employee.setDateOfBirth(sdf.format(dlEmployee.getDateOfBirth()));
employee.setBasicSalary(dlEmployee.getBasicSalary().toPlainString());
employee.setPANNumber(dlEmployee.getPANNumber());
employee.setAadharCardNumber(dlEmployee.getAadharCardNumber());
employees.add(employee);
}
}catch(DAOException daoException)
{
//do nothing
}
return employees;
}
}

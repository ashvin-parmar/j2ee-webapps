package com.ashvin.hr.nexus.beans;

public class ModuleManagementBean implements java.io.Serializable
{
private int module=0;
public static int HOME=0;
public static int DESIGNATION=1;
public static int EMPLOYEE=2;
public ModuleManagementBean()
{
this.module=0;
HOME=0;
DESIGNATION=1;
EMPLOYEE=2;
}
public void setModule(int module)
{
this.module=module;
}
public int getModule()
{
return this.module;
}
public int getHOME()
{
return HOME;
}
public int getDESIGNATION()
{
return DESIGNATION;
}
public int getEMPLOYEE()
{
return EMPLOYEE;
}
}

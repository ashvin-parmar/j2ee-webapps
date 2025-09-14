package com.ashvin.hr.nexus.beans;

import java.util.*;

public class RefreshBean implements java.io.Serializable 
{
private String formId="";
public RefreshBean()
{
this.formId="";
}
public void setFormId(String formId)
{
this.formId=UUID.randomUUID().toString();
//System.out.println("UUID: "+this.formId);
}
public String getFormId()
{
return this.formId;
}
}

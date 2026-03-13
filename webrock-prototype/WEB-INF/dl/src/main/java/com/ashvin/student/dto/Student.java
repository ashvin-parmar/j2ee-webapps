package com.ashvin.student.dto;

public class Student implements java.io.Serializable
{
private int rollNumber;
private String name;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public void setName(String name)
{
this.name=name;
}
public int getRollNumber()
{
return this.rollNumber;
}
public String getName()
{
return this.name;
}
}

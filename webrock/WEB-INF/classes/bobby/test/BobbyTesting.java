package bobby.test;

public class BobbyTesting implements java.io.Serializable
{
private Integer rollNumber;
private String name;
public void setRollNumber(int rollNumber)
{
this.rollNumber=rollNumber;
}
public int getRollNumber()
{
return this.rollNumber;
}
public void setName(String name)
{
this.name=name;
}
public String getName()
{
return this.name;
}

}

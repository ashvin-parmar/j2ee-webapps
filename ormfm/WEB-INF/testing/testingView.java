import java.lang.*;
import java.util.*;
import java.text.*;
import java.io.*;

import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

class testingView {
public static void main(String[] args) 
{
try
{
DataManager dm=null;
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
dm=DataManager.getDataManager();
}catch(DataException de)
{
System.out.println("Problem: "+de.getMessage());
return;
}
try
{
List<V1> v1s;
dm.begin();
v1s=(List<V1>)dm.select(V1.class).fire();       //V1 View is used
dm.end();
for(V1 v1:v1s)
{
System.out.println(v1.toString());
}
}catch(DataException de)
{
dm.end();
de.printStackTrace();
System.out.println("Statement1 Problem: "+de.getMessage());
}
System.out.println();
System.out.println();

try
{
dm.begin();
List<V4> v4s=(List<V4>)dm.select(V4.class).orderBy("first_name").fire();
dm.end();
for(V4 v4:v4s)
{
System.out.println(v4.toString());
}
}catch(DataException de)
{
dm.end();
System.out.println("Statement 2 Problem: "+de.getMessage());
}
System.out.println();
System.out.println();

try
{
List<V1> v1s;
dm.begin();
v1s=(List<V1>)dm.view(V1.class);
dm.end();
System.out.println("Size: "+v1s.size());

}catch(DataException de)
{
dm.end();
System.out.println("Statement1 Problem: "+de.getMessage());
de.printStackTrace();
}
System.out.println();
System.out.println();

try
{
dm.begin();
List<V4> v4s=(List<V4>)dm.view(V4.class);
dm.end();
for(V4 std:v4s)
{
System.out.print(std.getFirstName()+" ");
System.out.print(std.getRollNumber()+" ");
System.out.print(std.getLastName()+" ");
System.out.print(std.getGender()+" ");
System.out.print(std.getCourseCode()+" ");
System.out.print(std.getAadharCardNumber()+" ");
System.out.println(std.getDateOfBirth());
}
}catch(DataException de)
{
dm.end();
System.out.println("Statement 2 Problem: "+de.getMessage());
}

}catch(Exception exception)
{
System.out.println(exception);
}
}
}


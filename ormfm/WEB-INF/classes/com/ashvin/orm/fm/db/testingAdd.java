import java.lang.*;

import java.util.*;

public class testingAdd
{
public static void main(String args[])
{
DataManager dm=DataManager.getDataManager();
try
{
dm.begin();
Course c=new Course();
c.setTitle(args[0]);
Integer code=(Integer)dm.save(c);
dm.end();
System.out.println("Course added with code as : "+code);

dm.begin();
List<Course> courses=(List<Course>)dm.query(Course.class).fire();
dm.end();

for(Course course:courses)
{
System.out.println("----------");
System.out.println("Course code: "+course.getCode());
System.out.println("Course Name: "+course.getTitle());
System.out.println("----------");
}
}catch(DataException de)
{
dm.end();
System.out.println(de);
}
}
}

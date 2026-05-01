import java.lang.*;

import java.util.*;

public class testingUpdate
{
public static void main(String args[])
{
DataManager dm=DataManager.getDataManager();
try
{
dm.begin();
Course c=new Course();
int code=Integer.parseInt(args[0]);
c.setCode(code);
c.setTitle(args[1]);
dm.update(c);
dm.end();
System.out.println("Course updated with code as : "+code);

dm.begin();
List<Course> courses=(List<Course>)dm.query(Course.class).where("code").eq(code).fire();
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

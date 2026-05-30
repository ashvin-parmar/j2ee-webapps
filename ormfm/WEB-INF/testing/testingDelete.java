import java.lang.*;

import java.util.*;
import java.text.*;
import java.io.*;

import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

public class testingDelete
{
public static void main(String args[])
{
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
DataManager dm=DataManager.getDataManager();
dm.begin();
int code=Integer.parseInt(args[0]);
dm.delete(Course.class,code);
dm.end();
System.out.println("Course deleted with code as : "+code);

System.out.println("Courses are: ");
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
System.out.println(de);
}
}
}

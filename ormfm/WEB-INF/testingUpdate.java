/**
 * @author Ashvin
 * @since 2026-05-30
 * Description: 
 */


import java.lang.*;

import java.util.*;
import java.text.*;
import java.io.*;

import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

class testingUpdate {
    public static void main(String[] args) {
        
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
DataManager dm=DataManager.getDataManager();
dm.begin();
Course c=new Course();
c.setCode(Integer.parseInt(args[0]));
c.setTitle(args[1]);
dm.update(c);
dm.end();
System.out.println("Course updated");

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


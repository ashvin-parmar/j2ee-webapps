import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import java.io.*;

class testing
{
public static void main(String args[])
{
try
{
DataManager.initialize(new File(System.getProperty("user.dir")));
DataManager dataManager=DataManager.getDataManager();
}catch(DataException dataException)
{
System.out.println(dataException);
}
}
}

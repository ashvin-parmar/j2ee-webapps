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
System.out.println("------------DATA load ends------");
System.out.println(ORMDataModel.getAllInfo().size());

}catch(DataException dataException)
{
System.out.println(dataException);
}
}
}

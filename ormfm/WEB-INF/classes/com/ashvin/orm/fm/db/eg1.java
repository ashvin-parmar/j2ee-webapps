import java.io.*;
import com.google.gson.*; 
import java.sql.*;

class psp
{
public static void main(String args[])
{
File file=new File("conf.json");
if(file.exists())
{
try
{
FileReader fileReader=new FileReader(file);
JsonObject jsonObj=JsonParser.parseReader(fileReader).getAsJsonObject();
String jdbcDriver=(jsonObj.get("jdbc-driver")!=null?jsonObj.get("jdbc-driver").getAsString():"");
String connectionURL=(jsonObj.get("connection-url")!=null?jsonObj.get("connection-url").getAsString():"");
String username=(jsonObj.get("username")!=null?jsonObj.get("username").getAsString():"");
String password=(jsonObj.get("password")!=null?jsonObj.get("password").getAsString():"");

System.out.println(jdbcDriver);
System.out.println(connectionURL);
System.out.println(username);
System.out.println(password);

Class c=Class.forName(jdbcDriver);
Connection connection=DriverManager.getConnection(connectionURL,username,password);

DatabaseMetaData dbMetaData=connection.getMetaData();
System.out.println(dbMetaData);

ResultSet tables=dbMetaData.getTables(null,null,"%",new String[]{"TABLE"});
while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");
System.out.println("Table name: "+tableName);
ResultSet tableColumns=dbMetaData.getColumns(null,null,tableName,null);
ResultSet pkTableColumns=dbMetaData.getPrimaryKeys(null,null,tableName);
ResultSet fkTableColumns=dbMetaData.getImportedKeys(null,null,tableName);

while(pkTableColumns.next())
{
System.out.println("Primary key Column: "+pkTableColumns.getString("COLUMN_NAME"));
}
while(tableColumns.next())
{
String columnName=tableColumns.getString("COLUMN_NAME");
String columnType=tableColumns.getString("TYPE_NAME");
int size=tableColumns.getInt("COLUMN_SIZE");
String nullable=tableColumns.getString("IS_NULLABLE");
String isAutoIncrement=tableColumns.getString("IS_AUTOINCREMENT");
System.out.printf("Field: %s | Type: %s(%d) | Nullable: %s | Auto Increment: %s\n",columnName,columnType,size,nullable,isAutoIncrement);
}
while(fkTableColumns.next())
{
System.out.printf("Foreign Keys: %s -> references %s(%s)\n",fkTableColumns.getString("FKCOLUMN_NAME"),fkTableColumns.getString("PKTABLE_NAME"),fkTableColumns.getString("PKCOLUMN_NAME"));
}
}

}catch(Exception e)
{
System.out.println("Exception: "+e.getMessage());
}

}
else
{
System.out.println("conf.json file does not exists.");
}
}
}

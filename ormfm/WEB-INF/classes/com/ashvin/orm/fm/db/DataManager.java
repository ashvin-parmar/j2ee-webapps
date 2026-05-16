
import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;
import com.google.gson.*;
import java.lang.reflect.*;
import com.ashvin.orm.fm.utils.*;

public class DataManager
{
private String jdbcDriver="";
private String connectionURL="";
private String username="";
private String password="";

private Connection connection=null;

private String qStatement="";
private Class<?> qClass=null;
private boolean whereUsed=false;

private static DataManager dataManager=null;

private DataManager()
{
File file=new File("conf.json");
if(file.exists())
{
try
{
FileReader fileReader=new FileReader(file);
JsonObject jsonObj=JsonParser.parseReader(fileReader).getAsJsonObject();
jdbcDriver=(jsonObj.get("jdbc-driver")!=null?jsonObj.get("jdbc-driver").getAsString():"");
connectionURL=(jsonObj.get("connection-url")!=null?jsonObj.get("connection-url").getAsString():"");
username=(jsonObj.get("username")!=null?jsonObj.get("username").getAsString():"");
password=(jsonObj.get("password")!=null?jsonObj.get("password").getAsString():"");

//System.out.println(jdbcDriver);
//System.out.println(connectionURL);
//System.out.println(username);
//System.out.println(password);
}catch(Exception exception)
{
System.out.println("Error reading conf.json: "+exception);
}
}
if(!jdbcDriver.isBlank()) 
{
try
{
Class.forName(jdbcDriver);
}catch(ClassNotFoundException e)
{
System.out.println("JDBC Driver not found: "+e);
}
}
}
public static DataManager getDataManager()
{
if(DataManager.dataManager==null)
{
DataManager.dataManager=new DataManager();
}
return DataManager.dataManager;
}
public void begin() throws DataException
{
try
{
if(connection!=null) connection.close();
reset();
connection=DriverManager.getConnection(connectionURL,username,password);
}catch(SQLException sqlException)
{
throw new DataException(sqlException);
}
}
public void end()
{
try
{
if(connection!=null) connection.close();
}catch(SQLException sqlException)
{
System.out.println("Error closing connection: "+sqlException);
}
reset();
connection=null;
}
public void reset()
{
this.qStatement="";
this.qClass=null;
this.whereUsed=false;
}
public Object save(Object obj) throws DataException
{
if(connection==null) throw new DataException("Call begin() before save()");
try
{
Class<?> objClass=obj.getClass();
TableSchema tableSchema=Data.getInfo(objClass);
List<FieldSchema> nonAutoIncrementFields=tableSchema.getNonAutoIncrementFields();
StringBuilder values=new StringBuilder();
StringBuilder columns=new StringBuilder();
String sqlStatement="";
PreparedStatement preparedStatement;
ResultSet resultSet;
ResultSet generatedKeys;
for(int i=0;i<nonAutoIncrementFields.size();i++)
{
FieldSchema fs=nonAutoIncrementFields.get(i);
String fieldName=fs.getFieldName();
String columnName=fs.getColumnName();

columns.append(columnName);
Object value=null;
try
{
if(fs.isGetterAllowed())
{
try
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method getterMethod=objClass.getMethod("get"+sFieldName);
value=getterMethod.invoke(obj);
}catch(Exception e)
{
//System.out.println("invoke exception: "+e);
}
}
else if(fs.isPublicAllowed())
{
Field field=objClass.getField(fieldName);
value=field.get(obj);
}
values.append(formatValue(value));
}catch(Exception exception)
{
values.append("null");
}
if(fs.isPrimaryKey() || fs.isUnique())
{
sqlStatement="select "+columnName+" from "+tableSchema.getTableName()+" where "+columnName+"="+formatValue(value)+";";
System.out.println(sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Column: "+columnName+" must unique.");
}
resultSet.close();
preparedStatement.close();
}
if(fs.isForeignKey())
{
String fkParentClass=fs.getFKParentClass();
String fkParentColumn=fs.getFKParentColumn();
sqlStatement="select "+fkParentColumn+" from "+fkParentClass+" where "+fkParentColumn+"="+formatValue(value)+";";
System.out.println(sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Column "+columnName+" value must need to matched with "+fkParentClass+"'s "+fkParentColumn);
}
resultSet.close();
preparedStatement.close();
}
if(i+1<nonAutoIncrementFields.size())
{
columns.append(",");
values.append(",");
}
}
sqlStatement="insert into "+tableSchema.getTableName()+" ("+columns.toString()+") values("+values.toString()+");";
//System.out.println(sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement,Statement.RETURN_GENERATED_KEYS);
preparedStatement.executeUpdate();
generatedKeys=preparedStatement.getGeneratedKeys();
if(generatedKeys.next())
{
Object result=generatedKeys.getString(1);
List<FieldSchema> autoIncrementFields=tableSchema.getAutoIncrementFields();
if(!autoIncrementFields.isEmpty())
{
Class<?> autoIncrementType=autoIncrementFields.get(0).getType();
String keyValue=generatedKeys.getString(1);
generatedKeys.close();
preparedStatement.close();
return ORMUtils.parseTo(autoIncrementType,keyValue);
}
}
generatedKeys.close();
preparedStatement.close();
return null;
}catch(DataException de)
{
throw de;
}
catch(Exception exception)
{
throw new DataException(exception);
}
}
public void update(Object obj) throws DataException
{
if(connection==null) throw new DataException("Call begin() before update()");
try
{
Class<?> objClass=obj.getClass();
TableSchema tableSchema=Data.getInfo(objClass);
List<FieldSchema> nonAutoIncrementFields=tableSchema.getNonAutoIncrementFields();
List<FieldSchema> primaryKeyFields=tableSchema.getPrimaryKeyFields();
if(primaryKeyFields.isEmpty()) throw new DataException("No @PrimaryKey found in : "+objClass.getName());

StringBuilder setPart=new StringBuilder();
for(int i=0;i<nonAutoIncrementFields.size();i++)
{
FieldSchema fs=nonAutoIncrementFields.get(i);
String fieldName=fs.getFieldName();
String columnName=fs.getColumnName();
try
{
Object value=null;
if(fs.isGetterAllowed())
{
try
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method getterMethod=objClass.getMethod("get"+sFieldName);
value=getterMethod.invoke(obj);
}catch(Exception e)
{
//System.out.println("invoke exception: "+e);
}
}
else if(fs.isPublicAllowed())
{
Field field=objClass.getField(fieldName);
value=field.get(obj);
}
setPart.append(fs.getColumnName()).append("=").append(formatValue(value));
}catch(Exception exception)
{
setPart.append(fs.getColumnName()).append("=").append("null");
}
if(i+1<nonAutoIncrementFields.size()) setPart.append(",");
}
StringBuilder wherePart=new StringBuilder();
for(int i=0;i<primaryKeyFields.size();i++)
{
FieldSchema fs=primaryKeyFields.get(i);
String fieldName=fs.getFieldName();
String columnName=fs.getColumnName();
try
{
Object value=null;
if(fs.isGetterAllowed())
{
try
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method getterMethod=objClass.getMethod("get"+sFieldName);
value=getterMethod.invoke(obj);
}catch(Exception e)
{
//System.out.println("invoke exception: "+e);
}
}
else if(fs.isPublicAllowed())
{
Field field=objClass.getField(fieldName);
value=field.get(obj);
}
wherePart.append(fs.getColumnName()).append("=").append(formatValue(value));
}catch(Exception exception)
{
wherePart.append(fs.getColumnName()).append("=").append("null");
}
if(i+1<primaryKeyFields.size()) wherePart.append(",");
}
String sqlStatement="update "+tableSchema.getTableName()
+" set "+setPart
+" where "+wherePart;
System.out.println("SQL: "+sqlStatement);
PreparedStatement preparedStatement=connection.prepareStatement(sqlStatement);
preparedStatement.executeUpdate();
preparedStatement.close();
}catch(DataException de)
{
throw de;
}catch(Exception exception)
{
throw new DataException(exception);
}

}
public void delete(Class<?> objClass,Object primaryKey) throws DataException
{
if(connection==null) throw new DataException("Call begin() before delete()");
try
{
TableSchema tableSchema=Data.getInfo(objClass);
List<FieldSchema> primaryKeyFields=tableSchema.getPrimaryKeyFields();
if(primaryKeyFields.isEmpty()) throw new DataException("No @PrimaryKey found in: "+objClass.getName());
FieldSchema primaryKeyField=primaryKeyFields.get(0);
String sqlStatement="delete from "+tableSchema.getTableName()
+" where "+primaryKeyField.getColumnName()
+"="+formatValue(primaryKey);
System.out.println("SQL Statement: "+sqlStatement);
PreparedStatement preparedStatement=connection.prepareStatement(sqlStatement);
preparedStatement.executeUpdate();
preparedStatement.close();
}catch(DataException de)
{
throw de;
}catch(Exception exception)
{
throw new DataException(exception);
}
}
public DataManager query(Class objClass) throws DataException
{
TableSchema tableSchema=Data.getInfo(objClass);
this.qClass=objClass;
this.qStatement="select * from "+tableSchema.getTableName();
return this;
}
public DataManager where(String columnName)
{
if(!whereUsed) this.qStatement+=" where "+columnName;
else this.qStatement+=columnName;
whereUsed=true;
return this;
}
public DataManager eq(Object value)
{
this.qStatement+=("="+formatValue(value));
return this;
}
public DataManager gt(Object value)
{
this.qStatement+=(">"+formatValue(value));
return this;
}
public DataManager lt(Object value)
{
this.qStatement+=("<"+formatValue(value));
return this;
}
public DataManager ge(Object value)
{
this.qStatement+=(">="+formatValue(value));
return this;
}
public DataManager le(Object value)
{
this.qStatement+=("<="+formatValue(value));
return this;
}
public DataManager ne(Object value)
{
this.qStatement+=("!="+formatValue(value));
return this;

}
public DataManager and()
{
this.qStatement+=" and ";
return this;
}
public DataManager or()
{
this.qStatement+=" or ";
return this;
}
public Object fire() throws DataException
{
if(connection==null) throw new DataException("Call begin() before fire()");
if(qClass==null) throw new DataException("Call query() before fire()");
try
{
System.out.println("SQLStatement: "+qStatement);
TableSchema tableSchema=Data.getInfo(qClass);
PreparedStatement preparedStatement=connection.prepareStatement(qStatement);
ResultSet resultSet=preparedStatement.executeQuery();
List<Object> resultList=new ArrayList<>();
while(resultSet.next())
{
Object instance=qClass.getDeclaredConstructor().newInstance();
for(FieldSchema fs:tableSchema.getAllFields())
{
try
{
String fieldName=fs.getFieldName();
Object value=resultSet.getObject(fs.getColumnName());
if(fs.isSetterAllowed())
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method setterMethod=qClass.getMethod("set"+sFieldName,fs.getType());
setterMethod.invoke(instance,value);
}
else if(fs.isPublicAllowed())
{
Field field=qClass.getField(fieldName);
field.set(instance,value);
}
else
{
System.out.println("Field: "+fieldName+" not allowed to show");
}
}catch(Exception exception)
{
System.out.println("Exception: "+exception);
}
}
resultList.add(instance);
}
resultSet.close();
preparedStatement.close();
reset();
return resultList;
}catch(DataException de)
{
throw de;
}catch(Exception exception)
{
throw new DataException(exception);
}
}
private String getValueFor(FieldSchema fs,Object obj)
{
try
{
Field field=obj.getClass().getField(fs.getFieldName());
Object value=field.get(obj);
return formatValue(value);
}catch(Exception exception)
{
return "null";
}
}
private String formatValue(Object value)
{
if(value==null) return "null";
if(value instanceof String) return "'"+value+"'";
if(value instanceof java.util.Date) 
{
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
return "'"+sdf.format(value)+"'";
}
return String.valueOf(value);
}
}


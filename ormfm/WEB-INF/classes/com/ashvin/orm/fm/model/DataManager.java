package com.ashvin.orm.fm.model;

import java.sql.*;
import java.io.*;
import java.util.*;
import java.text.*;
import com.google.gson.*;
import java.lang.reflect.*;
import com.ashvin.orm.fm.utils.*;
import com.ashvin.orm.fm.exceptions.*;

public class DataManager
{
private static Map<Class<?>,Map<String,StatementDS>> statements=new HashMap<>();
private String jdbcDriver="";
private String connectionURL="";
private String username="";
private String password="";
private String packageName="";
private Connection connection=null;

private String qStatement="";
private Class<?> qClass=null;
private boolean whereUsed=false;

private static DataManager dataManager=null;
private static File parentWorkingDirectory;
private static List<TableSchema> tables;
private DataManager() throws DataException
{
try
{
File file=new File(parentWorkingDirectory,"conf.json");
if(!file.exists())
{
throw new DataException("Configuration file required");
}
FileReader fileReader=new FileReader(file);
JsonObject jsonObj=JsonParser.parseReader(fileReader).getAsJsonObject();
if(jsonObj==null)
{
throw new DataException("Invalid json configuration file");
}
jdbcDriver=(jsonObj.get("jdbc-driver")!=null?jsonObj.get("jdbc-driver").getAsString():"");
connectionURL=(jsonObj.get("connection-url")!=null?jsonObj.get("connection-url").getAsString():"");
username=(jsonObj.get("username")!=null?jsonObj.get("username").getAsString():"");
password=(jsonObj.get("password")!=null?jsonObj.get("password").getAsString():"");
packageName=(jsonObj.get("package-name")!=null?jsonObj.get("package-name").getAsString():"testing.pojo");

//System.out.println("JDBC Driver: "+jdbcDriver);
//System.out.println("Connection URL: "+connectionURL);
//System.out.println("Username: "+username);
//System.out.println("Pass: "+password);
//System.out.println("Package name: "+packageName);
try
{
Class c=Class.forName(jdbcDriver);
}catch(Exception exception)
{
throw new DataException("Invalid json configuration file");
}
this.jdbcDriver=jdbcDriver;
this.connectionURL=connectionURL;
this.username=username;
this.password=password;
this.packageName=packageName;

//load all class files to Model
tables=new ArrayList<>();
loadAllPojoClassesToDS();		//Also loaded all table in tables.

//Creating DataManager DS
for(TableSchema tableSchema:tables)
{
Class<?> objClass=tableSchema.getObjectClass();
String tableName=tableSchema.getTableName();
Map<String,StatementDS> tableMap=new HashMap<>();
//insert statement start here.
try
{
connection=DriverManager.getConnection(connectionURL,username,password);
DatabaseMetaData dbMetaData=connection.getMetaData();

List<Method> jdbcSetterMethods=new ArrayList<>();
List<Method> classGetterMethods=new ArrayList<>();
List<FieldSchema> nonAutoIncrementFields=tableSchema.getNonAutoIncrementFields();
List<Integer> paramsType=new ArrayList<>();
StringBuilder values=new StringBuilder();
StringBuilder columns=new StringBuilder();
String sqlStatement="";
PreparedStatement preparedStatement;
ResultSet resultSet;
ResultSet generatedKeys;
for(int i=0;i<nonAutoIncrementFields.size();i++)
{
FieldSchema fs=nonAutoIncrementFields.get(i);
String fieldName=fs.getMethodName();
String columnName=fs.getColumnName();
columns.append(columnName);
try
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method getterMethod=objClass.getMethod("get"+sFieldName);
classGetterMethods.add(getterMethod);
values.append("?");
}catch(Exception exception)
{
classGetterMethods.add(i,null);
values.append("?");
}
if(i+1<nonAutoIncrementFields.size())
{
columns.append(",");
values.append(",");
}

ResultSet colRS=dbMetaData.getColumns(null,null,tableSchema.getTableName(),columnName);
int sqlType=Types.OTHER;
if(colRS.next())
{
sqlType=colRS.getInt("DATA_TYPE");
}
colRS.close();
Method setter=JDBCMethodExtractor.getJDBCSetter(sqlType);
jdbcSetterMethods.add(setter);
paramsType.add(sqlType);
}
sqlStatement="insert into "+tableSchema.getTableName()+" ("+columns.toString()+") values("+values.toString()+")";
System.out.println(sqlStatement);
/*
preparedStatement=connection.prepareStatement(sqlStatement);	//No need to create PreparedStatement.

ParameterMetaData pmd=preparedStatement.getParameterMetaData();
for(int i=1;i<=pmd.getParameterCount();i++)
{
int sqlType=pmd.getParameterType(i);
String typeName=pmd.getParameterTypeName(i);
Method setter=JDBCMethodExtractor.getJDBCSetter(sqlType);
jdbcSetterMethods.add(setter);
paramsType.add(sqlType);
}
*/
StatementDS statementDS=new StatementDS();
statementDS.setJDBCMethods(jdbcSetterMethods);
statementDS.setClassMethods(classGetterMethods);
statementDS.setStatement(sqlStatement);
statementDS.setParamsCount(paramsType.size());
statementDS.setParamsType(paramsType);
tableMap.put("insert",statementDS);
connection.close();
}catch(Exception e)
{
connection.close();
System.out.println(e);
}
statements.put(objClass,tableMap);
}
}catch(DataException de)
{
throw de;
}catch(Exception e)
{
System.out.println(e);
}
}
private  void loadFiles(File rootFolder,File currentFile) throws DataException
{
File[] files=currentFile.listFiles();
if(files==null) return;
for(File file:files)
{
if(file.isDirectory())
{
loadFiles(rootFolder,file);
}
else if(file.getName().endsWith(".class"))
{
String relativePath=rootFolder.toURI().relativize(file.toURI()).getPath();
relativePath=relativePath.replace("\\","/");
System.out.println("Adding entry: "+relativePath);
try
{

String classNameWithPackage=relativePath.replace(".class","").replace("/",".");
Class objClass=Class.forName(classNameWithPackage);
if(objClass==null) continue;
TableSchema table=ORMDataModel.getInfo(objClass);
if(table!=null) tables.add(table);

}catch(ClassNotFoundException cnfe)
{
System.out.println("Exception: "+cnfe);
}catch(DataException de)
{
System.out.println(de);
}catch(Exception e)
{
System.out.println("Exception: "+e);
}
}
}
}
private void loadAllPojoClassesToDS() throws DataException
{
File srcFolder=new File(parentWorkingDirectory,"src");
if(!srcFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
try
{
loadFiles(srcFolder,srcFolder);
}catch(DataException de)
{
System.out.println(de);
}
}

public static synchronized void initialize(File parentWorkingDirectory) throws DataException
{
if(parentWorkingDirectory==null) throw new DataException("Configuration file contains directory required");
if(!parentWorkingDirectory.exists() || !parentWorkingDirectory.isDirectory()) throw new DataException("Configuration file contains directory required");
System.out.println(parentWorkingDirectory.getAbsolutePath());
DataManager.parentWorkingDirectory=parentWorkingDirectory;
try
{
if(DataManager.dataManager==null)
{
DataManager.dataManager=new DataManager();
}
}catch(DataException de)
{
throw de;
}catch(Exception e)
{
//System.out.println("Exception: "+e);
throw new DataException(e);
}
}
public static DataManager getDataManager() throws DataException
{
if(dataManager==null) throw new DataException("Must call initialize along with parent working directory");
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
//donedone
public Object insert(Object obj) throws DataException
{
if(connection==null) throw new DataException("Call begin() before save()");
try
{
Class<?> objClass=obj.getClass();
TableSchema tableSchema=ORMDataModel.getInfo(objClass);
List<FieldSchema> nonAutoIncrementFields=tableSchema.getNonAutoIncrementFields();
String sqlStatement="";
PreparedStatement preparedStatement;
ResultSet resultSet;
ResultSet generatedKeys;
for(int i=0;i<nonAutoIncrementFields.size();i++)
{
FieldSchema fs=nonAutoIncrementFields.get(i);
String fieldName=fs.getMethodName();
String columnName=fs.getColumnName();
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
}catch(Exception exception)
{
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
}
StatementDS statementDS=statements.get(objClass).get("insert");
preparedStatement=connection.prepareStatement(statementDS.getStatement(),Statement.RETURN_GENERATED_KEYS);
List<Method> jdbcSetterMethods=statementDS.getJDBCMethods();
List<Method> classGetterMethods=statementDS.getClassMethods();
List<Integer> sqlTypes=statementDS.getParamsType();
Object convertedData;
for(int i=0;i<statementDS.getParamsCount();i++)
{
System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convert(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
{
preparedStatement.setNull(i+1,sqlTypes.get(i));
}
else
{
jdbcSetterMethods.get(i).invoke(preparedStatement,i+1,convertedData);
}
}catch(Exception e)
{
preparedStatement.setNull(i+1,sqlTypes.get(i));	//null set
//donedone
System.out.println("Error: "+e);
}
}

preparedStatement.executeUpdate();
generatedKeys=preparedStatement.getGeneratedKeys();
if(generatedKeys.next())
{
Object result=generatedKeys.getString(1);
List<FieldSchema> autoIncrementFields=tableSchema.getAutoIncrementFields();
if(!autoIncrementFields.isEmpty())	//handles only one auto increment key
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
public Object save(Object obj) throws DataException
{
if(connection==null) throw new DataException("Call begin() before save()");
try
{
Class<?> objClass=obj.getClass();
TableSchema tableSchema=ORMDataModel.getInfo(objClass);
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
String fieldName=fs.getMethodName();
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
TableSchema tableSchema=ORMDataModel.getInfo(objClass);
List<FieldSchema> nonAutoIncrementFields=tableSchema.getNonAutoIncrementFields();
List<FieldSchema> primaryKeyFields=tableSchema.getPrimaryKeyFields();
if(primaryKeyFields.isEmpty()) throw new DataException("No @PrimaryKey found in : "+objClass.getName());

StringBuilder wherePart=new StringBuilder();
String primaryKeyColumnName="";
Object primaryKeyValue=null;
String sqlStatement;
PreparedStatement preparedStatement;
ResultSet resultSet;
for(int i=0;i<primaryKeyFields.size();i++)
{
FieldSchema fs=primaryKeyFields.get(i);
String fieldName=fs.getMethodName();
String columnName=fs.getColumnName();
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
primaryKeyColumnName=columnName;
primaryKeyValue=formatValue(value);
wherePart.append(fs.getColumnName()).append("=").append(formatValue(value));
}catch(Exception exception)
{
wherePart.append(fs.getColumnName()).append("=").append("null");
}
if(fs.isPrimaryKey())
{
sqlStatement="select "+columnName+" from "+tableSchema.getTableName()+" where "+columnName+"="+formatValue(value)+";";
System.out.println(sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid "+columnName+": "+formatValue(value));
}
resultSet.close();
preparedStatement.close();
}
if(i+1<primaryKeyFields.size()) wherePart.append(",");
}


StringBuilder setPart=new StringBuilder();
for(int i=0;i<nonAutoIncrementFields.size();i++)
{
FieldSchema fs=nonAutoIncrementFields.get(i);
String fieldName=fs.getMethodName();
String columnName=fs.getColumnName();
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
setPart.append(fs.getColumnName()).append("=").append(formatValue(value));
}catch(Exception exception)
{
setPart.append(fs.getColumnName()).append("=").append("null");
}
if(fs.isUnique())
{
sqlStatement="select "+columnName+" from "+tableSchema.getTableName()+" where "+columnName+"="+formatValue(value)+" and "+primaryKeyColumnName+" <> "+formatValue(primaryKeyValue)+";";
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

if(i+1<nonAutoIncrementFields.size()) setPart.append(",");
}
sqlStatement="update "+tableSchema.getTableName()
+" set "+setPart
+" where "+wherePart;
System.out.println("SQL: "+sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
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
TableSchema tableSchema=ORMDataModel.getInfo(objClass);
Object obj=objClass.getDeclaredConstructor().newInstance();

List<FieldSchema> primaryKeyFields=tableSchema.getPrimaryKeyFields();
if(primaryKeyFields.isEmpty()) throw new DataException("No @PrimaryKey found in: "+objClass.getName());
FieldSchema primaryKeyField=primaryKeyFields.get(0);
PreparedStatement preparedStatement;
ResultSet resultSet;
String sqlStatement;
preparedStatement=connection.prepareStatement("select * from "+tableSchema.getTableName()+" where "+primaryKeyField.getColumnName()+"="+formatValue(primaryKey));
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
for(FieldSchema fs:tableSchema.getAllFields())
{
try
{
String fieldName=fs.getMethodName();
Object value=resultSet.getObject(fs.getColumnName());
if(fs.isSetterAllowed())
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method setterMethod=objClass.getMethod("set"+sFieldName,fs.getType());
setterMethod.invoke(obj,value);
}
else if(fs.isPublicAllowed())
{
Field field=objClass.getField(fieldName);
field.set(obj,value);
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
resultSet.close();
preparedStatement.close();
}
else
{
preparedStatement.close();
throw new DataException("Invalid "+primaryKeyField.getMethodName()+": "+primaryKey);
}

//donedone
List<TableSchema> tables=ORMDataModel.getAllInfo();
System.out.println("table size: "+tables.size());
for(TableSchema table:tables)
{
System.out.println(table.getTableName());
if(tableSchema.equals(table)) continue;
List<FieldSchema> fkFields=table.getForeignKeyFields();
for(FieldSchema fkField:fkFields)
{
System.out.println(fkField.getFKParentClass()+", "+fkField.getFKParentColumn()+", "+fkField.getColumnName()+", "+fkField.getMethodName());

if(fkField.getFKParentClass().equals(tableSchema.getTableName()))
{
String fkParentColumn=fkField.getFKParentColumn();
FieldSchema fs=tableSchema.getFieldByColumnName(fkParentColumn);
String fieldName=fs.getMethodName();
String columnName=fs.getColumnName();
System.out.println(fieldName+", "+columnName);
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
}catch(Exception exception)
{
}
value=formatValue(value);
sqlStatement="select * from "+table.getTableName()+" where "+fkField.getColumnName()+"="+value+";";
System.out.println(sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Unable to delete record, since this record is attached with other child record(s).");
}
preparedStatement.close();
}
}
}
sqlStatement="delete from "+tableSchema.getTableName()
+" where "+primaryKeyField.getColumnName()
+"="+formatValue(primaryKey);
System.out.println("SQL Statement: "+sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
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
TableSchema tableSchema=ORMDataModel.getInfo(objClass);
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
TableSchema tableSchema=ORMDataModel.getInfo(qClass);
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
String fieldName=fs.getMethodName();
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
Field field=obj.getClass().getField(fs.getMethodName());
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



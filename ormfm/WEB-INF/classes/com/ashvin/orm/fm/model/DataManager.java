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
List<TableSchema> tables;
tables=new ArrayList<>();
loadAllPojoClassesToDS(tables);		//Also loaded all table in tables.

String tableName;
Map<String,StatementDS> tableMap;
DatabaseMetaData dbMetaData;
List<FieldSchema> fields;
List<Method> jdbcSetterMethods;
List<Method> jdbcGetterMethods;
List<Method> classGetterMethods;
List<Method> classSetterMethods;
List<Integer> paramsType;
List<String> columns;
List<String> values;
String fieldName;
String columnName;
String standardFieldName;
Method classGetterMethod;
Method classSetterMethod;
Method jdbcSetterMethod;
Method jdbcGetterMethod;
ResultSet colRS;
int sqlType;

//Creating DataManager DS
connection=DriverManager.getConnection(connectionURL,username,password);
dbMetaData=connection.getMetaData();
for(TableSchema tableSchema:tables)
{
Class<?> objClass=tableSchema.getObjectClass();
tableName=tableSchema.getTableName();
tableMap=new HashMap<>();
//insert statement start here.
try
{

fields=tableSchema.getAllFields();

jdbcSetterMethods=new ArrayList<>();
jdbcGetterMethods=new ArrayList<>();
classGetterMethods=new ArrayList<>();
classSetterMethods=new ArrayList<>();
paramsType=new ArrayList<>();
columns=new ArrayList<>();
values=new ArrayList<>();

for(FieldSchema fs:fields)
{
fieldName=fs.getMethodName();
columnName=fs.getColumnName();
standardFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
try
{
classGetterMethod=objClass.getMethod("get"+standardFieldName);
}catch(Exception exception)
{
classGetterMethod=null;
}
try
{
classSetterMethod=objClass.getMethod("set"+standardFieldName);
}catch(Exception exception)
{
classSetterMethod=null;
}

colRS=dbMetaData.getColumns(null,null,tableName,columnName);
sqlType=Types.OTHER;
if(colRS.next()) sqlType=colRS.getInt("DATA_TYPE");
colRS.close();
jdbcSetterMethod=JDBCMethodExtractor.getJDBCSetter(sqlType);
jdbcGetterMethod=JDBCMethodExtractor.getJDBCGetter(sqlType);

columns.add(columnName);
values.add("?");
paramsType.add(sqlType);
jdbcSetterMethods.add(jdbcSetterMethod);
classGetterMethods.add(classGetterMethod);
jdbcGetterMethods.add(jdbcGetterMethod);
classSetterMethods.add(classSetterMethod);
}

StatementDS insertStatementDS=new StatementDS();
insertStatementDS.append("INSERT INTO ").append(tableName).append(" SET ");

StatementDS updateStatementDS=new StatementDS();
updateStatementDS.append("UPDATE ").append(tableName).append(" SET ");

StatementDS deleteStatementDS=new StatementDS();
deleteStatementDS.append("DELETE FROM ").append(tableName);

StatementDS primaryKeyValidation=new StatementDS();
primaryKeyValidation.setQuery(true);
StatementDS getByPrimaryKey=new StatementDS();
getByPrimaryKey.setQuery(true);

StatementDS uniqueKeyValidation=new StatementDS();		//All unique key are set in one go.
uniqueKeyValidation.setQuery(true);
StatementDS uniqueAndPrimaryKeyValidation=new StatementDS();
uniqueAndPrimaryKeyValidation.setQuery(true);
StatementDS getByUniqueKey=new StatementDS();
getByUniqueKey.setQuery(true);

StatementDS foreignKeyValidation=new StatementDS();		//All foreign key are set in one go.
foreignKeyValidation.setQuery(true);
StatementDS getByForeignKey=new StatementDS();
getByForeignKey.setQuery(true);

int nonAutoIncrementCount=0;
int primaryKeyIndex=-1;
for(int i=0;i<fields.size();i++)
{
FieldSchema fs=fields.get(i);
if(fs.isPrimaryKey())
{
if(primaryKeyIndex!=-1) throw new DataException("Multiple primary key are not allowed");		//FM User may add Annotation, so secure it.
primaryKeyIndex=i;
}
if(fs.isAutoIncrement())
{
}
else
{
if(nonAutoIncrementCount!=0) 
{
insertStatementDS.append(",");
updateStatementDS.append(",");
}
insertStatementDS.append(columns.get(i)).append("=").append(values.get(i));
insertStatementDS.addJDBCSetterMethod(jdbcSetterMethods.get(i));
insertStatementDS.addClassGetterMethod(classGetterMethods.get(i));
insertStatementDS.addStatementParamType(paramsType.get(i));

updateStatementDS.append(columns.get(i)).append("=").append(values.get(i));
updateStatementDS.addJDBCSetterMethod(jdbcSetterMethods.get(i));
updateStatementDS.addClassGetterMethod(classGetterMethods.get(i));
updateStatementDS.addStatementParamType(paramsType.get(i));
nonAutoIncrementCount++;
}
if(fs.isUnique())
{
uniqueKeyValidation.append("SELECT ").append(columns.get(i)).append(" FROM ").append(tableName).append(" WHERE ").append(columns.get(i)).append("=? ;");
uniqueKeyValidation.addJDBCSetterMethod(jdbcSetterMethods.get(i));
uniqueKeyValidation.addClassGetterMethod(classGetterMethods.get(i));
uniqueKeyValidation.addStatementParamType(paramsType.get(i));
uniqueKeyValidation.addClassSetterMethod(classSetterMethods.get(i));
uniqueKeyValidation.addJDBCGetterMethod(jdbcGetterMethods.get(i));
uniqueKeyValidation.addResultParamType(paramsType.get(i));

uniqueAndPrimaryKeyValidation.append("SELECT ").append(columns.get(i)).append(" FROM ").append(tableName).append(" WHERE ").append(columns.get(i)).append("=? ;");
uniqueAndPrimaryKeyValidation.addJDBCSetterMethod(jdbcSetterMethods.get(i));
uniqueAndPrimaryKeyValidation.addClassGetterMethod(classGetterMethods.get(i));
uniqueAndPrimaryKeyValidation.addStatementParamType(paramsType.get(i));
uniqueAndPrimaryKeyValidation.addClassSetterMethod(classSetterMethods.get(i));
uniqueAndPrimaryKeyValidation.addJDBCGetterMethod(jdbcGetterMethods.get(i));
uniqueAndPrimaryKeyValidation.addResultParamType(paramsType.get(i));

getByUniqueKey.append("SELECT * FROM ").append(columns.get(i)).append(" FROM ").append(tableName).append(" WHERE ").append(columns.get(i)).append("=? ;");
getByUniqueKey.addJDBCSetterMethod(jdbcSetterMethods.get(i));
getByUniqueKey.addClassGetterMethod(classGetterMethods.get(i));
getByUniqueKey.addStatementParamType(paramsType.get(i));

}
if(fs.isForeignKey())
{
try
{
foreignKeyValidation.append("SELECT ").append(fs.getFKParentColumn()).append(" FROM ").append(fs.getFKParentClass()).append(" WHERE ").append(fs.getFKParentColumn()).append("=? ;");
foreignKeyValidation.addJDBCSetterMethod(jdbcSetterMethods.get(i));
foreignKeyValidation.addClassGetterMethod(classGetterMethods.get(i));
foreignKeyValidation.addStatementParamType(paramsType.get(i));
foreignKeyValidation.addClassSetterMethod(classSetterMethods.get(i));
foreignKeyValidation.addJDBCGetterMethod(jdbcGetterMethods.get(i));
foreignKeyValidation.addResultParamType(paramsType.get(i));
}catch(Exception exception)
{
System.out.println("Problem: "+foreignKeyValidation.getStatement());
System.out.println("Exception: "+exception);
exception.printStackTrace();
foreignKeyValidation.clear();
}

getByForeignKey.append("SELECT * FROM ").append(tableName).append(" WHERE ").append(columns.get(i)).append("=? ;");
getByForeignKey.addJDBCSetterMethod(jdbcSetterMethods.get(i));
getByForeignKey.addClassGetterMethod(classGetterMethods.get(i));
getByForeignKey.addStatementParamType(paramsType.get(i));

}
getByPrimaryKey.addClassSetterMethod(classSetterMethods.get(i));
getByPrimaryKey.addJDBCGetterMethod(jdbcGetterMethods.get(i));
getByPrimaryKey.addResultParamType(paramsType.get(i));
getByUniqueKey.addClassSetterMethod(classSetterMethods.get(i));
getByUniqueKey.addJDBCGetterMethod(jdbcGetterMethods.get(i));
getByUniqueKey.addResultParamType(paramsType.get(i));
getByForeignKey.addClassSetterMethod(classSetterMethods.get(i));
getByForeignKey.addJDBCGetterMethod(jdbcGetterMethods.get(i));
getByForeignKey.addResultParamType(paramsType.get(i));

}
if(primaryKeyIndex!=-1)
{
//UNIQUE KEY WITHOUT PRIMARY KEY [UPDATE UNIQUE KEY VALIDATION]
StringBuilder replaceWith=new StringBuilder();
replaceWith.append(" AND ").append(columns.get(primaryKeyIndex)).append(" <> ").append("? ;");
String uapkvStatement=uniqueAndPrimaryKeyValidation.getStatement().toString();
uapkvStatement=uapkvStatement.replace(";",replaceWith.toString());
uniqueAndPrimaryKeyValidation.setStatement(new StringBuilder(uapkvStatement));
uniqueAndPrimaryKeyValidation.addJDBCSetterMethod(jdbcSetterMethods.get(primaryKeyIndex));
uniqueAndPrimaryKeyValidation.addClassGetterMethod(classGetterMethods.get(primaryKeyIndex));
uniqueAndPrimaryKeyValidation.addStatementParamType(paramsType.get(primaryKeyIndex));

primaryKeyValidation.append("SELECT ").append(columns.get(primaryKeyIndex)).append(" FROM ").append(tableName).append(" WHERE ").append(columns.get(primaryKeyIndex)).append("=?");
primaryKeyValidation.addJDBCSetterMethod(jdbcSetterMethods.get(primaryKeyIndex));
primaryKeyValidation.addClassGetterMethod(classGetterMethods.get(primaryKeyIndex));
primaryKeyValidation.addStatementParamType(paramsType.get(primaryKeyIndex));
primaryKeyValidation.addClassSetterMethod(classSetterMethods.get(primaryKeyIndex));
primaryKeyValidation.addJDBCGetterMethod(jdbcGetterMethods.get(primaryKeyIndex));
primaryKeyValidation.addResultParamType(paramsType.get(primaryKeyIndex));

getByPrimaryKey.append("SELECT * FROM ").append(tableName).append(" WHERE ").append(columns.get(primaryKeyIndex)).append("=?");
getByPrimaryKey.addJDBCSetterMethod(jdbcSetterMethods.get(primaryKeyIndex));
getByPrimaryKey.addClassGetterMethod(classGetterMethods.get(primaryKeyIndex));
getByPrimaryKey.addStatementParamType(paramsType.get(primaryKeyIndex));

updateStatementDS.append(" WHERE ").append(columns.get(primaryKeyIndex)).append("=").append(values.get(primaryKeyIndex));
updateStatementDS.addJDBCSetterMethod(jdbcSetterMethods.get(primaryKeyIndex));
updateStatementDS.addClassGetterMethod(classGetterMethods.get(primaryKeyIndex));
updateStatementDS.addStatementParamType(paramsType.get(primaryKeyIndex));

deleteStatementDS.append(" WHERE ").append(columns.get(primaryKeyIndex)).append("=").append(values.get(primaryKeyIndex));
deleteStatementDS.addJDBCSetterMethod(jdbcSetterMethods.get(primaryKeyIndex));
deleteStatementDS.addClassGetterMethod(classGetterMethods.get(primaryKeyIndex));
deleteStatementDS.addStatementParamType(paramsType.get(primaryKeyIndex));

}
else		//Primary Key Required for DELETE and UPDATE
{
deleteStatementDS.clear();
updateStatementDS.clear();
primaryKeyValidation.clear();
getByPrimaryKey.clear();
//uniqueAndPrimaryKeyValidation.clear();	//Already sufficent to proceed.
}
System.out.println("-----------------xxxxx------------------");
System.out.println("Primary key index: "+primaryKeyIndex);
System.out.println(insertStatementDS.getStatement().toString());
System.out.println(updateStatementDS.getStatement().toString());
System.out.println(deleteStatementDS.getStatement().toString());
System.out.println(primaryKeyValidation.getStatement().toString());
System.out.println(getByPrimaryKey.getStatement().toString());
System.out.println(uniqueKeyValidation.getStatement().toString());
System.out.println(uniqueAndPrimaryKeyValidation.getStatement().toString());
System.out.println(getByUniqueKey.getStatement().toString());
System.out.println(foreignKeyValidation.getStatement().toString());
System.out.println(getByForeignKey.getStatement().toString());
System.out.println("-----------------xxxxx------------------");
tableMap.put("insert",insertStatementDS);
tableMap.put("INSERT",insertStatementDS);
tableMap.put("update",updateStatementDS);
tableMap.put("UPDATE",updateStatementDS);
tableMap.put("delete",deleteStatementDS);
tableMap.put("DELETE",deleteStatementDS);

tableMap.put("SELECT_BY_PRIMARY_KEY",getByPrimaryKey);
tableMap.put("select_by_primary_key",getByPrimaryKey);
tableMap.put("GET_BY_PRIMARY_KEY",getByPrimaryKey);
tableMap.put("get_by_primary_key",getByPrimaryKey);
tableMap.put("PRIMARY_KEY_VALIDATION",primaryKeyValidation);
tableMap.put("primary_key_validation",primaryKeyValidation);

tableMap.put("SELECT_BY_UNIQUE_KEY",getByUniqueKey);
tableMap.put("select_by_primary_key",getByUniqueKey);
tableMap.put("GET_BY_UNIQUE_KEY",getByUniqueKey);
tableMap.put("get_by_unique_key",getByUniqueKey);
tableMap.put("UNIQUE_KEY_VALIDATION",uniqueKeyValidation);
tableMap.put("unique_key_validation",uniqueKeyValidation);
tableMap.put("UNIQUE_AND_PRIMARY_KEY_VALIDATION",uniqueAndPrimaryKeyValidation);
tableMap.put("unique_and_primary_key_validation",uniqueAndPrimaryKeyValidation);

tableMap.put("SELECT_BY_FOREIGN_KEY",getByForeignKey);
tableMap.put("select_by_foreign_key",getByForeignKey);
tableMap.put("GET_BY_FOREIGN_KEY",getByForeignKey);
tableMap.put("get_by_foreign_key",getByForeignKey);
tableMap.put("FOREIGN_KEY_VALIDATION",foreignKeyValidation);
tableMap.put("foreign_key_validation",foreignKeyValidation);


}catch(Exception e)
{
System.out.println(e);
e.printStackTrace();
}
statements.put(objClass,tableMap);
}
connection.close();
}catch(DataException de)
{
try
{
connection.close();
}catch(SQLException sqlException)
{
}
throw de;
}catch(Exception e)
{
System.out.println(e);
}
}
private  void loadFiles(File rootFolder,File currentFile,List<TableSchema> tables) throws DataException
{
File[] files=currentFile.listFiles();
if(files==null) return;
for(File file:files)
{
if(file.isDirectory())
{
loadFiles(rootFolder,file,tables);
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
private void loadAllPojoClassesToDS(List<TableSchema> tables) throws DataException
{
File srcFolder=new File(parentWorkingDirectory,"src");
if(!srcFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
try
{
loadFiles(srcFolder,srcFolder,tables);
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
public Object save(Object obj) throws DataException
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
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Some problem occured");		//donedone change the message
preparedStatement=connection.prepareStatement(sqlStatement,Statement.RETURN_GENERATED_KEYS);
List<Method> jdbcSetterMethods=statementDS.getJDBCSetterMethods();
List<Method> classGetterMethods=statementDS.getClassGetterMethods();
List<Integer> sqlTypes=statementDS.getStatementParamsType();
Object convertedData;
for(int i=0;i<statementDS.getStatementParamsCount();i++)
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



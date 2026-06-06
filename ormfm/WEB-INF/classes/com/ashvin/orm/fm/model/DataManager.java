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
private static Map<Class<?>,Map<Object,Object>> cache=new HashMap<>();
private static DataManager dataManager=null;
private static File parentWorkingDirectory;
private String jdbcDriver="";
private String connectionURL="";
private String username="";
private String password="";
private String packageName="";

private static final class Session
{
Connection connection=null;
String qStatement="";
Class<?> qClass=null;
boolean whereUsed=false;
boolean orderByUsed=false;
StatementDS qStatementDS=null;
}
private static final ThreadLocal<Session> threadSession=ThreadLocal.withInitial(()->new Session());

private DataManager() throws DataException
{
try
{
File file=new File(parentWorkingDirectory,"conf.json");
if(!file.exists()) throw new DataException("Configuration file required");

FileReader fileReader=new FileReader(file);
JsonObject jsonObj=JsonParser.parseReader(fileReader).getAsJsonObject();
if(jsonObj==null) throw new DataException("Invalid json configuration file");

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
throw new DataException("Invalid JDBC driver: "+jdbcDriver);
}

//load all POJO classes
List<ViewSchema> views=new ArrayList<>();
List<TableSchema> tables=new ArrayList<>();
loadAllPojoClassesToDS(tables,views);		//Also loaded all table in tables, and view in views.

String tableName;
Map<String,StatementDS> map;
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
int nonAutoIncrementCount=0;
int primaryKeyIndex=-1;

Map<Object,Object> tableCache;

//Creating DataManager DS
Connection connection=DriverManager.getConnection(connectionURL,username,password);
dbMetaData=connection.getMetaData();
for(TableSchema tableSchema:tables)
{
Class<?> objClass=tableSchema.getObjectClass();
tableName=tableSchema.getTableName();
map=new HashMap<>();
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
classSetterMethod=objClass.getMethod("set"+standardFieldName,fs.getType());
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

StatementDS selectStatement=new StatementDS();
selectStatement.setQuery(true);

nonAutoIncrementCount=0;
primaryKeyIndex=-1;
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
insertStatementDS.setQuery(true);
insertStatementDS.addJDBCGetterMethod(jdbcGetterMethods.get(i));
insertStatementDS.addClassSetterMethod(classSetterMethods.get(i));
insertStatementDS.addResultParamType(paramsType.get(i));

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

getByUniqueKey.append("SELECT * FROM").append(tableName).append(" WHERE ").append(columns.get(i)).append("=? ;");
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
//System.out.println("Exception: "+exception);
//exception.printStackTrace();
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

selectStatement.append("SELECT * FROM ").append(tableName);
selectStatement.setJDBCSetterMethods(jdbcSetterMethods);
selectStatement.setJDBCGetterMethods(jdbcGetterMethods);
selectStatement.setClassGetterMethods(classGetterMethods);
selectStatement.setClassSetterMethods(classSetterMethods);
selectStatement.setStatementParamsType(paramsType);
selectStatement.setResultParamsType(paramsType);

// System.out.println("-----------------xxxxx------------------");
// System.out.println("Primary key index: "+primaryKeyIndex);
// System.out.println(insertStatementDS.getStatement().toString());
// System.out.println(updateStatementDS.getStatement().toString());
// System.out.println(deleteStatementDS.getStatement().toString());
// System.out.println(primaryKeyValidation.getStatement().toString());
// System.out.println(getByPrimaryKey.getStatement().toString());
// System.out.println(uniqueKeyValidation.getStatement().toString());
// System.out.println(uniqueAndPrimaryKeyValidation.getStatement().toString());
// System.out.println(getByUniqueKey.getStatement().toString());
// System.out.println(foreignKeyValidation.getStatement().toString());
// System.out.println(getByForeignKey.getStatement().toString());
// System.out.println("-----------------xxxxx------------------");
map.put("insert",insertStatementDS);
map.put("INSERT",insertStatementDS);
map.put("update",updateStatementDS);
map.put("UPDATE",updateStatementDS);
map.put("delete",deleteStatementDS);
map.put("DELETE",deleteStatementDS);
map.put("SELECT",selectStatement);
map.put("select",selectStatement);

map.put("SELECT_BY_PRIMARY_KEY",getByPrimaryKey);
map.put("select_by_primary_key",getByPrimaryKey);
map.put("GET_BY_PRIMARY_KEY",getByPrimaryKey);
map.put("get_by_primary_key",getByPrimaryKey);
map.put("PRIMARY_KEY_VALIDATION",primaryKeyValidation);
map.put("primary_key_validation",primaryKeyValidation);

map.put("SELECT_BY_UNIQUE_KEY",getByUniqueKey);
map.put("select_by_unique_key",getByUniqueKey);
map.put("GET_BY_UNIQUE_KEY",getByUniqueKey);
map.put("get_by_unique_key",getByUniqueKey);
map.put("UNIQUE_KEY_VALIDATION",uniqueKeyValidation);
map.put("unique_key_validation",uniqueKeyValidation);
map.put("UNIQUE_AND_PRIMARY_KEY_VALIDATION",uniqueAndPrimaryKeyValidation);
map.put("unique_and_primary_key_validation",uniqueAndPrimaryKeyValidation);

map.put("SELECT_BY_FOREIGN_KEY",getByForeignKey);
map.put("select_by_foreign_key",getByForeignKey);
map.put("GET_BY_FOREIGN_KEY",getByForeignKey);
map.put("get_by_foreign_key",getByForeignKey);
map.put("FOREIGN_KEY_VALIDATION",foreignKeyValidation);
map.put("foreign_key_validation",foreignKeyValidation);


}catch(Exception e)
{
//System.out.println(e);
e.printStackTrace();
continue;
}
statements.put(objClass,map);

if(tableSchema.isCacheable())
{
if(primaryKeyIndex!=-1)
{
tableCache=new LinkedHashMap<>();
StatementDS selectStatement=map.get("select");
PreparedStatement preparedStatement=connection.prepareStatement(selectStatement.getStatement().toString());
ResultSet resultSet=preparedStatement.executeQuery();

Object instance;
Object convertedData=null;
Object data;
Object primaryKeyObj=null;
while(resultSet.next())
{
instance=objClass.getDeclaredConstructor().newInstance();
for(int i=0;i<selectStatement.getResultParamsCount();i++)
{
try
{
data=jdbcGetterMethods.get(i).invoke(resultSet,i+1);
convertedData=JDBCMethodExtractor.convertToJava(paramsType.get(i),data);
classSetterMethods.get(i).invoke(instance,convertedData);
}catch(Exception e)
{
convertedData=null;
}
if(primaryKeyIndex==i) primaryKeyObj=convertedData;
}
tableCache.put(primaryKeyObj,instance);
}
cache.put(objClass,tableCache);
resultSet.close();
preparedStatement.close();
}
}
}


for(ViewSchema viewSchema:views)
{
Class<?> objClass=viewSchema.getObjectClass();
String viewName=viewSchema.getViewName();
map=new HashMap<>();
try
{
fields=viewSchema.getAllFields();
jdbcGetterMethods=new ArrayList<>();
classSetterMethods=new ArrayList<>();
paramsType=new ArrayList<>();
for(FieldSchema fs:fields)
{
fieldName=fs.getMethodName();
columnName=fs.getColumnName();
standardFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
try
{
classSetterMethod=objClass.getMethod("set"+standardFieldName,fs.getType());
}catch(Exception exception)
{
classSetterMethod=null;
}

colRS=dbMetaData.getColumns(null,null,viewName,columnName);
sqlType=Types.OTHER;
if(colRS.next()) sqlType=colRS.getInt("DATA_TYPE");
colRS.close();
jdbcGetterMethod=JDBCMethodExtractor.getJDBCGetter(sqlType);

paramsType.add(sqlType);
jdbcGetterMethods.add(jdbcGetterMethod);
classSetterMethods.add(classSetterMethod);
}

StatementDS selectStatement=new StatementDS();
selectStatement.setQuery(true);
selectStatement.append("SELECT * FROM ").append(viewName);
selectStatement.setClassSetterMethods(classSetterMethods);
selectStatement.setJDBCGetterMethods(jdbcGetterMethods);
selectStatement.setResultParamsType(paramsType);

map.put("SELECT",selectStatement);
map.put("select",selectStatement);
}catch(Exception e)
{
//System.out.println(e);
e.printStackTrace();
}
statements.put(objClass,map);
}


connection.close();
}catch(DataException de)
{
throw de;
}catch(Exception e)
{
//System.out.println(e);
throw new DataException(e);
}
}
private  void loadFiles(File rootFolder,File currentFile,List<TableSchema> tables,List<ViewSchema> views) throws DataException
{
File[] files=currentFile.listFiles();
if(files==null) return;
for(File file:files)
{
if(file.isDirectory())
{
loadFiles(rootFolder,file,tables,views);
}
else if(file.getName().endsWith(".class"))
{
String relativePath=rootFolder.toURI().relativize(file.toURI()).getPath();
relativePath=relativePath.replace("\\","/");
//System.out.println("Adding entry: "+relativePath);
try
{
String classNameWithPackage=relativePath.replace(".class","").replace("/",".");
Class objClass=Class.forName(classNameWithPackage);
if(objClass==null) continue;
Schema schema=ORMDataModel.getInfo(objClass);
if(schema==null) continue;
if(schema instanceof TableSchema table) tables.add(table);
if(schema instanceof ViewSchema view) views.add(view);

}catch(ClassNotFoundException cnfe)
{
//System.out.println("Exception: "+cnfe);
}catch(DataException de)
{
//System.out.println(de);
}catch(Exception e)
{
// System.out.println("Exception: "+e);
}
}
}
}
private void loadAllPojoClassesToDS(List<TableSchema> tables,List<ViewSchema> views) throws DataException
{
File srcFolder=new File(parentWorkingDirectory,"src");
if(!srcFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
try
{
loadFiles(srcFolder,srcFolder,tables,views);
}catch(DataException de)
{
//     System.out.println(de);
}
}

public static synchronized void initialize(File parentWorkingDirectory) throws DataException
{
if(dataManager!=null) throw new DataException("Already initialized, can not call again");
if(parentWorkingDirectory==null) throw new DataException("Configuration file contains directory required");
if(!parentWorkingDirectory.exists() || !parentWorkingDirectory.isDirectory()) throw new DataException("Configuration file contains directory required");
// System.out.println(parentWorkingDirectory.getAbsolutePath());
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
public static boolean isInitialized()
{
if(dataManager!=null) return true;
return false;
}
public static DataManager getDataManager() throws DataException
{
if(dataManager==null) throw new DataException("Must call initialize along with parent working directory");
return DataManager.dataManager;
}

private static Session session()
{
return threadSession.get();
}
private static Connection conn()
{
return session().connection;
}

public void begin() throws DataException
{
Session s=session();
try
{
if(s.connection!=null && !s.connection.isClosed()) s.connection.close();
reset();
s.connection=DriverManager.getConnection(connectionURL,username,password);
}catch(SQLException sqlException)
{
throw new DataException(sqlException);
}
}
public void end()
{
Session s=session();
try
{
if(s.connection!=null) s.connection.close();
}catch(SQLException sqlException)
{
// System.out.println("Error closing connection: "+sqlException);
}
reset();
threadSession.remove();
}
public void reset()
{
Session s=session();
s.qStatement="";
s.qClass=null;
s.whereUsed=false;
s.orderByUsed=false;
s.qStatementDS=null;    //reset may called mid-session too, must not intrupt an ongoing transaction.
}
public void save(Object obj) throws DataException
{
Connection connection=conn();
if(connection==null) throw new DataException("Call begin() before save()"); 
boolean exists=false;
try
{
Class<?> objClass=obj.getClass();
Map<String,StatementDS> statementMap=statements.get(objClass);
if(statementMap==null) throw new DataException("Invalid data provided, Data required.");

Schema s=ORMDataModel.getInfo(objClass);
if(s==null) throw new DataException("Invalid data provided, Data required");
TableSchema tableSchema;
if(s instanceof TableSchema) tableSchema=(TableSchema)s;
else throw new DataException("Invalid data provided, Data required");

StatementDS statementDS;
List<Method> jdbcSetterMethods;
List<Method> classGetterMethods;
List<Integer> sqlTypes;

String sqlStatement="";
PreparedStatement preparedStatement;
ResultSet resultSet;
ResultSet generatedKeys;
Object convertedData;
Method primaryKeyMethod=null;

if(!tableSchema.isPrimaryKeyAutoIncremented())
{
statementDS=statementMap.get("primary_key_validation");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
preparedStatement=connection.prepareStatement(sqlStatement);
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
if(statementDS.getStatementParamsCount()==1)
{
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(0)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(0),((classGetterMethods.get(0)).invoke(obj))))==null)
{
throw new DataException("Invalid data provided to primary key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(0));
}
else
{
jdbcSetterMethods.get(0).invoke(preparedStatement,1,convertedData);
}
primaryKeyMethod=classGetterMethods.get(0);
}catch(Exception e)
{
throw new DataException("Invalid data provided to primary key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(0));	//null set
//System.out.println("Error: "+e);
}
}
else
{
throw new DataException("Invalid data provided to primary key, Data required");
}
resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
if(exists)
{
throw new DataException("This record already exists. Please use a unique identifier.");
}
}

String[] sqlStatements;
statementDS=statementMap.get("unique_key_validation");
sqlStatement=statementDS.getStatement().toString();
if(!sqlStatement.isBlank())
{ 
sqlStatements=sqlStatement.split(";");
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
for(int i=0;i<statementDS.getStatementParamsCount();i++)
{
String subSQL=(i<sqlStatements.length)?sqlStatements[i].trim():"";
if(subSQL.isBlank()) continue;
preparedStatement=connection.prepareStatement(subSQL);
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
{
throw new DataException("Invalid data provided to unique key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));
}
else
{
jdbcSetterMethods.get(i).invoke(preparedStatement,1,convertedData);
}
}catch(Exception e)
{
throw new DataException("Invalid data provided to unique key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));	//null set
//System.out.println("Error: "+e);
}
resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
if(exists)
{
throw new DataException("This "+convertedData+" is already in use. Please try another.");
}
}
}
statementDS=statementMap.get("foreign_key_validation");
sqlStatement=statementDS.getStatement().toString();
if(!sqlStatement.isBlank())
{ 
sqlStatements=sqlStatement.split(";");
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
for(int i=0;i<statementDS.getStatementParamsCount();i++)
{
String subSQL=(i<sqlStatements.length)?sqlStatements[i].trim():"";
if(subSQL.isBlank()) continue;
preparedStatement=connection.prepareStatement(subSQL);
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
{
throw new DataException("Invalid data provided to foreign key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));
}
else
{
jdbcSetterMethods.get(i).invoke(preparedStatement,1,convertedData);
}
}catch(Exception e)
{
throw new DataException("Invalid data provided to foreign key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));	//null set
//System.out.println("Error: "+e);
}
resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
if(!exists)
{
throw new DataException("Referenced parent record "+convertedData+" does not exist. Please select a valid entry.");
}
}
}

statementDS=statements.get(objClass).get("insert");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
preparedStatement=connection.prepareStatement(sqlStatement,Statement.RETURN_GENERATED_KEYS);
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
for(int i=0;i<statementDS.getStatementParamsCount();i++)
{
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
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
// System.out.println("Error: "+e);
}
}
preparedStatement.executeUpdate();
generatedKeys=preparedStatement.getGeneratedKeys();
Object data=null;        //Only one auto generated key allowed.
if(generatedKeys.next())
{
List<Method> jdbcGetterMethods=statementDS.getJDBCGetterMethods();
List<Method> classSetterMethods=statementDS.getClassSetterMethods();
List<Integer> resultParamTypes=statementDS.getResultParamsType();
// System.out.println(classSetterMethods.size());
// System.out.println(jdbcGetterMethods.size());
for(int i=0;i<statementDS.getResultParamsCount();i++)
{
try
{
data=jdbcGetterMethods.get(i).invoke(generatedKeys,i+1);
convertedData=JDBCMethodExtractor.convertToJava(resultParamTypes.get(i),data);
classSetterMethods.get(i).invoke(obj,convertedData);
}catch(Exception e)
{
}
}
}
generatedKeys.close();
preparedStatement.close();
if(tableSchema.isCacheable())
{
Object clonedObj=objClass.getDeclaredConstructor().newInstance();
PojoCopier.copy(clonedObj,obj);     //Cloned Object stored in DS Cache
Object primaryKeyObj;

if(!tableSchema.isPrimaryKeyAutoIncremented()) primaryKeyObj=primaryKeyMethod.invoke(clonedObj);
else primaryKeyObj=data;

cache.get(objClass).put(primaryKeyObj,clonedObj);             //We can't set the same object in our DS too.
//System.out.println("Cloned Obj: "+primaryKeyObj);
}
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
Connection connection=conn();
if(connection==null) throw new DataException("Call begin() before update()");
boolean exists=false;
try
{
Class<?> objClass=obj.getClass();
Map<String,StatementDS> statementMap=statements.get(objClass);
if(statementMap==null) throw new DataException("Invalid data provided, Data required.");
Schema s=ORMDataModel.getInfo(objClass);
if(s==null) throw new DataException("Invalid data provided, Data required");
TableSchema tableSchema;
if(s instanceof TableSchema) tableSchema=(TableSchema)s;
else throw new DataException("Invalid data provided, Data required");

StatementDS statementDS;
List<Method> jdbcSetterMethods;
List<Method> classGetterMethods;
List<Integer> sqlTypes;

String sqlStatement="";
String[] sqlStatements;
PreparedStatement preparedStatement;
ResultSet resultSet;
ResultSet generatedKeys;
Object convertedData=null;

Method pkClassGetterMethod=null;
Object pkConvertedData=null;
Integer pkSQLType=-1;
Method pkJDBCSetterMethod=null;


Object prevObj=objClass.getDeclaredConstructor().newInstance();

FieldSchema primaryKeyField=tableSchema.getPrimaryKeyField();
if(primaryKeyField==null) throw new DataException("Invalid data provided, Data required");

statementDS=statementMap.get("get_by_primary_key");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
preparedStatement=connection.prepareStatement(sqlStatement);
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
if(statementDS.getStatementParamsCount()==1)
{
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(0)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(0),((classGetterMethods.get(0)).invoke(obj))))==null)
{
throw new DataException("Invalid data provided to primary key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(0));
}
else
{
jdbcSetterMethods.get(0).invoke(preparedStatement,1,convertedData);
}
}catch(Exception e)
{
throw new DataException("Invalid data provided to primary key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(0));	//null set
//System.out.println("Error: "+e);
}
}
else
{
throw new DataException("Invalid changes performed in pojo");   //better message later on
}
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid "+primaryKeyField.getMethodName()+": "+convertedData);
}
else
{
List<Method> jdbcGetterMethods=statementDS.getJDBCGetterMethods();
List<Method> classSetterMethods=statementDS.getClassSetterMethods();
List<Integer> resultParamTypes=statementDS.getResultParamsType();
// System.out.println(classSetterMethods.size());
// System.out.println(jdbcGetterMethods.size());
for(int i=0;i<statementDS.getResultParamsCount();i++)
{
try
{
Object data=jdbcGetterMethods.get(i).invoke(resultSet,i+1);
convertedData=JDBCMethodExtractor.convertToJava(resultParamTypes.get(i),data);
classSetterMethods.get(i).invoke(prevObj,convertedData);
}catch(Exception e)
{
}
}
}
resultSet.close();
preparedStatement.close();

updateAndDeleteForeignKeyConstrainOnCompleteDB(prevObj,tableSchema);

statementDS=statementMap.get("unique_and_primary_key_validation");
sqlStatement=statementDS.getStatement().toString();
if(!sqlStatement.isBlank())
{
sqlStatements=sqlStatement.split(";");

jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
try
{
if(((pkClassGetterMethod=classGetterMethods.get(classGetterMethods.size()-1))==null) || (pkConvertedData=JDBCMethodExtractor.convertToJDBC((pkSQLType=sqlTypes.get(sqlTypes.size()-1)),pkClassGetterMethod.invoke(obj)))==null)
{
//pkClassGetterMethod=null;
//pkConvertedData=null;
throw new DataException("Invalid data provided to primary key, Data required");
}
else
{
pkJDBCSetterMethod=jdbcSetterMethods.get(jdbcSetterMethods.size()-1);
}
}catch(Exception e)
{
//pkClassGetterMethod=null;
//pkJDBCSetterMethod=null;
throw new DataException("Invalid data provided for primary key, Data required");
}
for(int i=0;i<statementDS.getStatementParamsCount()-1;i++)
{
String subSQL=(i<sqlStatements.length)?sqlStatements[i].trim():"";

if(subSQL.isBlank()) continue;
preparedStatement=connection.prepareStatement(subSQL);
// System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
{
    throw new DataException("Invalid data provided for unique key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));
}
else
{
jdbcSetterMethods.get(i).invoke(preparedStatement,1,convertedData);
}
pkJDBCSetterMethod.invoke(preparedStatement,2,pkConvertedData);
}catch(Exception e)
{
    throw new DataException("Invalid data provided for unique key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));	//null set
//preparedStatement.setNull(2,sqlTypes.get(sqlTypes.size()-1));
}
resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
if(exists)
{
throw new DataException("This "+convertedData+" is already in use. Please try another.");
}
}
}
statementDS=statementMap.get("foreign_key_validation");
sqlStatement=statementDS.getStatement().toString();
if(!sqlStatement.isBlank())
{
sqlStatements=sqlStatement.split(";");
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
for(int i=0;i<statementDS.getStatementParamsCount();i++)
{
String subSQL=(i<sqlStatements.length)?sqlStatements[i].trim():"";
if(subSQL.isBlank()) continue;
preparedStatement=connection.prepareStatement(subSQL);
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
{
    throw new DataException("Invalid data provided to foreign key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));
}
else
{
jdbcSetterMethods.get(i).invoke(preparedStatement,1,convertedData);
}
}catch(Exception e)
{
    throw new DataException("Invalid data provided to foreign key, Data required");
//preparedStatement.setNull(1,sqlTypes.get(i));	//null set
//System.out.println("Error: "+e);
}
resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
if(!exists)
{
throw new DataException("The selected "+convertedData+" does not exist. Please select a valid entry.");
}
}
}

statementDS=statementMap.get("update");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");	
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
preparedStatement=connection.prepareStatement(sqlStatement);
for(int i=0;i<statementDS.getStatementParamsCount();i++)
{
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
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
// System.out.println("Error: "+e);
}
}
preparedStatement.executeUpdate();
preparedStatement.close();
if(tableSchema.isCacheable())
{
Object clonedObj=objClass.getDeclaredConstructor().newInstance();
PojoCopier.copy(clonedObj,obj);     //Cloned Object stored in DS Cache
Object primaryKeyObj=pkClassGetterMethod.invoke(clonedObj);
cache.get(objClass).remove(primaryKeyObj);
cache.get(objClass).put(primaryKeyObj,clonedObj);             //We can't set the same object in our DS too.
//System.out.println("Cloned Obj: "+primaryKeyObj);
}
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
Connection connection=conn();
if(connection==null) throw new DataException("Call begin() before delete()");
if(primaryKey==null) throw new DataException("Invalid data provided to primary key, Data required");

try
{
Map<String,StatementDS> statementMap=statements.get(objClass);
if(statementMap==null) throw new DataException("Invalid data provided, Data required.");
Schema s=ORMDataModel.getInfo(objClass);
if(s==null) throw new DataException("Invalid data provided, Data required");
TableSchema tableSchema;
if(s instanceof TableSchema) tableSchema=(TableSchema)s;
else throw new DataException("Invalid data provided, Data required");

Object obj=objClass.getDeclaredConstructor().newInstance();
StatementDS statementDS;
List<Method> jdbcSetterMethods;
List<Method> classGetterMethods;
List<Integer> sqlTypes;

String sqlStatement="";
String[] sqlStatements;
PreparedStatement preparedStatement;
ResultSet resultSet;
ResultSet generatedKeys;
Object convertedData=null;

FieldSchema primaryKeyField=tableSchema.getPrimaryKeyField();
if(primaryKeyField==null) throw new DataException("Invalid data provided, Data required");

if(tableSchema.isCacheable())
{
obj=cache.get(objClass).get(primaryKey);
if(obj==null) throw new DataException("Invalid "+primaryKeyField.getMethodName()+": "+primaryKey);
}
else
{
statementDS=statementMap.get("get_by_primary_key");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
preparedStatement=connection.prepareStatement(sqlStatement);
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
if(statementDS.getStatementParamsCount()==1)
{
int i=0;
try
{
jdbcSetterMethods.get(i).invoke(preparedStatement,i+1,primaryKey);
}catch(Exception e)
{
preparedStatement.setNull(i+1,sqlTypes.get(i));	//null set
throw new DataException("Invalid data provided to primary key, Data required");
}
}
else
{
throw new DataException("Invalid data provided to primary key, Data required");
}
resultSet=preparedStatement.executeQuery();
if(!resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Invalid "+primaryKeyField.getMethodName()+": "+primaryKey);
}
else
{
List<Method> jdbcGetterMethods=statementDS.getJDBCGetterMethods();
List<Method> classSetterMethods=statementDS.getClassSetterMethods();
List<Integer> resultParamTypes=statementDS.getResultParamsType();
// System.out.println(classSetterMethods.size());
// System.out.println(jdbcGetterMethods.size());
for(int i=0;i<statementDS.getResultParamsCount();i++)
{
try
{
Object data=jdbcGetterMethods.get(i).invoke(resultSet,i+1);
convertedData=JDBCMethodExtractor.convertToJava(resultParamTypes.get(i),data);
classSetterMethods.get(i).invoke(obj,convertedData);
}catch(Exception e)
{
}
}
}
resultSet.close();
preparedStatement.close();
}
updateAndDeleteForeignKeyConstrainOnCompleteDB(obj,tableSchema);

statementDS=statementMap.get("delete");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
preparedStatement=connection.prepareStatement(sqlStatement);
for(int i=0;i<statementDS.getStatementParamsCount();i++)
{
//System.out.println(classGetterMethods.get(i).getName());
try
{
if(classGetterMethods.get(i)==null || (convertedData=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),classGetterMethods.get(i).invoke(obj)))==null)
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
// System.out.println("Error: "+e);
}
}
preparedStatement.executeUpdate();
preparedStatement.close();
if(tableSchema.isCacheable())
{
cache.get(objClass).remove(primaryKey);
}
}catch(DataException de)
{
throw de;
}catch(Exception exception)
{
throw new DataException(exception);
}
}
public DataManager select(Class objClass,String columns[]) throws DataException
{
Schema s=ORMDataModel.getInfo(objClass);
if(s==null) throw new DataException("Invalid data provided, Data required");
TableSchema tableSchema;
if(s instanceof TableSchema) tableSchema=(TableSchema)s;
else throw new DataException("Invalid data provided, Table required");

StringBuilder sb=new StringBuilder();
int i=0;
for(String col:columns)
{
if(i!=0) sb.append(", ");
sb.append(col);
i++;
}
session().qClass=objClass;
session().qStatement=("SELECT "+sb.toString()+" FROM "+tableSchema.getTableName());
return this;
}
public DataManager query(Class objClass) throws DataException
{
Schema s=ORMDataModel.getInfo(objClass);
if(s==null) throw new DataException("Invalid data provided, Data required");
TableSchema tableSchema;
if(s instanceof TableSchema) tableSchema=(TableSchema)s;
else throw new DataException("Invalid data provided, Table required");

session().qClass=objClass;
session().qStatement="SELECT * FROM "+tableSchema.getTableName();
return this;
}
public Object queryDS(Class objClass) throws DataException
{
Schema s=ORMDataModel.getInfo(objClass);
if(s==null) throw new DataException("Invalid data provided, Data required");
TableSchema tableSchema;
if(s instanceof TableSchema) tableSchema=(TableSchema)s;
else throw new DataException("Invalid data provided, Table required");

if(!tableSchema.isCacheable()) throw new DataException(objClass.getName()+" is not declared as Cacheable, call for query() instead of using queryDS()");
session().qClass=objClass;
List<Object> results=new ArrayList<>();
try
{
for(Object obj:cache.get(objClass).values())
{
Object clonedObj=objClass.getDeclaredConstructor().newInstance();
PojoCopier.copy(clonedObj,obj);
results.add(clonedObj);
}
}catch(Exception exception)
{
exception.printStackTrace();//comment it
throw new DataException("Unable to load data, try query() method");
}
return results;
}
public DataManager select(Class objClass) throws DataException
{
Schema schema=ORMDataModel.getInfo(objClass);
if(schema==null) throw new DataException("Invalid data provided, Data required");
ViewSchema viewSchema;
if(schema instanceof ViewSchema) viewSchema=(ViewSchema)schema;
else throw new DataException("Invalid data provided, View required");
Session s=session();
Connection connection=s.connection;
if(connection==null) throw new DataException("Call begin() before select()");
try
{
StatementDS selectStatement=statements.get(objClass).get("select");
String sqlStatement=selectStatement.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
s.qClass=objClass;
s.qStatement=sqlStatement;
return this;
}catch(DataException de)
{
throw de;
}
}
public Object view(Class objClass) throws DataException
{
Schema schema=ORMDataModel.getInfo(objClass);
if(schema==null) throw new DataException("Invalid data provided, Data required");
ViewSchema viewSchema;
if(schema instanceof ViewSchema) viewSchema=(ViewSchema)schema;
else throw new DataException("Invalid data provided, View required");
Session s=session();
Connection connection=s.connection;
if(connection==null) throw new DataException("Call begin() before view()");
try
{
StatementDS selectStatement=statements.get(objClass).get("select");
String sqlStatement=selectStatement.getStatement().toString();
if(sqlStatement.isBlank()) throw new DataException("Invalid data provided, Data required");
PreparedStatement preparedStatement=connection.prepareStatement(sqlStatement);

ResultSet resultSet=preparedStatement.executeQuery();
List<Object> resultList=new ArrayList<>();

List<Method> jdbcGetterMethods=selectStatement.getJDBCGetterMethods();
List<Method> classSetterMethods=selectStatement.getClassSetterMethods();
List<Integer> resultParamTypes=selectStatement.getResultParamsType();
Object instance;
Object convertedData;
Object data;
while(resultSet.next())
{
instance=objClass.getDeclaredConstructor().newInstance();
for(int i=0;i<selectStatement.getResultParamsCount();i++)
{
try
{
data=jdbcGetterMethods.get(i).invoke(resultSet,i+1);
convertedData=JDBCMethodExtractor.convertToJava(resultParamTypes.get(i),data);
classSetterMethods.get(i).invoke(instance,convertedData);
}catch(Exception e)
{
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
}catch(SQLException sqlException)
{
throw new DataException("Invalid data provided, view required");
}
catch(Exception exception)
{
throw new DataException(exception);
}
}
public DataManager where(String columnName)
{
Session s=session();
s.qStatement+=(s.whereUsed ?" ":" WHERE ")+columnName;
s.whereUsed=true;
return this;
}
public DataManager orderBy(String columnName) throws DataException
{
Session s=session();
if(s.orderByUsed) throw new DataException("Invalid statement, can't use multiple 'ORDER BY' in one statement");
s.qStatement+=(" ORDER BY "+columnName);
s.orderByUsed=true;
return this;
}
public DataManager eq(Object value)
{
session().qStatement+=("="+formatValue(value));
return this;
}
public DataManager gt(Object value)
{
session().qStatement+=(">"+formatValue(value));
return this;
}
public DataManager lt(Object value)
{
session().qStatement+=("<"+formatValue(value));
return this;
}
public DataManager ge(Object value)
{
session().qStatement+=(">="+formatValue(value));
return this;
}
public DataManager le(Object value)
{
session().qStatement+=("<="+formatValue(value));
return this;
}
public DataManager ne(Object value)
{
session().qStatement+=("!="+formatValue(value));     //<>
return this;
}
public DataManager and()
{
session().qStatement+=" AND ";
return this;
}
public DataManager or()
{
session().qStatement+=" OR ";
return this;
}
public Object fire() throws DataException
{
Session s=session();
Connection connection=s.connection;
if(connection==null) throw new DataException("Call begin() before fire()");
if(s.qClass==null) throw new DataException("Call query() before fire()");
try
{
Schema schema=ORMDataModel.getInfo(s.qClass);
if(schema==null) throw new DataException("Invalid data provided, Data required");

//System.out.println(s.qStatement);

PreparedStatement preparedStatement=connection.prepareStatement(s.qStatement);
ResultSet resultSet=preparedStatement.executeQuery();
List<Object> resultList=new ArrayList<>();
while(resultSet.next())
{
Object instance=s.qClass.getDeclaredConstructor().newInstance();
for(FieldSchema fs:schema.getAllFields())
{
try
{
String fieldName=fs.getMethodName();
Object value=resultSet.getObject(fs.getColumnName());
if(fs.isSetterAllowed())
{
String sFieldName=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
Method setterMethod=s.qClass.getMethod("set"+sFieldName,fs.getType());
setterMethod.invoke(instance,value);
}
else if(fs.isPublicAllowed())
{
Field field=s.qClass.getField(fieldName);
field.set(instance,value);
}
else
{
// System.out.println("Field: "+fieldName+" not allowed to show");
}
}catch(Exception exception)
{
// System.out.println("Exception: "+exception);
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
}catch(SQLException sqlException)
{
throw new DataException("Invalid statement provided to fire()");
}
catch(Exception exception)
{
throw new DataException(exception);
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
private void updateAndDeleteForeignKeyConstrainOnCompleteDB(Object obj,TableSchema tableSchema) throws DataException
{
//This IS THE CODE -> FOR ALL DB TRAVERSAL AND CHECK FOREIGN KEY CONSTRAINT -> NEED TO ALSO CHECKED IN UPDATE TOO [LATER ON]
Connection connection=conn();
String sqlStatement;
Class objClass=obj.getClass();
PreparedStatement preparedStatement;
ResultSet resultSet;
try
{
List<TableSchema> tables=ORMDataModel.getAllTableInfo();
// System.out.println("table size: "+tables.size());
for(TableSchema table:tables)
{
// System.out.println(table.getTableName());
if(tableSchema.equals(table)) continue;
List<FieldSchema> fkFields=table.getForeignKeyFields();
for(FieldSchema fkField:fkFields)
{
//     System.out.println(fkField.getFKParentClass()+", "+fkField.getFKParentColumn()+", "+fkField.getColumnName()+", "+fkField.getMethodName());

if(fkField.getFKParentClass().equals(tableSchema.getTableName()))
{
String fkParentColumn=fkField.getFKParentColumn();
FieldSchema fs=tableSchema.getFieldByColumnName(fkParentColumn);
String fieldName=fs.getMethodName();
String columnName=fs.getColumnName();
// System.out.println(fieldName+", "+columnName);
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
// System.out.println(sqlStatement);
preparedStatement=connection.prepareStatement(sqlStatement);
resultSet=preparedStatement.executeQuery();
if(resultSet.next())
{
resultSet.close();
preparedStatement.close();
throw new DataException("Unable to update or delete record, since this record is attached with other child record(s).");
}
preparedStatement.close();
}
}
}
}catch(SQLException sqlE)
{
// System.out.println(sqlE);
}
}

void _updateAndDeleteForeignKeyConstrainOnCompleteDB(Object obj,TableSchema tableSchema) throws DataException     //other way
{
/*
try
{
boolean exists=false;
Class<?> objClass=obj.geClass();
List<TableSchema> tables=ORMDataModel.getAllTableInfo();
PreparedStatement preparedStatement;
ResultSet resultSet;
Map<String,StatementDS> fkStatementMap;
// System.out.println("table size: "+tables.size());
for(TableSchema table:tables)
{
if(tableSchema.equals(table)) continue;
fkStatementMap=statements.get(table.getObjectClass());
if(statementMap==null) continue;
statementDS=fkStatementMap.get("foreign_key_validation");
sqlStatement=statementDS.getStatement().toString();
if(sqlStatement.isBlanl()) continue;
sqlStatements=sqlStatement.split(";");

jdbcSetterMethods=statementDS.getJDBCSetterMethods();
classGetterMethods=statementDS.getClassGetterMethods();
sqlTypes=statementDS.getStatementParamsType();
for(int i=0;i<statementDS.getStatementParamCount();i++)
{
String subSQL=(i<sqlStatements.length)?sqlStatements[i].trim():"";
if(subSQL.isBlank()) continue;
if(!subSQL.toUpperCase().contains(tableSchema.getTableName().toUpperCase())) continue;
List<FieldSchema> fkFields=table.getForeignKeyFields();
if(i>=fkFields.size()) continue;
FieldSchema fkField=fkFields.get(i);

String parentColName=fkField.getFKParentColumn();
TableSchema parentSchema=tableSchema;
FieldSchema parentField=parentSchema.getFieldByColumnName(parentColName);

if(parentField==null) continue;
Object value=getFieldValue(obj,objClass,parentField);
String checkSQL="SELECT 1 FROM "+table.getTableName()+" WHERE "+fkFields.getColumnName()+"=?";
 
preparedStatement=connection.prepareStatement(checkSQL);
try
{
if (value == null || classGetterMethods.get(i) == null)
{
preparedStatement.setNull(1, sqlTypes.get(i));
}
else
{
Object converted=JDBCMethodExtractor.convertToJDBC(sqlTypes.get(i),value);
if(converted==null) preparedStatement.setNull(1,sqlTypes.get(i));
else jdbcSetters.get(i).invoke(ps,1,converted);
}
}
catch(Exception e)
{
ps.setNull(1,sqlTypes.get(i));
}
resultSet=preparedStatement.executeQuery();
exists=resultSet.next();
resultSet.close();
preparedStatement.close();
if (exists) throw new DataException("Cannot update or delete: record is referenced by '"+ table.getTableName() + "'.");
}
}
}catch(DataException de)
{
throw de;
}catch(SQLException e)
{
}catch(Exception exception)
{
System.out.println(exception);
}
*/
}
private Object getFieldValue(Object obj, Class<?> objClass, FieldSchema fs)
{
String fieldName = fs.getMethodName();
try
{
if (fs.isGetterAllowed())
{
Method m = objClass.getMethod("get" + capitalize(fieldName));
return m.invoke(obj);
}
else if (fs.isPublicAllowed())
{
return objClass.getField(fieldName).get(obj);
}
}
catch (Exception ignored) {}
return null;
}
private static String capitalize(String s)
{
if (s == null || s.isEmpty()) return s;
return s.substring(0, 1).toUpperCase() + s.substring(1);
}
}

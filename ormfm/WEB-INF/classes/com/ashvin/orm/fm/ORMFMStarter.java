package com.ashvin.orm.fm;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.nio.charset.*;
import java.sql.*;

import java.util.*;
import com.google.gson.*;
import java.util.jar.*;
import javax.tools.*;

import com.ashvin.orm.fm.utils.*;
import com.ashvin.orm.fm.exceptions.*;
import com.ashvin.orm.fm.model.*;

public class ORMFMStarter extends HttpServlet
{
private File webINFFolder;
private ORMDataModel ormDataModel;
private String configFileName="conf.json";		//default name of configuration file, have to created in folder '/WEB-INF/conf.json'
public ORMFMStarter()
{
ormDataModel=ORMDataModel.getORMDataModel();
}
public void init()
{
System.out.println("INIT Method invoked");
ServletContext sc=getServletContext();
this.webINFFolder=new File(sc.getRealPath("/WEB-INF"));
System.out.println(this.webINFFolder.getAbsolutePath());

String configFileName=(String)sc.getInitParameter("CONFIG_FILE_NAME");
if(configFileName!=null && !configFileName.isBlank()) this.configFileName=configFileName;
createPojoJar();
}
private void createPojoJar()
{
StringBuilder sb;
File file=new File(this.webINFFolder,this.configFileName);
//System.out.println("File: "+file.getAbsolutePath());
if(!file.exists())
{
System.out.println("Configuration file required");
return;
}
try
{
FileReader fileReader=new FileReader(file);
JsonObject jsonObj=JsonParser.parseReader(fileReader).getAsJsonObject();
if(jsonObj==null)
{
System.out.println("Invalid json configuration file");
return;
}
String jdbcDriver=(jsonObj.get("jdbc-driver")!=null?jsonObj.get("jdbc-driver").getAsString():"");
String connectionURL=(jsonObj.get("connection-url")!=null?jsonObj.get("connection-url").getAsString():"");
String username=(jsonObj.get("username")!=null?jsonObj.get("username").getAsString():"");
String password=(jsonObj.get("password")!=null?jsonObj.get("password").getAsString():"");
String packageName=(jsonObj.get("package-name")!=null?jsonObj.get("package-name").getAsString():"testing.pojo");

//System.out.println("JDBC Driver: "+jdbcDriver);
//System.out.println("Connection URL: "+connectionURL);
//System.out.println("Username: "+username);
//System.out.println("Pass: "+password);
//System.out.println("Package name: "+packageName);

Class c=Class.forName(jdbcDriver);
Connection connection=DriverManager.getConnection(connectionURL,username,password);

DatabaseMetaData dbMetaData=connection.getMetaData();
//System.out.println(dbMetaData);
ResultSet tables=dbMetaData.getTables(null,null,"%",new String[]{"TABLE"});

File srcFolder=new File(this.webINFFolder,"src");
//System.out.println("Source file: "+srcFolder.getAbsolutePath());
if(!srcFolder.exists())
{
srcFolder.mkdir();
}
String packageNameWithSeperator=packageName.replace(".",File.separator);

File packageFolder=new File(srcFolder,packageNameWithSeperator);
if(!packageFolder.exists())
{
packageFolder.mkdirs();
}
//System.out.println(packageFolder.getAbsolutePath());
JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
if(compiler==null)
{
System.out.println("Error: JDK required");
return;
}
File jarFile=new File(this.webINFFolder.getPath()+File.separator+"lib"+File.separator+"ormfm.jar");
if(!jarFile.exists()) 
{
System.out.println("ormfm.jar required: (" + jarFile.getAbsolutePath() + ")");
return;
}

String classpath=jarFile.getPath()+System.getProperty("path.separator")+srcFolder.getPath()+File.separator+System.getProperty("path.separator")+".";

//System.out.println("Classpath: "+classpath);
File javaFile=null;
while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");
//System.out.println("Table name: "+tableName);
String standardTableName=ORMUtils.camelCaseRepresent(tableName);
standardTableName=standardTableName.substring(0,1).toUpperCase()+standardTableName.substring(1);
//System.out.println("standardTableName: "+standardTableName);

javaFile=new File(packageFolder,standardTableName+".java");
if(javaFile.exists()) javaFile.delete();
RandomAccessFile randomAccessFile=new RandomAccessFile(javaFile,"rw");
randomAccessFile.writeBytes("package "+packageName+";\r\n\r\n");
randomAccessFile.writeBytes("import com.ashvin.orm.fm.annotations.*;\r\n\r\n");

randomAccessFile.writeBytes("@Table(name=\""+tableName+"\")\r\n");
randomAccessFile.writeBytes("public class "+standardTableName+"\r\n");
randomAccessFile.writeBytes("{\r\n");

ResultSet tableColumns=dbMetaData.getColumns(null,null,tableName,null);
ResultSet pkTableColumns=dbMetaData.getPrimaryKeys(null,null,tableName);
ResultSet fkTableColumns=dbMetaData.getImportedKeys(null,null,tableName);
List<String> pkColumnNames=new LinkedList<>();
Map<String,List<String>> fkColumnNames=new HashMap<>();
List<String> fkList;

while(pkTableColumns.next())
{
String pkColumnName=pkTableColumns.getString("COLUMN_NAME");
//System.out.println("Primary key Column: "+pkColumnName);
pkColumnNames.add(pkColumnName);
}
while(fkTableColumns.next())
{
fkList=new LinkedList<>();
String fkColumnName=fkTableColumns.getString("FKCOLUMN_NAME");
fkList.add(fkColumnName);
fkList.add(fkTableColumns.getString("FKTABLE_NAME"));
fkList.add(fkTableColumns.getString("PKCOLUMN_NAME"));
fkList.add(fkTableColumns.getString("PKTABLE_NAME"));
fkColumnNames.put(fkColumnName,fkList);

//System.out.printf("Foreign Keys: %s -> references %s(%s)\n",fkTableColumns.getString("FKCOLUMN_NAME"),fkTableColumns.getString("PKTABLE_NAME"),fkTableColumns.getString("PKCOLUMN_NAME"));
}
//Unique constraints adding is pending -> done later on.
while(tableColumns.next())
{
String columnName=tableColumns.getString("COLUMN_NAME");
if(pkColumnNames.contains(columnName))
{
//System.out.println("add primary key annotation: "+columnName);
randomAccessFile.writeBytes("@PrimaryKey\r\n");
//Add primary key annotation.
}
if((fkList=fkColumnNames.get(columnName))!=null)
{
String fkParent=fkList.get(3);

String fkParentCamelCase=ORMUtils.camelCaseRepresent(fkParent);
fkParentCamelCase=fkParentCamelCase.substring(0,1).toUpperCase()+fkParentCamelCase.substring(1);
String fkColumn=fkList.get(2);
String fkColumnCamelCase=ORMUtils.camelCaseRepresent(fkColumn);

randomAccessFile.writeBytes("@ForeignKey(parent=\""+fkParent+"\",column=\""+fkColumn+"\")\r\n");
}
String columnType=tableColumns.getString("TYPE_NAME");
int size=tableColumns.getInt("COLUMN_SIZE");
String nullable=tableColumns.getString("IS_NULLABLE");
String isAutoIncrement=tableColumns.getString("IS_AUTOINCREMENT");
if(isAutoIncrement.equals("YES"))
{
randomAccessFile.writeBytes("@AutoIncrement\r\n");
}
String camelCaseColumnName=ORMUtils.camelCaseRepresent(columnName);
String javaTypeName=ORMUtils.jdbcToJavaMappedType(JDBCType.valueOf(tableColumns.getInt("DATA_TYPE"))).getName();
randomAccessFile.writeBytes("@Column(name=\""+columnName+"\")\r\n");
randomAccessFile.writeBytes("@SetterGetter\r\n");
randomAccessFile.writeBytes("private "+javaTypeName+" "+camelCaseColumnName+";\r\n");
String sCamelCaseColumnName=camelCaseColumnName.substring(0,1).toUpperCase()+camelCaseColumnName.substring(1);
sb=new StringBuilder();
sb.append("public void set").append(sCamelCaseColumnName).append("(").append(javaTypeName).append(" ").append(camelCaseColumnName).append(")\r\n");
sb.append("{\r\n").append("this.").append(camelCaseColumnName).append("=").append(camelCaseColumnName).append(";\r\n").append("}\r\n");
sb.append("public ").append(javaTypeName).append(" get").append(sCamelCaseColumnName).append("(").append(")\r\n");
sb.append("{\r\n").append("return this.").append(camelCaseColumnName).append(";\r\n").append("}\r\n");
randomAccessFile.writeBytes(sb.toString());
//System.out.printf("Field: %s | Type: %s(%d) | Nullable: %s | Auto Increment: %s\n",columnName,columnType,size,nullable,isAutoIncrement);
}
randomAccessFile.writeBytes("}\r\n");

//System.out.println("srcFile: "+javaFile.getAbsolutePath());

//javac -classpath 
String[] javacArguments=new String[]{
"-cp",classpath,
javaFile.getPath()
};
int compilationResult=compiler.run(null,null,null,javacArguments);
if(compilationResult!=0)
{
System.out.println("File: '"+javaFile.getAbsolutePath()+"' compilation failed!");
}
else
{
System.out.println("File: '"+javaFile.getAbsolutePath()+"' compiled successfully.");
}
}
File targetJarFolder=new File(this.webINFFolder+File.separator+"dist");
if(!targetJarFolder.exists())
{
targetJarFolder.mkdir();
}
File targetJarFile=new File(targetJarFolder,"generated_pojo.jar");
try
{
try(FileOutputStream fos=new FileOutputStream(targetJarFile);JarOutputStream jos=new JarOutputStream(fos)){
addFilesToJar(srcFolder,srcFolder,jos);
jos.flush();
}
System.out.println("JAR File '"+targetJarFile.getPath()+"' created.");
}catch(IOException ie)
{
System.out.println("IOException: "+ie);
}
}catch(Exception e)
{
System.out.println("Exception: "+e.getMessage());
}
}
private void addFilesToJar(File rootFolder,File currentFile,JarOutputStream jos) throws IOException
{
File[] files=currentFile.listFiles();
if(files==null) return;
for(File file:files)
{
if(file.isDirectory())
{
addFilesToJar(rootFolder,file,jos);
}
else if(file.getName().endsWith(".class"))
{
String relativePath=rootFolder.toURI().relativize(file.toURI()).getPath();
relativePath=relativePath.replace("\\","/");
//System.out.println("Adding entry: "+relativePath);
JarEntry jarEntry=new JarEntry(relativePath);
jos.putNextEntry(jarEntry);
try
{
FileInputStream fis=new FileInputStream(file);
byte[] buffer=new byte[4096];
int bytesRead;
while((bytesRead=fis.read(buffer))!=-1)
{
jos.write(buffer,0,bytesRead);
}
}catch(IOException e)
{
//System.out.println("Failed to add entry: "+relativePath+" - "+e.getMessage());
//throw e;
}finally
{
jos.closeEntry(); 
}

}

}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{

}
public void doPost(HttpServletRequest request,HttpServletResponse response)
{

}
}

package com.ashvin.orm.fm;

import java.io.*;
import java.nio.charset.*;
import java.sql.*;
import java.net.*;

import java.util.*;
import com.google.gson.*;
import java.lang.reflect.*;
import java.util.jar.*;
import javax.tools.*;

import com.ashvin.orm.fm.annotations.*;
import com.ashvin.orm.fm.utils.*;
import com.ashvin.orm.fm.exceptions.*;
import com.ashvin.orm.fm.model.*;

import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.*;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.*;
import com.itextpdf.layout.borders.*;
import com.itextpdf.io.image.*;

public class ORMFMTool 
{
private File parentWorkingDirectory;
private String configFileName="conf.json";		//Should available in current working directory.
private String jdbcDriver;
private String connectionURL;
private String username;
private String password;
private String packageName;
public ORMFMTool()
{
}
public void init() throws DataException
{
this.parentWorkingDirectory=new File(System.getProperty("user.dir"));
// System.out.println(this.parentWorkingDirectory.getAbsolutePath());

File file=new File(this.parentWorkingDirectory,this.configFileName);
//System.out.println("File: "+file.getAbsolutePath());
if(!file.exists())
{
//System.out.println("Configuration file required");
throw new DataException("Configuration file required");
}
try
{
FileReader fileReader=new FileReader(file);
JsonObject jsonObj=JsonParser.parseReader(fileReader).getAsJsonObject();
if(jsonObj==null)
{
//System.out.println("Invalid json configuration file");
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

Class c=Class.forName(jdbcDriver);
}catch(Exception e)
{
//System.out.println("Exception: "+e);
throw new DataException(e.getMessage());
}
}
public final void createPojo() throws DataException
{
StringBuilder sb;
try
{
Connection connection=DriverManager.getConnection(connectionURL,username,password);

DatabaseMetaData dbMetaData=connection.getMetaData();
//System.out.println(dbMetaData);
ResultSet tables=dbMetaData.getTables(null,null,"%",new String[]{"TABLE"});

File srcFolder=new File(this.parentWorkingDirectory,"src");
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
File jarFile=new File(this.parentWorkingDirectory.getPath()+File.separator+"lib"+File.separator+"ormfm.jar");
if(!jarFile.exists()) 
{
throw new DataException("ormfm.jar required: (" + jarFile.getAbsolutePath() + ")");
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
ResultSet ukTableColumns=dbMetaData.getIndexInfo(null,null,tableName,true,false);

List<String> pkColumnNames=new LinkedList<>();
Map<String,List<String>> fkColumnNames=new HashMap<>();
List<String> ukColumnNames=new LinkedList<>();
List<String> fkList;

while(pkTableColumns.next())
{
String pkColumnName=pkTableColumns.getString("COLUMN_NAME");
//System.out.println("Primary key Column: "+pkColumnName);
pkColumnNames.add(pkColumnName);
}
pkTableColumns.close();

while(ukTableColumns.next())
{
boolean nonUnique=ukTableColumns.getBoolean("NON_UNIQUE");
String ukColumnName=ukTableColumns.getString("COLUMN_NAME");
if(!nonUnique && !pkColumnNames.contains(ukColumnName))
{
ukColumnNames.add(ukColumnName);
}
}
ukTableColumns.close();

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
fkTableColumns.close();
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
if(ukColumnNames.contains(columnName))
{
randomAccessFile.writeBytes("@Unique\r\n");
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
tableColumns.close();
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.close();

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
tables.close();
connection.close();
}catch(Exception e)
{
// System.out.println("Exception: "+e.getMessage());
throw new DataException(e.getMessage());
}
}
public final void createViewPojo() throws DataException
{
StringBuilder sb;
try
{
Connection connection=DriverManager.getConnection(connectionURL,username,password);

DatabaseMetaData dbMetaData=connection.getMetaData();
//System.out.println(dbMetaData);
ResultSet viewRS=dbMetaData.getTables(null,null,"%",new String[]{"VIEW"});

File srcFolder=new File(this.parentWorkingDirectory,"src");
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
File jarFile=new File(this.parentWorkingDirectory.getPath()+File.separator+"lib"+File.separator+"ormfm.jar");
if(!jarFile.exists()) 
{
throw new DataException("ormfm.jar required: (" + jarFile.getAbsolutePath() + ")");
}

String classpath=jarFile.getPath()+System.getProperty("path.separator")+srcFolder.getPath()+File.separator+System.getProperty("path.separator")+".";
//System.out.println("Classpath: "+classpath);

File javaFile=null;
while(viewRS.next())
{
String viewName=viewRS.getString("TABLE_NAME");
//System.out.println("Table name: "+tableName);
String standardViewName=ORMUtils.camelCaseRepresent(viewName);
standardViewName=standardViewName.substring(0,1).toUpperCase()+standardViewName.substring(1);
//System.out.println("standardTableName: "+standardTableName);

javaFile=new File(packageFolder,standardViewName+".java");
if(javaFile.exists()) javaFile.delete();
RandomAccessFile randomAccessFile=new RandomAccessFile(javaFile,"rw");
randomAccessFile.writeBytes("package "+packageName+";\r\n\r\n");
randomAccessFile.writeBytes("import com.ashvin.orm.fm.annotations.*;\r\n\r\n");

randomAccessFile.writeBytes("@View(name=\""+viewName+"\")\r\n");
randomAccessFile.writeBytes("public class "+standardViewName+"\r\n");
randomAccessFile.writeBytes("{\r\n");

ResultSet viewColumns=dbMetaData.getColumns(null,null,viewName,null);
while(viewColumns.next())
{
String columnName=viewColumns.getString("COLUMN_NAME");
String camelCaseColumnName=ORMUtils.camelCaseRepresent(columnName);
String javaTypeName=ORMUtils.jdbcToJavaMappedType(JDBCType.valueOf(viewColumns.getInt("DATA_TYPE"))).getName();

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
}
viewColumns.close();
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.close();
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
viewRS.close();
connection.close();
}catch(Exception e)
{
// System.out.println("Exception: "+e.getMessage());
throw new DataException(e.getMessage());
}
}
public final void createJar(String targetJarFileName) throws DataException		//Compile and Create Jar
{
targetJarFileName=targetJarFileName.replace(" ","");
targetJarFileName=targetJarFileName.replace(".jar","");
if(targetJarFileName.isBlank()) targetJarFileName="generated_pojo";
try
{
File srcFolder=new File(this.parentWorkingDirectory,"src");
//System.out.println("Source file: "+srcFolder.getAbsolutePath());
if(!srcFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
String packageNameWithSeperator=packageName.replace(".",File.separator);
File packageFolder=new File(srcFolder,packageNameWithSeperator);
if(!packageFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
//System.out.println(packageFolder.getAbsolutePath());

JavaCompiler compiler=ToolProvider.getSystemJavaCompiler();
if(compiler==null)
{
throw new DataException("Error: JDK required");
}
File jarFile=new File(this.parentWorkingDirectory.getPath()+File.separator+"lib"+File.separator+"ormfm.jar");
if(!jarFile.exists()) 
{
throw new DataException("ormfm.jar required: (" + jarFile.getAbsolutePath() + ")");
}

String classpath=jarFile.getPath()+System.getProperty("path.separator")+srcFolder.getPath()+File.separator+System.getProperty("path.separator")+".";
//System.out.println("Classpath: "+classpath);

File[] files=packageFolder.listFiles();
for(File javaFile:files)
{
if(!javaFile.isDirectory() && javaFile.getName().endsWith(".java"))
{
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
}
File targetJarFolder=new File(this.parentWorkingDirectory+File.separator+"dist");
if(!targetJarFolder.exists())
{
targetJarFolder.mkdir();
}
File targetJarFile=new File(targetJarFolder,targetJarFileName+".jar");
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
}catch(DataException de)
{
throw de;
}catch(Exception e)
{
//System.out.println("Exception: "+e);
throw new DataException("Unable to create JAR file.");
}

}
private  void addFilesToJar(File rootFolder,File currentFile,JarOutputStream jos) throws IOException
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
throw e;
}finally
{
jos.closeEntry(); 
}
}
}
}
/*
public void loadAllPojoClassesToDS() throws DataException
{
File srcFolder=new File(this.parentWorkingDirectory,"src");
//System.out.println("Source file: "+srcFolder.getAbsolutePath());
if(!srcFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
String packageNameWithSeperator=packageName.replace(".",File.separator);
File packageFolder=new File(srcFolder,packageNameWithSeperator);
if(!packageFolder.exists())
{
throw new DataException("No source file available to create JAR file.");
}
//System.out.println(packageFolder.getAbsolutePath());

File[] files=packageFolder.listFiles();
for(File file:files)
{
try
{
if(file.exists() && !file.isDirectory() && file.getName().endsWith(".class"))
{
// System.out.println(file.getName());
Class objClass=Class.forName(packageName+"."+file.getName().replace(".class",""));
if(objClass==null) continue;
if(!objClass.isAnnotationPresent(Table.class))
{
//System.out.println("Class: "+objClass.getName()+" has no @Table annotation");
//continue;
throw new DataException("Class "+objClass.getName()+" has no @Table annotation"); 
}
//com.ashvin.orm.fm.annotations.Table tableAnnotation=objClass.getAnnotation(com.ashvin.orm.fm.annotations.Table.class);
Table tableAnnotation=(Table)objClass.getAnnotation(Table.class);
String tableName=tableAnnotation.name();
TableSchema tableSchema=new TableSchema(objClass,tableName);
Field[] javaFields=objClass.getDeclaredFields();
for(Field javaField:javaFields)
{
if(!javaField.isAnnotationPresent(Column.class)) continue;
Column columnAnnotation=javaField.getAnnotation(Column.class);
String columnName=columnAnnotation.name();
String fieldName=javaField.getName();
Class<?> fieldType=javaField.getType();

FieldSchema fieldSchema=new FieldSchema(fieldName,columnName,fieldType);

if(javaField.isAnnotationPresent(PrimaryKey.class))
{
fieldSchema.setPrimaryKey(true);
}
if(javaField.isAnnotationPresent(AutoIncrement.class))
{
boolean trueValue=true;
fieldSchema.setAutoIncrement(trueValue);
}
if(javaField.isAnnotationPresent(Unique.class))
{
boolean trueValue=true;
fieldSchema.setUnique(trueValue);
}
if(javaField.isAnnotationPresent(ForeignKey.class))
{
ForeignKey fkAnnotation=javaField.getAnnotation(ForeignKey.class);
String fkParentClass=fkAnnotation.parent();
String fkParentColumn=fkAnnotation.column();
fieldSchema.setForeignKey(fkParentClass,fkParentColumn);
}
int mods=javaField.getModifiers();
if(javaField.isAnnotationPresent(SetterGetter.class))
{
fieldSchema.setSetterAllowed(true);
fieldSchema.setGetterAllowed(true);
}
else if(Modifier.isPublic(mods))
{
fieldSchema.setPublicAllowed(true);
}
else
{
continue;   //Private properties with no setter getter are not included in this scenario
}
tableSchema.addField(fieldSchema);
}
ORMDataModel.addInfo(objClass,tableSchema);
//cache.put(objClass,tableSchema);
}
}catch(ClassNotFoundException cnfe)
{
// System.out.println("Exception: "+cnfe);
}catch(DataException de)
{
// System.out.println(de);
}catch(Exception e)
{
// System.out.println("Exception: "+e);
}
}
}
*/

public void createPDFDocumentation(String pdfFileName) throws DataException
{
ORMDataModel ormData;
List<TableSchema> tables=null;
List<ViewSchema> views=null;
try
{
if(!DataManager.isInitialized()) DataManager.initialize(this.parentWorkingDirectory);
ormData=ORMDataModel.getORMDataModel();
tables=ormData.getAllTableInfo();
views=ormData.getAllViewInfo();
}catch(DataException de)
{
throw de;
}

try
{
Connection connection=null;
DatabaseMetaData dbMetaData=null;
connection=DriverManager.getConnection(connectionURL,username,password);
dbMetaData=connection.getMetaData();
try
{
if(false) throw new DataException("");
try
{

File targetJarFolder=new File(this.parentWorkingDirectory+File.separator+"dist");
if(!targetJarFolder.exists())
{
targetJarFolder.mkdir();
}
String pwdPath=this.parentWorkingDirectory.getAbsolutePath()+File.separator;
File file=new File(targetJarFolder,pdfFileName+".pdf");
if(file.exists()) file.delete();
//System.out.println("PDF File: "+file.getAbsolutePath());
PdfWriter pdfWriter=new PdfWriter(file.getAbsolutePath());
PdfDocument pdfDocument=new PdfDocument(pdfWriter);
Document document=new Document(pdfDocument);
PdfFont titleFont=PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
PdfFont dataFont=PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
//Header
//System.out.println("Site image path: "+pwdPath+"logo.png");
Image logo=null;
Image correct=null;
Image incorrect=null;
try
{
logo=new Image(ImageDataFactory.create(pwdPath+"logo.png"));
logo.scaleToFit(50,50);
logo.setPaddingTop(10);
}catch(Exception e)
{
System.out.println(e.getMessage());
}
try
{
URL imageURL=getClass().getResource("images/correct.png");
if(imageURL==null)
{
System.out.println("Unable to fetch correct image");
}
else
{
correct=new Image(ImageDataFactory.create(imageURL));
correct.scaleToFit(16,16);
correct.setMarginTop(4);
correct.setMarginLeft(5);
}
imageURL=getClass().getResource("images/incorrect.png");
if(imageURL==null)
{
System.out.println("Unable to create incorrect image");
}
else
{
incorrect=new Image(ImageDataFactory.create(imageURL));
incorrect.scaleToFit(16,16);
incorrect.setMarginTop(4);
incorrect.setMarginLeft(5);
}
}catch(Exception e)
{
System.out.println(e.getMessage());
}

Paragraph top=new Paragraph();
top.add(logo);
top.add(new Text(" "));
top.add("ORM POJO Document").setFont(titleFont).setFontSize(30).setTextAlignment(TextAlignment.JUSTIFIED);
Paragraph title=new Paragraph("TABLE POJO(s)");
title.setFont(titleFont).setFontSize(20).setTextAlignment(TextAlignment.CENTER);
Text pageNumberText;

float[] columnWidth={1,10};
float[] innerTableColumnWidth={1,5};
float[] innerMostTableColumnWidth={1,7};
float[] fieldTableColumnWidth={4,3};
float[] foreignTableColumnWidth={4,4};
Table table=new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
Table innerTable;
Table innerMostTable;
Table fieldTable;
Table foreignFieldTable;

Cell cell0;
Cell cell1;

Cell headerCell0=new Cell().add(new Paragraph(" S.No. ").setFont(titleFont).setFontSize(18).setBackgroundColor(ColorConstants.BLUE));
Cell headerCell1=new Cell().add(new Paragraph(" Table ").setFont(titleFont).setFontSize(18).setBackgroundColor(ColorConstants.BLUE)); 

Paragraph creator=new Paragraph("Creator: Ashvin Parmar");
creator.setFont(titleFont).setFontSize(18).setFontColor(ColorConstants.BLACK);

int totalSize=tables.size();
int sno=0;
int pageSize=totalSize;
boolean newPage=true;
int pageNumber=0;
TableSchema tableSchema=null;
FieldSchema fieldSchema=null;
List<FieldSchema> fields=null;
int j=0;
for(int i=0;i<totalSize;i++)
{
//System.out.println("i="+i);
tableSchema=tables.get(i);
if(newPage)
{
document.add(top);
//pageNumberText=new Text("Page no: "+String.valueOf(++pageNumber));
//pageNumberText.setTextAlignment(TextAlignment.RIGHT).setFont(dataFont).setFontSize(18);
document.add(title);
//document.add(new Paragraph(pageNumberText).setTextAlignment(TextAlignment.RIGHT));
table=new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
table.addHeaderCell(headerCell0);
table.addHeaderCell(headerCell1);
//create Header
newPage=false;
}
//Add row to table
sno++;
cell0=new Cell().add(new Paragraph(String.valueOf(sno)));
cell0.setFont(dataFont).setFontSize(16).setTextAlignment(TextAlignment.CENTER);
//From here, the data inner table starts
innerTable=new Table(UnitValue.createPercentArray(innerTableColumnWidth)).useAllAvailableWidth();
innerTable.addCell(new Cell().add((new Paragraph("Table Name: ")).setFont(titleFont)).setPaddingLeft(5));
innerTable.addCell(new Cell().add(new Paragraph(tableSchema.getTableName())).setPaddingLeft(5));
innerTable.addCell(new Cell().add((new Paragraph("Pojo Class")).setFont(titleFont)));
innerTable.addCell(new Cell().add(new Paragraph(tableSchema.getObjectClass().getName())));

innerTable.addCell(new Cell().add(new Paragraph("Is Cacheable?").setFont(titleFont)));
if(tableSchema.isCacheable()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

innerTable.addCell(new Cell().add(new Paragraph("Field(s)").setFont(titleFont)));
fields=tableSchema.getAllFields();
if(fields.size()==0) 
{
innerTable.addCell(new Cell().add(new Paragraph("--no-fields--")));
}
else
{
innerMostTable=new Table(UnitValue.createPercentArray(innerMostTableColumnWidth)).useAllAvailableWidth();

Cell innerHeaderCell0=new Cell().add(new Paragraph(" S.No. ").setFont(titleFont).setFontSize(14).setBackgroundColor(ColorConstants.BLUE));
Cell innerHeaderCell1=new Cell().add(new Paragraph(" Field(s) ").setFont(titleFont).setFontSize(14).setBackgroundColor(ColorConstants.BLUE)); 

innerMostTable.addHeaderCell(innerHeaderCell0);
innerMostTable.addHeaderCell(innerHeaderCell1);
j=1;
for(FieldSchema fs:fields)
{
innerMostTable.addCell(new Cell().add((new Paragraph(""+j)).setFont(titleFont)).setPaddingLeft(5));

ResultSet colRS=dbMetaData.getColumns(null,null,tableSchema.getTableName(),fs.getColumnName());
int sqlType=Types.OTHER;
String sqlTypeName="OTHER";
String isNull="";
if(colRS.next()) 
{
sqlType=colRS.getInt("DATA_TYPE");
sqlTypeName=colRS.getString("TYPE_NAME");
isNull=colRS.getString("IS_NULLABLE");
}
colRS.close();
fieldTable=new Table(UnitValue.createPercentArray(fieldTableColumnWidth)).useAllAvailableWidth();

fieldTable.addCell(new Cell().add(new Paragraph("Column name [DB]: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add((new Paragraph(fs.getColumnName()))).setPaddingLeft(5));
fieldTable.addCell(new Cell().add(new Paragraph("SQL type [DB]: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add((new Paragraph(sqlTypeName))).setPaddingLeft(5));

fieldTable.addCell(new Cell().add(new Paragraph("Field name [Class]: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add((new Paragraph(fs.getMethodName()))).setPaddingLeft(5));
fieldTable.addCell(new Cell().add(new Paragraph("Java type [DB]: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add((new Paragraph(fs.getType().getName()))).setPaddingLeft(5));

fieldTable.addCell(new Cell().add(new Paragraph("Is nullable: ")).setPaddingLeft(5));
if("YES".equals(isNull)) fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
else fieldTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

if(fs.isPrimaryKey()) 
{
fieldTable.addCell(new Cell().add(new Paragraph("Is primary key?: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
}
if(fs.isAutoIncrement()) 
{
fieldTable.addCell(new Cell().add(new Paragraph("Is auto increment key?: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
}
if(fs.isUnique()) 
{
fieldTable.addCell(new Cell().add(new Paragraph("Is unique key?: ")).setPaddingLeft(5));
fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
}
if(fs.isForeignKey()) 
{
fieldTable.addCell(new Cell().add(new Paragraph("Foreign key: ")).setPaddingLeft(5));
foreignFieldTable=new Table(UnitValue.createPercentArray(foreignTableColumnWidth)).useAllAvailableWidth();

foreignFieldTable.addCell(new Cell().add(new Paragraph("Parent table: ")).setPaddingLeft(5));
foreignFieldTable.addCell(new Cell().add((new Paragraph(fs.getFKParentClass()))).setPaddingLeft(5));

foreignFieldTable.addCell(new Cell().add(new Paragraph("Parent column: ")).setPaddingLeft(5));
foreignFieldTable.addCell(new Cell().add((new Paragraph(fs.getFKParentColumn()))).setPaddingLeft(5));
//Class specification ==> later on...
fieldTable.addCell(new Cell().add(foreignFieldTable));
}

fieldTable.addCell(new Cell().add(new Paragraph("Is public allowed?: ")).setPaddingLeft(5));
if(fs.isPublicAllowed())  fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
else fieldTable.addCell(new Cell().add(new Paragraph().add(incorrect)));


fieldTable.addCell(new Cell().add(new Paragraph("Is setter allowed?: ")).setPaddingLeft(5));
if(fs.isSetterAllowed())  fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
else fieldTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

fieldTable.addCell(new Cell().add(new Paragraph("Is getter allowed?: ")).setPaddingLeft(5));
if(fs.isGetterAllowed())  fieldTable.addCell(new Cell().add(new Paragraph().add(correct)));
else fieldTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

innerMostTable.addCell(new Cell().add(fieldTable));
j++;
}
innerTable.addCell(new Cell().add(innerMostTable));
}

cell1=new Cell().add(innerTable);
//cell1.setFont(dataFont).setFontSize(16).setTextAlignment(TextAlignment.JUSTIFIED);
table.addCell(cell0);
table.addCell(cell1);

if(sno%pageSize==0 || sno==totalSize)
{
document.add(table);
document.add(creator);
if(sno<totalSize)
{
//add new page
}
newPage=true;
}
}

//VIEW pending

document.close();
System.out.println(pdfFileName+" PDF created");
}catch(IOException ioException)
{
System.out.println(ioException.getMessage());
}
}catch(DataException de)
{
System.out.println(de.getMessage());
}catch(Exception exception)
{
System.out.println("Unable to create "+pdfFileName+" file.");
System.out.println("Problem: "+exception.getMessage());
}
connection.close();
}catch(SQLException sqlException)
{
System.out.println(sqlException.getMessage());
}
}

public static void main(String args[])
{
ORMFMTool ormTool=new ORMFMTool();
if(args.length<1)
{
System.out.println("Choice required: ['generate_pojo','generate_view_pojo','generate_jar','generate_doc_pdf']");
return;
}
try
{
ormTool.init();
Scanner scanner=new Scanner(System.in);
for(String choice:args)
{
try
{
switch(choice)
{
case "generate_pojo": 
	ormTool.createPojo();
	break;
case "generate_view_pojo":
    ormTool.createViewPojo();
    break;
case "generate_jar":
	System.out.print("Enter Jar File name: ");
	String jarFileName=scanner.nextLine();
	ormTool.createJar(jarFileName);
	break;
case "generate_doc_pdf":
	System.out.print("Enter PDF Documentation File name: ");
	String pdfFileName=scanner.nextLine();
	ormTool.createPDFDocumentation(pdfFileName); 
	break;
default:
	System.out.println("Invalid request");
}
}catch(DataException de)
{
System.out.println(de.getMessage());
}
}
scanner.close();
}catch(DataException de)
{
System.out.println(de.getMessage());
}
}
}

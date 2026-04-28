import java.io.*;
import com.google.gson.*; 
import java.sql.*;
import java.util.*;

import com.ashvin.orm.fm.utils.*;

class eg2psp
{
static int uniqueFieldCount=1;
public static String camelCaseRepresent(String field)
{
StringBuilder camelCaseField=new StringBuilder("");
int i=0;
while(i<field.length() && !Character.isLetter(field.charAt(i))) i++;
if(i==field.length()) return "tmp"+(uniqueFieldCount++);
char m=field.charAt(i);
if(Character.isUpperCase(m)) m=Character.toLowerCase(m);
camelCaseField.append(m);
i++;
while(i<field.length())
{
if(Character.isLetterOrDigit(field.charAt(i)))
{
camelCaseField.append(field.charAt(i));
}
else
{
while(i<field.length() && !Character.isLetterOrDigit(field.charAt(i))) i++;
if(i<field.length())
{
m=field.charAt(i);
if(Character.isLowerCase(m)) m=Character.toUpperCase(m);
//if(m>=97 && m<=122) m=(m-(char)32);
camelCaseField.append(m);
}
}
i++;
}
return camelCaseField.toString();
}

public static void main(String args[])
{
StringBuilder sb;

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

//System.out.println(jdbcDriver);
//System.out.println(connectionURL);
//System.out.println(username);
//System.out.println(password);

Class c=Class.forName(jdbcDriver);
Connection connection=DriverManager.getConnection(connectionURL,username,password);

DatabaseMetaData dbMetaData=connection.getMetaData();
System.out.println(dbMetaData);
ResultSet tables=dbMetaData.getTables(null,null,"%",new String[]{"TABLE"});

File javaFile=null;
while(tables.next())
{
String tableName=tables.getString("TABLE_NAME");
System.out.println("Table name: "+tableName);
String standardTableName=camelCaseRepresent(tableName);
standardTableName=standardTableName.substring(0,1).toUpperCase()+standardTableName.substring(1);
//System.out.println("standardTableName: "+standardTableName);

javaFile=new File(standardTableName+".java");
if(javaFile.exists()) javaFile.delete();
RandomAccessFile randomAccessFile=new RandomAccessFile(javaFile,"rw");

randomAccessFile.writeBytes("import com.ashvin.orm.fm.annotations.*;\r\n\r\n");

randomAccessFile.writeBytes("@Table(name=\""+tableName+"\")\r\n");
randomAccessFile.writeBytes("class "+standardTableName+"\r\n");
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
System.out.println("Primary key Column: "+pkColumnName);
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

System.out.printf("Foreign Keys: %s -> references %s(%s)\n",fkTableColumns.getString("FKCOLUMN_NAME"),fkTableColumns.getString("PKTABLE_NAME"),fkTableColumns.getString("PKCOLUMN_NAME"));
}

while(tableColumns.next())
{
String columnName=tableColumns.getString("COLUMN_NAME");
if(pkColumnNames.contains(columnName))
{
System.out.println("add primary key annotation: "+columnName);
randomAccessFile.writeBytes("@PrimaryKey\r\n");
//Add primary key annotation.
}
if((fkList=fkColumnNames.get(columnName))!=null)
{
String fkParent=camelCaseRepresent(fkList.get(3));
fkParent=fkParent.substring(0,1).toUpperCase()+fkParent.substring(1);
String fkColumn=camelCaseRepresent(fkList.get(2));

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
String camelCaseColumnName=camelCaseRepresent(columnName);
String javaTypeName=ORMUtils.jdbcToJavaMappedType(JDBCType.valueOf(tableColumns.getInt("DATA_TYPE"))).getName();
randomAccessFile.writeBytes("@Column(name=\""+camelCaseColumnName+"\")\r\n");
randomAccessFile.writeBytes("@SetterGetter\r\n");
randomAccessFile.writeBytes("private "+javaTypeName+" "+camelCaseColumnName+";\r\n");
String sCamelCaseColumnName=camelCaseColumnName.substring(0,1).toUpperCase()+camelCaseColumnName.substring(1);
sb=new StringBuilder();
sb.append("public void set").append(sCamelCaseColumnName).append("(").append(javaTypeName).append(" ").append(camelCaseColumnName).append(")\r\n");
sb.append("{\r\n").append("this.").append(camelCaseColumnName).append("=").append(camelCaseColumnName).append(";\r\n").append("}\r\n");
sb.append("public ").append(javaTypeName).append(" get").append(sCamelCaseColumnName).append("(").append(")\r\n");
sb.append("{\r\n").append("return this.").append(camelCaseColumnName).append(";\r\n").append("}\r\n");
randomAccessFile.writeBytes(sb.toString());
System.out.printf("Field: %s | Type: %s(%d) | Nullable: %s | Auto Increment: %s\n",columnName,columnType,size,nullable,isAutoIncrement);
}
randomAccessFile.writeBytes("}\r\n");
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

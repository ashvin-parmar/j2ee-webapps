package com.ashvin.web.rock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.stream.*;
import java.nio.file.*;
import java.util.*;
import java.lang.annotation.*;
import java.lang.reflect.*;

import com.ashvin.web.rock.pojo.*;
import com.ashvin.web.rock.model.*;
import com.ashvin.web.rock.annotations.*;

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

public class WebRockStarter extends HttpServlet
{
private WebRockModel webRockModel;
private String jsFileName=null;
private File webINFFolder;
public WebRockStarter()
{
webRockModel=WebRockModel.getWebRockModel();
}
public void init()
{
ServletContext sc=getServletContext();
String servicePackagePrefix=(String)sc.getInitParameter("SERVICE_PACKAGE_PREFIX");
String jsFileName=(String)sc.getInitParameter("JS_FILE_NAME");
if(jsFileName!=null) this.jsFileName=jsFileName;
//System.out.println(servicePackagePrefix);
String pathToClassFolder=sc.getRealPath("/WEB-INF/classes");
this.webINFFolder=new File(sc.getRealPath("/WEB-INF"));
//System.out.println(pathToClassFolder);
File classesDir=new File(pathToClassFolder);
//System.out.println(classesDir.exists());
File[] folders=classesDir.listFiles();
for(File folder:folders)
{
if(folder.isDirectory() && folder.getName().startsWith(servicePackagePrefix))
{
//System.out.println(folder.getAbsolutePath());
//traverseToClassFiles(folder);
loadAllPathServices(folder);
}
}
List<Service> services=webRockModel.getServices();
SecurityAccess securityAccess;
Class checkPost1,checkPost2;
Method guard1,guard2;
for(Service service:services)
{
for(Service service2:services)
{
if(service==service2) continue;
securityAccess=service.getSecurityAccess();
if(securityAccess!=null)
{
checkPost1=securityAccess.getCheckPost();
checkPost2=service2.getServiceClass();
guard1=securityAccess.getGuard();
guard2=service2.getServiceMethod();
if(checkPost1!=null &&  checkPost2!=null &&  guard1!=null && guard2!=null && securityAccess.getCheckPost().equals(service2.getServiceClass()) && securityAccess.getGuard().equals(service2.getServiceMethod()))
{
//System.out.println("PATH to secured: "+service2.getPath());
securityAccess.setServicePath(service2.getPath());
//webRockModel.setPathService(service2.getPath(),service);
}
}
}
}
String siteName=sc.getInitParameter("SITE_NAME");
String sitePath=sc.getRealPath("/");
String webINFPath=sc.getRealPath("/WEB-INF/");
String pdfFileName=siteName+"DOC.pdf";
try
{
try
{
int totalSize=services.size();
//System.out.println("Size: "+totalSize);
//System.out.println("sitePath: "+sitePath);
//System.out.println("WebINFPath: "+webINFPath);
File file=new File(sitePath+pdfFileName);
if(file.exists()) file.delete();
//System.out.println("PDF File: "+file.getAbsolutePath());
PdfWriter pdfWriter=new PdfWriter(file.getAbsolutePath());
PdfDocument pdfDocument=new PdfDocument(pdfWriter);
Document document=new Document(pdfDocument);
PdfFont titleFont=PdfFontFactory.createFont(StandardFonts.TIMES_BOLD);
PdfFont dataFont=PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
//Header
Paragraph top=new Paragraph();
System.out.println("Site image path: "+sitePath+"student.png");
Image logo=new Image(ImageDataFactory.create(sitePath+"student.png"));
logo.scaleToFit(50,50);
logo.setPaddingTop(10);
//System.out.println("Student image created");
System.out.println("Correct image path: "+webINFPath+"correct.png");
Image correct=new Image(ImageDataFactory.create(webINFPath+"correct.png"));
correct.scaleToFit(16,16);
correct.setMarginTop(4);
correct.setMarginLeft(5);
//System.out.println("Correct image created");
System.out.println("Incorrect image path: "+webINFPath+"incorrect.png");
Image incorrect=new Image(ImageDataFactory.create(webINFPath+"incorrect.png"));
incorrect.scaleToFit(16,16);
incorrect.setMarginTop(4);
incorrect.setMarginLeft(5);
//System.out.println("Incorrect image created");

top.add(logo);
top.add(new Text(" "));
top.add(siteName+" DOC").setFont(titleFont).setFontSize(30).setTextAlignment(TextAlignment.JUSTIFIED);
Paragraph title=new Paragraph(siteName.toUpperCase()+" Service(s)");
title.setFont(titleFont).setFontSize(20).setTextAlignment(TextAlignment.CENTER);
Text pageNumberText;

float[] columnWidth={1,10};
float[] innerTableColumn={1,3};
float[] threeTableColumn={2,2,4};
Table table=new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
Table innerTable;
Table innerMostTable;
Cell cell0;
Cell cell1;

Cell headerCell0=new Cell().add(new Paragraph("S.No.").setFont(titleFont).setFontSize(18).setBackgroundColor(ColorConstants.BLUE));
Cell headerCell1=new Cell().add(new Paragraph("Service(s)").setFont(titleFont).setFontSize(18).setBackgroundColor(ColorConstants.BLUE)); 

Paragraph creator=new Paragraph("Creator: Ashvin Parmar");
creator.setFont(titleFont).setFontSize(18).setFontColor(ColorConstants.BLACK);
int sno=0;
int pageSize=totalSize;
boolean newPage=true;
int pageNumber=0;
Service service=null;
int j=0;
for(int i=0;i<totalSize;i++)
{
//System.out.println("i="+i);
service=services.get(i);
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
innerTable=new Table(UnitValue.createPercentArray(innerTableColumn)).useAllAvailableWidth();
innerTable.addCell(new Cell().add((new Paragraph("Path")).setFont(titleFont)).setPaddingLeft(5));
innerTable.addCell(new Cell().add(new Paragraph(service.getPath())).setPaddingLeft(5));
innerTable.addCell(new Cell().add((new Paragraph("Class")).setFont(titleFont)));
innerTable.addCell(new Cell().add(new Paragraph(service.getServiceClass().getName())));

innerTable.addCell(new Cell().add(new Paragraph("GET Allowed?").setFont(titleFont)));
if(service.isGetAllowed()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

innerTable.addCell(new Cell().add(new Paragraph("POST Allowed?").setFont(titleFont)));
if(service.isPostAllowed()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));


Method method=service.getServiceMethod();
innerTable.addCell(new Cell().add(new Paragraph("Method name").setFont(titleFont)));
innerTable.addCell(new Cell().add(new Paragraph(method.getName())));

innerTable.addCell(new Cell().add(new Paragraph("Return Type").setFont(titleFont)));
innerTable.addCell(new Cell().add(new Paragraph(method.getReturnType().getName())));

innerTable.addCell(new Cell().add(new Paragraph("Parameter(s)").setFont(titleFont)));
List<RequestParameterOnMethod> rpoms=service.getRequestParametersOnMethod();
if(rpoms.size()==0) 
{
innerTable.addCell(new Cell().add(new Paragraph("void")));
}
else
{
innerMostTable=new Table(UnitValue.createPercentArray(threeTableColumn)).useAllAvailableWidth();
j=1;
for(RequestParameterOnMethod rpom:rpoms)
{
innerMostTable.addCell(new Cell().add((new Paragraph("Parameter "+j)).setFont(titleFont)).setPaddingLeft(5));
if(rpom.getName()!=null) innerMostTable.addCell(new Cell().add((new Paragraph(rpom.getName()))).setPaddingLeft(5));
else 
{
Class parameterType=rpom.getParameterType();
if(parameterType.equals(ApplicationScope.class) || parameterType.equals(SessionScope.class) || parameterType.equals(RequestScope.class) || parameterType.equals(ApplicationDirectory.class))
{
innerMostTable.addCell(new Cell().add((new Paragraph("--autofilled--"))).setPaddingLeft(5));
}
else
{
innerMostTable.addCell(new Cell().add((new Paragraph("--json data arrived--"))).setPaddingLeft(5));
}
}
innerMostTable.addCell(new Cell().add(new Paragraph(rpom.getParameterType().getName())).setPaddingLeft(5));
j++;
}
innerTable.addCell(new Cell().add(innerMostTable));
}

innerTable.addCell(new Cell().add(new Paragraph("Error(s)").setFont(titleFont)));
Class<?> errorExceptions[]=method.getExceptionTypes();
if(errorExceptions.length==0) 
{
innerTable.addCell(new Cell().add(new Paragraph().add("--no exception--")));
}
else
{
innerMostTable=new Table(UnitValue.createPercentArray(innerTableColumn)).useAllAvailableWidth();
j=1;
for(Class errorException:errorExceptions)
{
innerMostTable.addCell(new Cell().add((new Paragraph("Exception "+j)).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(errorException.getName())).setPaddingLeft(5));
j++;
}
innerTable.addCell(new Cell().add(innerMostTable));
}

innerTable.addCell(new Cell().add(new Paragraph("Is run on start-up?").setFont(titleFont)));
if(service.getRunOnStartup()) innerTable.addCell(new Cell().add(new Paragraph("Priority: "+service.getPriority())));
else innerTable.addCell(new Cell().add(new Paragraph().add("--lazy loading--")));

innerTable.addCell(new Cell().add(new Paragraph("Request forwarding?").setFont(titleFont)));
String forwardTo=service.getForwardTo();
if(forwardTo==null || forwardTo.isBlank()) innerTable.addCell(new Cell().add(new Paragraph().add("--no forwarding--")));
else innerTable.addCell(new Cell().add(new Paragraph("to '"+forwardTo+"'")));


innerTable.addCell(new Cell().add(new Paragraph("Security Access?").setFont(titleFont)));
securityAccess=service.getSecurityAccess();
if(securityAccess!=null)
{
innerMostTable=new Table(UnitValue.createPercentArray(innerTableColumn)).useAllAvailableWidth();

checkPost1=securityAccess.getCheckPost();
guard1=securityAccess.getGuard();
if(checkPost1!=null && guard1!=null)
{
innerMostTable.addCell(new Cell().add((new Paragraph("Check post")).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(securityAccess.getCheckPost().getName())).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add((new Paragraph("Guard")).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(securityAccess.getGuard().getName())).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add((new Paragraph("Service Path")).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(securityAccess.getServicePath())).setPaddingLeft(5));
}
else
{
innerMostTable.addCell(new Cell().add((new Paragraph("Check post")).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph("--invalid check post class--")).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add((new Paragraph("Guard")).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph("--invalid guard method")).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add((new Paragraph("Service Path")).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph("--no service found--")).setPaddingLeft(5));
}
innerTable.addCell(new Cell().add(innerMostTable));
}
else 
{
innerTable.addCell(new Cell().add(new Paragraph().add("--no security--")));
}

innerTable.addCell(new Cell().add(new Paragraph("Auto Wired").setFont(titleFont)));
List<AutoWiredField> awfs=service.getAutoWiredFields();
if(awfs.size()==0) 
{
innerTable.addCell(new Cell().add(new Paragraph().add("--no auto-wired fields--")));
}
else
{
innerMostTable=new Table(UnitValue.createPercentArray(threeTableColumn)).useAllAvailableWidth();
j=1;
for(AutoWiredField awf:awfs)
{
innerMostTable.addCell(new Cell().add((new Paragraph("Field"+j)).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(awf.getName())).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(awf.getField().getType().getName())).setPaddingLeft(5));
j++;
}
innerTable.addCell(new Cell().add(innerMostTable));
}

innerTable.addCell(new Cell().add(new Paragraph("Inject Request Parameter(s)").setFont(titleFont)));
List<RequestParameterOnField> rpofs=service.getInjectRequestParameterFields();
if(rpofs.size()==0) 
{
innerTable.addCell(new Cell().add(new Paragraph().add("--no injection of query-string fields--")));
}
else
{
innerMostTable=new Table(UnitValue.createPercentArray(threeTableColumn)).useAllAvailableWidth();
j=1;
for(RequestParameterOnField rpof:rpofs)
{
innerMostTable.addCell(new Cell().add((new Paragraph("Field "+j)).setFont(titleFont)).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(rpof.getName())).setPaddingLeft(5));
innerMostTable.addCell(new Cell().add(new Paragraph(rpof.getField().getType().getName())).setPaddingLeft(5));
j++;
}
innerTable.addCell(new Cell().add(innerMostTable));
}

innerTable.addCell(new Cell().add(new Paragraph("Injection of application scope allowed?").setFont(titleFont)));
if(service.getInjectApplicationScope()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

innerTable.addCell(new Cell().add(new Paragraph("Injection of session scope allowed?").setFont(titleFont)));
if(service.getInjectSessionScope()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

innerTable.addCell(new Cell().add(new Paragraph("Injection of request scope allowed?").setFont(titleFont)));
if(service.getInjectRequestScope()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));

innerTable.addCell(new Cell().add(new Paragraph("Injection of application directory allowed?").setFont(titleFont)));
if(service.getInjectApplicationDirectory()) innerTable.addCell(new Cell().add(new Paragraph().add(correct)));
else innerTable.addCell(new Cell().add(new Paragraph().add(incorrect)));


// inner table data insertion ends here...

cell1=new Cell().add(innerTable);
//cell1.setFont(dataFont).setFontSize(16).setTextAlignment(TextAlignment.JUSTIFIED);
table.addCell(cell0);
table.addCell(cell1);

if(sno%pageSize==0 || sno==totalSize)
{
//add table to page
//add creator name
document.add(table);
document.add(creator);
if(sno<totalSize)
{
//add new page
//System.out.println("New page to add");
}
newPage=true;
}
}
document.close();
System.out.println(pdfFileName+" PDF created");
}catch(IOException ioException)
{
System.out.println(ioException.getMessage());
}
}catch(Exception exception)
{
System.out.println("Unable to create "+pdfFileName+" file.");
System.out.println("Problem: "+exception.getMessage());
}


//call all services which are specified to being called on startup. 
Collections.sort(services,((left,right)->left.getPriority()-right.getPriority()));
List<Service> startupServices=new ArrayList<>();
for(Service service:services)
{
if(service.getRunOnStartup())
{
if(service.getPriority()>=0)
{
//System.out.println("Priority: "+service.getPriority());
startupServices.add(service);
}
}
}

for(Service service:startupServices)
{
Class serviceClass=service.getServiceClass();
Method serviceMethod=service.getServiceMethod();
Object serviceClassObject;
Class returnType;
String jsonString="";
com.google.gson.Gson g1=new com.google.gson.Gson();   //Now Working
Object[] parametersValue=null;
try
{
serviceClassObject=serviceClass.newInstance();
returnType=serviceMethod.getReturnType();
//System.out.println(returnType.getName());
if(returnType.getName().equals("void")==false) continue;
serviceMethod.invoke(serviceClassObject,parametersValue);
}catch(Exception exception)
{
System.out.println("Exception: "+exception);
}
}
}
private String getClassName(String rootDir, Path classPath) 
{
String relativePath = Paths.get(rootDir).relativize(classPath).toString();  //Remove rootPath from classPath
return relativePath.replace(File.separator, ".").replace(".class", "");
}
private List<Path> findClassFiles(Path rootPath) 
{
List<Path> filesList;
try (Stream<Path> paths = Files.walk(rootPath)) 
{
filesList = paths
              .filter(Files::isRegularFile)
              .filter(p -> p.getFileName().toString().endsWith(".class"))
              .collect(Collectors.toList());
}catch (IOException e) 
{
System.out.println("Error traversing directory: " + e.getMessage());
filesList = List.of();
}
return filesList;
}
private void loadAllPathServices(File folder)
{
File jsFolder=new File(webINFFolder,"js");
if(jsFolder.exists() && jsFolder.isDirectory())
{
for(File f:jsFolder.listFiles())
{
f.delete();
}
}
jsFolder.delete();
jsFolder.mkdir();
//System.out.println("JS Folder Path: "+jsFolder.getAbsolutePath());
File jsFile=null;
RandomAccessFile randomAccessFile=null;
if(jsFileName!=null)
{
jsFile=new File(jsFolder,jsFileName);
if(jsFile.exists()) jsFile.delete();
}
try
{
String parentPath=folder.getParent();
List<Path> classPaths=findClassFiles(Paths.get(folder.getAbsolutePath()));
//Class<?> pathAnnotationClass=Class.forName("com.ashvin.web.rock.annotations.Path");
InjectApplicationScope injectApplicationScopeAvailableOnClass=null;
InjectSessionScope injectSessionScopeAvailableOnClass=null;
InjectRequestScope injectRequestScopeAvailableOnClass=null;
InjectApplicationDirectory injectApplicationDirectoryAvailableOnClass=null;
PATH pathAvailableOnClass=null;
PATH pathAvailableOnMethod=null;
POJO pojoAvailableOnClass=null;
GET getAvailableOnClass=null;
GET getAvailableOnMethod=null;
POST postAvailableOnClass=null;
POST postAvailableOnMethod=null;
SecuredAccess securedAccessAvailableOnClass=null;
SecuredAccess securedAccessAvailableOnMethod=null;
FORWARD forwardAvailableOnMethod=null;
OnStartup onStartupAvailableOnMethod=null;
AutoWired autoWiredAvailableOnField=null;
InjectRequestParameter injectRequestParameterAvailableOnField=null;

Class c;
Method m;
Method methods[];
Field fields[];
Annotation[][] parameterAnnotations;
Class<?>[] parameterTypes;

List<AutoWiredField> autoWiredFields=null;
List<RequestParameterOnField> injectRequestParameterFields=null;
List<RequestParameterOnMethod> requestParametersOnMethod=null;

AutoWiredField autoWiredField=null;
RequestParameterOnField requestParameterOnField=null;
RequestParameter requestParameterAvailableOnMethodParameter=null;
SecurityAccess securityAccess=null;

int i=0,priority;
int j=0;
int count;
String checkPost=null;
String guard=null;
boolean valid=false;
int methodCount=0;

for(Path path:classPaths)
{
pathAvailableOnClass=null;
pojoAvailableOnClass=null;
getAvailableOnClass=null;
postAvailableOnClass=null;
injectApplicationScopeAvailableOnClass=null;
injectSessionScopeAvailableOnClass=null;
injectRequestScopeAvailableOnClass=null;
injectApplicationDirectoryAvailableOnClass=null;
securedAccessAvailableOnClass=null;

String className=getClassName(parentPath,path);
//Class<?> loadedClass=cl.loadClass(className);    //URLClassLoader
Class<?> loadedClass=Class.forName(className);
System.out.println("Class: "+loadedClass.getName());
Annotation[] annos=loadedClass.getDeclaredAnnotations();
for(Annotation anno:annos)
{
if(pathAvailableOnClass==null && anno instanceof PATH)
{    
pathAvailableOnClass=(PATH)anno;
}
if(pojoAvailableOnClass==null && anno instanceof POJO)
{
pojoAvailableOnClass=(POJO)anno;
break;
}
if(getAvailableOnClass==null && (anno instanceof GET))
{
getAvailableOnClass=(GET)anno;
}
if(postAvailableOnClass==null && (anno instanceof POST))
{
postAvailableOnClass=(POST)anno;
}
if(injectApplicationScopeAvailableOnClass==null && (anno instanceof InjectApplicationScope))
{
injectApplicationScopeAvailableOnClass=(InjectApplicationScope)anno;
}
if(injectSessionScopeAvailableOnClass==null && (anno instanceof InjectSessionScope))
{
injectSessionScopeAvailableOnClass=(InjectSessionScope)anno;
}
if(injectRequestScopeAvailableOnClass==null && (anno instanceof InjectRequestScope))
{
injectRequestScopeAvailableOnClass=(InjectRequestScope)anno;
}
if(injectApplicationDirectoryAvailableOnClass==null && (anno instanceof InjectApplicationDirectory))
{
injectApplicationDirectoryAvailableOnClass=(InjectApplicationDirectory)anno;
}
if(securedAccessAvailableOnClass==null && (anno instanceof SecuredAccess))
{
securedAccessAvailableOnClass=(SecuredAccess)anno;
}
}
if(pojoAvailableOnClass!=null && pathAvailableOnClass!=null) throw new Exception("@POJO and @PATH both annotations can't set at the same time");
if(pojoAvailableOnClass!=null)
{
if(jsFileName==null)
{
jsFile=new File(jsFolder,pojoAvailableOnClass.value()+".js");
if(jsFile.getName().equals(".js")) jsFile=new File(jsFolder,loadedClass.getSimpleName()+".js");
}

String constructorFirstLine="constructor(";
String constructorInnerLine="{\r\n";
String fieldName;
String fieldNameCamelCase;
RandomAccessFile tmpRandomAccessFile=null;
File tmpFile=new File(jsFolder,UUID.randomUUID().toString());
if(tmpFile.exists()) tmpFile.delete();

tmpRandomAccessFile=new RandomAccessFile(tmpFile,"rw");
tmpRandomAccessFile.seek(0);

fields=loadedClass.getDeclaredFields();
i=0;
for(Field field:fields)
{
//System.out.println("Field name: "+field.getName());
fieldName=field.getName();
fieldNameCamelCase=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
tmpRandomAccessFile.writeBytes("set"+fieldNameCamelCase+"("+fieldName+")\r\n");
tmpRandomAccessFile.writeBytes("{\r\n");
tmpRandomAccessFile.writeBytes("this."+fieldName+"="+fieldName+";\r\n");
tmpRandomAccessFile.writeBytes("}\r\n");
tmpRandomAccessFile.writeBytes("get"+fieldNameCamelCase+"()\r\n");
tmpRandomAccessFile.writeBytes("{\r\n");
tmpRandomAccessFile.writeBytes("return this."+fieldName+";\r\n");
tmpRandomAccessFile.writeBytes("}\r\n");
constructorFirstLine+=(fieldName+((i+1)==fields.length?"":","));
constructorInnerLine+=("this."+fieldName+"="+fieldName+";\r\n");
i++;
}
constructorFirstLine+=(")\r\n");
constructorInnerLine+=("}\r\n");
tmpRandomAccessFile.seek(0);

randomAccessFile=new RandomAccessFile(jsFile,"rw");
randomAccessFile.seek(randomAccessFile.length());
randomAccessFile.writeBytes("class "+loadedClass.getSimpleName()+"\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes(constructorFirstLine);
randomAccessFile.writeBytes(constructorInnerLine);
while(tmpRandomAccessFile.getFilePointer()<tmpRandomAccessFile.length())
{
randomAccessFile.writeBytes(tmpRandomAccessFile.readLine()+"\r\n");
}
tmpRandomAccessFile.close();
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.close();
tmpFile.delete();
continue;
}
if(pathAvailableOnClass==null) continue;
//System.out.println("-------------PATH ON CLASS AVAILABLE-------------");

if(jsFileName==null)
{
jsFile=new File(jsFolder,pathAvailableOnClass.value().substring(1)+".js");
if(jsFile.getName()==null || jsFile.getName().isBlank()) jsFile=new File(jsFolder,loadedClass.getSimpleName()+".js");
}
String constructorFirstLine="constructor(";
String constructorInnerLine="{\r\n";
String fieldName;
String fieldNameCamelCase;
File tmpFile=new File(UUID.randomUUID().toString());
RandomAccessFile tmpRandomAccessFile=null;
if(tmpFile.exists()) tmpFile.delete();

tmpRandomAccessFile=new RandomAccessFile(tmpFile,"rw");
tmpRandomAccessFile.seek(0);

autoWiredFields=new LinkedList<>();
injectRequestParameterFields=new LinkedList<>();

fields=loadedClass.getDeclaredFields();
i=0;
for(Field field:fields)
{
autoWiredAvailableOnField=null;
injectRequestParameterAvailableOnField=null;
Annotation[] annos3=field.getDeclaredAnnotations();
for(Annotation anno3:annos3)
{
if(anno3 instanceof AutoWired)
{
autoWiredAvailableOnField=(AutoWired)anno3;
}
if(anno3 instanceof InjectRequestParameter)
{
injectRequestParameterAvailableOnField=(InjectRequestParameter)anno3;
}
}
if(autoWiredAvailableOnField!=null)
{
autoWiredField=new AutoWiredField();
autoWiredField.setField(field);
autoWiredField.setName(autoWiredAvailableOnField.name());
autoWiredFields.add(autoWiredField);
}
if(injectRequestParameterAvailableOnField!=null)
{
requestParameterOnField=new RequestParameterOnField();
requestParameterOnField.setName(injectRequestParameterAvailableOnField.value());
requestParameterOnField.setField(field);
injectRequestParameterFields.add(requestParameterOnField);
}
if(!(field.getType().equals(ApplicationScope.class) || field.getType().equals(SessionScope.class) || field.getType().equals(RequestScope.class) || field.getType().equals(ApplicationDirectory.class) || true)) //remove 'true' when confirm what to take in Service JS-class constructor
{
System.out.println("Field name: "+field.getName());
fieldName=field.getName();
fieldNameCamelCase=fieldName.substring(0,1).toUpperCase()+fieldName.substring(1);
tmpRandomAccessFile.writeBytes("set"+fieldNameCamelCase+"("+fieldName+")\r\n");
tmpRandomAccessFile.writeBytes("{\r\n");
tmpRandomAccessFile.writeBytes("this."+fieldName+"="+fieldName+";\r\n");
tmpRandomAccessFile.writeBytes("}\r\n");
tmpRandomAccessFile.writeBytes("get"+fieldNameCamelCase+"()\r\n");
tmpRandomAccessFile.writeBytes("{\r\n");
tmpRandomAccessFile.writeBytes("return this."+fieldName+";\r\n");
tmpRandomAccessFile.writeBytes("}\r\n");
//constructorFirstLine+=(fieldName+((i+1)==fields.length?"":","));
//constructorInnerLine+=("this."+fieldName+"="+fieldName+";\r\n");
}
i++;
}
constructorFirstLine+=(")\r\n");
constructorInnerLine+=("}\r\n");
tmpRandomAccessFile.seek(0);

randomAccessFile=new RandomAccessFile(jsFile,"rw");
randomAccessFile.seek(randomAccessFile.length());
randomAccessFile.writeBytes("class "+loadedClass.getSimpleName()+"\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes(constructorFirstLine);
randomAccessFile.writeBytes(constructorInnerLine);
while(tmpRandomAccessFile.getFilePointer()<tmpRandomAccessFile.length())
{
randomAccessFile.writeBytes(tmpRandomAccessFile.readLine()+"\r\n");
}
tmpRandomAccessFile.close();
tmpFile.delete();
//System.out.println(autoWiredFields.size());
methods=loadedClass.getDeclaredMethods();
j=0;
long positionToRewrite=randomAccessFile.getFilePointer();
for(Method method:methods)
{
pathAvailableOnMethod=null;
getAvailableOnMethod=null;
postAvailableOnMethod=null;
forwardAvailableOnMethod=null;
onStartupAvailableOnMethod=null;
securedAccessAvailableOnMethod=null;

Annotation[] annos2=method.getDeclaredAnnotations();
for(Annotation anno2:annos2)
{
if(pathAvailableOnMethod==null && anno2 instanceof PATH)
{
pathAvailableOnMethod=(PATH)anno2;
}
if(getAvailableOnClass==null && getAvailableOnMethod==null && anno2 instanceof GET)
{
getAvailableOnMethod=(GET)anno2;
}
if(postAvailableOnClass==null && postAvailableOnMethod==null && anno2 instanceof POST)
{
postAvailableOnMethod=(POST)anno2;
}
if(forwardAvailableOnMethod==null && anno2 instanceof FORWARD)
{
forwardAvailableOnMethod=(FORWARD)anno2;
}
if(onStartupAvailableOnMethod==null && anno2 instanceof OnStartup)
{
onStartupAvailableOnMethod=(OnStartup)anno2;
}
if(securedAccessAvailableOnMethod==null && anno2 instanceof SecuredAccess)
{
securedAccessAvailableOnMethod=(SecuredAccess)anno2;
}
}
if(pathAvailableOnMethod==null) continue;
//System.out.println("-------------------PATH AVAILABLE ON METHOD ----------");
//donedone
parameterAnnotations=method.getParameterAnnotations();
parameterTypes=method.getParameterTypes();
requestParameterAvailableOnMethodParameter=null;
requestParametersOnMethod=new ArrayList<>();
i=0;
for(Annotation[] pAnnos:parameterAnnotations)
{
requestParameterAvailableOnMethodParameter=null;
for(Annotation pAnno:pAnnos)
{
if(pAnno instanceof RequestParameter)
{
requestParameterAvailableOnMethodParameter=(RequestParameter)pAnno;
}
}
if(requestParameterAvailableOnMethodParameter!=null)
{
requestParametersOnMethod.add(new RequestParameterOnMethod(requestParameterAvailableOnMethodParameter.name().isBlank()?("val"+(i+1)):requestParameterAvailableOnMethodParameter.name(),parameterTypes[i]));
}
else
{
//requestParametersOnMethod.add(null);
requestParametersOnMethod.add(new RequestParameterOnMethod(null,parameterTypes[i]));
}
i++;
}
//System.out.println("Method: "+method.getName()+" parameters count: "+requestParametersOnMethod.size());

Service service=new Service();
String fullPath;
String path1=pathAvailableOnClass.value();
String path2=pathAvailableOnMethod.value();
fullPath=path1+path2;
System.out.println(fullPath);
service.setPath(fullPath);
service.setServiceMethod(method);
service.setServiceClass(loadedClass);
service.setAutoWiredFields(autoWiredFields);
service.setInjectRequestParameterFields(injectRequestParameterFields);
service.setRequestParametersOnMethod(requestParametersOnMethod);
//SecurityAccess method available
securityAccess=null;
checkPost=null;
guard=null;
if(securedAccessAvailableOnClass!=null)
{
checkPost=securedAccessAvailableOnClass.checkPost();
guard=securedAccessAvailableOnClass.guard();
}
if(securedAccessAvailableOnMethod!=null)
{
checkPost=securedAccessAvailableOnMethod.checkPost();
guard=securedAccessAvailableOnMethod.guard();
}
if(checkPost!=null && !checkPost.isBlank() && guard!=null && !guard.isBlank())
{
try
{
c=Class.forName(checkPost);
methods=c.getDeclaredMethods();
m=null;
methodCount=0;
for(Method guardMethod:methods)
{
if(guardMethod.getName().equals(guard))
{
valid=true;
for(Class fieldType:guardMethod.getParameterTypes())
{
if(!(fieldType.equals(ApplicationScope.class) ||  fieldType.equals(SessionScope.class) || fieldType.equals(RequestScope.class) || fieldType.equals(ApplicationDirectory.class)))
{
valid=false;
break;
}
}
if(valid)
{
m=guardMethod;
methodCount++;
}
}
}
if(methodCount==1)
{
securityAccess=new SecurityAccess();
securityAccess.setCheckPost(c);
securityAccess.setGuard(m);
}
else
{
securityAccess=new SecurityAccess();
}
}catch(Exception exception)
{
securityAccess=new SecurityAccess();  //with null class and null method
}
service.setSecurityAccess(securityAccess);
}
else if(securedAccessAvailableOnClass!=null || securedAccessAvailableOnMethod!=null)
{
service.setSecurityAccess(new SecurityAccess());
}
if(forwardAvailableOnMethod!=null)
{
String forwardToPath=forwardAvailableOnMethod.value();
if(forwardToPath!=null && forwardToPath.isBlank()==false)
{
service.setForwardTo(forwardToPath);
}
}
if(getAvailableOnClass!=null)
{
//System.out.println("GET on Class");
//webRockModel.setPathService(fullPath,service,"GET");
service.setIsGetAllowed(true);
}
if(postAvailableOnClass!=null)
{
//System.out.println("POST on class");
//webRockModel.setPathService(fullPath,service,"POST");
service.setIsPostAllowed(true);
}
if(getAvailableOnMethod!=null)
{
//System.out.println("GET on method");
//webRockModel.setPathService(fullPath,service,"GET");
service.setIsGetAllowed(true);
}
if(postAvailableOnMethod!=null)
{
//System.out.println("POST on method");
//webRockModel.setPathService(fullPath,service,"POST");
service.setIsPostAllowed(true);
}
if(service.getIsPostAllowed()==false && service.getIsGetAllowed()==false)
{
//System.out.println("NO GET, NO POST on METHOD and CLASS");
//webRockModel.setPathService(fullPath,service,"GET");
//webRockModel.setPathService(fullPath,service,"POST");
service.setIsPostAllowed(true);
service.setIsGetAllowed(true);
}
if(onStartupAvailableOnMethod!=null)
{
priority=0;
priority=onStartupAvailableOnMethod.priority();
if(priority>=0)
{
service.setRunOnStartup(true);
service.setPriority(priority);
}
}
if(injectApplicationScopeAvailableOnClass!=null)
{
service.setInjectApplicationScope(true);
}
if(injectSessionScopeAvailableOnClass!=null)
{
service.setInjectSessionScope(true);
}
if(injectRequestScopeAvailableOnClass!=null)
{
service.setInjectRequestScope(true);
}
if(injectApplicationDirectoryAvailableOnClass!=null)
{
service.setInjectApplicationDirectory(true);
}
String methodStr="";
String simpleName="";
methodStr=method.getName()+"(";

j=0;
count=0;
String bodyStr="";
String paramsStr="";
String queryStr="";
String name;
Class parameterType;
for(RequestParameterOnMethod rpom:requestParametersOnMethod)
{
simpleName="";
name=rpom.getName();
parameterType=rpom.getParameterType();
if(parameterType.equals(ApplicationScope.class) || parameterType.equals(SessionScope.class) || parameterType.equals(RequestScope.class) || parameterType.equals(ApplicationDirectory.class)) continue;
//System.out.println(name+", "+parameterType.getSimpleName());
if(name!=null)
{
simpleName=name;
simpleName=simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
if(j!=0) paramsStr+=",";
paramsStr+=simpleName;
j++;
}
else
{
simpleName=parameterType.getSimpleName();
simpleName=simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
bodyStr+="const body=JSON.stringify("+simpleName+");\r\n";
methodStr+=simpleName;
count++;
}
}
if(count>1 || (count==1 && j!=0)) continue;   //Service should not added, because wrong service.
//Complete method String
methodStr+=paramsStr;
methodStr+=")\r\n{\r\n";
//Complete Body String
if(bodyStr.isBlank())  bodyStr+="xhr.send();\r\n";
else bodyStr+="xhr.send(body);\r\n";
//Complete Param Query String
if(!paramsStr.isBlank()) 
{
queryStr="const queryString=new URLSearchParams({"+paramsStr+"}).toString();\r\n";
queryStr+="finalUrl+=\"?${queryString}\";\r\n";
}
else
{
queryStr="";
}
positionToRewrite=randomAccessFile.getFilePointer();
randomAccessFile.writeBytes(methodStr);
randomAccessFile.writeBytes("return new Promise((resolve,reject)=>{\r\n");
randomAccessFile.writeBytes("const xhr=new XMLHttpRequest();\r\n");
randomAccessFile.writeBytes("let finalUrl='"+fullPath.substring(1)+"';\r\n");
randomAccessFile.writeBytes(queryStr);
randomAccessFile.writeBytes("xhr.open(\"");
if(count==0 && service.isGetAllowed()) randomAccessFile.writeBytes("GET");
else if(count==1 && service.isPostAllowed()) randomAccessFile.writeBytes("POST");
else 
{
randomAccessFile.seek(positionToRewrite);
continue;
}
randomAccessFile.writeBytes("\",finalUrl);\r\n");
randomAccessFile.writeBytes("xhr.setRequestHeader('Content-Type','application/json');\r\n");
randomAccessFile.writeBytes("xhr.onload=()=>{\r\n");
randomAccessFile.writeBytes("if(xhr.status>=200 && xhr.status<300)\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes("resolve(JSON.parse(xhr.responseText));\r\n");
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.writeBytes("else\r\n");
randomAccessFile.writeBytes("{\r\n");
randomAccessFile.writeBytes("reject({\r\n");
randomAccessFile.writeBytes("status:xhr.status,\r\n");
randomAccessFile.writeBytes("message:JSON.parse(xhr.responseText).error\r\n");
randomAccessFile.writeBytes("});\r\n");
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.writeBytes("};\r\n");

randomAccessFile.writeBytes("xhr.onerror=()=>reject(new Error('Network Error'));\r\n");
randomAccessFile.writeBytes(bodyStr);
randomAccessFile.writeBytes("});\r\n}\r\n");

webRockModel.setPathService(fullPath,service);
}
randomAccessFile.writeBytes("}\r\n");
randomAccessFile.setLength(randomAccessFile.getFilePointer());
randomAccessFile.close();
}
}catch(Exception ioException)
{
System.out.println("Exception: "+ioException.getMessage());
}
}
public void doGet(HttpServletRequest request,HttpServletResponse response)
{

}
public void doPost(HttpServletRequest requese,HttpServletResponse response)
{

}
}

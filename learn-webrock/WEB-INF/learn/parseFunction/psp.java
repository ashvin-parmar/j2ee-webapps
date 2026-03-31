import com.google.gson.*;

class Student implements java.io.Serializable
{
public int rollNumber;
public String name;
}
class psp
{
public Object parseTo(Class parameterType,String parameterValue)
{
return parseTo(parameterType,parameterValue,null);
}
public Object parseTo(Class parameterType,String parameterValue,String convertFrom)
{
Object result;
if(parameterType==null) return null;
if(parameterValue==null) parameterValue="";
//System.out.println("Type: "+parameterType+" value: "+parameterValue);
result=null;
if(parameterType.equals(Long.TYPE) || parameterType.equals(java.lang.Long.class))
{
try
{
result=Long.parseLong(parameterValue);
}catch(NumberFormatException nfe)
{
result=(long)0;
}
}
if(parameterType.equals(Integer.TYPE) || parameterType.equals(java.lang.Integer.class))
{
try
{
result=Integer.parseInt(parameterValue);
}catch(NumberFormatException nfe)
{
result=(int)0;
}
}
if(parameterType.equals(Short.TYPE) || parameterType.equals(java.lang.Short.class))
{
try
{
result=Short.parseShort(parameterValue);
}catch(NumberFormatException nfe)
{
result=(short)0;
}
}
if(parameterType.equals(Byte.TYPE) || parameterType.equals(java.lang.Byte.class))
{
try
{
result=Byte.parseByte(parameterValue);
}catch(NumberFormatException nfe)
{
result=(byte)0;
}
}
if(parameterType.equals(Double.TYPE) || parameterType.equals(java.lang.Double.class))
{
try
{
result=Double.parseDouble(parameterValue);
}catch(NumberFormatException nfe)
{
result=(double)0.0;
}
}
if(parameterType.equals(Float.TYPE) || parameterType.equals(java.lang.Float.class))
{
try
{
result=Float.parseFloat(parameterValue);
}catch(NumberFormatException nfe)
{
result=(float)0.0;
}
}
if(parameterType.equals(Boolean.TYPE) || parameterType.equals(java.lang.Boolean.class))
{
if(parameterValue.isBlank()==false) result=Boolean.parseBoolean(parameterValue);
else result=(boolean)false;
}
if(parameterType.equals(Character.TYPE) || parameterType.equals(java.lang.Character.class))
{
if(parameterValue.isBlank()==false) result=parameterValue.charAt(0);
else result=(char)' ';
}
if(parameterType.equals(java.lang.String.class))
{
result=parameterValue;
}
if(result==null)
{
if(convertFrom.equals("json"))
{
Gson gson=new Gson();
try
{
result=gson.fromJson(parameterValue,parameterType);
}catch(Exception exception)
{
result=null;
}
}
}
return result;
}
public static void main(String args[])
{
psp p=new psp();
Gson gson=new Gson();
int a=(int)p.parseTo(Integer.class,"10");
float b=(float)p.parseTo(Float.class,"243.234");
psp pp=(psp)p.parseTo(psp.class,gson.toJson(p,psp.class),"json");
String abc=(String)pp.parseTo(String.class,"Something");
System.out.println("-------Some testing-----");
System.out.println((Integer)p.parseTo(int.class,"1203"));
System.out.println(a);
System.out.println(b);
System.out.println(pp);
System.out.println(abc);

System.out.println("--------main testing---------");
System.out.println("Testing on long: "+(long)p.parseTo(long.class,"242523435246342"));

System.out.println("Testing on int: "+(int)p.parseTo(Integer.class,"12312"));
System.out.println("Testing on short: "+(short)p.parseTo(short.class,"534"));
System.out.println("Testing on byte: "+(Byte)p.parseTo(Byte.class,"100"));
System.out.println("Testing on double: "+(Double)p.parseTo(double.class,"2425342.5234234"));
System.out.println("Testing on float: "+(Float)p.parseTo(float.class,"423.2332"));
System.out.println("Testing on char: "+(Character)p.parseTo(char.class,"A"));
System.out.println("Testing on boolean: "+(boolean)p.parseTo(boolean.class,"true"));
System.out.println("Testing on string: "+(String)p.parseTo(String.class,"There are some information"));
System.out.println("Testing on complex object: "+((Student)p.parseTo(Student.class,"{\"name\":\"Ashvin\",\"rollNumber\":\"1001\"}","json")).name);

System.out.println("-------Corner cases ---------");
System.out.println("Passing null testing: "+(int)p.parseTo(Integer.class,"null"));    //Invalid data passed, then default value of integer is set.
System.out.println("Passing empty string: "+(double)p.parseTo(double.class,""));
//System.out.println("Passing null to parameterType: "+(int)p.parseTo(null,null));      //This line to show that its framework user mistake, not the framework developer mistake. Return type is Object, cannot take in primitive if you are not sure about its primitive returning value. 
System.out.println("Passing null to parameterType: "+(Integer)p.parseTo(null,null));
}
}

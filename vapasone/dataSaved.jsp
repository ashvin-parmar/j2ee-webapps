<!DOCTYPE HTML>
<html>
<head>
<meta charset='utf-8'>
<title>"My first web application (Using JSP)1"</title>
</head>
<body>
<%
String name=request.getParameter("name").trim();
String rollNumber=request.getParameter("rollNumber").trim();
int rn=0;
if(rollNumber!=null)
{
try
{
rn=Integer.parseInt(rollNumber);
}catch(NumberFormatException nfe)
{
rn=0;
}
}
String gender=request.getParameter("gender");

System.out.println("Name: "+name);
System.out.println("Roll number: "+rn);
System.out.println("Gender: "+(gender.equals("M")?"Male":"Female"));
%>
<b>Data saved</b><br>
Roll number: <%=rn%><br>
Name: <%=name%><br>		<!--  Over here only variable part are written-->
<%
out.println("Gender: "+(gender.equals("M")?"Male":"Female"));
%>

<form action='/vapasone/abcd.html'>
<button type='submit'>OK</button>
</form>
</body>
</html>

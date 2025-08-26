<!DOCTYPE html>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>"My first JSP Example"</title>
</head>
<body>
Data Saved<br>
RollNumber:
<%=
request.getParameter("rollNumber").trim()
%>
<br>
Name:
<%=
request.getParameter("name").trim()
%>
<br>
Gender:
<%=
request.getParameter("gender").trim()
%>
<br>
<form action='/vapasone/index.html'>
<button type='submit'>OK</button>
</form>
</body>
</html>

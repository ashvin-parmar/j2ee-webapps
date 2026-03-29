<!DOCTYPE html>
<html lang='en'>
<head>
<title>AJAX Examples</title>
<meta charset='utf-8'>
</head>
<body>
<h3 >Student</h3>
<div id='addStudentPanel' >
<form id='studentAddForm' style='width:500px;margin:5px;' method="POST" action='/webrock/autoWiredTesting3Line.jsp'>
Roll Number: &nbsp;
<input id='rollNumber' name='rollNumber' type='text' required><br>
Name: &nbsp;
<input id='name' name='name' type='text' required><br>
<span id='errorSection' style='color:red;'></span><br>
<button type='submit' id='addStudentButton' >Add Student</button><br><br>
</form>
<br>
<a href='/webrock/index.html'>Home</a><br>
</div>
</body>
</html>

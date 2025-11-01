<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<link rel='stylesheet' type='text/css' href='/stylethree/css/styles.css'>
</head>
<body>
<!-- Main container start here -->
<div class='main-container'>
<!-- header start here -->
<div class='header'>
<a href='/stylethree/index.jsp'><img src='/stylethree/images/hr_nexus_logo.png' class='logo'></a>
<div class='brand-name'>HR-Nexus</div>

<div class='user-field'> 
<img src='/stylethree/images/admin.png' class='user-logo'>
<span id='username'></span>
<a href='/stylethree/logout' class='logout'>Logout</a>
</div>
</div>
<!-- header ends here -->
<!-- middle content start here -->
<div class='content'>
<!-- left panel start here -->
<div class='content-left-panel'>

<tm:If condition='${module==DESIGNATION}'>
<b>Designations</b><br>
</tm:If>
<tm:If condition='${module!=DESIGNATION}'>
<a href='/stylethree/Designations.jsp'>Designations</a><br>
</tm:If>
<tm:If condition='${module==EMPLOYEE}'>
<b>Employees</b><br>
</tm:If>
<tm:If condition='${module!=EMPLOYEE}'>
<a href='/stylethree/Employees.jsp'>Employees</a><br>
</tm:If>
<tm:If condition='${module!=HOME}'>
<a href='/stylethree/index.jsp'>Home</a>
</tm:If>
</div>
<!-- left panel ends here -->
<!-- right panel start here -->
<div class='content-right-panel'>

<script>
function validateLogin()
{
var username=document.getElementById('username');
//alert(username);
var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
var responseData=JSON.parse(this.responseText);
if(responseData.username!=null)
{
//alert(this.responseText);
username.innerHTML=responseData.username;
}
if(responseData.error!=null)
{
//alert("Not login");
window.location.href="LoginPage.jsp";
}
}
else
{
alert("Some problem");
window.location.href="LoginPage.jsp";
}
}
}
xmlHttpRequest.open("POST","validateLoginCredentials",true);
//xmlHttpRequest.setRequestHeader("Content-Type","application/json");
xmlHttpRequest.send();
}
window.addEventListener('load',validateLogin());
</script>


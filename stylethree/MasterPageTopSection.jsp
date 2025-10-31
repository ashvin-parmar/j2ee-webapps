<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<script>
function validateLogin()
{
var username=document.getElementById('username');
var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
var responseData=this.responseText;
alert(responseData);
var un=JSON.parse(responseData);
if(un==null)
{
alert("Not login");
}
else
{
username.innerHTML=un;
}
}
else
{
alert("Some problem");
}
}
}
xmlHttpRequest.open("POST","validateLoginCredentials",true);
//xmlHttpRequest.setRequestHeader("Content-Type","application/json");
xmlHttpRequest.send();
}
window.addEventListener('load',validateLogin());
</script>
<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<link rel='stylesheet' type='text/css' href='/styletwo/css/styles.css'>
</head>
<body>
<!-- Main container start here -->
<div class='main-container'>
<!-- header start here -->
<div class='header'>
<a href='/styletwo/index.jsp'><img src='/styletwo/images/hr_nexus_logo.png' class='logo'></a>
<div class='brand-name'>HR-Nexus</div>

<div class='user-field'> 
<img src='/styletwo/images/admin.png' class='user-logo'>
<span id='username'></span>
<a href='/styletwo/logout' class='logout'>Logout</a>
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
<a href='/styletwo/Designations.jsp'>Designations</a><br>
</tm:If>
<tm:If condition='${module==EMPLOYEE}'>
<b>Employees</b><br>
</tm:If>
<tm:If condition='${module!=EMPLOYEE}'>
<a href='/styletwo/Employees.jsp'>Employees</a><br>
</tm:If>
<tm:If condition='${module!=HOME}'>
<a href='/styletwo/index.jsp'>Home</a>
</tm:If>
</div>
<!-- left panel ends here -->
<!-- right panel start here -->
<div class='content-right-panel'>

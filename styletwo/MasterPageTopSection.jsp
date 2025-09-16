<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:ValidateLogin>
<jsp:forward page='/LoginPage.jsp' />
</tm:ValidateLogin>
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

<div style='float:right;display:flex;align-items:center;padding-left:2px;padding-right:2px;'> 
<img src='/styletwo/images/admin.png' style='width:20px;'>
${username}
<a href='/styletwo/logout' style='padding:4px;margin-left:4px;'>logout</a>
</div>


<div class='brand-name'>HR-Nexus</div>
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

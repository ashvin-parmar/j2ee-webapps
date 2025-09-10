<jsp:useBean id='messageBean' scope='request' class='com.ashvin.hr.nexus.beans.MessageBean' />
<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<!DOCTYPE HTML>
<html>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<link rel='stylesheet' type='text/css' href='/styletwo/css/styles.css'>
</head>
<body>
<!-- Main content start here -->
<div class='main-container'>
<!-- header start here -->
<div class='header'>
<img src='/styletwo/images/hr_nexus_logo.png' class='logo'>
<div class='brand-name'>HR-Nexus</div>
</div>
<!-- header ends here -->
<!-- middle content start here -->
<div class='content'>
<!-- left panel start here -->
<div class='content-left-panel'>
<a href='/styletwo/designations.jsp' style='float:left'>Designations</a><br>
<a href='/styletwo/employees.jsp' style='float:left'>Employees</a><br>
</div>
<!-- left panel ends here -->
<!-- right panel start here -->
<div class='content-right-panel'>
<h2>${messageBean.heading}</h2>
${messageBean.message}<br>
<tm:If condition='${messageBean.hasToGenerateButtons}'>
<table>
<tr>
<td>
<form action='${messageBean.buttonOneAction}'>
<button type='submit'>${messageBean.buttonOneText}</button>
</form>
</td>
<tm:If condition='${messageBean.hasToGenerateTwoButtons}'>
<td>
<form action='${messageBean.buttonTwoAction}'>
<button type='submit'>${messageBean.buttonTwoText}</button>
</form>
</td>
</tm:If>
</tr>
</table>
</tm:If>
</div>
<!-- right panel ends here -->
</div>
<!-- middle content ends here -->
<!-- footer start here -->
<div class='footer'>
&copy; HR-Nexus 2025
</div>
<!-- footer ends here -->
</div>
<!-- Main content ends here -->
</body>
</html>

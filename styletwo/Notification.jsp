<jsp:useBean id='messageBean' scope='request' class='com.ashvin.hr.nexus.beans.MessageBean' />
<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<!DOCTYPE HTML>
<html>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
</head>
<body>
<!-- Main content start here -->
<div style='width:90hw;height:95vh;border:1px solid black'>
<!-- header start here -->
<div style='width:90hw;margin:5px;border:1px solid black'>
<img src='/styletwo/images/hr_nexus_logo.png' style='width:30px;float:left'>
<div style='margin:4px;font-size:15pt'>HR-Nexus</div>
</div>
<!-- header ends here -->
<!-- middle content start here -->
<div style='width:90hw;height:72vh;margin:5px;border:1px solid white'>
<!-- left panel start here -->
<div style='height:65vh;margin:5px;padding:5px;float:left;border:1px solid black'>
<a href='/styletwo/designations.jsp' style='float:left'>Designations</a><br>
<a href='/styletwo/employees.jsp' style='float:left'>Employees</a><br>
</div>
<!-- left panel ends here -->
<!-- right panel start here -->
<div style='height:65vh;margin-left:110px;margin-right:5px;margin-bottom:5px;margin-top:5px;padding:5px;border:1px solid black'>
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
<div style='text-align:center;margin:5px;font-size:10pt;border:1px solid white'>
&copy; HR-Nexus 2025
</div>
<!-- footer ends here -->
</div>
<!-- Main content ends here -->
</body>
</html>

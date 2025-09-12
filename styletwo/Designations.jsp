<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<jsp:useBean id="designationBean" scope="request" class="com.ashvin.hr.nexus.beans.DesignationBean" />

<!DOCTYPE HTML>
<html>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<link rel='stylesheet' type='text/css' href='/styletwo/css/styles.css' >
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
<b>Designations</b><br>
<a href='/styletwo/employees.jsp' style='float:left'>Employees</a><br><br>
<a href='/styletwo/index.html'>Home</a>
</div>
<!-- left panel ends here -->
<!-- right panel start here -->
<div class='content-right-panel'>
<h2>Designations</h2>
<table border='1'>
<thead>
<tr>
<th colspan='4' style='text-align:right'><a href='/styletwo/AddDesignationForm.jsp'>Add new designation</a></th>
</tr>
<tr>
<th style='width:40px;text-align:center'>S.No.</th>
<th style='width:200px;text-align:center'>Designation</th>
<th style='width:80px;text-align:center'>Edit</th>
<th style='width:80px;text-align:center'>Delete</th>
</tr>
</thead>
<tbody>
<tm:Designations>
<tr>
<td style='text-align:right'>${serialNumber}</td>
<td>${designationBean.title}</td>
<td style='text-align:center'><a href='/styletwo/editDesignation?code=${designationBean.code}'>edit</a></td>
<td style='text-align:center'><a href='/styletwo/confirmDeleteDesignation?code=${designationBean.code}'>delete</a></td>
</tr>
</tm:Designations>
</tbody>
</table>
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

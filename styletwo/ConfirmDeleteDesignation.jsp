<jsp:useBean id='designationBean' scope='request' class='com.ashvin.hr.nexus.beans.DesignationBean' />
<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<script src='/styletwo/js/ConfirmDeleteDesignation.js'></script>
<link rel='stylesheet' type='text/css' href='/styletwo/css/styles.css'>
</head>
<body>
<!-- Main container start here -->
<div class='main-container'>
<!-- header start here -->
<div class='header'>
<a href='/styletwo/index.html'><img src='/styletwo/images/hr_nexus_logo.png' class='logo'></a>
<div class='brand-name'>HR-Nexus</div>
</div>
<!-- header ends here -->
<!-- middle content start here -->
<div class='content'>
<!-- left panel start here -->
<div class='content-left-panel'>
<b>Designations</b><br>
<a href='/styletwo/Employees.jsp' style='float:left'>Employees</a><br><br>
<a href='/styletwo/index.html'>Home</a>
</div>
<!-- left panel ends here -->
<!-- right panel start here -->
<div class='content-right-panel'>
<h2>Designation (Delete Module)</h2>
<form method='post' action='/styletwo/DeleteDesignation.jsp' onsubmit='return validateDesignation(this)'>
Designation: ${designationBean.title}
Are you sure, you want to delete designation <b>${designationBean.title}</b>'?<br>
<input type='hidden' id='code' name='code' value='${designationBean.code}'>
<input type='hidden' id='title' name='title' maxlength='35' size='36' value='${designationBean.title}'>
<span id='titleErrorSection' style='color:red'></span><br>
<button type='submit'>Yes</button>
<button type='button' onclick='cancelDeletion()'>No</button>
</form>
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
<form id='cancelDeletionForm' action='/styletwo/Designations.jsp'>
</form>
</body>
</html>

<jsp:useBean id='administratorBean' scope='request' class='com.ashvin.hr.nexus.beans.AdministratorBean' />
<jsp:useBean id='errorBean' scope='request' class='com.ashvin.hr.nexus.beans.ErrorBean' />

<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<script src='/styletwo/js/Login.js'></script>
<link rel='stylesheet' type='text/css' href='/styletwo/css/styles.css'>
</head>
<body style='display:flex;align-items:center;justify-content:center;background-color:#6A89A7;height:95vh;'>
<div style='display:flex;align-items:center;justify-content:center;width:50%;height:60vh;padding:10px;background-color:#FFFAF0;border:1px solid black;'>
<div style=''>		<!-- width:80% height:40vh border:1px solid blue; -->
<center>
<h3>HR-Nexus</h3>
<span id='errorSection' class='error'>
<jsp:getProperty name='errorBean' property='error' />
</span>
</center>
<form method='post' action='/styletwo/Login.jsp' onsubmit='return validateLoginForm(this)'>
<b>Username: </b>
<input type='text' id='username' name='username' maxlength='16' size='16' value='${administratorBean.username}'><br>
<span id='usernameErrorSection' class='error'></span>
<br>
<b>Password: </b>
<input type='text' id='password' name='password' maxlength='16' size='16' value='${administratorBean.password}'><br>
<span id='passwordErrorSection' class='error'></span>
<br>
<center><button type='submit' style='left-padding:5px;right-padding:5px;margin:5px;background-color:#87CEEB;'>log-in</button></center>
</form>
</div>
</div>
</body>
</html>

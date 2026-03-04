<jsp:useBean id='errorBean' scope='request' class='com.ashvin.hr.nexus.beans.ErrorBean' />

<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<script src='/styletwo/js/Login.js'></script>
<link rel='stylesheet' type='text/css' href='/styletwo/css/styles.css'>
</head>
<body>
<div class='main-container'>
<!-- header start here -->
<div class='header'>
<a href='/styletwo/LoginPage.jsp'><img src='/styletwo/images/hr_nexus_logo.png' class='logo'></a>
<div class='brand-name'>HR-Nexus</div>
</div>
<!-- header ends here -->
<!-- middle content start here -->
<div class='content'>
<center>
<div class='login-form'>
<center>
<span id='errorSection' class='error'>
<jsp:getProperty name='errorBean' property='error' />
</span>
<form method='post' action='/styletwo/Login.jsp' onsubmit='return validateLoginForm(this)'>
<b>Username: </b>
<input type='text' id='username' name='username' maxlength='16' size='16' ><br>
<span id='usernameErrorSection' class='error'></span>
<br>
<b>Password: </b>
<input type='password' id='password' name='password' maxlength='16' size='16'><br>
<span id='passwordErrorSection' class='error'></span>
<br>
<center><button type='submit' class='login-button'>log-in</button></center>
</form>
</center>
</div>
</center>

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


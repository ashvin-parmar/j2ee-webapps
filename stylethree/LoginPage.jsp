<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>HR-Nexus</title>
<script src='/stylethree/js/Login.js'></script>
<link rel='stylesheet' type='text/css' href='/stylethree/css/styles.css'>
</head>
<body>
<div class='main-container'>
<!-- header start here -->
<div class='header'>
<a href='/stylethree/index.jsp'><img src='/stylethree/images/hr_nexus_logo.png' class='logo'></a>
<div class='brand-name'>HR-Nexus</div>
</div>
<!-- header ends here -->
<!-- middle content start here -->
<div class='content'>
<center>
<div class='login-form'>
<center>
<span id='errorSection' class='error'>
</span><br>
<b>Username: </b>
<input type='text' id='username' name='username' maxlength='16' size='16' ><br>
<span id='usernameErrorSection' class='error'></span>
<br>
<b>Password: </b>
<input type='password' id='password' name='password' maxlength='16' size='16'><br>
<span id='passwordErrorSection' class='error'></span>
<br>
<center><button type='submit' class='login-button' onclick='javascript:performLoginChecks()'>log-in</button></center>
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

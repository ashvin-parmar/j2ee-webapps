function validateLoginForm()
{
var isValid=true;
var firstInvalidElement=null;
var username=document.getElementById("username").value.trim();
var usernameErrorSection=document.getElementById("usernameErrorSection");
usernameErrorSection.innerHTML="";
if(username.length==0)
{
usernameErrorSection.innerHTML="Username required";
if(firstInvalidElement==null) firstInvalidElement=document.getElementById("username");
isValid=false;
}
else
{
var check="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
for(var i=0;i<username.length;i++)
{
if(check.indexOf(username.charAt(i))==-1)
{
usernameErrorSection.innerHTML="Only alphabets and numeric allowed.";
if(firstInvalidElement==null) firstInvalidElement=document.getElementById("username");
isValid=false;
break;
}
}
}
var password=document.getElementById("password").value.trim();
var passwordErrorSection=document.getElementById("passwordErrorSection");
passwordErrorSection.innerHTML="";
if(password.length==0)
{
passwordErrorSection.innerHTML="Password required";
if(firstInvalidElement==null) firstInvalidElement=document.getElementById("password");
isValid=false;
}
if(!isValid) firstInvalidElement.focus();
//alert("triggerd : "+isValid+": ");
return isValid;
}
function performLoginChecks()
{
if(validateLoginForm()==false) return ;
var username=document.getElementById("username").value.trim();
var password=document.getElementById("password").value.trim();
var errorSection=document.getElementById("errorSection");
errorSection.innerHTML="";
var loginCredentials={
"username":username,
"password":password
};
var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
responseData=JSON.parse(this.responseText);
if(responseData.error!=null)
{
errorSection.innerHTML=responseData.error;
}
else
{
//alert(this.responseText);
window.location.href="index.jsp";
}
}
else
{
alert("Some problem");
}
}
};
xmlHttpRequest.open("POST","login",true);
xmlHttpRequest.setRequestHeader("Content-Type","application/json");
xmlHttpRequest.send(JSON.stringify(loginCredentials));
}

function validateLoginForm(frm)
{
var isValid=true;
var firstInvalidElement=null;
var username=frm.username.value.trim();
var usernameErrorSection=document.getElementById("usernameErrorSection");
usernameErrorSection.innerHTML="";
if(username.length==0)
{
usernameErrorSection.innerHTML="Username required";
if(firstInvalidElement==null) firstInvalidElement=frm.title;
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
if(firstInvalidElement==null) firstInvalidElement=frm.title;
isValid=false;
break;
}
}
}
var password=frm.password.value.trim();
var passwordErrorSection=document.getElementById("passwordErrorSection");
passwordErrorSection.innerHTML="";
if(password.length==0)
{
passwordErrorSection.innerHTML="Password required";
if(firstInvalidElement==null) firstInvalidElement=frm.password;
isValid=false;
}
//if(!isValid) firstInvalidElement.focus();
//alert("triggerd : "+isValid+": ");
return isValid;
}

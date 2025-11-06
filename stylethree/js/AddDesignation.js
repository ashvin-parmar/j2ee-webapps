function validateDesignation()
{
var title=document.getElementById('title').value.trim();
var titleErrorSection=document.getElementById('titleErrorSection');
titleErrorSection.innerHTML="";
if(title.length==0)
{
titleErrorSection.innerHTML="Required";
document.getElementById("title").focus();
return false;
}
return true;
}
function cancelAddition()
{
window.location.href='/stylethree/Designations.jsp';
}
function addDesignation()
{
if(validateDesignation()==false) return ;
var title=document.getElementById("title").value.trim();
var errorSection=document.getElementById("errorSection");
errorSection.innerHTML="";
var designation={
"code":0,
"title":title
};

var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
var textResponseData=this.responseText;
var responseData=JSON.parse(textResponseData);
if(responseData.error!=null)
{
errorSection.innerHTML=responseData.error;
}
else
{
var messageText=textResponseData;
var message=JSON.parse(textResponseData);
localStorage.setItem("message",encodeURIComponent(messageText));
window.location.replace("Notification.jsp");
}
}
else
{
alert("Some problem ");
window.location.href="ErrorPage.html";
}
}
};
xmlHttpRequest.open("POST","addDesignation",true);
xmlHttpRequest.setRequestHeader("Content-Type","application/json");
xmlHttpRequest.send(JSON.stringify(designation));
}

function setMessages(message)
{
var messageHeader=document.getElementById("messageHeading");
var messageData=document.getElementById("messageData");
var messageGridTable=document.getElementById("messageGridTable");
messageHeading.innerHTML=message.heading;
messageData.innerHTML=message.message;
var messageGridTableRowTemplate=messageGridTable.getElementsByTagName("tr")[0];
messageGridTableRowTemplate.remove();
if(message.hasToGenerateButtons)
{
var rowTemplate;
var placeHolder;
var cellTemplates=[];
var buttonOne;
var buttonTwo;

cellTemplates=messageGridTableRowTemplate.getElementsByTagName("td");
rowTemplate=messageGridTableRowTemplate.cloneNode();
messageGridTable.append(rowTemplate);
rowTemplate.append(cellTemplates[0]);
buttonOne=document.getElementById("buttonOne");
buttonOne.innerHTML=message.buttonOneText;
buttonOne.onclick=function(){
window.location.href=message.buttonOneAction;
};
if(message.hasToGenerateTwoButtons)
{
rowTemplate.append(cellTemplates[0]);
buttonTwo=document.getElementById("buttonTwo");
buttonTwo.innerHTML=message.buttonTwoText;
buttonTwo.onclick=function(){
window.location.href=message.buttonTwoAction;
};
}
}
}
function populateMessage()
{
var message=null;
var decodedMessageText;
var encodedMessageText=localStorage.getItem("message");
if(encodedMessageText!=null) 
{
decodedMessageText=decodeURIComponent(encodedMessageText);
message=JSON.parse(decodedMessageText);
}
if(message!=null)
{
setMessages(message)
localStorage.removeItem("message");
}
else
{
var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
message=JSON.parse(this.responseText);
setMessages(message);
}
else
{
alert("Some problem");
window.location.href="ErrorPage.jsp";
}
}
};
xmlHttpRequest.open("POST","notificationResubmission",true);
xmlHttpRequest.send();
}
}
window.addEventListener('load',populateMessage());

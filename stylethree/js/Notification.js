function populateMessage()
{
var messageText=localStorage.getItem("message");
var message=JSON.parse(localStorage.getItem("message"));
if(message)
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
var buttonOne=document.getElementById("buttonOne");
var buttonTwo=document.getElementById("buttonTwo");

cellTemplates=messageGridTableRowTemplate.getElementsByTagName("td");
rowTemplate=messageGridTableRowTemplate.cloneNode();
messageGridTable.append(rowTemplate);
rowTemplate.append(cellTemplates[0]);
buttonOne.innerHTML=message.buttonOneText;
buttonOne.onclick=function(){
window.location.href=message.buttonOneAction;
};
if(message.hasToGenerateTwoButtons)
{
rowTemplate.append(cellTemplates[0]);
buttonTwo.innerHTML=message.buttonTwoText;
buttonTwo.onclick=function(){
window.location.href=message.buttonTwoAction;
};
}
}
localStorage.removeItem("message");
}
}
window.addEventListener('load',populateMessage());

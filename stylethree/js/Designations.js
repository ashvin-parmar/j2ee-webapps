function Designation()
{
this.code=0;
this.title="";
}
var designations=[];
function populateDesignationsGridTable()
{
var designationsGridTable=document.getElementById("designationsGridTable");
//alert(designationsGridTable);
//alert(designationsGridTable.innerHTML);
var designationsGridTableBody=designationsGridTable.getElementsByTagName("tbody")[0];
//alert(designationsGridTableBody.innerHTML);
var designationsGridTableRowTemplate=designationsGridTableBody.getElementsByTagName("tr")[0];
//alert(designationsGridTableRowTemplate.innerHTML);
designationsGridTableRowTemplate.remove();

var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
//alert(this.responseText);
designations=JSON.parse(this.responseText);
var i;
var j;
var elements;
var dynamicRowTemplate;
var placeHolder;
for(i=0;i<designations.length;i++)
{
dynamicRowTemplate=designationsGridTableRowTemplate.cloneNode(true);
designationsGridTableBody.append(dynamicRowTemplate);
elements=dynamicRowTemplate.getElementsByTagName("td");
for(j=0;j<elements.length;j++)
{
placeHolder=elements[j].getAttribute("placeHolderId");
if(placeHolder==null) continue;
if(placeHolder=="serialNumber") elements[j].innerHTML=(i+1);
if(placeHolder=="title") elements[j].innerHTML=designations[i].title;
if(placeHolder=="editOption") elements[j].innerHTML="<a href='/stylethree/editDesignation?code="+designations[i].code+"'>Edit</a>";
if(placeHolder=="deleteOption") elements[j].innerHTML="<a href='/stylethree/deleteDesignation?code="+designations[i].code+"'>Delete</a>";
}
}
}
else
{
alert("Some problem");
window.location.href="index.jsp"
}
}
};
xmlHttpRequest.open("GET","designations",true);
xmlHttpRequest.send();
}
window.addEventListener('load',populateDesignationsGridTable());


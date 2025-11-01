function Designation()
{
this.code=0;
this.title="";
}
function Employee()
{
this.employeeId="";
this.name="";
this.dateOfBirth="";
this.designation=null;
this.gender="";
this.isIndian=false;
this.basicSalary="0";
this.panNumber="";
this.aadharCardNumber="";
}
var employees=[];
var selectedRow=null;
function selectEmployee(row,employeeId)
{
if(selectedRow==row) return;
if(selectedRow!=null)
{
selectedRow.style.background="white";
selectedRow.style.color="black";
}
row.style.background="grey";
row.style.color="white";
selectedRow=row;
var i;
for(i=0;i<employees.length;i++)
{
if(employees[i].employeeId==employeeId)
{
break;
}
}
var emp=employees[i];
document.getElementById('detailsPanel_employeeId').innerHTML=emp.employeeId;
document.getElementById('detailsPanel_name').innerHTML=emp.name;
document.getElementById('detailsPanel_designation').innerHTML=emp.designation.title;
document.getElementById('detailsPanel_dateOfBirth').innerHTML=emp.dateOfBirth;
document.getElementById('detailsPanel_isIndian').innerHTML=(emp.isIndian?"Yes":"No");
document.getElementById('detailsPanel_gender').innerHTML=(emp.gender=="M"?"Male":"Female");
document.getElementById('detailsPanel_basicSalary').innerHTML=emp.basicSalary;
document.getElementById('detailsPanel_panNumber').innerHTML=emp.panNumber;
document.getElementById('detailsPanel_aadharCardNumber').innerHTML=emp.aadharCardNumber;
}
function createDynamicRowClickHandler(rowAddress,employeeId)
{
return function()
{
selectEmployee(rowAddress,employeeId);
};
}

function populateEmployeesGridTable()
{
//alert("Something");
var employeesGridTable=document.getElementById("employeesGridTable");
//alert(employeesGridTable);
//alert(employeesGridTable.innerHTML);		//Such that we can see the HTML and word accordingly
var employeesGridTableBodyTemplate=employeesGridTable.getElementsByTagName("tbody")[0];		//We want 0 index data
//alert(employeesGridTableBodyTemplate);
var employeesGridTableRowTemplate=employeesGridTableBodyTemplate.getElementsByTagName("tr")[0];	//we want 0 index data
//alert(employeesGridTableRowTemplate);
//alert(employeesGridTableRowTemplate.innerHTML);
//var employeesGridTableColumnsTemplateCollection=employeesGridTableRowTemplate.getElementsByTagName("td");	//Not required
//alert(employeesGridTableColumnsTemplateCollection);
employeesGridTableRowTemplate.remove();	//To remove the existing template of row
var k;
var dynamicRowTemplate;
var dynamicRowCells;
var cellTemplate;
var placeHolderFor;
var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
employees=JSON.parse(this.responseText);
for(var i=0;i<employees.length;i++)
{
dynamicRowTemplate=employeesGridTableRowTemplate.cloneNode(true);
//alert(dynamicRowTemplate);
//alert(dynamicRowTemplate.innerHTML);
employeesGridTableBodyTemplate.appendChild(dynamicRowTemplate);
//alert(employees[i].employeeId);
//dynamicRowTemplate.setAttribute("onclick","selectEmployee(this,'"+employees[i].employeeId+"')");
//Dynamically set onclick attribute to a 'tr' tag
dynamicRowTemplate.onclick=createDynamicRowClickHandler(dynamicRowTemplate,employees[i].employeeId);
dynamicRowCells=dynamicRowTemplate.getElementsByTagName("td");
for(k=0;k<dynamicRowCells.length;k++)
{
cellTemplate=dynamicRowCells[k];
//alert(cellTemplate.innerHTML);
//alert(cellTemplate.placeHolderId);		//This is also used but for something else work
//alert(cellTemplate["placeHolderId"]);		//This is also used but for something else
placeHolderFor=cellTemplate.getAttribute("placeHolderId");	//This is used for respective work for placeHolderId main thing
if(placeHolderFor==null) continue;
//alert(placeHolderFor);
if(placeHolderFor=="serialNumber") cellTemplate.innerHTML=(i+1);
if(placeHolderFor=="employeeId") cellTemplate.innerHTML=employees[i].employeeId;
if(placeHolderFor=="name") cellTemplate.innerHTML=employees[i].name;
if(placeHolderFor=="designationCode") cellTemplate.innerHTML=employees[i].designation.code;
if(placeHolderFor=="designation") cellTemplate.innerHTML=employees[i].designation.title;
if(placeHolderFor=="dateOfBirth") cellTemplate.innerHTML=employees[i].dateOfBirth;
if(placeHolderFor=="gender") cellTemplate.innerHTML=employees[i].gender;
if(placeHolderFor=="basicSalary") cellTemplate.innerHTML=employees[i].basicSalary;
if(placeHolderFor=="isIndian") cellTemplate.innerHTML=employees[i].isIndian;
if(placeHolderFor=="panNumber") cellTemplate.innerHTML=emloyees[i].panNumber;
if(placeHolderFor=="aadharCardNumber") cellTemplate.innerHTML=employees[i].aadharCardNumber;
if(placeHolderFor=="editOption") cellTemplate.innerHTML="<a href='/stylethree/editEmployee?employeeId="+employees[i].employeeId+"'>Edit</a>";
if(placeHolderFor=="deleteOption") cellTemplate.innerHTML="<a href='/stylethree/confirmDeleteEmployee?employeeId="+employees[i].employeeId+"'>Delete</a>";
}
}
}
else
{
alert("Some problem");
window.location.href="index.jsp";
}
}
};
xmlHttpRequest.open("GET","employees",true);
xmlHttpRequest.send();
}
window.addEventListener('load',populateEmployeesGridTable);

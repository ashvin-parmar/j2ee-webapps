<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='EMPLOYEE' />
<jsp:include page='/MasterPageTopSection.jsp' />
<script src='/styletwo/js/Employees.js'></script>
<script>
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
for(var i=0;i<employees.length;i++)
{
dynamicRowTemplate=employeesGridTableRowTemplate.cloneNode(true);
//alert(dynamicRowTemplate);
//alert(dynamicRowTemplate.innerHTML);
employeesGridTableBodyTemplate.appendChild(dynamicRowTemplate);
//alert(employees[i].employeeId);
dynamicRowTemplate.setAttribute("onclick","selectEmployee(this,'"+employees[i].employeeId+"')");
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
if(placeHolderFor=="designationCode") cellTemplate.innerHTML=employees[i].designationCode;
if(placeHolderFor=="designation") cellTemplate.innerHTML=employees[i].designation;
if(placeHolderFor=="dateOfBirth") cellTemplate.innerHTML=employees[i].dateOfBirth;
if(placeHolderFor=="gender") cellTemplate.innerHTML=employees[i].gender;
if(placeHolderFor=="basicSalary") cellTemplate.innerHTML=employees[i].basicSalary;
if(placeHolderFor=="isIndian") cellTemplate.innerHTML=employees[i].isIndian;
if(placeHolderFor=="panNumber") cellTemplate.innerHTML=emloyees[i].panNumber;
if(placeHolderFor=="aadharCardNumber") cellTemplate.innerHTML=employees[i].aadharCardNumber;
if(placeHolderFor=="editOption") cellTemplate.innerHTML="<a href='/styletwo/editEmployee?"+employees[i].employeeId+"'>Edit</a>";
if(placeHolderFor=="deleteOption") cellTemplate.innerHTML="<a href='/styletwo/confirmDeleteEmployee?"+employees[i].employeeId+"'>Delete</a>";
}
}





}
window.addEventListener('load',populateEmployeesGridTable);
</script>
<b>Employees</b><br>
<!-- table division start here -->
<link rel='stylesheet' type='text/css' href='/styletwo/css/employee.css'>
<div class='employee-table'>
<table id='employeesGridTable' border='1'>
<thead>
<tr>
<th colspan='6' class='employee-add-option'><a href='/styletwo/AddEmployeeForm.jsp'>Add employee</a></th>
</tr>
<tr>
<th class='employee-column-sno'>S.No.</th>
<th class='employee-column-id'>ID</th>
<th class='employee-column-name'>Name</th>
<th class='employee-column-designation'>Designation</th>
<th class='employee-column-edit'>Edit</th>
<th class='employee-column-delete'>Delete</th>
</tr>
</thead>
<tbody>
<tr placeHolderId='onSelect' class='employee-table-row' onclick='selectEmployee(this,"")'>
<td placeHolderId='serialNumber' class='employee-data-sno'></td>
<td placeHolderId='employeeId' class='employee-data-id'></td>
<td placeHolderId='name'></td>
<td placeHolderId='designation'></td>
<td placeHolderId='editOption' class='employee-data-edit'></td>
<td placeHolderId='deleteOption' class='employee-data-delete'></td>
</tr>
</tbody>
</table>
</div>		
<!-- table division ends here -->
<!-- data show panel start here -->
<div class='show-panel-division'>
<label class='details-label'>Details</label>
<table border='0'  width='100%'>
<tr>
<td>ID: <span id='detailsPanel_employeeId' ></span></td>
<td>Name: <span id='detailsPanel_name' ></span></td>
<td>Designation: <span id='detailsPanel_designation'></span></td>
</tr>
<tr>
<td>Is Indian?: <span id='detailsPanel_isIndian'></span></td>
<td>Gender: <span id='detailsPanel_gender'></span></td>
<td>Date of Birth: <span id='detailsPanel_dateOfBirth'></span></td>
</tr>
<tr>
<td>Basic salary: <span id='detailsPanel_basicSalary'></span></td>
<td>PAN number: <span id='detailsPanel_panNumber'></span></td>
<td>Aadhar card number: <span id='detailsPanel_aadharCardNumber'></span></td>
</tr>
</table >
</div>
<!-- data show panel ends here -->
<jsp:include page='/MasterPageBottomSection.jsp' />

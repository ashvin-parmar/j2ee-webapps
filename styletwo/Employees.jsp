<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='EMPLOYEE' />
<jsp:include page='/MasterPageTopSection.jsp' />
<script src='/styletwo/js/Employees.js'></script>
<b>Employees</b><br>
<!-- table division start here -->
<link rel='stylesheet' type='text/css' href='/styletwo/css/employee.css'>
<div class='employee-table'>
<table border='1'>
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

<tm:EntityList populateClass='com.ashvin.hr.nexus.dl.EmployeeDAO' populateMethod='getAll' name='employee'>
<tr class='employee-table-row' onclick='selectEmployee(this,"${employee.employeeId}")'>
<td class='employee-data-sno'>${serialNumber}.</td>
<td class='employee-data-id'>${employee.employeeId}</td>
<td>${employee.name}</td>
<td>${employee.designation}</td>
<td class='employee-data-edit'><a href='/styletwo/editEmployee?employeeId=${employee.employeeId}'>edit</a></td>
<td class='employee-data-delete'><a href='/styletwo/confirmDeleteEmployee?employeeId=${employee.employeeId}'>delete</a></td>
</tr>
</tm:EntityList>
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

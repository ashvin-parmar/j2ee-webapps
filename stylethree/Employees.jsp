<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='EMPLOYEE' />
<jsp:include page='/MasterPageTopSection.jsp' />
<b>Employees</b><br>
<!-- table division start here -->
<link rel='stylesheet' type='text/css' href='/stylethree/css/employee.css'>
<div class='employee-table'>
<table id='employeesGridTable' border='1'>
<thead>
<tr>
<th colspan='6' class='employee-add-option'><a href='/stylethree/AddEmployeeForm.jsp'>Add employee</a></th>
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
<script src='/stylethree/js/Employees.js'></script>

<!-- data show panel ends here -->
<jsp:include page='/MasterPageBottomSection.jsp' />

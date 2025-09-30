<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='EMPLOYEE' />

<jsp:useBean id='employeeBean' scope='request' class='com.ashvin.hr.nexus.beans.EmployeeBean' />
<script src='/styletwo/js/ConfirmDeleteEmployee.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Employee (Delete Module)</h2>
Employee ID:  ${employeeBean.employeeId}<br>
Name:  ${employeeBean.name}<br>
Designation: ${employeeBean.designation}<br>
Gender: ${employeeBean.gender}<br>
<tm:If condition='${employeeBean.isIndian}'>
Nationality: Indian<br>
</tm:If>
<tm:If condition='${!employeeBean.isIndian}'>
Nationality: Not an indian<br>
</tm:If>
Date of Birth: ${employeeBean.dateOfBirth}<br>
Basic salary: ${employeeBean.basicSalary}<br>
PAN number: ${employeeBean.panNumber}<br>
Aadhar card number: ${employeeBean.aadharCardNumber}<br><br>
Are you sure, you want to delete employee '<b>${employeeBean.name}</b>'?
<form method='post' action='/styletwo/DeleteEmployee.jsp'>
<tm:FormID />
<input type='hidden' id='employeeId' name='employeeId' value='${employeeBean.employeeId}'>
<input type='hidden' id='name' name='name' value='${employeeBean.name}'>
<table>
<tr>
<td>
<button type='submit' >Yes</button>
</td>
<td>
<button type='button' onclick='cancelDeletion()'>No</button>
</td>
</tr>
</table>
</form>
<form id='cancelDeletionForm' action='/styletwo/Employees.jsp'>
</form>
<jsp:include page='/MasterPageBottomSection.jsp' />

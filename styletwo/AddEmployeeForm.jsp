<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<jsp:useBean id='employeeBean' scope='request' class='com.ashvin.hr.nexus.beans.EmployeeBean' />
<jsp:useBean id='errorBean' scope='request' class='com.ashvin.hr.nexus.beans.ErrorBean' />
<tm:Module name='EMPLOYEE' />
<script src='/styletwo/js/AddEmployee.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<b>Employee (Add Module)</b><br>
<form method='post' action='/styleone/AddEmployee.jsp' onsubmit='return validateEmployee(this)'>
<table>
<tr>
<td><b>Name: </b></td>
<td>
<input type='text' id='name' name='name' maxlength='50' size='51' value='${employeeBean.name}'>
<span id='nameErrorSection' style='color:red'></span>
</td>
</tr>

<tr>
<td><b>Designation: </b></td>
<td>
<select id='designationCode' name='designationCode'>
<option value='-1'>&lt;Select designation&gt;</option>
<tm:EntityList populateClass='com.ashvin.hr.nexus.bl.DesignationBL' 
	       populateMethod='getAll'
	       name='designation' >
<tm:If condition='${designation.code==employeeBean.designationCode}'>
<option selected value='${designation.code}'>${designation.title}</option>
</tm:If>
<tm:If condition='${designation.code!=employeeBean.designationCode}'>
<option value='${designation.code}'>${designation.title}</option>
</tm:If>

</tm:EntityList>
</select>
&nbsp;<span id='designationCodeErrorSection' style='color:red'></span>
</td>
</tr>
<tr>
<td>Date of birth: </td>
<td><input type='date' id='dateOfBirth' name='dateOfBirth' value='${employeeBean.dateOfBirth}'>
<span id='dateOfBirthErrorSection' style='color:red'></span></td>
</tr>
<tr>
<td>Gender: </td>
<td>
<tm:If condition='${!employeeBean.isFemale()}'>
<input checked type='radio' id='male' name='gender' value='Male'>Male
&nbsp;&nbsp;
<input type='radio' id='female' name='gender' value='Female'>Female
</tm:If>
<tm:If condition='${employeeBean.isFemale()}'>
<input type='radio' id='male' name='male' value='Male'>Male
&nbsp;&nbsp;      
<input checked type='radio' id='female' name='gender' value='Female'>Female
</tm:If>
<span id='genderErrorSection' style='color:red'></span>
</td>
</tr>
<tr>
<td>Indian? : </td>
<td>
<tm:If condition='${employeeBean.isIndian}'>
<input checked type='checkbox' id='isIndian' name='isIndian' value='Y'>
</tm:If>
<tm:If condition='${!employeeBean.isIndian}'>
<input type='checkbox' id='isIndian' name='isIndian' value='N'>
</tm:If>
</td>
</tr>
<tr>
<td>Basic Salary: </td>
<td>
<input type='number' id='basicSalary' name='basicSalary' value='${employeeBean.basicSalary}'>
<span id='basicSalaryErrorSection' style='color:red'></span>
</td>
</tr>

<tr>
<td>PAN number: </td>
<td>
<input type='text' id='panNumber' name='panNumber' maxlength='15' size='16' value='${employeeBean.panNumber}'>
<span id='panNumberErrorSection' style='color:red'></span>
</td>
</tr>
<tr>
<td>Aadhar card number: </td>
<td>
<input type='text' id='aadharCardNumber' name='aadharCardNumber' maxlength='15' size='16' value='${employeeBean.aadharCardNumber}'>
<span id='aadharCardNumberErrorSection' style='color:red'></span>
</td>
</tr>
<td colspan='2'>
<button type='submit'>Add</button> &nbsp;&nbsp;
<button type='button' onclick='cancelAddition()'>Cancel</button>
</td>
</table>
</form>
<form id='cancelAdditionForm' action='/styletwo/Employees.jsp'>
</form>
<jsp:include page='/MasterPageBottomSection.jsp' />

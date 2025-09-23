<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<script>
var employee;
<tm:EntityList populateClass='com.ashvin.hr.nexus.bl.EmployeeBL' 
	       populateMethod='getAll' name='employeeBean' >
employee=new Employee();
employee.employeeId="${employeeBean.employeeId}";
employee.name='${employeeBean.name}';
employee.designationCode='${employeeBean.designationCode}';
employee.designation='${employeeBean.designation}';
employee.gender='${employeeBean.gender}';
employee.isIndian='${employeeBean.isIndian}';
employee.dateOfBirth='${employeeBean.dateOfBirth}';
employee.basicSalary='${employeeBean.basicSalary}';
employee.panNumber='${employeeBean.panNumber}';
employee.aadharCardNumber='${employeeBean.aadharCardNumber}';
employees[${serialNumber-1}]=employee;
</tm:EntityList>
</script>

function Employee()
{
this.employeeId="";
this.name="";
this.designationCode=0;
this.designation="";
this.dateOfBirth="";
this.gender="";
this.isIndian=true;
this.basicSalary=0;
this.panNumber="";
this.aadharCardNumber="";
}
var employees=[];
var employee;
employee=new Employee();
employee.employeeId="A100016";
employee.name="Ayush Salve";
employee.designationCode=29;
employee.designation="Security Guard"
employee.dateOfBirth="30/06/1989";
employee.gender="Male";
employee.isIndian=true;
employee.basicSalary=20.00;
employee.panNumber="ABC54321";
employee.aadharCardNumber="UID13513";
employees[0]=employee;
employee=new Employee();
employee.employeeId="A100003";
employee.name="Rohit Solanki";
employee.designationCode=21;
employee.designation="Guard"
employee.dateOfBirth="21/01/2000";
employee.gender="Male";
employee.isIndian=true;
employee.basicSalary=41500.00;
employee.panNumber="PAN1234512345";
employee.aadharCardNumber="UID123412345";
employees[1]=employee;
employee=new Employee();
employee.employeeId="A100015";
employee.name="Shubham Kala";
employee.designationCode=21;
employee.designation="Guard"
employee.dateOfBirth="02/11/1990";
employee.gender="Male";
employee.isIndian=true;
employee.basicSalary=10000.00;
employee.panNumber="PAN415243";
employee.aadharCardNumber="UID1415232";
employees[2]=employee;
employee=new Employee();
employee.employeeId="A100013";
employee.name="Siddarth";
employee.designationCode=27;
employee.designation="Developer"
employee.dateOfBirth="27/02/2003";
employee.gender="Male";
employee.isIndian=false;
employee.basicSalary=39999.00;
employee.panNumber="PAN123412345";
employee.aadharCardNumber="UID1234123456";
employees[3]=employee;
employee=new Employee();
employee.employeeId="A100002";
employee.name="Sumit Singh Mahobia";
employee.designationCode=26;
employee.designation="Engineer"
employee.dateOfBirth="12/12/2000";
employee.gender="Male";
employee.isIndian=true;
employee.basicSalary=200000.00;
employee.panNumber="PAN12341234";
employee.aadharCardNumber="UID12341234";
employees[4]=employee;
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
document.getElementById('detailsPanel_designation').innerHTML=emp.designation;
document.getElementById('detailsPanel_dateOfBirth').innerHTML=emp.dateOfBirth;
document.getElementById('detailsPanel_isIndian').innerHTML=emp.isIndian?"Yes":"No";
document.getElementById('detailsPanel_gender').innerHTML=emp.gender;
document.getElementById('detailsPanel_basicSalary').innerHTML=emp.basicSalary;
document.getElementById('detailsPanel_panNumber').innerHTML=emp.panNumber;
document.getElementById('detailsPanel_aadharCardNumber').innerHTML=emp.aadharCardNumber;
}


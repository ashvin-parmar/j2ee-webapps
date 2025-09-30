function Employee()
{
this.employeeId="";
this.name="";
this.designationCode=0;
this.designation="";
this.dateOfBirth="";
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
document.getElementById('detailsPanel_designation').innerHTML=emp.designation;
document.getElementById('detailsPanel_dateOfBirth').innerHTML=emp.dateOfBirth;
document.getElementById('detailsPanel_isIndian').innerHTML=(emp.isIndian?"Yes":"No");
document.getElementById('detailsPanel_gender').innerHTML=emp.gender;
document.getElementById('detailsPanel_basicSalary').innerHTML=emp.basicSalary;
document.getElementById('detailsPanel_panNumber').innerHTML=emp.panNumber;
document.getElementById('detailsPanel_aadharCardNumber').innerHTML=emp.aadharCardNumber;
}




function validateEmployee(frm)
{
var firstInvalidComponent=null;
var valid=true;
var name=frm.name.value.trim();
var nameErrorSection=document.getElementById('nameErrorSection');
nameErrorSection.innerHTML='';
if(name.length==0)
{
nameErrorSection.innerHTML='Required';
firstInvalidComponent=frm.name;
valid=false;
}
var designationCode=frm.designationCode.value;
var designationCodeErrorSection=document.getElementById('designationCodeErrorSection');
designationCodeErrorSection.innerHTML='';
if(designationCode==-1)
{
designationCodeErrorSection.innerHTML="Select designation";
if(firstInvalidComponent==null) firstInvalidComponent=frm.designationCode;
valid=false;
}
var dateOfBirth=frm.dateOfBirth.value;
var dateOfBirthErrorSection=document.getElementById('dateOfBirthErrorSection');
dateOfBirthErrorSection.innerHTML='';
if(dateOfBirth.length==0)
{
dateOfBirthErrorSection.innerHTML='Select date of birth';
if(firstInvalidComponent==null) firstInvalidComponent=frm.dateOfBirth;
valid=false;
}
var gender=frm.gender;
var genderErrorSection=document.getElementById('genderErrorSection');
genderErrorSection.innerHTML='';
if(gender[0].checked==false && gender[1].checked==false)
{
genderErrorSection.innerHTML='Select gender';
valid=false;
}
var basicSalary=frm.basicSalary.value;
var basicSalaryErrorSection=document.getElementById('basicSalaryErrorSection');
basicSalaryErrorSection.innerHTML='';
if(basicSalary<=0)
{
basicSalaryErrorSection.innerHTML='Invalid basic salary';
if(firstInvalidComponent==null) firstInvalidComponent=frm.basicSalary;
valid=false;
}
var panNumber=frm.panNumber.value.trim();
var panNumberErrorSection=document.getElementById('panNumberErrorSection');
panNumberErrorSection.innerHTML='';
if(panNumber.length==0)
{
panNumberErrorSection.innerHTML='PAN number required';
if(firstInvalidComponent==null) firstInvalidComponent=frm.panNumber;
valid=false;
}
var aadharCardNumber=frm.aadharCardNumber.value.trim();
var aadharCardNumberErrorSection=document.getElementById('aadharCardNumberErrorSection');
aadharCardNumberErrorSection.innerHTML='';
if(aadharCardNumber.length==0)
{
aadharCardNumberErrorSection.innerHTML='Aadhar card number required';
if(firstInvalidComponent==null) firstInvalidComponent=frm.aadharCardNumber;
valid=false;
}
if(!valid && firstInvalidComponent!=null) firstInvalidComponent.focus();
return valid;
}
function cancelEditing()
{
document.getElementById('cancelEditingForm').submit();
}


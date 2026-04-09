class StudentManager
{
constructor()
{
}
add(student)
{
if(student instanceof Student)
{
alert(JSON.stringify(student));
return $.ajax({
type:"POST",
url:"StudentManager/add",
contentType:"application/json",
data:JSON.stringify(student)
});
}
return null;
}
update(student)
{
if(student instanceof Student)
{
return $.ajax({
type:"POST",
url:"StudentManager/update",
contentType:"application/json",
data:JSON.stringify(student)
});
}
return null;
}
delete(rollNumber)
{
var rn=Number(rollNumber);
if(!Number.isInteger(rn)) return null;
return $.ajax({
type:"GET",
url:"StudentManager/delete",
contentType:"application/x-www-form-urlencoded",
data:{rollNUmber:rn}
});
}
getByRollNumber(rollNumber)
{
var rn=Number(rollNumber);
if(!Number.isInteger(rn)) return null;
return $.ajax({
type:"POST",
url:"StudentManager/getByRollNumber",
data:{rollNumber:rn}
});
}
getAll()
{
return $.ajax({
type:"GET",
url:"StudentManager/getAll",
});
}
}


class Student
{
constructor(rollNumber,name)
{
this.rollNumber=rollNumber;
this.name=name;
}
setRollNumber(rollNumber)
{
this.rollNumber=rollNumber;
}
getRollNumber()
{
return this.rollNumber;
}
setName(name)
{
this.name=name;
}
getName()
{
return this.name;
}
}
class StudentManager
{
constructor()
{
}
addStudent(student)
{
return new Promise((resolve,reject)=>{
$.ajax({
url:"rockService",
type:"POST",
dataType:"json",
data:JSON.stringify(student),
success:function(response){
var servletResponse=response;
if(servletResponse.isSuccess)
{
resolve(servletResponse.result);
}
else
{
reject(servletResponse.exception.detailMessage);
}
},
error:function(){
alert("Error: Some problem");
}
});
});
}

getStudents()
{
return new Promise((resolve,reject)=>{
$.ajax({
url:"rockService",
type:"GET",
dataType:"json"
})
.done(function(response){
var servletResponse=response;
if(servletResponse.isSuccess)
{
resolve(servletResponse.result);
}
else
{
reject(servletResponse.exception.detailedMessage);
}
})
.fail(function(){
alert("Error: Some problem");
});
});
}

}

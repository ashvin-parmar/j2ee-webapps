class StudentManager
{
constructor()
{
}
addStudent(student)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='studentService/add';
xhr.open("POST",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload = () => {
if (xhr.status >= 200 && xhr.status < 300) 
{
resolve(xhr.response);
}
else
{
reject(new Error('Request failed with status ${xhr.status}'));
}
};
xhr.onerror = () => reject(new Error('Network Error'));
let body=null;
body=JSON.stringify(student);
xhr.send(body);
});
}
getStudents()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='studentService/getAll';
xhr.open("GET",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload = () => {
if (xhr.status >= 200 && xhr.status < 300) 
{
resolve(xhr.response);
}
else
{
reject(new Error('Request failed with status ${xhr.status}'));
}
};
xhr.onerror = () => reject(new Error('Network Error'));
xhr.send();
});
}
isRollNumberExists(int)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='studentService/isRollNumber';
xhr.open("POST",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload = () => {
if (xhr.status >= 200 && xhr.status < 300) 
{
resolve(xhr.response);
}
else
{
reject(new Error('Request failed with status ${xhr.status}'));
}
};
xhr.onerror = () => reject(new Error('Network Error'));
let body=null;
body=JSON.stringify(int);
xhr.send(body);
});
}
}

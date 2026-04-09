class StudentDAO
{
constructor()
{
}
update(studentDTO)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/update';
xhr.open("POST",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.onload=()=>{
if(xhr.status>=200 && xhr.status<300)
{
resolve(JSON.parse(xhr.responseText));
}
else
{
let errorBody=xhr.responseText;
reject({
status:xhr.status,
message:errorBody
});
}
};
xhr.onerror=()=>reject({status:xhr.status,message:"Network Error"});
const body=JSON.stringify(studentDTO);
xhr.send(body);
});
}
add(studentDTO)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/add';
xhr.open("POST",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload=()=>{
if(xhr.status>=200 && xhr.status<300)
{
resolve(xhr.response);
}
else
{
reject(xhr.status);
}
};
xhr.onerror=()=>reject(new Error('Network Error'));
const body=JSON.stringify(studentDTO);
xhr.send(body);
});
}
delete(rollNumber)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/delete';
const queryString=new URLSearchParams({rollNumber}).toString();
finalUrl+="?${queryString}";
xhr.open("GET",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload=()=>{
if(xhr.status>=200 && xhr.status<300)
{
resolve(xhr.response);
}
else
{
reject(xhr.status);
}
};
xhr.onerror=()=>reject(new Error('Network Error'));
xhr.send();
});
}
getAll()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/getAll';
xhr.open("GET",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload=()=>{
if(xhr.status>=200 && xhr.status<300)
{
resolve(xhr.response);
}
else
{
reject(xhr.status);
}
};
xhr.onerror=()=>reject(new Error('Network Error'));
xhr.send();
});
}
getByRollNumber(rollNumber)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/getByRollNumber';
const queryString=new URLSearchParams({rollNumber}).toString();
finalUrl+="?${queryString}";
xhr.open("GET",finalUrl);
xhr.setRequestHeader('Content-Type','application/json');
xhr.responseType='json';
xhr.onload=()=>{
if(xhr.status>=200 && xhr.status<300)
{
resolve(xhr.response);
}
else
{
reject(xhr.status);
}
};
xhr.onerror=()=>reject(new Error('Network Error'));
xhr.send();
});
}
}

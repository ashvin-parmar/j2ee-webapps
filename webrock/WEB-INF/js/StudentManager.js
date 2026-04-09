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
body=JSON.stringify(studentDTO);
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
body=JSON.stringify(studentDTO);
xhr.send(body);
});
}
delete(val1)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/delete';
const queryString=new URLSearchParams({val1}).toString();
finalUrl+="?${queryString}";
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
getAll()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/getAll';
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
getByRollNumber(val1)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='StudentManager/getByRollNumber';
const queryString=new URLSearchParams({val1}).toString();
finalUrl+="?${queryString}";
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
}

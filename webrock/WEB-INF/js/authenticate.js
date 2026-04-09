class Authenticate
{
constructor()
{
}
logout()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='authenticate/logout';
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
login()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='authenticate/login';
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
login3()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='authenticate/login3';
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

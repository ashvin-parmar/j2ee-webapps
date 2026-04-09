class RequestParameterTesting
{
constructor()
{
}
service2(longValue,intValue,shortValue,byteValue,floatValue,doubleValue,booleanValue,charValue,stringValue)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='requestParameterTesting/testing2';
const queryString=new URLSearchParams({longValue,intValue,shortValue,byteValue,floatValue,doubleValue,booleanValue,charValue,stringValue}).toString();
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
service4(student)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='requestParameterTesting/testing4';
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
service5()
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='requestParameterTesting/testing5';
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
service1(name,rollNumber)
{
return new Promise((resolve,reject)=>{
const xhr=new XMLHttpRequest();
let finalUrl='requestParameterTesting/testing1';
const queryString=new URLSearchParams({name,rollNumber}).toString();
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

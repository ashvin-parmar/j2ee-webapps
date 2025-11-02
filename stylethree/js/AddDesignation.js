function validateDesignation()
{
var title=document.getElementById('title').value.trim();
var titleErrorSection=document.getElementById('titleErrorSection');
titleErrorSection.innerHTML="";
if(title.length==0)
{
titleErrorSection.innerHTML="Required";
document.getElementById("title").focus();
return false;
}
return true;
}
function cancelAddition()
{
window.location.href='/stylethree/Designations.jsp';
}


<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />
<script src='/stylethree/js/AddDesignation.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designation (Add Module)</h2>
<!-- Something about jsp tags to fetch data and more -->
<span class='error' id='errorSection'>
</span><br>
<script>
function addDesignation()
{
if(validateDesignation()==false) return ;
var title=document.getElementById("title").value.trim();
var errorSection=document.getElementById("errorSection");
errorSection.innerHTML="";
var designation={
"code":0,
"title":title
};

var xmlHttpRequest=new XMLHttpRequest();
xmlHttpRequest.onreadystatechange=function(){
if(this.readyState==4)
{
if(this.status==200)
{
var textResponseData=this.responseText;
var responseData=JSON.parse(textResponseData);
if(responseData.error!=null)
{
errorSection.innerHTML=responseData.error;
}
else
{
var message=textResponseData;
alert(message);
localStorage.setItem("message",message);
alert("Message send");
window.location.replace("Notification.jsp");
}
}
else
{
alert("Some problem ");
}
}
};
xmlHttpRequest.open("POST","addDesignation",true);
xmlHttpRequest.setRequestHeader("Content-Type","application/json");
xmlHttpRequest.send(JSON.stringify(designation));
}
</script>
Designation
&nbsp;
<tm:FormID />
<input type='text' id='title' name='title' maxlength='35' size='36'>
<span id='titleErrorSection' class='error'></span><br>
<button type='button' onclick='addDesignation()'>Add</button>
<button type='button' onclick='cancelAddition()'>Cancel</button>
<jsp:include page='/MasterPageBottomSection.jsp' />

<%@ taglib  uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />
<jsp:useBean id='designationBean' scope='request' class='com.ashvin.hr.nexus.beans.DesignationBean' />
<jsp:useBean id='errorBean' scope='request' class='com.ashvin.hr.nexus.beans.ErrorBean' />
<script src='/styletwo/js/EditDesignation.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designation (Update Module)</h2>
<span id='updateError' class='error'>${errorBean.error}</span>
<form method='post' action='/styletwo/UpdateDesignation.jsp' onsubmit='return validateDesignation(this)'>
Designation
&nbsp;
<tm:FormID />
<input type='hidden' id='code' name='code' value='${designationBean.code}'>
<input type='text' id='title' name='title' maxlength='35' size='36' value='${designationBean.title}'>
<span id='titleErrorSection' style='color:red'></span><br>
<button type='submit'>Update</button>
<button type='button' onclick='cancelEditing()'>Cancel</button>
</form>
<form id='cancelEditionForm' action='/styletwo/Designations.jsp'>
</form>
<jsp:include page='/MasterPageBottomSection.jsp' />

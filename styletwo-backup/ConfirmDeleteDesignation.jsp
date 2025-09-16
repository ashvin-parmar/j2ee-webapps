<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />

<jsp:useBean id='designationBean' scope='request' class='com.ashvin.hr.nexus.beans.DesignationBean' />
<script src='/styletwo/js/ConfirmDeleteDesignation.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designation (Delete Module)</h2>
<form method='post' action='/styletwo/DeleteDesignation.jsp' onsubmit='return validateDesignation(this)'>
Designation: ${designationBean.title}
Are you sure, you want to delete designation <b>${designationBean.title}</b>'?<br>
<tm:FormID />
<input type='hidden' id='code' name='code' value='${designationBean.code}'>
<input type='hidden' id='title' name='title' maxlength='35' size='36' value='${designationBean.title}'>
<span id='titleErrorSection' style='color:red'></span><br>
<button type='submit'>Yes</button>
<button type='button' onclick='cancelDeletion()'>No</button>
</form>
<form id='cancelDeletionForm' action='/styletwo/Designations.jsp'>
</form>
<jsp:include page='/MasterPageBottomSection.jsp' />

<jsp:useBean id='designationBean' scope='request' class='com.ashvin.hr.nexus.beans.DesignationBean' />
<jsp:useBean id='errorBean' scope='request' class='com.ashvin.hr.nexus.beans.ErrorBean' />
<script src='/styletwo/js/AddDesignation.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designation (Add Module)</h2>
<!-- Something about jsp tags to fetch data and more -->
<span class='error'>
<jsp:getProperty name='errorBean' property='error'/>
</span>

<form method='post' action='/styletwo/AddDesignation.jsp' onsubmit='return validateDesignation(this)'>
Designation
&nbsp;
<input type='text' id='title' name='title' maxlength='35' size='36' value='${designationBean.title}'>
<span id='titleErrorSection' class='error'></span><br>
<button type='submit'>Add</button>
<button type='button' onclick='cancelAddition()'>Cancel</button>
</form>
<form id='cancelAdditionForm' action='/styletwo/Designations.jsp'>
</form>
<jsp:include page='/MasterPageBottomSection.jsp' />

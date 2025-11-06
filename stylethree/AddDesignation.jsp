<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />
<script src='/stylethree/js/AddDesignation.js'></script>
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designation (Add Module)</h2>
<!-- Something about jsp tags to fetch data and more -->
<span class='error' id='errorSection'>
</span><br>
Designation
&nbsp;
<tm:FormID />
<input type='text' id='title' name='title' maxlength='35' size='36'>
<span id='titleErrorSection' class='error'></span><br>
<button type='button' onclick='addDesignation()'>Add</button>
<button type='button' onclick='cancelAddition()'>Cancel</button>
<jsp:include page='/MasterPageBottomSection.jsp' />

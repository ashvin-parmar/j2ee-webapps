<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<jsp:useBean id='messageBean' scope='request' class='com.ashvin.hr.nexus.beans.MessageBean' />

<jsp:include page='/MasterPageTopSection.jsp' />
<h2 id='messageHeading'></h2>
<span id='messageData'></span><br>
<table id='messageGridTable'>
<tr>
<td>
<button id='buttonOne' type='button'></button>
</td>
<td>
<button type='button' id='buttonTwo'></button>
</td>
</tr>
</table>
<script src='js/Notification.js'></script>
<jsp:include page='/MasterPageBottomSection.jsp' />

<jsp:useBean id='messageBean' scope='request' class='com.ashvin.hr.nexus.beans.MessageBean' />
<%@ taglib uri='WEB-INF/taglib/tmtags.tld' prefix='tm' %>

<jsp:include page='/MasterPageTopSection.jsp' />
<h2>${messageBean.heading}</h2>
${messageBean.message}<br>
<tm:If condition='${messageBean.hasToGenerateButtons}'>
<table>
<tr>
<td>
<form action='${messageBean.buttonOneAction}'>
<button type='submit'>${messageBean.buttonOneText}</button>
</form>
</td>
<tm:If condition='${messageBean.hasToGenerateTwoButtons}'>
<td>
<form action='${messageBean.buttonTwoAction}'>
<button type='submit'>${messageBean.buttonTwoText}</button>
</form>
</td>
</tm:If>
</tr>
</table>
</tm:If>
<jsp:include page='/MasterPageBottomSection.jsp' />

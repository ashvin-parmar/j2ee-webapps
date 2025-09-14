<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />
<jsp:useBean id='designationBean' scope='request' class='com.ashvin.hr.nexus.beans.DesignationBean' />
<jsp:setProperty name='designationBean' property='*' />
<jsp:forward page='deleteDesignation' />

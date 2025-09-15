<jsp:useBean id='administratorBean' scope='request' class='com.ashvin.hr.nexus.beans.AdministratorBean' />
<jsp:setProperty name='administratorBean' property='*' />
<jsp:forward page='/login' />

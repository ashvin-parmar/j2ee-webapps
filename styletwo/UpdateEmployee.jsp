<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:ValidateLogin>
<jsp:forward page='/LoginPage.jsp' />
</tm:ValidateLogin>
<tm:FormResubmitted>
<tm:Module name='HOME' />
<jsp:forward page='/notifyFormResubmission' />
</tm:FormResubmitted>

<tm:Module name='EMPLOYEE' />
<jsp:useBean id='employeeBean' scope='request' class='com.ashvin.hr.nexus.beans.EmployeeBean'/>
<jsp:setProperty name='employeeBean' property='*' />
<jsp:forward page='/updateEmployee'/>



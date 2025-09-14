<jsp:useBean id='moduleManagementBean' scope='request' class='com.ashvin.hr.nexus.beans.ModuleManagementBean' />
<jsp:setProperty name='moduleManagementBean' property='module' value='${moduleManagementBean.HOME}' />
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Welcome</h2>
<jsp:include page='/MasterPageBottomSection.jsp' />

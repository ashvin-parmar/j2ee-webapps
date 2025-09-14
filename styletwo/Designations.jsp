<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<jsp:useBean id="designationBean" scope="request" class="com.ashvin.hr.nexus.beans.DesignationBean" />
<jsp:useBean id='moduleManagementBean' scope='request' class='com.ashvin.hr.nexus.beans.ModuleManagementBean' />
<jsp:setProperty name='moduleManagementBean' property='module' value='${moduleManagementBean.DESIGNATION}' />
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designations</h2>
<table border='1'>
<thead>
<tr>
<th colspan='4' style='text-align:right'><a href='/styletwo/AddDesignationForm.jsp'>Add new designation</a></th>
</tr>
<tr>
<th style='width:40px;text-align:center'>S.No.</th>
<th style='width:200px;text-align:center'>Designation</th>
<th style='width:80px;text-align:center'>Edit</th>
<th style='width:80px;text-align:center'>Delete</th>
</tr>
</thead>
<tbody>
<tm:Designations>
<tr>
<td style='text-align:right'>${serialNumber}</td>
<td>${designationBean.title}</td>
<td style='text-align:center'><a href='/styletwo/editDesignation?code=${designationBean.code}'>edit</a></td>
<td style='text-align:center'><a href='/styletwo/confirmDeleteDesignation?code=${designationBean.code}'>delete</a></td>
</tr>
</tm:Designations>
</tbody>
</table>
<jsp:include page='/MasterPageBottomSection.jsp' />

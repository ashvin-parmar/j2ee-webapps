<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designations</h2>
<link rel='stylesheet' type='text/css' href='/styletwo/css/designation.css'>
<table border='1'>
<thead>
<tr>
<th colspan='4' class='designation-add-option'><a href='/styletwo/AddDesignationForm.jsp'>Add new designation</a></th>
</tr>
<tr>
<th class='designation-column-sno'>S.No.</th>
<th class='designation-column-designation'>Designation</th>
<th class='designation-column-edit'>Edit</th>
<th class='designation-column-delete'>Delete</th>
</tr>
</thead>
<tbody>
<tm:EntityList populateClass='com.ashvin.hr.nexus.bl.DesignationBL'
	       populateMethod='getAll' 
	       name='designationBean'>
<tr>
<td class='designation-data-sno'>${serialNumber}</td>
<td class='designation-data-title'>${designationBean.title}</td>
<td class='designation-data-edit'><a href='/styletwo/editDesignation?code=${designationBean.code}'>edit</a></td>
<td class='designation-data-delete'><a href='/styletwo/confirmDeleteDesignation?code=${designationBean.code}'>delete</a></td>
</tr>
</tm:EntityList>
</tbody>
</table>
<jsp:include page='/MasterPageBottomSection.jsp' />

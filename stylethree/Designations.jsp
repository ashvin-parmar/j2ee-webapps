<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm' %>
<tm:Module name='DESIGNATION' />
<jsp:include page='/MasterPageTopSection.jsp' />
<h2>Designations</h2>
<link rel='stylesheet' type='text/css' href='/stylethree/css/designation.css'>
<table id='designationsGridTable' border='1'>
<thead>
<tr>
<th colspan='4' class='designation-add-option'><a href='/stylethree/AddDesignation.jsp'>Add new designation</a></th>
</tr>
<tr>
<th class='designation-column-sno'>S.No.</th>
<th class='designation-column-designation'>Designation</th>
<th class='designation-column-edit'>Edit</th>
<th class='designation-column-delete'>Delete</th>
</tr>
</thead>
<tbody>
<tr>
<td placeHolderId='serialNumber' class='designation-data-sno'></td>
<td placeHolderId='title' class='designation-data-title'></td>
<td placeHolderId='editOption' class='designation-data-edit'></td>
<td placeHolderId='deleteOption' class='designation-data-delete'></td>
</tr>
</tbody>
</table>
<script src='/stylethree/js/Designations.js'> </script>
<jsp:include page='/MasterPageBottomSection.jsp' />


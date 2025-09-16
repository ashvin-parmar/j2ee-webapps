<%@ taglib uri='/WEB-INF/taglib/tmtags.tld' prefix='tm'%>
<jsp:useBean id='designationBean' scope='request' class='com.ashvin.hr.nexus.beans.DesignationBean' />
<!DOCTYPE HTML>
<html lang='en'>
<head>
<meta charset='utf-8'>
<title>Testing different styles</title>
</head>
<body>
<h2>Designations</h2>
<table>
<thead>
<tr>
<td>Serial Number</td>
<td>Designation</td>
</tr>
</thead>
<tbody>
<tm:Designations>
<tr>
<td>${serialNumber}</td>
<td>${designationBean.title}</td>
</tr>
</tm:Designations>
</tbody>
</table>
<br>
<br>

<select name='designationCode'>
<option value='-1'>&lt; Select &gt;</option>
<tm:Designations>
<option value='${designationBean.code}'>${designationBean.title}(${designationBean.code})</option>
</tm:Designations>
</body>
</html>

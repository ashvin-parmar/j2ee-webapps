<%
String name=request.getParameter("name");
session.setAttribute("name",name);
response.sendRedirect("/webrock/authenticate/login3");
%>

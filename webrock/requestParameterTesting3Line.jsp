<%-- Just for testing purpose for type
<jsp:useBean id='xyz' scope='request' class='bobby.test.BobbyTesting'/>
--%>
<jsp:useBean id='student' scope='session' class='bobby.test.Student' />
<jsp:setProperty name='student' property='*'/>
<jsp:forward page='/requestParameterTesting/testing1'/>


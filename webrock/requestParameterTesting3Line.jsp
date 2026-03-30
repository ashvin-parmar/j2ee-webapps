<%-- Just for testing purpose for type
<jsp:useBean id='xyz' scope='request' class='bobby.test.BobbyTesting'/>
--%>
<jsp:useBean id='xyz' scope='request' class='bobby.test.Student' />
<jsp:setProperty name='xyz' property='*'/>
<jsp:forward page='/requestParameterTesting/testing1'/>


## HttpSession Object for old-Session Tracking for configured time-duration

HttpSession Object --> managed at server side and store old-session information for some time-duration [configured from web.xml or thorugh programm]. 

### Session accepting from request
* Same approuch to get old-session HttpSession Object if available, otherwise new created
```
HttpSession hs=request.getSession();
hs=request.getSession(true);
```
* Check for old-session, if availble then HttpSession object address returned, otherwise null returned
```
HttpSession hs=request.getSession(false);
```

### Session response sends and data extacted using setter/getter
hs.setAttrubute("name",name);		(key,value)		--> value of Object type [any]
Object hs.getAttribute("name");		--> have to be type casted before assign.

## Session Time-duration settings
```hs.setMaxInactiveInterval(30);```
This will set 30 seconds interval time for session to track, if no next request arrived in this time duration, the HttpSession object killed.
If any request arrived, time duration start from when that new request -> response sends.

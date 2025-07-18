## Cookie Session Tracking

We have created cookie to store on browser to memories old-session identities to remember or fulfill for future session requirement.

## [Problems discussion: ]
1: Hidden form session tracking --> view source to see the data.
2: URL-rewriting: view source to see the url rewritten in the code.
3: Cookie: Stored at client-side(browser-end), can be deleted by user any-time. 

All above three methods are explained and have thier own unique use, at different time. for now, we want the security from user/client end.

## **Solution**
Next --> HttpSession Object --> managed at server side and store old-session information for some time-duration [configured from web.xml or thorugh programm]. 

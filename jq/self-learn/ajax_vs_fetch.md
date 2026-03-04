# How JQuery handles Promises and Fetch

jQuery handles asynchronous operations using its own implementation of the Promise pattern, centered on the Deferred object and methods like $.ajax(). The native Fetch API, in contrast, is a built-in JavaScript interface that returns a native, standards-compliant Promise object. 

## jQuery's Approach: Deferred and jqXHR
jQuery introduced its Promise implementation (via the Deferred object) in version 1.5 to manage asynchronous tasks like AJAX calls and animations more cleanly than nested callbacks (callback hell). 
***Deferred Object***: This object represents a task that is not yet complete. It has methods like resolve() and reject() to control the state of the operation.
Promise Object: A read-only view of a Deferred object, which only allows you to attach handlers (.done(), .fail(), .always(), .then()) but not change the state.
jqXHR Object: jQuery's AJAX methods ($.ajax(), $.get(), etc.) return a jqXHR object, which is a superset of the native XMLHttpRequest and implements the jQuery Promise interface.
***Chaining and Composition*** : You can chain multiple actions using .then() or manage multiple concurrent operations with $.when().
***Error Handling***: By default, $.ajax() calls its .fail() handler for any HTTP error status (e.g., 404 Not Found, 500 Internal Server Error) or network failures. 

## The Fetch API's Approach: Native Promises
The fetch() API is a modern, built-in JavaScript method for making network requests that offers a cleaner, promise-based alternative to the older XMLHttpRequest. 
Native Promises: The fetch() function returns a native JavaScript Promise that resolves to a Response object.
Two-Stage Handling: Fetch requests often require two chained .then() calls: the first to handle the initial Response object (e.g., checking response.ok status), and the second to parse the body data (e.g., response.json() or response.text(), which are also asynchronous).
Error Handling Distinction: Crucially, fetch() only rejects its promise for network failures (like a DNS error or the network being down). It fulfills the promise for HTTP error statuses like 404 or 500, requiring you to manually check response.ok or response.status within the first .then() block to handle these cases.
async/await: Fetch works seamlessly with the modern async/await syntax, which makes asynchronous code appear more synchronous and readable. 

## Summary of Differences
Feature 	jQuery AJAX (jqXHR)	Native Fetch API
Returns	jQuery jqXHR object (a thenable/Promise-like object)	Native JavaScript Promise object
Error Status (e.g., 404/500)	Triggers .fail() (rejects the Deferred) automatically	Fulfills the Promise; requires manual check of response.ok property
Response Data	Data is typically passed directly to the success handler	Returns a Response object, which needs a second promise to parse the body (e.g., .json())
Syntax	Uses .done(), .fail(), .always(), or .then()	Uses .then(), .catch(), .finally(), or async/await
Browser Support	Wide, including legacy browsers like IE	Supported by all modern browsers (since ~2017); not in Internet Explorer

-----------------------------------------------------------------------------



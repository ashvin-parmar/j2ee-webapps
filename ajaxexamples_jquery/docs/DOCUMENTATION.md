# Library-Assisted (jQuery) & Fetch-Based AJAX Style: `ajaxexamples_jquery`

This document details the third style of AJAX programming found in the [ajaxexamples_jquery](file:///media/ashvin/code/tryout/ajaxexamples_jquery) folder, which replaces verbose native `XMLHttpRequest` boilerplate with jQuery `$.ajax` utility methods and the native modern JavaScript Fetch API.

---

## 1. Architectural Overview

This pattern leverages modern client-side APIs and libraries to improve code readability, simplify error handling, and orchestrate global user experience triggers (such as progress indicators).

*   **Client Libraries**: Uses the external **jQuery** library (`jquery.js`) to provide promise-based AJAX wrappers, or uses the native modern **Fetch API** (ES6+).
*   **Boilerplate Reduction**: Replaces multiple status/state checks (`readyState == 4`, `status == 200`) with clean callbacks or promise chains.
*   **User Experience (UX) Enhancements**: Demonstrates global loading indicator triggers hookable to server response states.

---

## 2. Examples Breakdown

### Example 1: GET Request using jQuery Promises (`eg1.html`)
*   **jQuery Solution**: Invokes `$.ajax` and links callbacks using promise methods (`.done()`, `.fail()`, `.always()`):
    ```javascript
    var jqxhr = $.ajax({ "type": "GET", "url": "servletOne" })
    .done(function() {
        var designations = JSON.parse(jqxhr.responseText);
        // Render table...
    })
    .fail(function() {
        alert("Some problem");
    })
    .always(function() {
        alert("Always block executed");
    });
    ```

### Example 2: Parameterized GET using jQuery DOM bindings (`eg2.html`)
*   **jQuery Solution**: Binds click listeners using jQuery selectors and makes an AJAX request:
    ```javascript
    $("#getButton").click(function() {
        var code = $("#code").val().trim();
        var jqXHR = $.ajax({
            "type": "GET",
            "url": "servletTwo?code=" + code
        }).done(function() {
            var d = JSON.parse(jqXHR.responseText);
            if(d.title != null) $("#designation").html(d.title);
        });
    });
    ```

### Example 3: POST Request using jQuery (`eg3.html`)
*   **jQuery Solution**: Configures `contentType` and serializes data directly inside the config object:
    ```javascript
    var jqXHR = $.ajax({
        "url": "servletThree",
        "type": "POST",
        "contentType": "application/json",
        "data": JSON.stringify(customer)
    }).done(function() {
        var cus = JSON.parse(jqXHR.responseText);
        // update UI...
    });
    ```

### Example 4: Global UX Loading Hooks (`eg4.html` & `ServletFour.java`)
Simulates a slow database load to demonstrate UX controls.
*   **Backend Delay**: `ServletFour.java` runs `Thread.sleep(5000);` before printing the response.
*   **Global Event Hooks**: Uses jQuery global events to show and hide a loading spinner gif:
    ```javascript
    $(document).ajaxStart(function() {
        $('#loading-spinner').show();
        $('#content-division').hide();
    });
    $(document).ajaxStop(function(){
        $('#loading-spinner').hide();
        $("#content-division").show();
    });
    ```
*   **Inline Callbacks**: Uses `success`, `error`, and `complete` callbacks configured directly in the `$.ajax` options instead of chained promise methods.

---

## 3. Evaluation of this Style

### Pros:
1.  **Cleaner Code**: Eliminates boilerplate state check boilerplate.
2.  **Promise Chaining**: Chaining requests is straightforward, which prevents callback hell.
3.  **Global Handlers**: Global hooks simplify progress bar or loader implementation across the app.
4.  **No Library Requirement (Fetch)**: The native Fetch API is built directly into modern browsers.

### Cons:
1.  **jQuery Overhead**: Using `$.ajax` requires importing the external `jquery.js` library, which increases client load times.
2.  **Lack of Fetch Interoperability**: jQuery's global hooks don't intercept native Fetch requests, requiring separate loader management for Fetch APIs.

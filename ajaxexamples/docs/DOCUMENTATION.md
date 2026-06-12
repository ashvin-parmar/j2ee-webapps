# Delimiter-Based (Plain Text) AJAX Style: `ajaxexamples`

This document details the first style of AJAX programming found in the [ajaxexamples](file:///media/ashvin/code/tryout/ajaxexamples) folder, which utilizes raw comma-delimited strings (CSV-like) for client-server communication.

---

## 1. Architectural Overview

In this pattern, data is sent and received as flat, delimited plain text rather than structured data structures (like JSON or XML).

*   **Client Communication**: Uses native `XMLHttpRequest` objects to dispatch GET and POST requests.
*   **Request Encoding**: POST requests are formatted manually as query strings using standard URL parameter encoding (`application/x-www-form-urlencoded`).
*   **Response Format**: The server returns responses with content-type `text/plain`, separating fields with commas (e.g., `code,title,code,title...`).
*   **Parsing Logic**: The client splits the plain text response by commas and reconstructs the data array based on index positions.

---

## 2. Examples Breakdown

### Example 1: GET Request without Parameters (`eg1.html`)
Fetches all designations from the database.
*   **Client Request**: Sends a GET request to `servletOne`.
*   **Server Response (`ServletOne.java`)**: 
    Sets content-type to `text/plain` and streams designation records sequentially, separated by commas:
    ```java
    pw.print(designationDTO.getCode() + "," + designationDTO.getTitle());
    ```
*   **Client Parsing**:
    Splits the string response by comma and iterates by `2` to extract key-value pairs:
    ```javascript
    var splits = responseData.split(",");
    for(var i = 0; i < splits.length; i += 2) {
        var code = splits[i];
        var title = splits[i+1];
        // Populate dropdown...
    }
    ```

### Example 2: GET Request with Parameters (`eg2.html`)
Retrieves a single designation by its code.
*   **Client Request**: Sends a GET request to `servletTwo?code=123`.
*   **Server Response (`ServletTwo.java`)**: 
    If code exists, returns `code,title`. If not found, catches a `DAOException` and responds with `"INVALID"`.
*   **Client Parsing**:
    Splits the response and checks if the first element is `"INVALID"`:
    ```javascript
    var splits = responseData.split(",");
    if(splits[0] == "INVALID") {
        alert("Invalid code");
    } else {
        designationSpan.innerHTML = splits[1];
    }
    ```

### Example 3: POST Request with URL-encoded Parameters (`eg3.html`)
Submits customer enquiry details.
*   **Client Request**: Manually constructs a query parameter string, setting `Content-Type` to `application/x-www-form-urlencoded`:
    ```javascript
    var requestString = "firstName=" + encodeURI(firstName) + "&lastName=" + encodeURI(lastName) + "&age=" + encodeURI(age);
    xmlHttpRequest.open("POST", "servletThree", true);
    xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    xmlHttpRequest.send(requestString);
    ```
*   **Server Response (`ServletThree.java`)**: 
    Reads parameters via standard servlet APIs (`request.getParameter()`) and echoes back the fields as a comma-delimited string:
    ```java
    pw.print(firstName + "," + lastName + "," + age);
    ```

---

## 3. Evaluation of this Style

### Pros:
1.  **Extremely Lightweight**: No brackets, keys, or packaging metadata in the payload—only values and delimiters.
2.  **Simple Server Logic**: Requires no external libraries or complex JSON writers on the backend; responses can be printed directly using `PrintWriter`.

### Cons:
1.  **Fragile Parsing**: Parsing is highly dependent on value ordering and index positions. If a value itself contains a delimiter (e.g. a designation title like `"Clerk, Junior"`), the split logic breaks.
2.  **Lack of Hierarchy**: Representing complex, nested objects or variable structures in a single flat delimited line is extremely difficult.
3.  **Manual Query String Assembly**: Developers must manually build and URL-encode POST query parameters on the client side, which is prone to formatting mistakes.

# Native JSON AJAX Style: `ajaxexamples2`

This document details the second style of AJAX programming found in the [ajaxexamples2](file:///media/ashvin/code/tryout/ajaxexamples2) folder, which shifts from delimiter-separated plain text to standard JSON (JavaScript Object Notation) payloads.

---

## 1. Architectural Overview

This pattern leverages structured JSON formatting to exchange messages, resolving data-splitting vulnerabilities and supporting complex object structures.

*   **Client Communication**: Uses native `XMLHttpRequest` objects to dispatch GET and POST requests.
*   **Data Exchange Format**: Handled exclusively as `application/json` for both requests and responses.
*   **Serialization**: Uses native `JSON.stringify()` on the client to serialize JavaScript objects and Google `Gson` on the backend to serialize Java DTOs/lists.
*   **Deserialization**: Uses native `JSON.parse()` on the client to convert strings back into JavaScript objects and `Gson` to deserialize incoming request body streams directly into Java classes.

---

## 2. Examples Breakdown

### Example 1: GET Request without Parameters (`eg1.html`)
Retrieves all designations.
*   **Client Request**: Sends a GET request to `servletOne`.
*   **Server Response (`ServletOne.java`)**: 
    Uses Google `Gson` to convert the `List<DesignationDTO>` directly to a JSON string array and responds with content-type `application/json`:
    ```java
    Gson gson = new Gson();
    String d = gson.toJson(designations);
    pw.print(d);
    ```
*   **Client Parsing**:
    Directly converts response payload into a JavaScript array using `JSON.parse()`, then loops over the array objects using property names:
    ```javascript
    var designations = JSON.parse(this.responseText);
    for(var i = 0; i < designations.length; i++) {
        var code = designations[i].code;
        var title = designations[i].title;
        // Populate dropdown...
    }
    ```

### Example 2: GET Request with Parameters (`eg2.html`)
Retrieves a single designation by its code.
*   **Client Request**: Sends a GET request to `servletTwo?code=123`.
*   **Server Response (`ServletTwo.java`)**: 
    Converts a single DTO to a JSON object. If a `DAOException` is thrown, returns an empty JSON object `{}`.
*   **Client Parsing**:
    Parses the response and evaluates whether a property (e.g., `title`) is present:
    ```javascript
    var d = JSON.parse(responseData);
    if(d.title != null) {
        designationSpan.innerHTML = d.title;
    } else {
        alert("Invalid code");
    }
    ```

### Example 3: POST Request with JSON Payloads (`eg3.html`)
Submits customer details as a JSON payload.
*   **Client Request**: Serializes a customer object to JSON, sets `Content-Type` to `application/json`, and sends the stringified payload:
    ```javascript
    var customer = { "firstName": firstName, "lastName": lastName, "age": age };
    xmlHttpRequest.open("POST", "servletThree", true);
    xmlHttpRequest.setRequestHeader("Content-Type", "application/json");
    xmlHttpRequest.send(JSON.stringify(customer));
    ```
*   **Server Response (`ServletThree.java`)**: 
    Reads the request input stream via a `BufferedReader`, collects the raw JSON string, and deserializes it directly into a Java [Customer](file:///media/ashvin/code/tryout/ajaxexamples2/WEB-INF/classes/com/ashvin/hr/nexus/servlets/Customer.java) class using `Gson`. It then echoes the object back:
    ```java
    BufferedReader br = request.getReader();
    // read stream...
    Customer customer = gson.fromJson(sb.toString(), Customer.class);
    pw.print(gson.toJson(customer));
    ```

---

## 3. Evaluation of this Style

### Pros:
1.  **Robust Data Handling**: Structured JSON avoids parsing collision issues. Comma characters within values are safely encapsulated.
2.  **Explicit Key-Value Mapping**: Data fields are mapped explicitly to property names, making client code more readable and self-documenting.
3.  **Structured Hierarchy**: Supports complex relationships (such as nested objects, lists, and variable attributes).

### Cons:
1.  **Increased Payload Size**: Metadata repetition (keys and brackets) increases network footprint.
2.  **Native XMLHttp Boilerplate**: Developers must still write verbose `onreadystatechange` state checks and manually handle stream collection on the server.

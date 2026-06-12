# Technical Comparison: styletwo vs. stylethree

This document provides a comparative technical analysis between the JSP-Servlet MVC model implemented in **styletwo** and the asynchronous Single Page Application (SPA) model implemented in **stylethree** for the **HR-Nexus** application.

---

## 1. Architectural Paradigm Comparison

The transition from **styletwo** to **stylethree** represents a transition from a server-rendered web application to an asynchronous client-driven Single Page Application (SPA).

| Feature / Category | styletwo (JSP MVC) | stylethree (Asynchronous SPA) | Status / Evaluation |
| :--- | :--- | :--- | :--- |
| **Architectural Model** | **Model 2 MVC**: Servlets process requests and forward to JSP files to render HTML. | **SPA**: HTML is static; Servlets act as pure JSON API endpoints; client-side JS manages the DOM. | **Decoupled**: Fully separates API data from presentation rendering. |
| **Page Reloads / Navigation** | **Full Page Reloads**: Every click, form submission, or validation error reloads the page. | **No Page Reloads**: View templates are fetched via AJAX and injected into a persistent shell (`index.html`). | **Improved UX**: Smooth navigation without page flickering or browser reloads. |
| **Boilerplate Layout** | **Server-Side Include**: Reusable shells are loaded on the server using `<jsp:include>`. | **Client-Side SPA Shell**: Layout elements reside in `index.html` once; views are injected. | **Optimized DRY**: Reduces server processing overhead for page headers/footers. |
| **Custom Tag Libraries** | **JSP Custom Tags**: Uses tag handlers like `<tm:Module>`, `<tm:FormID>`, `<tm:If>`. | **Vanilla JS / DOM**: Tag libraries are completely removed. DOM logic is handled in clean JavaScript. | **Zero Overhead**: Eliminates JSP-compilation overhead and custom tag handling. |
| **Decoupling** | **Tight Coupling**: JSP and Servlet code are tied together inside the Tomcat Container. | **Loose Coupling**: Frontend is pure static HTML/JS/CSS; backend is a REST-like JSON API. | **Modernized**: The frontend can now be hosted independently from the Java container (e.g. CDNs). |
| **Data Transfer Format** | **JSP Request Attributes**: Java objects are passed via request scope to JSP engines. | **JSON Payloads**: All data is exchanged asynchronously via JSON using `XMLHttpRequest` & Google Gson. | **Interoperable**: Makes the backend APIs easily consumable by mobile or external clients. |
| **Double-Submission Prevention** | **Token-in-Session Tag**: Uses custom server tags to verify UUIDs stored in user sessions. | **AJAX Control & JS Flow**: Disables submit actions or uses controlled JS redirect sequences. | **Simplified**: Does not pollute the user session or require JspWriter interventions. |

---

## 2. In-Depth Architectural Solutions in stylethree

### A. Dynamic View Injection
*   **styletwo Approach**: The layout is server-assembled. Navigating to designations loads a full page, calling `<jsp:include page="/MasterPageTopSection.jsp" />`.
*   **stylethree Solution**: The main container is requested once. When navigating, the routing engine fetches the view's raw HTML file:
    ```javascript
    xmlHttpRequest.open("GET", viewUrl, true);
    ```
    And replaces the innerHTML of `#content-right-panel`. It then parses and executes the nested script blocks.

### B. Pure JSON Servlets
*   **styletwo Approach**: Servlets read parameters from raw request forms and forward to JSP views. E.g.
    ```java
    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/AddDesignationForm.jsp");
    requestDispatcher.forward(request, response);
    ```
*   **stylethree Solution**: Servlets read inputs from `request.getReader()`, deserialize the JSON payload using `Gson`, interact with business/data layers, and return serialized JSON directly:
    ```java
    response.setContentType("application/json");
    pw.print(gson.toJson(responseMessage));
    ```

### C. Client-Side Notifications
*   **styletwo Approach**: Operations forward to `Notification.jsp` with a server-set `MessageBean` attribute.
*   **stylethree Solution**: The AJAX response contains the message details. The client-side controller stores this in `localStorage` and loads `Notification.html` inside the view panel. `Notification.html` reads the storage, populates the message card, and maps actions to SPA view transitions.

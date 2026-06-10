# Technical Comparison: styleone vs. styletwo

This document provides a comparative technical analysis between the legacy **styleone** development paradigm and the newer **styletwo** Model 2 (MVC) approach for the **HR-Nexus** Java EE application.

---

## 1. Architectural Paradigm Comparison

The transition from **styleone** to **styletwo** represents a significant step from **Model 1 (Servlet-Centric Rendering)** to **Model 2 (JSP-Servlet MVC)** development.

| Feature / Category | styleone (Legacy Model 1) | styletwo (Model 2 MVC) | Status / Evaluation |
| :--- | :--- | :--- | :--- |
| **Architectural Model** | **Model 1**: Servlets handle both control logic and HTML view output. | **Model 2**: MVC pattern with Servlets (Controller), JSPs (View), and DAOs/DTOs (Model). | **Solved**: Decouples business logic and database queries from page rendering. |
| **Separation of Concerns** | **Tight Coupling**: HTML tags, CSS styles, and client JS are printed inside Java strings. | **Decoupled**: View layer is separated into JSPs with pure HTML, dynamic EL expressions, and custom tags. | **Solved**: Simplifies debugging of design issues and logic flows independently. |
| **Boilerplate Layout** | **High Redundancy**: Every servlet hardcodes the navigation menu, sidebar links, header, and footer. | **Centralized**: Reusable layout shells (`MasterPageTopSection.jsp` and `MasterPageBottomSection.jsp`) are loaded via `<jsp:include>`. | **Solved**: Complies with the **DRY (Don't Repeat Yourself)** design principles. |
| **Access Control (Auth)** | **Repetitive**: Each servlet manually tests `session.getAttribute("username") == null` and sends redirects. | **Reusable**: Centralized validation in a custom tag `<tm:ValidateLogin>` embedded in JSPs/templates. | **Solved**: Minimizes authorization code duplication across controller components. |
| **Form Resubmission** | **Unprotected**: No built-in double-submission prevention mechanism. | **Token-Based**: Unique UUIDs injected via `<tm:FormID>` and verified using `<tm:FormResubmitted>`. | **Solved**: Effectively blocks duplicate POST requests (e.g. from browser page refreshes). |
| **Database Connection Leaks** | **Present**: Resource cleanup `.close()` is bypassed on SQL exceptions, leading to leaks. | **Present**: Same unsafe JDBC try-catch structures persist in DAOs without `finally` or Try-with-Resources. | **Not Solved**: Critical resource leaks remain when runtime exceptions or SQL issues occur. |
| **Database Configurations** | **Hardcoded**: Driver name, URL, and credentials are hardcoded inside `DAOConnection.java`. | **Hardcoded**: Exactly the same configurations remain compiled directly inside `DAOConnection.java`. | **Not Solved**: Changing databases still requires source code modifications and recompilation. |
| **Whitespace Preservation** | **Collapsed**: Multiple spaces render collapsed due to default HTML rendering rules. | **Collapsed**: Standard values are mapped into table cell DOM nodes via JavaScript, still collapsing spaces. | **Not Solved**: Needs CSS properties (`white-space: pre-wrap;`) or text escaping (`&nbsp;`). |

---

## 2. In-Depth Architectural Solutions in styletwo

### A. View-Controller Separation
*   **styleone Issue**: Editing a page header or style required modifying Java servlets, compiling `.class` files, and redeploying.
*   **styletwo Solution**: JSPs contain standard markup, CSS references, and client-side validation scripts. Java Servlets handle inputs, populate JavaBeans, set request scope attributes, and forward to JSPs for formatting.
    *   *Example Servlet Flow in `AddDesignation.java`:*
        ```java
        // Business execution
        designationDAO.add(designationDTO);
        // Bind UI states to JavaBeans
        request.setAttribute("messageBean", messageBean);
        // Forward to View Page
        requestDispatcher.forward(request, response);
        ```

### B. Prevention of Layout Boilerplate Redundancy
*   **styleone Issue**: Changing the navbar or footer text required editing over a dozen Java classes.
*   **styletwo Solution**: Using `<jsp:include page="/MasterPageTopSection.jsp" />` ensures layout alterations need to be written only once. The top section also handles site-wide login verification seamlessly.

### C. Prevent Form Double-Submissions
*   **styleone Issue**: Clicking a submit button twice or reloading a page after form submission re-triggered insert queries in the database.
*   **styletwo Solution**:
    1.  The view form includes the `<tm:FormID />` custom tag handler. It generates a random UUID, saves it to the session scope, and renders a hidden field:
        ```html
        <input type='hidden' id='formId' name='formId' value='[Generated-UUID]'>
        ```
    2.  The receiving JSP wraps the forwarding block in the `<tm:FormResubmitted>` tag.
    3.  During processing, the tag handler compares the request parameter `formId` to the session-scoped UUID. On match, it deletes the token from the session and allows the request. Subsequent duplicate submissions fail to find the token in the session and are forwarded to a resubmission warning page.

---

## 3. Persistent Legacy Issues in both Workspaces

Despite modernizing the presentation layers, **styletwo** carries over some critical database management and design issues:

### A. Resource Leaks in Data Access Objects (DAOs)
Both codebases handle database resources inside a try-catch structure, closing connections inside the `try` block:
```java
try {
    Connection connection = DAOConnection.getConnection();
    // ... statements and queries execute here
    preparedStatement.close();
    connection.close(); // Executed ONLY on successful run!
} catch(SQLException sqlException) {
    throw new DAOException(sqlException.getMessage()); // Leaks connection!
}
```
*   **The Problem**: If an query fails or a database constraint is violated, the program throws a `DAOException`, bypassing the `.close()` lines.
*   **The Remedy**: Implement **Try-with-Resources** in all DAO classes, ensuring implicit closure of database connectivity resources.

### B. Environment Coupling in Configuration
Both apps couple database environments tightly with the code:
```java
connection=DriverManager.getConnection("jdbc:mysql://localhost:3306/tmdb","tmdbuser","tmdb#User1");
```
*   **The Problem**: Deploying the application to staging or production requires updating Java source files, recompiling, and packaging the war.
*   **The Remedy**: Implement properties loading (`java.util.Properties`) from an external configuration file or fetch connections via a server-managed JNDI connection pool.

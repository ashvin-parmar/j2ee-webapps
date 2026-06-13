# WebRock Framework Usecase Guide: Student Management System

This document is a practical developer walkthrough showing how to build a complete database-driven web application using the **WebRock Framework**. We will implement a Student Database Management dashboard featuring data inserts, updates, deletes, search queries, and route authorization checks.

---

## 1. Usecase Architecture

The application is structured into the following layers:

```
[ Frontend: HTML / jQuery ]
         │
         ▼  (Dispatched via generated JS Promises)
[ Front Controller Servlet: WebRock ]
         │
         ▼  (Intercepted via @SecuredAccess guard)
[ Authentication CheckPost: Authenticate.java ]
         │
         ▼  (Executes if security passes)
[ Business Controller / Service: StudentDAO.java ]
         │
         ▼  (Data persistence)
[ MySQL Database: database (example: webrock_db)) ]
```

---

## 2. Step 1: Database Setup [Skip this part]

First, initialize the MySQL database schema and configure access privileges.

### Database Creation (`db_schema.sql`)
```sql
CREATE DATABASE webrock_db;
CREATE USER 'user' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON webrock_db.* TO 'user';
FLUSH PRIVILEGES;
```

### Table Schema (`table_schema.sql`)
```sql
USE webrock_db;
CREATE TABLE student (
    rollNumber INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('M', 'F'))
);
```

---

## 3. Step 2: Database Connection Provider (`DAOConnection.java`)

Create a utility class to supply standard JDBC connections.

```java
package bobby.student;

import java.sql.*;

public class DAOConnection {
    private DAOConnection() {}

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/webrock_db",
                "user",
                "pass"
            );
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return connection;
    }
}
```

---

## 4. Step 3: Data Transfer Object (`StudentDTO.java`)

Declare the data bean representing the Student structure. Marking it with `@POJO` instructs the WebRock engine to compile a matching ES6 JavaScript class on startup.

```java
package bobby.student;

import com.ashvin.web.rock.annotations.*;
import java.io.Serializable;

@POJO
public class StudentDTO implements Serializable {
    private int rollNumber;
    private String name;
    private String gender;

    public StudentDTO() {}

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }
    public int getRollNumber() {
        return this.rollNumber;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return this.gender;
    }
}
```

---

## 5. Step 4: Authentication & Security Guard (`Authenticate.java`)

Create a security endpoint class to validate login requests and act as the checkpost guard for sensitive endpoints.

```java
package bobby.test;

import com.ashvin.web.rock.annotations.*;
import com.ashvin.web.rock.pojo.*;
import com.ashvin.web.rock.exceptions.SecurityException;

@PATH("/authenticate")
@InjectSessionScope
public class Authenticate {
    private SessionScope sessionScope;

    public void setSessionScope(SessionScope sessionScope) {
        this.sessionScope = sessionScope;
    }

    // Normal Login endpoint
    @PATH("/login")
    public void login(SessionScope sessionScope) throws SecurityException {
        String name = (String) sessionScope.getAttribute("name");

        // Simple authentication check
        if (name != null && name.equalsIgnoreCase("ASHVIN")) {
            sessionScope.setAttribute("name", "ashvin");
        } else {
            throw new SecurityException("Invalid username/password credentials.");
        }
    }

    // Secondary method guard check for auto-wire validation
    @PATH("/login3")
    public void login3() {
        System.out.println("Guard checkpost login3 invoked.");
    }
}
```

---

## 6. Step 5: Service Controller (`StudentDAO.java`)

Implement the CRUD service endpoints. The class is annotated with `@PATH("/StudentManager")` to define the base URI mapping.

```java
package bobby.student;

import com.ashvin.web.rock.annotations.*;
import java.sql.*;
import java.util.*;

@PATH("/StudentManager")
public class StudentDAO {

    // POST: Adds a student. Body payload is parsed from JSON body automatically
    @PATH("/add")
    @POST
    public void add(StudentDTO student) throws DAOException {
        if (student == null) throw new DAOException("Student data required.");

        try (Connection connection = DAOConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                 "INSERT INTO student (name, gender) VALUES (?, ?)",
                 Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, student.getName());
            statement.setString(2, student.getGender().toUpperCase().substring(0, 1));
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setRollNumber(keys.getInt(1));
                }
            }
        } catch (SQLException sqlException) {
            throw new DAOException("Error adding student record: " + sqlException.getMessage());
        }
    }

    // GET: Deletes a student. Parameter is bound to the query string: ?rollNumber=x
    @PATH("/delete")
    @GET
    public void delete(@RequestParameter("rollNumber") int rollNumber) throws DAOException {
        if (rollNumber <= 0) throw new DAOException("Invalid roll number.");

        try (Connection connection = DAOConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM student WHERE rollNumber = ?")) {
            statement.setInt(1, rollNumber);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new DAOException("No student found with roll number: " + rollNumber);
            }
        } catch (SQLException sqlException) {
            throw new DAOException("Error deleting student record.");
        }
    }

    // GET: Retrieves list of all students
    @PATH("/getAll")
    @GET
    public List<StudentDTO> getAll() throws DAOException {
        List<StudentDTO> list = new ArrayList<>();
        try (Connection connection = DAOConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM student");
             ResultSet results = statement.executeQuery()) {

            while (results.next()) {
                StudentDTO student = new StudentDTO();
                student.setRollNumber(results.getInt("rollNumber"));
                student.setName(results.getString("name").trim());
                String gender = results.getString("gender").trim();
                student.setGender(gender.equalsIgnoreCase("M") ? "Male" : "Female");
                list.add(student);
            }
        } catch (SQLException sqlException) {
            throw new DAOException("Unable to load student directory list.");
        }
        return list;
    }
}
```

---

## 7. Step 6: Frontend Integration (`Student.html`)

Include the compiled client JS libraries and execute service calls via promises in the frontend dashboard.

```html
<!DOCTYPE HTML>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Student Directory Management</title>

    <!-- Import framework-compiled JavaScript libraries -->
    <script src="jsFile?name=StudentManager.js"></script>
    <script src="jsFile?name=StudentDTO.js"></script>
    <script src="jquery/jquery.js"></script>
</head>
<body>
    <div id="appContainer">
        <h2>Student Directory</h2>

        <!-- Add Student Form -->
        <div id="addForm">
            <input type="text" id="studentName" placeholder="Name" />
            <input type="radio" name="gender" value="Male" checked> Male
            <input type="radio" name="gender" value="Female"> Female
            <button id="addBtn">Add Student</button>
        </div>

        <!-- Directory List -->
        <table id="studentsTable" border="1" style="margin-top:20px;">
            <thead>
                <tr>
                    <th>Roll Number</th>
                    <th>Name</th>
                    <th>Gender</th>
                </tr>
            </thead>
            <tbody></tbody>
        </table>
    </div>

    <script>
        $(() => {
            // Instantiate the framework-generated API client wrapper
            const studentDAO = new StudentDAO();

            // Load all directory records on startup
            function loadDirectory() {
                studentDAO.getAll()
                    .then((students) => {
                        const tbody = $("#studentsTable tbody").empty();
                        students.forEach(student => {
                            tbody.append(`
                                <tr>
                                    <td>${student.rollNumber}</td>
                                    <td>${student.name}</td>
                                    <td>${student.gender}</td>
                                </tr>
                            `);
                        });
                    })
                    .catch((error) => {
                        alert("Error loading directory: " + error.message);
                    });
            }

            // Bind click handler for inserting a new record
            $("#addBtn").click(() => {
                const name = $("#studentName").val();
                const gender = $("input[name='gender']:checked").val();

                // Instantiate generated DTO class
                const student = new StudentDTO(0, name, gender);

                // Dispatches POST request to StudentManager/add
                studentDAO.add(student)
                    .then((response) => {
                        alert("Student added successfully!");
                        $("#studentName").val("");
                        loadDirectory();
                    })
                    .catch((error) => {
                        alert("Error inserting student: " + error.message);
                    });
            });

            loadDirectory();
        });
    </script>
</body>
</html>
```

## 8. Integration with JSP Standard Actions (`<jsp:useBean>`)

While WebRock is designed for frontend AJAX communication, it integrates with legacy JSP templates by exploiting request-scoped attribute forwarding.

#### Integration Workflow:
1. **Instantiate & Populate Bean**:
   Legacy JSP pages can instantiate DTOs or test models using JSP standard action tags, populating them with request parameters via wildcard properties:
   ```jsp
   <jsp:useBean id="xyz" scope="request" class="bobby.test.Student" />
   <jsp:setProperty name="xyz" property="*"/>
   ```
2. **Context Forwarding**:
   The JSP forwards the request context to the WebRock Front Controller endpoint mapping:
   ```jsp
   <jsp:forward page="/autoWiredTesting/testing1"/>
   ```
3. **Implicit Dependency Resolution**:
   When the Front Controller resolves the target route, it scans the service class fields for `@AutoWired(name="xyz")`. Because the bean was stored in request scope by the `<jsp:useBean>` tag, WebRock automatically extracts the request attribute `"xyz"`, validates its class type, and auto-wires it into the service before executing the endpoint method.




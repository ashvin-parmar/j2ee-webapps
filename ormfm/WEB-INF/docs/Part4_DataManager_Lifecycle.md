# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 4 — DataManager: Initialization & Lifecycle

---

> [!NOTE]
> This is **Part 4** of the ORMFM documentation. It covers `DataManager` from the moment it is initialized to the moment each thread's session ends. Parts 5, 6, 7, and 8 will cover the actual CRUD and query operations built on top of this foundation.

---

## 1. What is DataManager?

`DataManager` is the **heart of the ORMFM runtime**. It is the single class that backend developers interact with for all database operations — saving, updating, deleting, and querying records.

It is designed around two core patterns:

### 1.1 Singleton Pattern
There is exactly **one `DataManager` instance** for the entire application lifetime. It is created once during `initialize()` and returned on every subsequent `getDataManager()` call. This single instance holds all the pre-built SQL structures for every POJO class — built once at startup, reused forever.

```java
// Only one instance exists — shared across all threads
private static DataManager dataManager = null;
```

### 1.2 ThreadLocal Session Pattern
While the `DataManager` instance is shared, **each thread gets its own `Session` object** stored in a `ThreadLocal`. This means every thread has its own database connection, its own query state, and its own transaction — completely isolated from every other thread.

```java
private static final ThreadLocal<Session> threadSession =
    ThreadLocal.withInitial(() -> new Session());
```

These two patterns together make `DataManager` both **globally accessible** and **thread-safe** without requiring any synchronization locks on individual operations.

---

## 2. The `Session` Inner Class

The `Session` is a private static final inner class of `DataManager`. Every thread has its own `Session` instance. It holds all state that belongs to **one thread's current transaction**:

```java
private static final class Session {
    Connection connection = null;   // active JDBC connection for this thread
    String qStatement     = "";     // query string being built (for query/select API)
    Class<?> qClass       = null;   // POJO class being queried
    boolean whereUsed     = false;  // tracks if WHERE has been added (prevents double WHERE)
    boolean orderByUsed   = false;  // tracks if ORDER BY has been added (prevents duplicates)
    StatementDS qStatementDS = null; // (reserved, used internally)
}
```

| Field | Purpose |
|---|---|
| `connection` | The live JDBC `Connection` for this thread. Created by `begin()`, closed by `end()`. |
| `qStatement` | Accumulates the SQL string when using the fluent query API (`query()`, `where()`, `eq()`, etc.). |
| `qClass` | The POJO class associated with the current query — used by `fire()` to know what type to instantiate. |
| `whereUsed` | Set to `true` when `where()` is first called. Subsequent `where()` calls append `AND` instead of `WHERE`. |
| `orderByUsed` | Set to `true` when `orderBy()` is called. A second `orderBy()` throws a `DataException`. |
| `qStatementDS` | Reserved field — not actively used in the current implementation. |

> [!NOTE]
> `reset()` clears `qStatement`, `qClass`, `whereUsed`, `orderByUsed`, and `qStatementDS` — but it does **not** close the connection. It is called automatically at the end of `fire()` and `view()` to clean up the query state after execution. It can also be called manually between two query chains within the same `begin()`/`end()` block.

---

## 3. Static-Level Data Structures

Besides the `ThreadLocal<Session>`, `DataManager` holds two **class-level (static) data structures** that are shared across all threads — built once during initialization and then only read, never modified:

```java
// Pre-built SQL + Method references for every POJO class
private static Map<Class<?>, Map<String, StatementDS>> statements = new HashMap<>();

// In-memory cache for @Cacheable tables (keyed by PK value)
private static Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();
```

### `statements` Map
- **Outer key**: The POJO `Class<?>` object (e.g., `Student.class`, `Course.class`).
- **Inner key**: A string operation name (e.g., `"insert"`, `"update"`, `"delete"`, `"select"`, `"get_by_primary_key"`, `"unique_key_validation"`, etc.).
- **Value**: A `StatementDS` object — contains the pre-built SQL string and all the pre-resolved `Method` references.

### `cache` Map
- **Outer key**: The POJO `Class<?>` — only populated for `@Cacheable` classes.
- **Inner key**: The primary key value (e.g., an `Integer` course code).
- **Value**: The POJO instance stored in memory.

Both maps are populated entirely during `initialize()` and are **read-only** at runtime (except that the `cache` map's inner maps are mutated by `save()`, `update()`, `delete()` to keep them in sync).

---

## 4. Method: `initialize(File parentWorkingDirectory)`

### Signature
```java
public static synchronized void initialize(File parentWorkingDirectory) throws DataException
```

### What It Does
This is the **single most important method** in the entire framework. It is called exactly **once** during application startup. It does everything needed to make the framework ready to use. It is `synchronized` to prevent two threads from accidentally calling it simultaneously.

### Internal Flow — Step by Step

```
1. Guard checks:
   - If already initialized → throws DataException("Already initialized, can not call again")
   - If parentWorkingDirectory is null → throws DataException
   - If the directory doesn't exist or isn't a directory → throws DataException

2. Stores parentWorkingDirectory as a static field.

3. Creates the single DataManager instance → new DataManager()
   (This triggers the private constructor which does all the heavy work below)

4. Inside the private DataManager() constructor:

   a. Reads conf.json from parentWorkingDirectory:
      - Loads: jdbc-driver, connection-url, username, password, package-name
      - Calls Class.forName(jdbcDriver) to verify driver availability

   b. Scans src/ folder → loadAllPojoClassesToDS(tables, views):
      - Recursively walks all .class files under src/
      - For each .class file, derives the fully qualified class name from the
        relative path (e.g., "your/package/Student.class" → "your.package.Student")
      - Calls Class.forName(className) to load the class
      - Calls ORMDataModel.getInfo(objClass) → builds TableSchema or ViewSchema
        from the class's annotations (reads @Table/@View, @Column, @PrimaryKey, etc.)
      - Adds to the appropriate list (tables or views)

   c. Opens one JDBC connection for setup:
      Connection connection = DriverManager.getConnection(...)
      DatabaseMetaData dbMetaData = connection.getMetaData()

   d. For each TableSchema (i.e., each @Table POJO found):
      For each field in the table:
        - Queries DatabaseMetaData for the column's SQL type
          → dbMetaData.getColumns(null, null, tableName, columnName)
        - Looks up the exact PreparedStatement setter method for that SQL type
          → JDBCMethodExtractor.getJDBCSetter(sqlType)
        - Looks up the exact ResultSet getter method for that SQL type
          → JDBCMethodExtractor.getJDBCGetter(sqlType)
        - Looks up the POJO's getter method: objClass.getMethod("getXxx")
        - Looks up the POJO's setter method: objClass.getMethod("setXxx", type)

      Builds the following StatementDS objects for that table:
        · insertStatementDS       → "INSERT INTO table SET col1=?, col2=?, ..."
        · updateStatementDS       → "UPDATE table SET col1=?, col2=?, ... WHERE pk=?"
        · deleteStatementDS       → "DELETE FROM table WHERE pk=?"
        · primaryKeyValidation    → "SELECT pk FROM table WHERE pk=?"
        · getByPrimaryKey         → "SELECT * FROM table WHERE pk=?"
        · uniqueKeyValidation     → "SELECT col FROM table WHERE col=?;"  (per unique col)
        · uniqueAndPrimaryKeyValidation → "SELECT col FROM table WHERE col=? AND pk<>?;"
        · getByUniqueKey          → "SELECT * FROM table WHERE col=?;"
        · foreignKeyValidation    → "SELECT parent_col FROM parent WHERE parent_col=?;"
        · getByForeignKey         → "SELECT * FROM table WHERE fk_col=?;"
        · selectStatement         → "SELECT * FROM table"

      Stores all StatementDS in: statements.get(objClass) map

      If the table is @Cacheable and has a @PrimaryKey:
        - Executes the selectStatement immediately (SELECT * FROM table)
        - Iterates all rows, populates POJO instances
        - Stores them in: cache.get(objClass) → LinkedHashMap<PK, POJO>

   e. For each ViewSchema (i.e., each @View POJO found):
      For each field:
        - Queries DatabaseMetaData for the column's SQL type
        - Resolves ResultSet getter method + POJO setter method
      Builds only:
        · selectStatement  → "SELECT * FROM viewName"
      Stores in: statements.get(objClass) map

   f. Closes the setup JDBC connection.
```

### After `initialize()` Completes

- `DataManager.isInitialized()` returns `true`.
- `DataManager.getDataManager()` returns the singleton instance.
- `statements` map contains pre-built SQL + reflection data for every POJO.
- `cache` map contains loaded data for every `@Cacheable` POJO.
- **No database connection is held open** — the setup connection is closed. Each thread will create its own connection on `begin()`.

---

## 5. `loadAllPojoClassesToDS()` — How POJOs Are Discovered

This private method is what allows `DataManager` to find your POJO classes **without you listing them manually**. It uses the file system, not a package scan.

```
loadAllPojoClassesToDS(tables, views)
  └── loadFiles(srcFolder, srcFolder, tables, views)
        └── For each file in src/ (recursively):
              If file ends with ".class":
                relativePath = srcFolder.toURI().relativize(file.toURI()).getPath()
                // e.g., "your/package/name/Student.class"
                className = relativePath.replace(".class","").replace("/",".")
                // e.g., "your.package.name.Student"
                objClass = Class.forName(className)
                schema = ORMDataModel.getInfo(objClass)
                  → reads @Table or @View annotation
                  → builds TableSchema / ViewSchema
                  → reads all @Column fields
                if schema is TableSchema → add to tables list
                if schema is ViewSchema  → add to views list
```

> [!IMPORTANT]
> `DataManager.initialize()` does not use `package-name` from `conf.json`. It scans the **entire `src/` folder** recursively. This means all POJO classes from all packages under `src/` are automatically discovered — no registration or listing required.

> [!NOTE]
> Classes without `@Table` or `@View` are silently skipped. Classes with annotation errors throw `DataException` internally, which is caught and ignored — initialization continues with the remaining classes.

---

## 6. Method: `isInitialized()`

### Signature
```java
public static boolean isInitialized()
```

Returns `true` if `initialize()` has been called successfully, `false` otherwise.

### Typical Use
Used to guard against calling `initialize()` more than once — or to check whether `DataManager` is ready before calling `getDataManager()`.

```java
if (!DataManager.isInitialized()) {
    DataManager.initialize(new File("/path/to/config/dir"));
}
DataManager dm = DataManager.getDataManager();
```

---

## 7. Method: `getDataManager()`

### Signature
```java
public static DataManager getDataManager() throws DataException
```

Returns the singleton `DataManager` instance. Throws `DataException("Must call initialize along with parent working directory")` if called before `initialize()`.

```java
DataManager dm = DataManager.getDataManager();
```

---

## 8. Method: `begin()`

### Signature
```java
public void begin() throws DataException
```

### What It Does
Opens a **new JDBC connection** for the current thread and stores it in the thread's `Session`.

```java
public void begin() throws DataException {
    Session s = session();
    // If there's already an open connection on this thread, close it first
    if (s.connection != null && !s.connection.isClosed()) s.connection.close();
    reset();   // clear any leftover query state
    s.connection = DriverManager.getConnection(connectionURL, username, password);
}
```

**Key behaviours:**
- If `begin()` is called while a connection is already open on this thread, the existing connection is **closed first** and a fresh one is opened. This is a safety mechanism.
- `reset()` is called automatically — clearing any unfinished query chain from a previous operation.
- Each call to `begin()` creates a brand-new connection — no connection pooling.

> [!WARNING]
> Every `begin()` must be paired with a corresponding `end()`. Failing to call `end()` leaves the database connection open for that thread indefinitely, causing a **connection leak**.

---

## 9. Method: `end()`

### Signature
```java
public void end()
```

### What It Does
Closes the current thread's connection and **fully removes** the `Session` from the `ThreadLocal`:

```java
public void end() {
    Session s = session();
    try {
        if (s.connection != null) s.connection.close();
    } catch (SQLException sqlException) { /* silently ignored */ }
    reset();
    threadSession.remove();   // removes Session from ThreadLocal entirely
}
```

**Key behaviours:**
- The JDBC `Connection` is closed — any uncommitted changes are rolled back by the database (since ORMFM does not use explicit transactions / auto-commit is the default).
- `threadSession.remove()` is critical for **thread pool environments** (like Tomcat): it prevents the `Session` from leaking to the next request handled by the same thread.
- All query state (`qStatement`, `qClass`, etc.) is cleared.

---

## 10. Method: `reset()`

### Signature
```java
public void reset()
```

### What It Does
Clears only the **query state** of the current thread's `Session` — without closing the connection:

```java
public void reset() {
    Session s = session();
    s.qStatement = "";
    s.qClass     = null;
    s.whereUsed  = false;
    s.orderByUsed = false;
    s.qStatementDS = null;
}
```

`reset()` is called:
- Automatically by `begin()` at the start of each transaction.
- Automatically by `fire()` after a query executes.
- Automatically by `view()` after a view query executes.
- Manually by the developer between two query chains **within the same session**.

> [!NOTE]
> `reset()` does **not** close the connection. Use it only to clear an unfinished query chain and start a new one, within an active `begin()`/`end()` block.

---

## 11. The Thread Lifecycle — Complete Picture

```
Thread starts (e.g., incoming HTTP request to a servlet)
│
├── DataManager dm = DataManager.getDataManager();
│     └── Returns the singleton — no thread-specific state yet
│
├── dm.begin()
│     ├── Session s = threadSession.get()   ← creates new Session for this thread
│     ├── s.connection = DriverManager.getConnection(...)   ← new DB connection
│     └── reset()   ← clear any leftover state
│
├── dm.save(obj) / dm.update(obj) / dm.delete(...) / dm.query().fire() / etc.
│     └── All use: session().connection   ← this thread's own connection
│
├── dm.end()
│     ├── s.connection.close()   ← connection returned to DB
│     ├── reset()
│     └── threadSession.remove()   ← Session destroyed for this thread
│
Thread ends (or is returned to pool for next request)
 └── No Session object remains — clean slate for next use
```

### Multiple `begin()`/`end()` in One Thread

A single thread can call `begin()` and `end()` multiple times — each pair is a **separate transaction**:

```java
DataManager dm = DataManager.getDataManager();

// Transaction 1
dm.begin();
dm.save(courseObj);
dm.end();

// Transaction 2
dm.begin();
List<Course> courses = (List<Course>) dm.query(Course.class).fire();
dm.end();

// Transaction 3
dm.begin();
dm.update(studentObj);
dm.end();
```

Each `begin()` opens a fresh connection; each `end()` closes it. This is fine and expected.

---

## 12. Correct Usage Pattern — try-finally Safety

In production code, always wrap `begin()`/`end()` in a `try-finally` to guarantee the connection is closed even when an exception occurs:

```java
DataManager dm = DataManager.getDataManager();

dm.begin();
try {
    dm.save(student);
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();   // ALWAYS called — prevents connection leaks
}
```

Without `finally`, an exception inside the `try` block would skip `end()`, leaving the connection open permanently for that thread.

---

## 13. Using DataManager in Tomcat (Web Application)

### Recommended: `ServletContextListener`

In a Tomcat web application, `DataManager.initialize()` should be called **once** when the web app starts, using a `ServletContextListener`. This ensures the framework is ready before the first HTTP request arrives.

**Step 1 — Create a listener class:**
```java
import javax.servlet.*;
import java.io.*;
import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;

public class AppStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Path to the folder containing conf.json and src/
            File configDir = new File(sce.getServletContext().getRealPath("/WEB-INF"));
            DataManager.initialize(configDir);
            System.out.println("DataManager initialized successfully.");
        } catch (DataException de) {
            System.out.println("DataManager initialization failed: " + de.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup if needed
    }
}
```

**Step 2 — Register it in `web.xml`:**
```xml
<listener>
    <listener-class>AppStartupListener</listener-class>
</listener>
```

> [!IMPORTANT]
> `ORMFMStarter.java` exists in the framework as an earlier approach to servlet-based startup. It is **not recommended** — use `DataManager.initialize(File)` directly in your own `ServletContextListener` as shown above. This works in any servlet container, not just Tomcat.

**Step 3 — Use in any Servlet:**
```java
public class CourseServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        try {
            DataManager dm = DataManager.getDataManager();
            dm.begin();
            List<Course> courses = (List<Course>) dm.query(Course.class).fire();
            dm.end();
            // ... send response
        } catch (DataException de) {
            // handle error
        }
    }
}
```

Each HTTP request runs in its own thread. Because `DataManager` uses `ThreadLocal<Session>`, each request gets its own independent database connection — no conflicts between concurrent requests.

---

## 14. Using DataManager in a Standalone Java Application

For non-web use (command-line tools, batch processing, testing), initialization is simpler:

```java
import java.io.*;
import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;

public class MyApp {
    public static void main(String[] args) {
        try {
            // Point to the folder containing conf.json and src/
            DataManager.initialize(new File(System.getProperty("user.dir")));
            DataManager dm = DataManager.getDataManager();

            dm.begin();
            // ... do work ...
            dm.end();

        } catch (DataException de) {
            System.out.println("Error: " + de.getMessage());
        }
    }
}
```

This is exactly the pattern used in all the `testing/*.java` examples.

---

## 15. All `DataException` Messages During Initialization

| Exception Message | When Thrown |
|---|---|
| `"Already initialized, can not call again"` | `initialize()` called a second time |
| `"Configuration file contains directory required"` | `parentWorkingDirectory` is `null` |
| `"Configuration file contains directory required"` | Provided path does not exist or is not a directory |
| `"Configuration(conf.json) file required"` | `conf.json` not found inside the provided directory |
| `"Invalid json configuration file"` | `conf.json` cannot be parsed as valid JSON |
| `"Invalid JDBC driver: <class>"` | The `jdbc-driver` class cannot be loaded |
| `"Must call initialize along with parent working directory"` | `getDataManager()` called before `initialize()` |
| `"No source file available to create JAR file."` | `src/` folder missing inside the provided directory |
| `"@Cacheable not allowed on the pojo, which does not have any primary key set."` | A `@Cacheable` class has no `@PrimaryKey` field |
| `"Multiple primary key are not allowed"` | A `@Table` POJO has more than one `@PrimaryKey` field |

---

## 16. Summary — Key Rules for DataManager Lifecycle

| Rule | Detail |
|---|---|
| **Call `initialize()` once** | Subsequent calls throw an exception |
| **Call `getDataManager()` only after `initialize()`** | Throws if called before |
| **Always pair `begin()` with `end()`** | Use `try-finally` in production code |
| **Each thread has its own connection** | `ThreadLocal<Session>` guarantees isolation |
| **`end()` must be called in thread-pool environments** | `threadSession.remove()` prevents session leaks |
| **`reset()` does not close the connection** | Use only to clear an unfinished query chain |
| **`initialize()` builds everything once** | Reflection, SQL statements, cache — all done at startup |
| **`src/` must contain compiled `.class` files** | `DataManager` scans `.class` files, not `.java` files |

---

*End of Part 4 — Please review and confirm to proceed to Part 5.*

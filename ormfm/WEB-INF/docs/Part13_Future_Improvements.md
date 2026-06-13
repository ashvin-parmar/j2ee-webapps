# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 13 — Future Improvements & Architecture Evolution

---

> [!NOTE]
> This is an official record of planned architectural improvements for the ORMFM framework. These modifications address known code smells (such as parallel lists) and hardcoded paths to make the framework more robust, object-oriented, and flexible for future development.

---

## 1. Data Structure Consolidation: `FieldSchema` and `StatementDS`

### Current Implementation Flaw (The "Parallel Lists" Smell)
Currently, column metadata is stored in `FieldSchema`, while the Java Reflection `Method` instances required to read and write those columns are stored separately inside `StatementDS` as parallel lists:
```java
// Inside StatementDS
private List<Method> jdbcSetterMethods;
private List<Method> classGetterMethods;
private List<Method> jdbcGetterMethods;
private List<Method> classSetterMethods;
private List<Integer> statementParamsType;
```
When executing a statement, the framework relies on the index `i` matching perfectly across all these lists to correctly map a POJO field to a JDBC parameter. This is fragile and makes it difficult to retrieve specific column methods dynamically (e.g., when reading directly from the in-memory cache).

### Proposed Solution: Enriched `FieldSchema`
The correct architectural move is to encapsulate the method references directly inside the field they belong to.

We will add the following properties to `FieldSchema`:

```java
// ── Statement Execution Context ──
private int sqlType;
private String sqlTypeName;
private Method setterClassMethod;  // POJO setXxx()
private Method getterClassMethod;  // POJO getXxx()
private Method setterJDBCMethod;   // PreparedStatement.setXxx()
private Method getterJDBCMethod;   // ResultSet.getXxx()

// ── Additional Column Constraints ──
private boolean isNullAllowed;
private int sizeConstraint;
```

### Impact on `StatementDS`
By enriching `FieldSchema`, `StatementDS` will no longer need 6 parallel lists. Instead, `StatementDS` will simply hold a list of the fields involved in that specific SQL statement:

```java
public class StatementDS {
    private StringBuilder statement;
    private boolean isQuery;
    private List<FieldSchema> activeFields; // Replaces all 6 parallel lists
}
```
During query execution, the loop becomes much safer and highly readable:
```java
for (int i = 0; i < activeFields.size(); i++) {
    FieldSchema fs = activeFields.get(i);
    Object pojoValue = fs.getGetterClassMethod().invoke(pojo);
    Object jdbcValue = JDBCMethodExtractor.convertToJDBC(fs.getSqlType(), pojoValue);
    fs.getSetterJDBCMethod().invoke(preparedStatement, i + 1, jdbcValue);
}
```

---

## 2. Configuration Improvements (`conf.json`)

### Current Implementation Flaw (Hardcoded Project Layout)
Currently, `ORMFMTool` and `DataManager` rely on a strictly enforced directory layout (`src/`, `dist/`, `lib/`). `ORMFMTool.createJar()` hardcodes paths like `../lib/*` assuming the developer is running it from a specific folder relative to `lib`.

Additionally, `package-name` is required in `conf.json` even though `DataManager` doesn't use it (since `DataManager` recursively scans the entire `src` folder for `.class` files).

### Proposed Solution: Path Configuration in `conf.json`
We will expose the directory layout configuration directly in `conf.json` so the framework can be embedded into any project structure.

```json
{
    "jdbc-driver"      : "com.mysql.cj.jdbc.Driver",
    "connection-url"   : "jdbc:mysql://localhost:3306/school",
    "username"         : "user",
    "password"         : "pass",

    "src_folder_path"  : "./src",
    "dist_folder_path" : "./dist",
    "lib_folder_path"  : "./lib",

    "package-name"     : "testing.school.pojo"
}
```

### Impact
1. **Flexibility:** Developers can place their source code in `app/src/java` or compile to `target/classes` by just changing `conf.json`.
2. **Clarity of Purpose:** The documentation will clearly state that `package-name` is **only** consumed by `ORMFMTool` to generate the `package` declaration in new `.java` files, whereas `DataManager` relies purely on `src_folder_path` for its recursive scanning.

---

## 3. Advanced Architectural Improvements (Long-Term Vision)

If this framework were to be evolved closer to industry-standard tools like Hibernate, these are the core architectural additions that would be necessary:

### A. Connection Pooling Integration
Currently, `dm.begin()` calls `DriverManager.getConnection()` to open a brand-new TCP connection to the database. This is very expensive and limits application throughput.
**Improvement:** Integrate a connection pool (like HikariCP or C3P0). `begin()` would simply borrow an existing, open connection from the pool, and `end()` would return it rather than closing it.

### B. Parameterized Query API (SQL Injection Prevention)
In the current fluent API (`select().where("name").eq(value).fire()`), the `value` is formatted as a string and concatenated directly into the SQL statement via `StringBuilder`. This is vulnerable to SQL injection if the value comes from user input.
**Improvement:** The query builder should store the actual values in a `List<Object> parameters`, build a SQL string with `?` placeholders (`WHERE name = ?`), and then use `PreparedStatement.setXxx()` in `fire()` to safely bind the parameters.

### C. Explicit Transaction Management
Currently, every `save()`, `update()`, and `delete()` executes as a single auto-committed transaction. If a developer needs to perform three inserts and roll them all back if the third one fails, they cannot.
**Improvement:** Add explicit transaction controls. Set `connection.setAutoCommit(false)` during `begin()`, and add `dm.commit()` and `dm.rollback()` methods. `end()` would either auto-commit or rollback uncommitted changes before closing.

### D. Thread-Safe Cache Concurrency
For `@Cacheable` tables, writing to the cache (`LinkedHashMap.put()`) during `save()` or `update()` from multiple threads concurrently can throw a `ConcurrentModificationException`.
**Improvement:** Replace `LinkedHashMap` with a custom thread-safe ordered map, or wrap the cache access in explicit `ReadWriteLock` synchronization blocks so multiple threads can read simultaneously, but writes safely block reads.

### E. Object Graph Navigation (Lazy Loading)
Currently, if a `Student` has a `course_code` FK, you get the integer ID. To get the `Course` title, you either write a View or query the Course table manually.
**Improvement:** Introduce Proxy objects and annotations like `@OneToOne` or `@ManyToOne`. Calling `student.getCourse()` would return a Proxy that dynamically fires a `SELECT * FROM course WHERE code=?` only when `course.getTitle()` is actually called (Lazy Loading).

---
*End of Future Improvements documentation.*

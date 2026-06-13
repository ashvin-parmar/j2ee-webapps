# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 6 — DataManager: Query API

---

> [!NOTE]
> This is **Part 6** of the ORMFM documentation. It covers the complete read/query API of `DataManager` — both the fluent chainable query builder and the direct view retrieval methods.

---

## 1. Overview — Two Query Styles

ORMFM provides two distinct styles of reading data:

### Style A — Fluent Query Builder (Tables)
Build a SQL `SELECT` query step-by-step using chained method calls, then execute it with `fire()`.

```java
// SELECT first_name, last_name FROM student WHERE first_name = 'Rohit' ORDER BY last_name
List<Student> students = (List<Student>) dm
    .select(Student.class, new String[]{"first_name", "last_name"})
    .where("first_name").eq("Rohit")
    .orderBy("last_name")
    .fire();
```

### Style B — Direct View Query (Views)
Execute a `SELECT * FROM view` immediately using either `select(ViewClass).fire()` or `view(ViewClass)`.

```java
// SELECT * FROM v_student_course
List<V1> results = (List<V1>) dm.view(V1.class);
```

---

## 2. How the Fluent Builder Works Internally

All fluent query methods operate on the **current thread's `Session`**. Each method appends to `session().qStatement` — a plain `String` that accumulates the SQL:

```
dm.query(Student.class)
  → session().qClass     = Student.class
  → session().qStatement = "SELECT * FROM student"

dm.where("first_name")
  → session().qStatement = "SELECT * FROM student WHERE first_name"
  → session().whereUsed  = true

dm.eq("Rohit")
  → session().qStatement = "SELECT * FROM student WHERE first_name='Rohit'"

dm.orderBy("last_name")
  → session().qStatement = "SELECT * FROM student WHERE first_name='Rohit' ORDER BY last_name"
  → session().orderByUsed = true

dm.fire()
  → Executes the qStatement as a PreparedStatement
  → Returns List<Student>
  → Calls reset() to clear session state
```

---

## 3. Method: `query(Class objClass)`

### Signature
```java
public DataManager query(Class objClass) throws DataException
```

### What It Does
Starts a fluent query for a **`@Table`-backed POJO** with `SELECT *`. Sets up the thread's query state and returns `this` for chaining.

### Internal Behaviour
```java
session().qClass     = objClass;
session().qStatement = "SELECT * FROM " + tableSchema.getTableName();
return this;
```

### Rules
- **Tables only** — if the class is a `@View` POJO, throws `DataException("Invalid data provided, Table required")`.
- Requires a subsequent call to `fire()` to execute.
- Does **not** require `begin()` to be called before `query()` itself — but `fire()` will fail without an active connection.

### Example
```java
dm.begin();
List<Student> all = (List<Student>) dm.query(Student.class).fire();
dm.end();
```

---

## 4. Method: `select(Class objClass, String[] columns)`

### Signature
```java
public DataManager select(Class objClass, String[] columns) throws DataException
```

### What It Does
Starts a fluent query for a **`@Table`-backed POJO** with **specific named columns** — equivalent to `SELECT col1, col2, ... FROM table`. Only the specified columns will be included in the SQL; unspecified fields will remain at their Java default values in the returned POJO.

### Internal Behaviour
```java
// Joins the column names into a comma-separated string
// e.g., new String[]{"first_name","last_name"} → "first_name, last_name"
session().qClass     = objClass;
session().qStatement = "SELECT first_name, last_name FROM student";
return this;
```

### Rules
- **Tables only** — same restriction as `query()`.
- Column names must be **exact database column names** (not Java field names). Invalid column names will cause `fire()` to throw `DataException("Invalid statement provided to fire()")` when the SQL executes.
- The returned POJO objects will only have the selected columns populated. All other fields remain at their default Java values (`null`, `0`, `false`, etc.).

### Example
```java
dm.begin();
List<Student> results = (List<Student>) dm
    .select(Student.class, new String[]{"first_name", "last_name"})
    .where("first_name").eq("Rohit")
    .fire();
dm.end();

// rollNumber, gender, courseCode etc. will be 0/null
// only firstName and lastName are populated
```

---

## 5. Method: `where(String columnName)`

### Signature
```java
public DataManager where(String columnName)
```

### What It Does
Appends either `WHERE` or `AND` to the query — automatically determined by whether `WHERE` has already been used in this query chain.

### Internal Behaviour
```java
// First call to where():
session().qStatement += " WHERE " + columnName;
session().whereUsed = true;

// Subsequent calls to where() (used after and() / or()):
session().qStatement += " " + columnName;
// (and() / or() already appended "AND" / "OR" before this call)
```

> [!IMPORTANT]
> `where()` on its own only appends the column name. It must be **immediately followed** by a comparison method (`eq()`, `gt()`, `lt()`, etc.) to form a complete condition. Calling `where("col").and()` without a comparison in between produces invalid SQL that will fail at `fire()`.

### Multiple Conditions

The correct pattern for multiple `WHERE` conditions uses `and()` or `or()` between `where()` chains:
```java
dm.query(Student.class)
  .where("first_name").eq("Rohit")   // first condition
  .and()
  .where("course_code").eq(22)       // second condition
  .fire();
// → SELECT * FROM student WHERE first_name='Rohit' AND course_code=22
```

On the **second** `where()` call (after `and()`), `whereUsed` is already `true`, so the method appends only the column name (not `WHERE` again) — because `and()` already added `AND`.

---

## 6. Comparison Operators

All comparison methods append the operator and the **formatted value** to the query string. They all return `this` for chaining.

### `eq(Object value)` — Equals
```java
public DataManager eq(Object value)
// Appends: = <formattedValue>
// Example: .where("first_name").eq("Rohit") → WHERE first_name='Rohit'
//          .where("course_code").eq(22)     → WHERE course_code=22
```

### `gt(Object value)` — Greater Than
```java
public DataManager gt(Object value)
// Appends: > <formattedValue>
// Example: .where("roll_number").gt(10000) → WHERE roll_number>10000
```

### `lt(Object value)` — Less Than
```java
public DataManager lt(Object value)
// Appends: < <formattedValue>
// Example: .where("roll_number").lt(20000) → WHERE roll_number<20000
```

### `ge(Object value)` — Greater Than or Equal To
```java
public DataManager ge(Object value)
// Appends: >= <formattedValue>
// Example: .where("roll_number").ge(10001) → WHERE roll_number>=10001
```

### `le(Object value)` — Less Than or Equal To
```java
public DataManager le(Object value)
// Appends: <= <formattedValue>
// Example: .where("roll_number").le(19999) → WHERE roll_number<=19999
```

### `ne(Object value)` — Not Equal To
```java
public DataManager ne(Object value)
// Appends: != <formattedValue>    (internally uses !=, equivalent to <>)
// Example: .where("gender").ne("F") → WHERE gender!='F'
```

---

## 7. How Values Are Formatted — `formatValue()`

All comparison operators pass their value through `formatValue()` before appending to the SQL string:

```java
private String formatValue(Object value) {
    if (value == null)                  return "null";
    if (value instanceof String)        return "'" + value + "'";
    if (value instanceof java.util.Date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return "'" + sdf.format(value) + "'";
    }
    return String.valueOf(value);        // numbers, booleans etc.
}
```

| Java Type | Example Input | Formatted Output in SQL |
|---|---|---|
| `null` | `null` | `null` |
| `String` | `"Rohit"` | `'Rohit'` |
| `int` / `Integer` | `22` | `22` |
| `long` / `Long` | `100000L` | `100000` |
| `double` / `Double` | `3.14` | `3.14` |
| `boolean` / `Boolean` | `true` | `true` |
| `java.util.Date` | Date object | `'2001-06-15'` |
| `java.sql.Date` | SQL Date object | `'2001-06-15'` |

> [!WARNING]
> String values are embedded directly into the SQL string (not as `PreparedStatement` parameters). This means `formatValue()` does **not** protect against SQL injection for dynamically constructed queries. For user-supplied string values in production systems, exercise caution with the query API. The CRUD operations (`save`, `update`, `delete`) always use `PreparedStatement` with `?` placeholders safely.

---

## 8. Method: `and()`

### Signature
```java
public DataManager and()
```

### What It Does
Appends ` AND ` to the query string. Used between two `where()` conditions.

```java
session().qStatement += " AND ";
return this;
```

### Example
```java
dm.query(Student.class)
  .where("first_name").eq("Rohit")
  .and()
  .where("course_code").eq(22)
  .fire();
// → SELECT * FROM student WHERE first_name='Rohit' AND course_code=22
```

---

## 9. Method: `or()`

### Signature
```java
public DataManager or()
```

### What It Does
Appends ` OR ` to the query string. Used between two `where()` conditions.

```java
session().qStatement += " OR ";
return this;
```

### Example
```java
dm.query(Student.class)
  .where("gender").eq("M")
  .or()
  .where("gender").eq("F")
  .fire();
// → SELECT * FROM student WHERE gender='M' OR gender='F'
```

---

## 10. Method: `orderBy(String columnName)`

### Signature
```java
public DataManager orderBy(String columnName) throws DataException
```

### What It Does
Appends ` ORDER BY <columnName>` to the query string.

```java
session().qStatement += " ORDER BY " + columnName;
session().orderByUsed = true;
return this;
```

### Rules
- **Only one `orderBy()` per query chain** — calling it a second time throws `DataException("Invalid statement, can't use multiple 'ORDER BY' in one statement")`. The `orderByUsed` flag in `Session` enforces this.
- Column name must be an exact database column name.
- Ascending order only (`ASC`) — there is no `DESC` option in the current implementation.
- `orderBy()` must come **after** all `where()` conditions in the chain.

### Example
```java
dm.select(Student.class, new String[]{"first_name","roll_number","last_name"})
  .where("first_name").eq("Rohit")
  .orderBy("last_name")
  .fire();
// → SELECT first_name, roll_number, last_name FROM student
//   WHERE first_name='Rohit' ORDER BY last_name
```

---

## 11. Method: `fire()`

### Signature
```java
public Object fire() throws DataException
```

Returns `Object` — cast to `List<YourPojoClass>` in usage.

### What It Does
Executes the SQL query that was built up by the preceding `query()`/`select()`, `where()`, `eq()`, etc. calls. Returns a `List` of populated POJO instances.

### Prerequisites
- `begin()` must have been called — throws `DataException("Call begin() before fire()")`.
- `query()` or `select()` must have been called first — throws `DataException("Call query() before fire()")` if `qClass` is null.

### Complete Internal Flow

```
1. Gets the current qStatement from session()
   e.g., "SELECT first_name, last_name FROM student WHERE first_name='Rohit'"

2. Gets qClass from session()
   e.g., Student.class

3. Retrieves the schema: ORMDataModel.getInfo(qClass)
   → TableSchema with all FieldSchema entries

4. Creates PreparedStatement from qStatement
   → connection.prepareStatement(qStatement)

5. Executes the query → ResultSet

6. For each row in ResultSet:
   a. Creates a new POJO instance: qClass.getDeclaredConstructor().newInstance()
   b. For each FieldSchema in the schema:
      - Gets the column value: resultSet.getObject(fs.getColumnName())
      - If @SetterGetter: looks up the setter method, calls it with the value
      - If public field: uses Field.set() directly
      - If neither: skips this field silently

7. Adds each populated POJO to resultList

8. Closes ResultSet and PreparedStatement

9. Calls reset() — clears qStatement, qClass, whereUsed, orderByUsed

10. Returns resultList (as Object — caller casts to List<T>)
```

> [!NOTE]
> `fire()` reads values using `resultSet.getObject(columnName)` — using the **column name** string, not a positional index. This is what allows `select(Class, String[] columns)` with specific columns to work correctly: only the requested columns are in the ResultSet, and each `FieldSchema`'s `getColumnName()` is used to look up values by name.

### After `fire()` Returns
- The query state is **reset** (`qStatement = ""`, `qClass = null`, `whereUsed = false`, `orderByUsed = false`).
- The `connection` is **still open** — the thread is still in the `begin()`/`end()` block.
- You can immediately chain another `query()/select()...fire()` within the same `begin()`/`end()`.

### Return Value
- Returns a `List<Object>` (typed as `Object` in the signature for Java flexibility).
- Returns an **empty list** if no rows matched — never `null`.
- The caller must cast: `(List<YourClass>) dm.query(YourClass.class).fire()`.

### Example — Full query patterns
```java
DataManager dm = DataManager.getDataManager();

// 1. SELECT * — get everything
dm.begin();
List<Student> all = (List<Student>) dm.query(Student.class).fire();
dm.end();

// 2. SELECT * WHERE condition
dm.begin();
List<Student> rohits = (List<Student>) dm
    .query(Student.class)
    .where("first_name").eq("Rohit")
    .fire();
dm.end();

// 3. SELECT specific columns WHERE two conditions
dm.begin();
List<Student> filtered = (List<Student>) dm
    .select(Student.class, new String[]{"first_name","roll_number","last_name"})
    .where("first_name").eq("Rohit")
    .and()
    .where("course_code").eq(22)
    .fire();
dm.end();

// 4. SELECT * WHERE condition ORDER BY
dm.begin();
List<Student> ordered = (List<Student>) dm
    .select(Student.class, new String[]{"first_name","roll_number","last_name"})
    .where("first_name").eq("Rohit")
    .orderBy("last_name")
    .fire();
dm.end();

// 5. SELECT * — no filter, just order
dm.begin();
List<Course> courses = (List<Course>) dm
    .query(Course.class)
    .orderBy("title")
    .fire();
dm.end();

// 6. Numeric range query
dm.begin();
List<Student> highRolls = (List<Student>) dm
    .query(Student.class)
    .where("roll_number").gt(10050)
    .fire();
dm.end();
```

---

## 12. Method: `select(Class objClass)` — View Overload

### Signature
```java
public DataManager select(Class objClass) throws DataException
```

> [!IMPORTANT]
> This is a **different method** from `select(Class, String[])`. The single-argument `select(Class)` is specifically for **`@View` POJOs**. It sets up the query using the pre-built `StatementDS` for that view and returns `this` for chaining with `where()`, `orderBy()`, and `fire()`.

### What It Does
Sets up the query state for a view POJO. Unlike the table version, it uses the pre-built `selectStatement` from the `statements` map (which holds `SELECT * FROM viewName`).

### Rules
- **Views only** — if a `@Table` class is passed, throws `DataException("Invalid data provided, View required")`.
- Requires `begin()` to have been called — throws `DataException("Call begin() before select()")` if connection is null.
- Must be followed by `fire()` to execute.

### Example
```java
// SELECT * FROM v_student_course (view)
dm.begin();
List<V1> v1s = (List<V1>) dm.select(V1.class).fire();
dm.end();

// SELECT * FROM v4 ORDER BY first_name (view with ordering)
dm.begin();
List<V4> v4s = (List<V4>) dm.select(V4.class).orderBy("first_name").fire();
dm.end();
```

---

## 13. Method: `view(Class objClass)`

### Signature
```java
public Object view(Class objClass) throws DataException
```

Returns `Object` — cast to `List<YourViewClass>`.

### What It Does
A **simpler, immediate alternative** to `select(ViewClass).fire()`. Executes `SELECT * FROM view` directly — no chaining needed. Does not go through the query builder at all; uses the pre-built `StatementDS` directly.

### Difference from `select(ViewClass).fire()`

| Aspect | `select(ViewClass).fire()` | `view(ViewClass)` |
|---|---|---|
| Supports `where()` chaining? | Yes | No — executes immediately |
| Supports `orderBy()` chaining? | Yes | No — executes immediately |
| Uses pre-built StatementDS? | Yes (gets SQL from it) | Yes (uses getter + setter Methods from it) |
| Calls `reset()` after? | Yes (in `fire()`) | Yes |
| Returns all rows? | All rows (unless filtered by `where`) | Always all rows |

### Internal Behaviour
`view()` uses the pre-built `StatementDS` for the view and uses the stored JDBC getter and POJO setter `Method` references directly (the same approach used for `@Cacheable` loading at initialization time). This is slightly more efficient than `fire()` which uses `resultSet.getObject(columnName)` with string lookup.

### Example
```java
dm.begin();
List<V1> v1s = (List<V1>) dm.view(V1.class);
dm.end();

System.out.println("Count: " + v1s.size());
```

> [!NOTE]
> `view()` internally calls `reset()` at the end, same as `fire()`. The connection remains open after `view()` returns — you still need `end()` to close it.

---

## 14. All `DataException` Messages from the Query API

| Method | Message | Cause |
|---|---|---|
| `query()` | `"Invalid data provided, Data required"` | Class has no schema (no `@Table`/`@View`) |
| `query()` | `"Invalid data provided, Table required"` | Class is a `@View` POJO, not a `@Table` |
| `select(Class, String[])` | `"Invalid data provided, Data required"` | Class has no schema |
| `select(Class, String[])` | `"Invalid data provided, Table required"` | Class is a `@View` POJO |
| `select(Class)` | `"Invalid data provided, Data required"` | Class has no schema |
| `select(Class)` | `"Invalid data provided, View required"` | Class is a `@Table` POJO, not a `@View` |
| `select(Class)` | `"Call begin() before select()"` | `begin()` not called |
| `view()` | `"Invalid data provided, Data required"` | Class has no schema |
| `view()` | `"Invalid data provided, View required"` | Class is a `@Table` POJO |
| `view()` | `"Call begin() before view()"` | `begin()` not called |
| `orderBy()` | `"Invalid statement, can't use multiple 'ORDER BY' in one statement"` | `orderBy()` called twice |
| `fire()` | `"Call begin() before fire()"` | `begin()` not called |
| `fire()` | `"Call query() before fire()"` | `query()`/`select()` not called |
| `fire()` | `"Invalid data provided, Data required"` | `qClass` schema no longer valid |
| `fire()` | `"Invalid statement provided to fire()"` | SQL was malformed (e.g., invalid column name) |

---

## 15. Complete Fluent Chain Reference

Every valid chain pattern, from simplest to most complex:

```java
// Pattern 1 — All rows, all columns (table)
dm.query(Student.class).fire();

// Pattern 2 — All rows, all columns, ordered (table)
dm.query(Student.class).orderBy("last_name").fire();

// Pattern 3 — All rows, specific columns (table)
dm.select(Student.class, new String[]{"first_name","last_name"}).fire();

// Pattern 4 — Filtered rows, all columns (single condition)
dm.query(Student.class).where("gender").eq("M").fire();

// Pattern 5 — Filtered rows, all columns (two conditions with AND)
dm.query(Student.class)
  .where("first_name").eq("Rohit")
  .and()
  .where("course_code").eq(22)
  .fire();

// Pattern 6 — Filtered rows, all columns (two conditions with OR)
dm.query(Student.class)
  .where("gender").eq("M")
  .or()
  .where("gender").eq("F")
  .fire();

// Pattern 7 — Filtered rows, specific columns, ordered
dm.select(Student.class, new String[]{"first_name","roll_number"})
  .where("first_name").eq("Rohit")
  .orderBy("roll_number")
  .fire();

// Pattern 8 — Range query
dm.query(Student.class)
  .where("roll_number").ge(10001)
  .and()
  .where("roll_number").le(10100)
  .fire();

// Pattern 9 — View, all rows
dm.select(V1.class).fire();

// Pattern 10 — View, all rows, ordered
dm.select(V4.class).orderBy("first_name").fire();

// Pattern 11 — View, all rows (direct, no chaining)
dm.view(V1.class);
```

---

## 16. Anti-Patterns and What Goes Wrong

| Bad Pattern | Result |
|---|---|
| `dm.query(Student.class)` — no `fire()` | Query state left dangling in Session; next `begin()` clears it via `reset()` |
| `.where("col")` without a comparison operator | Invalid SQL like `WHERE col` — `fire()` throws `DataException` |
| `.and()` before `.where()` | SQL becomes `SELECT * FROM table AND ...` — invalid, `fire()` fails |
| `.orderBy("col").orderBy("col2")` | `DataException("Invalid statement, can't use multiple 'ORDER BY'")` |
| `dm.query(V1.class)` (view class passed to `query()`) | `DataException("Invalid data provided, Table required")` |
| `dm.select(Student.class)` (table class passed to view `select()`) | `DataException("Invalid data provided, View required")` |
| `fire()` without `begin()` | `DataException("Call begin() before fire()")` |
| `fire()` without `query()`/`select()` | `DataException("Call query() before fire()")` |
| Invalid column name in `select(cols[])` | SQL executes but `fire()` throws `DataException("Invalid statement provided to fire()")` |
| Calling `query()` twice without `fire()` | Second `query()` overwrites the first — silently loses the first chain |

---

*End of Part 6 — Please review and confirm to proceed to Part 7.*

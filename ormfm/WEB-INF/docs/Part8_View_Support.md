# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 8 — View Support

---

> [!NOTE]
> This is **Part 8** of the ORMFM documentation. It covers database view support from end to end — what views are, how `ViewSchema` differs from `TableSchema`, how `FieldSchema` represents individual columns, how the framework reads views, and how read-only enforcement works.

---

## 1. What is a Database View in ORMFM?

A **database view** is a named `SELECT` query stored in the database that behaves like a virtual table. Views are commonly used to:
- Join multiple tables and present a simplified combined result.
- Hide complex joins from the application layer.
- Expose only specific columns from underlying tables.

In ORMFM, views are supported as **read-only POJOs**. The workflow is identical to table POJOs at the Java level, but the framework enforces that no write operations are possible on them.

```
Database View (stored SQL query)         ORMFM View POJO (Java class)
────────────────────────────────         ────────────────────────────────────────
CREATE VIEW v_student_course AS          @View(name="v_student_course")
SELECT s.first_name,                     public class VStudentCourse {
       s.roll_number,                        @Column(name="first_name")
       c.title                               @SetterGetter
FROM student s                               private String firstName;
JOIN course c                                ...
ON s.course_code = c.code;               }
```

The developer **never writes SQL joins** in their Java code. They simply call `dm.view(VStudentCourse.class)` or `dm.select(VStudentCourse.class).fire()` and get back a populated list of objects.

---

## 2. The `Schema` Interface — Common Contract

Both `TableSchema` and `ViewSchema` implement the `Schema` interface:

```java
public interface Schema {
    boolean isCacheable();
    void setCacheable();
    Class<?> getObjectClass();
    String getName();
    void addField(FieldSchema fieldSchema);
    List<FieldSchema> getAllFields();
    FieldSchema getFieldByMethodName(String methodName);
    FieldSchema getFieldByColumnName(String columnName);
}
```

This common contract allows `DataManager` to treat both schema types uniformly where possible (e.g., `ORMDataModel.getInfo()` returns a `Schema`, and the caller casts to `TableSchema` or `ViewSchema` as needed).

---

## 3. `TableSchema` vs `ViewSchema` — Side-by-Side

Both classes implement `Schema` and hold the same core data. The differences are in the **extra methods** `TableSchema` provides and the **semantic meaning** of each:

| Aspect | `TableSchema` | `ViewSchema` |
|---|---|---|
| Java class | `TableSchema` | `ViewSchema` |
| Created from | `@Table(name="...")` annotation | `@View(name="...")` annotation |
| Stores | `tableName` | `viewName` |
| `getName()` returns | `tableName` | `viewName` |
| Extra method: `getTableName()` | ✅ Yes | ❌ No |
| Extra method: `getViewName()` | ❌ No | ✅ Yes |
| `getPrimaryKeyField()` | ✅ Yes | ❌ No |
| `isPrimaryKeyAutoIncremented()` | ✅ Yes | ❌ No |
| `getForeignKeyFields()` | ✅ Yes | ❌ No |
| `getAutoIncrementFields()` | ✅ Yes | ❌ No |
| `getNonAutoIncrementFields()` | ✅ Yes | ❌ No |
| `isCacheable()` field | ✅ Yes (usable) | ✅ Yes (field exists but unused in practice) |
| `FieldSchema` entries contain | Full constraint info (PK, FK, Unique, AutoIncrement) | Column name + type + setter/getter only |
| Statements built by `DataManager` | 10+ statement types (insert, update, delete, pk\_validation, etc.) | Only `"select"` statement |
| Write operations allowed? | ✅ `save()`, `update()`, `delete()` | ❌ None — throws `DataException` |
| Read operations supported | `query()`, `select(cols)`, `fire()`, `queryDS()` | `select(ViewClass).fire()`, `view()` |

---

## 4. `FieldSchema` — Complete Reference

`FieldSchema` is the internal representation of a **single mapped field** (column) within any POJO, whether table or view. It is the atomic unit of schema metadata.

### All 11 Properties

| Property | Type | Getter/Setter | Description |
|---|---|---|---|
| `methodName` | `String` | `getMethodName()` / `setMethodName()` | Java field name (camelCase). Used to derive getter/setter names (`get` + capitalized). |
| `columnName` | `String` | `getColumnName()` / `setColumnName()` | Exact database column name. Used in SQL statements. |
| `type` | `Class<?>` | `getType()` / `setType()` | Java type of the field (e.g., `java.lang.String`, `java.lang.Integer`). Used to look up the setter method. |
| `isPrimaryKey` | `boolean` | `isPrimaryKey()` / `setPrimaryKey()` | `true` if `@PrimaryKey` is present. Determines WHERE clause column. |
| `isAutoIncrement` | `boolean` | `isAutoIncrement()` / `setAutoIncrement()` | `true` if `@AutoIncrement` is present. Column excluded from INSERT. |
| `isForeignKey` | `boolean` | `isForeignKey()` / `setForeignKey()` | `true` if `@ForeignKey` is present. |
| `fkParentClass` | `String` | `getFKParentClass()` / `setFKParentClass()` | Parent table name from `@ForeignKey(parent=...)`. `null` if not a FK. |
| `fkParentColumn` | `String` | `getFKParentColumn()` / `setFKParentColumn()` | Parent column name from `@ForeignKey(column=...)`. `null` if not a FK. |
| `isUnique` | `boolean` | `isUnique()` / `setUnique()` | `true` if `@Unique` is present. Triggers uniqueness validation. |
| `isSetterAllowed` | `boolean` | `isSetterAllowed()` / `setSetterAllowed()` | `true` if `@SetterGetter` is present. Framework uses `setXxx()` to write this field. |
| `isGetterAllowed` | `boolean` | `isGetterAllowed()` / `setGetterAllowed()` | `true` if `@SetterGetter` is present. Framework uses `getXxx()` to read this field. |
| `isPublicAllowed` | `boolean` | `isPublicAllowed()` / `setPublicAllowed()` | `true` if field is declared `public`. Framework uses `Field.get()`/`Field.set()` directly. |

### `FieldSchema` for a Table vs View Field

For a `@Table` field with constraints:
```
FieldSchema {
  methodName='aadharCardNumber',
  columnName='aadhar_card_number',
  type=java.lang.String,
  isPrimaryKey=false,
  isAutoIncrement=false,
  isForeignKey=false,
  fkParentClass='null',
  fkParentColumn='null',
  isUnique=true,
  isSetterAllowed=true,
  isGetterAllowed=true,
  isPublicAllowed=false
}
```

For a `@View` field (always simpler):
```
FieldSchema {
  methodName='firstName',
  columnName='first_name',
  type=java.lang.String,
  isPrimaryKey=false,
  isAutoIncrement=false,
  isForeignKey=false,
  fkParentClass='null',
  fkParentColumn='null',
  isUnique=false,
  isSetterAllowed=true,
  isGetterAllowed=true,
  isPublicAllowed=false
}
```

All constraint flags remain `false` for view fields — they are never set during `createViewPojo()` or during `ORMDataModel.getInfo()` processing of a `@View` class.

---

## 5. `ORMDataModel` — The Schema Registry

`ORMDataModel` is a static class that acts as the **central schema registry** — a global map of every loaded POJO class to its schema object:

```java
// Internal registry (simplified)
private static Map<Class<?>, Schema> registry = new HashMap<>();

// Called during DataManager initialization for every discovered class
public static Schema getInfo(Class<?> objClass) { ... }

// Returns all TableSchema objects (used by referential integrity check)
public static List<TableSchema> getAllTableInfo() { ... }
```

### How `getInfo()` Builds a Schema

When `getInfo(objClass)` is called for the first time on a class, it:

1. Checks for `@Table` → creates `TableSchema(objClass, tableName)`.
2. Checks for `@View` → creates `ViewSchema(objClass, viewName)`.
3. If neither is found → returns `null`.
4. Iterates all declared fields on the class.
5. For each field with `@Column`:
   - Creates a `FieldSchema(methodName, columnName, fieldType)`.
   - Sets `isPrimaryKey` if `@PrimaryKey` present.
   - Sets `isAutoIncrement` if `@AutoIncrement` present.
   - Sets `isUnique` if `@Unique` present.
   - Sets `isForeignKey`, `fkParentClass`, `fkParentColumn` if `@ForeignKey` present.
   - Sets `isSetterAllowed` + `isGetterAllowed` if `@SetterGetter` present.
   - Sets `isPublicAllowed` if field modifier is `public`.
   - Calls `schema.addField(fieldSchema)`.
6. Checks for `@Cacheable` → calls `schema.setCacheable()`.
7. Stores the result in the registry.
8. Returns the schema.

The result is cached in the registry — subsequent calls to `getInfo(objClass)` return the cached schema instantly.

---

## 6. What `DataManager` Builds for Views — Only One Statement

At initialization time, for each `ViewSchema`, `DataManager` builds **only one `StatementDS`** and stores it under the key `"select"`:

```java
// For a view named "v_student_course":
StatementDS selectStatement = new StatementDS();
selectStatement.getStatement().append("SELECT * FROM v_student_course");
// + jdbcGetterMethods list (one per column)
// + classSetterMethods list (one per column)
// + resultParamsType list (SQL type integers)

statements.get(VStudentCourse.class).put("select", selectStatement);
```

Compare this to a table, which gets 10+ statement types. A view gets exactly **one**.

### The Single `StatementDS` for Views Contains

| Stored in `StatementDS` | What it holds |
|---|---|
| `getStatement()` | `StringBuilder("SELECT * FROM viewName")` |
| `getJDBCGetterMethods()` | List of `ResultSet.getXxx(int)` Method objects — one per column, in column order |
| `getClassSetterMethods()` | List of POJO's `setXxx(type)` Method objects — one per column, in column order |
| `getResultParamsType()` | List of SQL type integers (`Types.VARCHAR`, `Types.INTEGER`, etc.) — one per column |
| `getResultParamsCount()` | Total number of columns in the view |

---

## 7. Read-Only Enforcement — What Happens with Write Operations

When `save()`, `update()`, or `delete()` are called with a `@View` class, the `DataManager` detects this and throws immediately — no SQL is ever executed:

```java
// Inside save():
Schema s = ORMDataModel.getInfo(objClass);
if (s instanceof TableSchema) tableSchema = (TableSchema) s;
else throw new DataException("Invalid data provided, Data required");
// → ViewSchema instance does NOT pass the instanceof TableSchema check
//   → DataException thrown before any DB operation
```

The enforcement relies on the **type check**: since `ViewSchema` and `TableSchema` are separate classes, `instanceof TableSchema` is `false` for views. This is enforced at the Java type level — there is no separate "isReadOnly" flag needed.

### Read-Only Protection Summary

| Operation | Called with `@View` class | Result |
|---|---|---|
| `save(viewObj)` | ❌ | `DataException("Invalid data provided, Data required")` |
| `update(viewObj)` | ❌ | `DataException("Invalid data provided, Data required")` |
| `delete(ViewClass, pk)` | ❌ | `DataException("Invalid data provided, Data required")` |
| `query(ViewClass)` | ❌ | `DataException("Invalid data provided, Table required")` |
| `select(ViewClass, String[])` | ❌ | `DataException("Invalid data provided, Table required")` |
| `queryDS(ViewClass)` | ❌ | `DataException("Invalid data provided, Table required")` |
| `select(ViewClass)` | ✅ | Sets up query — chain with `fire()` |
| `view(ViewClass)` | ✅ | Executes immediately, returns results |

---

## 8. `select(ViewClass).fire()` vs `view()` — Internal Execution Difference

Both methods execute `SELECT * FROM viewName`. The key difference is **how they read the ResultSet**:

### `select(ViewClass).fire()` — String-Based Column Lookup

`fire()` uses `resultSet.getObject(columnName)` — it retrieves each column value by **name**:

```java
// Inside fire() for views:
for (FieldSchema fs : viewSchema.getAllFields()) {
    Object value = resultSet.getObject(fs.getColumnName()); // by column name string
    // call POJO setter with value
}
```

This means only the columns whose names match a `FieldSchema.columnName` are read. Any column in the ResultSet that has no matching `FieldSchema` is silently ignored.

### `view()` — Positional Method Reference Lookup

`view()` uses the pre-built `Method` references stored in `StatementDS` — positional access:

```java
// Inside view():
StatementDS selectStatement = statements.get(objClass).get("select");
List<Method> jdbcGetterMethods = selectStatement.getJDBCGetterMethods();
List<Method> classSetterMethods = selectStatement.getClassSetterMethods();

for (int i = 0; i < selectStatement.getResultParamsCount(); i++) {
    // resultSet.getString(1), resultSet.getInt(2), etc.
    Object data = jdbcGetterMethods.get(i).invoke(resultSet, i + 1);
    Object converted = JDBCMethodExtractor.convertToJava(paramsType.get(i), data);
    // pojo.setFirstName(converted), pojo.setRollNumber(converted), etc.
    classSetterMethods.get(i).invoke(instance, converted);
}
```

Positional access (column index `i+1`) is slightly more efficient than string-based column lookup. The `Method` references are resolved **once at initialization** and reused on every `view()` call.

### When to Choose Each

| Situation | Use |
|---|---|
| Need `ORDER BY` | `select(ViewClass).orderBy("col").fire()` |
| Just need all rows, fastest possible | `view(ViewClass)` |
| Simple retrieval in most cases | Either — the difference is negligible |

---

## 9. Complete End-to-End Example — View in Practice

### Step 1 — Define the View in SQL

```sql
CREATE VIEW v_student_details AS
SELECT s.roll_number,
       s.first_name,
       s.last_name,
       s.gender,
       c.title AS course_title,
       s.date_of_birth
FROM student s
JOIN course c ON s.course_code = c.code;
```

### Step 2 — Run `ORMFMTool` to Generate the View POJO

```bash
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_view_pojo
```

### Step 3 — Generated POJO (`src/your/package/name/VStudentDetails.java`)

```java
package your.package.name;

import com.ashvin.orm.fm.annotations.*;

@View(name="v_student_details")
public class VStudentDetails
{
@Column(name="roll_number")
@SetterGetter
private java.lang.Integer rollNumber;
public void setRollNumber(java.lang.Integer rollNumber)
{
this.rollNumber=rollNumber;
}
public java.lang.Integer getRollNumber()
{
return this.rollNumber;
}
@Column(name="first_name")
@SetterGetter
private java.lang.String firstName;
public void setFirstName(java.lang.String firstName)
{
this.firstName=firstName;
}
public java.lang.String getFirstName()
{
return this.firstName;
}
@Column(name="last_name")
@SetterGetter
private java.lang.String lastName;
public void setLastName(java.lang.String lastName)
{
this.lastName=lastName;
}
public java.lang.String getLastName()
{
return this.lastName;
}
@Column(name="gender")
@SetterGetter
private java.lang.String gender;
public void setGender(java.lang.String gender)
{
this.gender=gender;
}
public java.lang.String getGender()
{
return this.gender;
}
@Column(name="course_title")
@SetterGetter
private java.lang.String courseTitle;
public void setCourseTitle(java.lang.String courseTitle)
{
this.courseTitle=courseTitle;
}
public java.lang.String getCourseTitle()
{
return this.courseTitle;
}
@Column(name="date_of_birth")
@SetterGetter
private java.util.Date dateOfBirth;
public void setDateOfBirth(java.util.Date dateOfBirth)
{
this.dateOfBirth=dateOfBirth;
}
public java.util.Date getDateOfBirth()
{
return this.dateOfBirth;
}
}
```

> [!NOTE]
> The column alias `course_title` in the SQL view definition becomes the `@Column(name="course_title")` in the POJO and the field name `courseTitle` — the camelCase conversion applies to aliases just as it does to plain column names.

### Step 4 — Package into JAR and Use

```java
DataManager dm = DataManager.getDataManager();

// Retrieve all student-course details
dm.begin();
List<VStudentDetails> details =
    (List<VStudentDetails>) dm.view(VStudentDetails.class);
dm.end();

for (VStudentDetails d : details) {
    System.out.println(d.getFirstName() + " " + d.getLastName()
        + " | " + d.getCourseTitle()
        + " | " + d.getDateOfBirth());
}

// Retrieve with filter (select chain)
dm.begin();
List<VStudentDetails> ordered =
    (List<VStudentDetails>) dm
        .select(VStudentDetails.class)
        .orderBy("last_name")
        .fire();
dm.end();
```

---

## 10. View vs Table POJO — Developer's Quick Reference

| Question | Answer |
|---|---|
| Do I need a PK in a view POJO? | No — views have no PK annotation or requirement |
| Can I save/update/delete via a view POJO? | No — `DataException` is thrown |
| Can I use `where()` with a view? | No — views only support `orderBy()`, not `where()` filtering |
| Can I use `orderBy()` with a view? | Yes — via `select(ViewClass).orderBy("col").fire()` |
| Can a view be `@Cacheable`? | Technically the annotation can be placed, but it has no effect at runtime — `DataManager` only caches tables with a PK |
| Does `queryDS()` work on views? | No — `DataException("Invalid data provided, Table required")` |
| Is view data always live from DB? | Yes — both `view()` and `select(ViewClass).fire()` always hit the DB |
| Can I add custom methods to view POJOs? | Yes — `toString()`, computed properties etc. are fine, just ensure they don't conflict with `getXxx()`/`setXxx()` naming |

---

## 11. All `DataException` Messages for View Operations

| Method | Message | Cause |
|---|---|---|
| `select(ViewClass)` | `"Invalid data provided, Data required"` | Class not known to framework |
| `select(ViewClass)` | `"Invalid data provided, View required"` | A `@Table` class was passed instead |
| `select(ViewClass)` | `"Call begin() before select()"` | Connection is null |
| `view(ViewClass)` | `"Invalid data provided, Data required"` | Class not known to framework |
| `view(ViewClass)` | `"Invalid data provided, View required"` | A `@Table` class was passed |
| `view(ViewClass)` | `"Call begin() before view()"` | Connection is null |
| `save(viewObj)` | `"Invalid data provided, Data required"` | `@View` class passed — fails `instanceof TableSchema` |
| `update(viewObj)` | `"Invalid data provided, Data required"` | `@View` class passed — fails `instanceof TableSchema` |
| `delete(ViewClass, pk)` | `"Invalid data provided, Data required"` | `@View` class passed — fails `instanceof TableSchema` |
| `query(ViewClass)` | `"Invalid data provided, Table required"` | Explicit table-only guard in `query()` |
| `queryDS(ViewClass)` | `"Invalid data provided, Table required"` | Explicit table-only guard in `queryDS()` |

---

*End of Part 8 — Please review and confirm to proceed to Part 9.*

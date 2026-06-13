# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 2 — Annotations: Complete Reference

---

> [!NOTE]
> This is **Part 2** of the ORMFM documentation. It provides a complete, detailed reference for every annotation the framework defines. Annotations are the primary way a backend developer communicates the database structure to the framework through POJO classes.

---

## Overview

ORMFM uses **9 custom Java annotations** to describe the mapping between Java POJO classes and the underlying database. These annotations are defined in the package:

```
com.ashvin.orm.fm.annotations
```

They are split into two groups based on their `@Target`:

| Group        | Annotations                                                                 |
|--------------|-----------------------------------------------------------------------------|
| **Class-level** | `@Table`, `@View`, `@Cacheable`                                          |
| **Field-level** | `@Column`, `@PrimaryKey`, `@AutoIncrement`, `@Unique`, `@ForeignKey`, `@SetterGetter` |

All annotations have `@Retention(RetentionPolicy.RUNTIME)` — meaning they are available at runtime via Java Reflection. This is what allows `ORMDataModel` and `DataManager` to read them and build the internal schema without any XML or external configuration.

---

## 1. `@Table`

### Definition
Marks a Java class as a **POJO that maps to a database table**. This is the entry point for any table-backed POJO. Without this annotation, the framework will not recognize the class at all.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    public String name() default "";
}
```

### Parameters

| Parameter | Type     | Required | Description                                          |
|-----------|----------|----------|------------------------------------------------------|
| `name`    | `String` | Yes      | The exact name of the table in the database          |

### How It Works Internally
When `ORMDataModel.getInfo(Class)` is called, the framework first checks if the class carries `@Table`. If yes, it reads the `name` attribute and creates a `TableSchema` object with that table name. This `TableSchema` is then populated with `FieldSchema` entries for each annotated field.

### Example
```java
import com.ashvin.orm.fm.annotations.*;

@Table(name="course")
public class Course {
    // fields...
}
```

### Rules
- **One per class** — each POJO class must have exactly one `@Table` annotation.
- **Exact table name** — the `name` value must exactly match the table name in the database (case-sensitive depending on your DB config).
- **Cannot combine with `@View`** — a class cannot carry both `@Table` and `@View`.
- A class without `@Table` or `@View` will throw a `DataException` when passed to any `DataManager` method.

---

## 2. `@View`

### Definition
Marks a Java class as a **POJO that maps to a database view**. A view-backed POJO is **read-only** — the framework only supports `SELECT` operations on it. No `save()`, `update()`, or `delete()` operations are possible on view POJOs.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface View {
    public String name() default "";
}
```

### Parameters

| Parameter | Type     | Required | Description                                          |
|-----------|----------|----------|------------------------------------------------------|
| `name`    | `String` | Yes      | The exact name of the view in the database           |

### How It Works Internally
Similar to `@Table`, when `ORMDataModel.getInfo(Class)` encounters a `@View`-annotated class, it creates a `ViewSchema` object. `ViewSchema` only supports the `SELECT` statement — no `INSERT`, `UPDATE`, or `DELETE` statements are built for it. The `DataManager` exposes separate methods (`select(ViewClass)` and `view(ViewClass)`) specifically for view POJOs.

### Example
```java
import com.ashvin.orm.fm.annotations.*;

@View(name="v_student_course")
public class V1 {
    // fields...
}
```

### Rules
- **Read-only** — calling `save()`, `update()`, or `delete()` with a `@View` class will throw a `DataException` ("Table required").
- **Exact view name** — the `name` must match the view name in the database exactly.
- **Cannot combine with `@Table`** — a class cannot have both `@Table` and `@View`.
- `@Cacheable` is technically assignable but has no practical effect on views (caching is only processed for table POJOs with a primary key).

---

## 3. `@Column`

### Definition
Maps a **Java field to a specific column in the database**. This is the most fundamental field-level annotation — only fields marked with `@Column` are recognized and processed by the framework. Any field without `@Column` is completely invisible to ORMFM.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    public String name() default "";
}
```

### Parameters

| Parameter | Type     | Required | Description                                                 |
|-----------|----------|----------|-------------------------------------------------------------|
| `name`    | `String` | Yes      | The exact column name in the database table or view        |

### How It Works Internally
`ORMDataModel.getInfo()` iterates through every declared field (`getDeclaredFields()`) on the class. Only fields that carry `@Column` are included in the `FieldSchema` list for that table/view. The column `name` is used in all generated SQL statements (`INSERT INTO table SET col=?`, `WHERE col=?`, etc.) and when querying `DatabaseMetaData` to determine the SQL type of that column.

### Example
```java
@Table(name="student")
public class Student {

    @Column(name="roll_number")
    @PrimaryKey
    @SetterGetter
    private int rollNumber;

    @Column(name="first_name")
    @SetterGetter
    private String firstName;

    // setters/getters...
}
```

### Rules
- **Required for any field to be recognized** — no `@Column` means the field is skipped entirely.
- **Exact column name** — `name` must match the actual column name in the database. A mismatch will cause incorrect SQL to be generated silently (the framework reads the column from `DatabaseMetaData` by this name).
- **Must pair with `@SetterGetter` or be `public`** — `@Column` alone is not enough. The framework also needs to know _how_ to read/write the field value. If neither `@SetterGetter` nor `public` modifier is present, the field is skipped even if it has `@Column`.
- **Works on both `@Table` and `@View` POJOs** — usage is identical for tables and views.

> [!IMPORTANT]
> A field with `@Column` but no `@SetterGetter` and not declared `public` will be **silently ignored** by the framework. Always pair `@Column` with `@SetterGetter` or make the field `public`.

---

## 4. `@PrimaryKey`

### Definition
Marks a field as the **primary key** of the table. The framework uses this field for all WHERE clauses in `UPDATE` and `DELETE` operations, for PK uniqueness checks during `INSERT`, and as the key for the in-memory cache when `@Cacheable` is present.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PrimaryKey {
}
```

### Parameters
None — this is a **marker annotation**.

### How It Works Internally
When building the `StatementDS` entries at `DataManager` initialization, the framework scans through all fields and records the index of the field marked `@PrimaryKey` (`primaryKeyIndex`). This index is then used to:
- Build the `WHERE pk_col = ?` clause for `UPDATE` and `DELETE`.
- Generate the `PRIMARY_KEY_VALIDATION` statement (a SELECT to check existence before INSERT, for non-auto-increment PKs).
- Generate the `GET_BY_PRIMARY_KEY` statement (a full SELECT used during UPDATE to load the current state of a record).
- Serve as the **cache key** in the in-memory `LinkedHashMap` for `@Cacheable` tables.

If no `@PrimaryKey` is found on any field of a `@Table` POJO, `UPDATE` and `DELETE` statements are **cleared** and those operations become unavailable for that class.

### Example
```java
@Table(name="student")
public class Student {

    @Column(name="roll_number")
    @PrimaryKey                   // This field is the PK
    @SetterGetter
    private int rollNumber;

    // ...
}
```

### Rules
- **Only one per class** — multiple `@PrimaryKey` annotations on the same class will cause a `DataException` ("Multiple primary key are not allowed").
- **Must also have `@Column`** — `@PrimaryKey` without `@Column` is meaningless; the field won't be included in the schema at all.
- **Composite primary keys are not supported** — ORMFM only supports single-column PKs.
- **Required for `@Cacheable`** — a class marked `@Cacheable` must have a `@PrimaryKey` field, otherwise initialization throws a `DataException` ("@Cacheable not allowed on the pojo, which does not have any primary key set.").
- **Required for `UPDATE` and `DELETE`** — without a `@PrimaryKey`, neither operation is possible for that class.

---

## 5. `@AutoIncrement`

### Definition
Marks a primary key field as **auto-incremented** by the database. When this annotation is present, the framework knows **not to include this column** in the `INSERT` statement — the database will generate the value automatically. After the insert, the framework reads back the generated key and sets it on the POJO object.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoIncrement {
}
```

### Parameters
None — this is a **marker annotation**.

### How It Works Internally
During the `StatementDS` construction loop, any field marked `@AutoIncrement` is **excluded** from the `INSERT INTO table SET col=?` statement parameters. Instead, the framework calls `PreparedStatement.executeUpdate()` with `Statement.RETURN_GENERATED_KEYS`, then reads the auto-generated key from `getGeneratedKeys()` and calls the field's setter on the POJO — so after `save()`, the POJO's PK field is automatically populated with the newly generated value.

For non-auto-increment PKs, the framework performs a **pre-insert SELECT** to verify the PK doesn't already exist before attempting the INSERT.

### Example
```java
@Table(name="course")
public class Course {

    @Column(name="code")
    @PrimaryKey
    @AutoIncrement           // DB generates this value; don't set it before save()
    @SetterGetter
    private int code;

    @Column(name="title")
    @SetterGetter
    private String title;

    // setters/getters...
}
```

```java
// Usage — note: no need to set 'code' before save()
Course c = new Course();
c.setTitle("Java Programming");

dm.begin();
dm.save(c);
dm.end();

// After save(), the generated PK is available:
System.out.println("Assigned code: " + c.getCode());
```

### Rules
- **Typically paired with `@PrimaryKey`** — while technically not enforced at annotation level, `@AutoIncrement` is only meaningful on a PK column.
- **Must also have `@Column`** — just like all other field annotations.
- **Do not set the value before `save()`** — the database manages this value. Setting it manually has no effect on the INSERT (it is excluded from the statement).
- **Only one auto-incremented key per table** — matches the database's own restriction.
- **The generated value is written back** — after `save()` returns, the POJO's PK setter is called with the new generated value automatically.

---

## 6. `@Unique`

### Definition
Marks a field as having a **unique constraint** in the database. Before any `INSERT` or `UPDATE`, the framework checks whether the value of this field already exists in the database, and throws a `DataException` if it does.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Unique {
}
```

### Parameters
None — this is a **marker annotation**.

### How It Works Internally
For each `@Unique` field, the framework builds two validation statements:

- **`UNIQUE_KEY_VALIDATION`** — used during `INSERT`. Generates a `SELECT col FROM table WHERE col = ?;` for each unique field (multiple unique fields produce multiple sub-queries separated by `;`). If any record is found, a `DataException` is thrown before the INSERT executes.
- **`UNIQUE_AND_PRIMARY_KEY_VALIDATION`** — used during `UPDATE`. Generates `SELECT col FROM table WHERE col = ? AND pk_col <> ?;` — this allows the same record to keep its own unique value while preventing another record from stealing it.

### Example
```java
@Table(name="student")
public class Student {

    @Column(name="roll_number")
    @PrimaryKey
    @SetterGetter
    private int rollNumber;

    @Column(name="aadhar_card_number")
    @Unique                    // No two students can share an Aadhar number
    @SetterGetter
    private String aadharCardNumber;

    // ...
}
```

### Rules
- **Multiple `@Unique` fields allowed** — each unique field generates its own sub-validation query. All are checked before the write proceeds.
- **Must also have `@Column`**.
- **Validated before INSERT and UPDATE** — the check happens at the application layer (via SELECT), not relying on the database's UNIQUE constraint error.
- **Error message is user-friendly** — throws `DataException("This <value> is already in use. Please try another.")`, not a raw SQL exception.
- **Not applicable to `@View` POJOs** — views are read-only; unique checks only matter for write operations.

---

## 7. `@ForeignKey`

### Definition
Marks a field as a **foreign key** that references a column in a parent table. Before any `INSERT` or `UPDATE`, the framework verifies that the referenced parent record actually exists. Before any `DELETE` or `UPDATE` of the *parent* record, the framework checks whether any *child* records reference it.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
    public String parent() default "";
    public String column() default "";
}
```

### Parameters

| Parameter  | Type     | Required | Description                                                               |
|------------|----------|----------|---------------------------------------------------------------------------|
| `parent`   | `String` | Yes      | The **table name** of the parent/referenced table in the database         |
| `column`   | `String` | Yes      | The **column name** in the parent table that this field references (usually the parent's PK) |

### How It Works Internally
For each `@ForeignKey` field, the framework builds two statement groups:

1. **`FOREIGN_KEY_VALIDATION`** — used during `INSERT` and `UPDATE`. Generates `SELECT parent_col FROM parent_table WHERE parent_col = ?;`. If no record is found, a `DataException` is thrown ("Referenced parent record does not exist.").

2. **`GET_BY_FOREIGN_KEY`** — a SELECT to retrieve all child records matching the FK value (stored for reference, used internally).

3. **Reverse constraint check** — during `DELETE` and `UPDATE` of a parent record, the framework scans **all other loaded table schemas** looking for any `@ForeignKey` fields that reference the current table. If child records exist, a `DataException` is thrown ("Unable to update or delete record, since this record is attached with other child record(s).").

### Example
```java
@Table(name="student")
public class Student {

    @Column(name="course_code")
    @ForeignKey(parent="course", column="code")   // References course.code
    @SetterGetter
    private int courseCode;

    // ...
}
```

The database schema this maps to:
```sql
CREATE TABLE student (
    course_code INT NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course(code)
);
```

### Rules
- **`parent` must be an exact table name** — it is used directly in the generated SQL (`SELECT column FROM parent WHERE column = ?`). A wrong value will silently generate invalid SQL.
- **`column` must be the parent's column name** — typically the parent's primary key column name.
- **Validation is bidirectional** — child-to-parent is checked on INSERT/UPDATE; parent-to-child is checked on DELETE/UPDATE of the parent.
- **Multiple FK fields allowed** — each generates its own validation query.
- **Must also have `@Column`**.

> [!WARNING]
> If the `parent` table name in `@ForeignKey` does not match a real table in the database, the FK validation query will silently fail during initialization (caught internally). The FK validation will be cleared and no check will be performed. Always double-check the `parent` value matches the exact table name.

---

## 8. `@Cacheable`

### Definition
Enables **in-memory caching** for a table-backed POJO. When a class is marked `@Cacheable`, all its records are loaded into a `LinkedHashMap` in memory at `DataManager` initialization time. All subsequent `queryDS()` calls serve data directly from this in-memory map without hitting the database. All `save()`, `update()`, and `delete()` operations keep the in-memory map in sync automatically.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cacheable {
}
```

### Parameters
None — this is a **marker annotation**.

### How It Works Internally
During `DataManager` initialization, after building all `StatementDS` entries for a table, if the class has `@Cacheable`:
1. A `LinkedHashMap<Object, Object>` is created (keyed by PK value, valued by POJO instance).
2. A full `SELECT * FROM table` is executed.
3. Every row is read and a POJO instance is created and populated using the result setters.
4. The POJO is stored in the map under its PK value.
5. This map is stored in the global `cache` map under the class as the key.

From that point:
- **`queryDS(Class)`** returns a copy of all values from the cache (each object is cloned via `PojoCopier.copy()` to prevent mutation of cached data).
- **`save(obj)`** — after successful INSERT, a clone of the new object is added to the cache.
- **`update(obj)`** — after successful UPDATE, the old cache entry is replaced with a clone of the updated object.
- **`delete(Class, pk)`** — after successful DELETE, the entry is removed from the cache by PK.

### Example

**Step 1 — Manually add `@Cacheable` to the generated POJO:**
```java
import com.ashvin.orm.fm.annotations.*;

@Table(name="course")
@Cacheable                     // ← Added manually after ORMFMTool generation
public class Course {

    @Column(name="code")
    @PrimaryKey
    @AutoIncrement
    @SetterGetter
    private int code;

    @Column(name="title")
    @SetterGetter
    private String title;

    // setters/getters...
}
```

**Step 2 — Use `queryDS()` instead of `query().fire()` to read from cache:**
```java
DataManager dm = DataManager.getDataManager();

// Reads from in-memory cache — NO database hit
dm.begin();
List<Course> courses = (List<Course>) dm.queryDS(Course.class);
dm.end();

for (Course c : courses) {
    System.out.println(c.getCode() + " — " + c.getTitle());
}
```

**Step 3 — CRUD operations automatically keep cache in sync:**
```java
// save() — inserts into DB and updates cache
dm.begin();
Course c = new Course();
c.setTitle("Data Structures");
dm.save(c);                    // Cache updated with new entry
dm.end();

// delete() — removes from DB and updates cache
dm.begin();
dm.delete(Course.class, c.getCode());   // Cache entry removed
dm.end();
```

### Rules
- **Requires `@PrimaryKey`** — if `@Cacheable` is placed on a class with no `@PrimaryKey` field, `DataManager` initialization throws a `DataException` ("@Cacheable not allowed on the pojo, which does not have any primary key set.").
- **Only for `@Table`** — `@Cacheable` is only meaningful on table POJOs. Views have no write operations, so a view cache would never be updated.
- **Added manually** — `ORMFMTool` does **not** automatically add `@Cacheable`. The developer must add it by hand to the generated POJO source file before running `createJar()`.
- **`queryDS()` vs `query()`** — `queryDS()` reads from the cache (no DB hit). `query().fire()` always hits the database. If you call `queryDS()` on a non-`@Cacheable` class, a `DataException` is thrown.
- **All data is loaded at startup** — the entire table is fetched into memory at `DataManager.initialize()` time. This is best suited for **small, frequently-read, rarely-changed** tables (like lookup/master tables).
- **Cloned objects are returned** — `queryDS()` always returns clones of cached objects. This protects the cache from being accidentally mutated by caller code.

> [!TIP]
> Use `@Cacheable` for reference/lookup tables such as `course`, `department`, `category`, `status` — tables that have a small, stable dataset read frequently. Avoid it for large or frequently updated tables like transaction logs.

---

## 9. `@SetterGetter`

### Definition
Tells the framework to access this field using its **standard JavaBean getter and setter methods** (`getXxx()` / `setXxx()`). This is the **recommended way** to expose POJO fields to the framework.

### Declaration
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SetterGetter {
}
```

### Parameters
None — this is a **marker annotation**.

### How It Works Internally
When `ORMDataModel` processes a field that has `@SetterGetter`, it sets both `fieldSchema.setSetterAllowed(true)` and `fieldSchema.setGetterAllowed(true)`. At runtime, `DataManager` uses these flags to reflectively look up and invoke the correct `getXxx()` / `setXxx()` methods on the POJO class.

The method names are derived from the **Java field name** (not the column name):
- Field `rollNumber` → getter `getRollNumber()`, setter `setRollNumber(type)`
- Field `aadharCardNumber` → getter `getAadharCardNumber()`, setter `setAadharCardNumber(type)`

The setter must accept exactly one argument of the field's declared type. If the getter or setter is missing from the class, the framework stores `null` for that method reference and silently skips reading/writing that field.

### Alternative — `public` field access
If a field is declared `public` (and does **not** have `@SetterGetter`), the framework falls back to direct field access via `Field.get()` / `Field.set()`. This is a supported but **non-recommended** pattern.

### Example

**Recommended — `@SetterGetter` with private field:**
```java
@Column(name="first_name")
@SetterGetter
private String firstName;

public void setFirstName(String firstName) {
    this.firstName = firstName;
}
public String getFirstName() {
    return this.firstName;
}
```

**Alternative — `public` field (no `@SetterGetter` needed):**
```java
@Column(name="first_name")
public String firstName;    // public → accessed directly
```

**Ignored — private without `@SetterGetter`:**
```java
@Column(name="first_name")
private String firstName;   // ← IGNORED by the framework entirely
```

### Rules
- **Method naming convention is strict** — the getter must be `get` + capitalized field name, and setter must be `set` + capitalized field name. If the POJO doesn't follow this convention exactly, the method reference will be `null` and data won't be read/written.
- **Setter must accept the exact field type** — the framework looks up the setter using `getMethod("setXxx", fieldType)`. A type mismatch will cause a `NoSuchMethodException` and the method reference will be stored as `null`.
- **`@SetterGetter` takes priority over `public`** — if both are present, the getter/setter path is used.
- **Generated by `ORMFMTool` automatically** — every field in a POJO generated by `ORMFMTool` comes with `@SetterGetter` and the corresponding getter/setter methods already written out. Manually written POJOs must add these themselves.

---

## Annotation Combination Rules

The following table summarizes which annotations can and must be combined for common scenarios:

| Scenario                             | Annotations Required on the Field                           |
|--------------------------------------|-------------------------------------------------------------|
| Regular column (private field)       | `@Column` + `@SetterGetter`                                |
| Regular column (public field)        | `@Column` (public modifier replaces `@SetterGetter`)        |
| Primary key, auto-incremented (private) | `@Column` + `@PrimaryKey` + `@AutoIncrement` + `@SetterGetter` |
| Primary key, manual (private)        | `@Column` + `@PrimaryKey` + `@SetterGetter`                |
| Unique column (private)              | `@Column` + `@Unique` + `@SetterGetter`                    |
| Foreign key column (private)         | `@Column` + `@ForeignKey(parent="..",column="..") ` + `@SetterGetter` |
| PK + FK (referencing parent's PK)    | `@Column` + `@PrimaryKey` + `@ForeignKey(...)` + `@SetterGetter` |
| Cacheable table class                | `@Table(name="..")` + `@Cacheable` (class level)           |

---

## Correct Annotation Placement Order

While Java does not enforce annotation ordering, the following ordering is used consistently throughout the project and in generated POJOs — it is recommended for clarity:

```java
// Class-level (top of class, before 'public class')
@Table(name="table_name")
@Cacheable                          // optional — add manually if needed
public class MyPojo {

    // Field-level (stacked above the field declaration, in this order)
    @PrimaryKey                     // 1st — key role
    @AutoIncrement                  // 2nd — generation strategy
    @Unique                         // 3rd — constraint
    @ForeignKey(parent="", column="")  // 4th — relationship
    @Column(name="col_name")        // 5th — column mapping
    @SetterGetter                   // 6th — access strategy
    private Type fieldName;
}
```

---

## Complete Annotated POJO Example

The following is a complete, realistic POJO showing all annotations in context, based on the test schema used in the `testing/` folder:

**Table: `course`** — uses auto-increment PK, `@Cacheable`
```java
package your.package.name;

import com.ashvin.orm.fm.annotations.*;

@Table(name="course")
@Cacheable
public class Course {

    @PrimaryKey
    @AutoIncrement
    @Column(name="code")
    @SetterGetter
    private int code;

    @Column(name="title")
    @SetterGetter
    private String title;

    public void setCode(int code)       { this.code = code; }
    public int getCode()                { return this.code; }
    public void setTitle(String title)  { this.title = title; }
    public String getTitle()            { return this.title; }
}
```

**Table: `student`** — uses manual PK, unique constraint, foreign key
```java
package your.package.name;

import com.ashvin.orm.fm.annotations.*;

@Table(name="student")
public class Student {

    @PrimaryKey
    @Column(name="roll_number")
    @SetterGetter
    private int rollNumber;

    @Column(name="first_name")
    @SetterGetter
    private String firstName;

    @Column(name="last_name")
    @SetterGetter
    private String lastName;

    @Unique
    @Column(name="aadhar_card_number")
    @SetterGetter
    private String aadharCardNumber;

    @ForeignKey(parent="course", column="code")
    @Column(name="course_code")
    @SetterGetter
    private int courseCode;

    @Column(name="gender")
    @SetterGetter
    private String gender;

    @Column(name="date_of_birth")
    @SetterGetter
    private java.sql.Date dateOfBirth;

    // setters / getters for all fields...
}
```

**View: `v_student_course`** — read-only, no PK/FK/Unique annotations needed
```java
package your.package.name;

import com.ashvin.orm.fm.annotations.*;

@View(name="v_student_course")
public class V1 {

    @Column(name="first_name")
    @SetterGetter
    private String firstName;

    @Column(name="title")
    @SetterGetter
    private String title;

    // setters / getters...
}
```

---

## Quick Reference — All Annotations at a Glance

| Annotation       | Target  | Params               | Purpose                                                   |
|------------------|---------|----------------------|-----------------------------------------------------------|
| `@Table`         | Class   | `name` (String)      | Maps the class to a database table                        |
| `@View`          | Class   | `name` (String)      | Maps the class to a database view (read-only)            |
| `@Cacheable`     | Class   | none                 | Loads entire table into in-memory map at startup          |
| `@Column`        | Field   | `name` (String)      | Maps the field to a specific column name                  |
| `@PrimaryKey`    | Field   | none                 | Marks the field as the single primary key                 |
| `@AutoIncrement` | Field   | none                 | Marks PK as DB-generated; excluded from INSERT            |
| `@Unique`        | Field   | none                 | Validates uniqueness before INSERT and UPDATE             |
| `@ForeignKey`    | Field   | `parent`, `column`   | Validates parent record existence before INSERT/UPDATE    |
| `@SetterGetter`  | Field   | none                 | Tells framework to use `getXxx()`/`setXxx()` for access  |

---

*End of Part 2 — Please review and confirm to proceed to Part 3.*

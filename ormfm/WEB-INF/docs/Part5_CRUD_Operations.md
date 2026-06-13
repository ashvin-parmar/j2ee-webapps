# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 5 — DataManager: CRUD Operations

---

> [!NOTE]
> This is **Part 5** of the ORMFM documentation. It covers the three write operations in `DataManager` — `save()`, `update()`, and `delete()` — in complete step-by-step detail, including all constraint validations, cache synchronization, and every possible exception.

---

## Overview

ORMFM provides three write operations, all of which follow a strict **validation-first** pattern: before any SQL write is executed, the framework performs a series of SELECT-based checks to verify data integrity at the application layer.

| Method | SQL Generated | Requires PK? | Auto handles FK check? |
|---|---|---|---|
| `save(obj)` | `INSERT INTO table SET col=?, ...` | Only if not `@AutoIncrement` | Yes — validates parent exists |
| `update(obj)` | `UPDATE table SET col=?, ... WHERE pk=?` | Yes — always required | Yes — validates parent exists |
| `delete(Class, pk)` | `DELETE FROM table WHERE pk=?` | Yes — always required | Yes — blocks if children exist |

---

## 1. Method: `save(Object obj)`

### Signature
```java
public void save(Object obj) throws DataException
```

### Prerequisites
- `begin()` must have been called — throws `DataException("Call begin() before save()")` if connection is null.
- `obj` must be an instance of a `@Table`-annotated POJO class that was loaded at initialization.

### What It Does
Inserts a new record into the database. Before inserting, it validates primary key uniqueness, unique field values, and foreign key references. After inserting, it reads back any auto-generated keys and updates the POJO and the in-memory cache if applicable.

---

### Complete Internal Flow

```
Phase 1 — PK Validation (only for NON-auto-increment primary keys)
│
│  If the table's PK is NOT auto-incremented:
│    Statement: "SELECT pk_col FROM table WHERE pk_col = ?"
│    Sets parameter: value of PK field from the obj via getter method
│    Executes query
│    If a record is found → record already exists
│      throws DataException("This record already exists. Please use a unique identifier.")
│    If PK value is null or getter returns null:
│      throws DataException("Invalid data provided to primary key, Data required")
│
│  If the table's PK IS auto-incremented → this phase is SKIPPED entirely
│
▼
Phase 2 — Unique Key Validation
│
│  For EACH @Unique field in the POJO:
│    Statement: "SELECT col FROM table WHERE col = ?;"  (semicolon-separated per field)
│    Sets parameter: value of that unique field from the obj via getter
│    Executes query
│    If a record is found → duplicate unique value
│      throws DataException("This <value> is already in use. Please try another.")
│    If value is null or getter returns null:
│      throws DataException("Invalid data provided to unique key, Data required")
│
│  If no @Unique fields exist → this phase is SKIPPED
│
▼
Phase 3 — Foreign Key Validation
│
│  For EACH @ForeignKey field in the POJO:
│    Statement: "SELECT parent_col FROM parent_table WHERE parent_col = ?;"
│    Sets parameter: value of the FK field from the obj
│    Executes query
│    If NO record found → parent does not exist
│      throws DataException("Referenced parent record <value> does not exist.
│                           Please select a valid entry.")
│    If value is null or getter returns null:
│      throws DataException("Invalid data provided to foreign key, Data required")
│
│  If no @ForeignKey fields exist → this phase is SKIPPED
│
▼
Phase 4 — INSERT Execution
│
│  Statement: "INSERT INTO table SET col1=?, col2=?, ..."
│  (Auto-increment columns are EXCLUDED from this statement)
│  For each non-auto-increment column:
│    Calls getter on obj → converts value to JDBC type → sets on PreparedStatement
│    If getter returns null → setNull(i, sqlType)
│  Executes: preparedStatement.executeUpdate()
│  Prepared with: Statement.RETURN_GENERATED_KEYS
│
▼
Phase 5 — Generated Key Read-back + Cache Sync
│
│  Reads generated keys: preparedStatement.getGeneratedKeys()
│  If a generated key exists (i.e., @AutoIncrement field present):
│    Reads the value using the ResultSet getter method
│    Converts to Java type
│    Calls the @AutoIncrement field's setter on obj
│    (The POJO now has its new PK value set)
│
│  If the table is @Cacheable:
│    Creates a clone of obj using PojoCopier.copy()
│    Determines the primary key value:
│      - For auto-increment: uses the generated key value
│      - For manual PK: uses the PK getter on the cloned obj
│    Puts the clone into: cache.get(objClass).put(pk, clonedObj)
│
└── save() returns normally
```

---

### Auto-Increment vs Manual PK Behaviour

| Scenario | Phase 1 | Phase 4 INSERT | Phase 5 Key Read-back |
|---|---|---|---|
| `@PrimaryKey` + `@AutoIncrement` | **Skipped** | PK column excluded from SET clause | PK value read from `getGeneratedKeys()`, set on POJO |
| `@PrimaryKey` (no `@AutoIncrement`) | **Runs** — checks PK is unique | PK column included in SET clause | No generated key expected |

---

### Code Examples

**Saving a Course (auto-increment PK):**
```java
DataManager dm = DataManager.getDataManager();

Course c = new Course();
c.setTitle("Data Structures");
// c.setCode(...) — DO NOT set, DB generates it

dm.begin();
try {
    dm.save(c);
    System.out.println("Saved with code: " + c.getCode()); // PK now available
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();
}
```

**Saving a Student (manual PK, FK, unique constraint):**
```java
Student s = new Student();
s.setRollNumber(10101);          // manual PK — must be unique
s.setFirstName("Rohit");
s.setLastName("Shah");
s.setAadharCardNumber("UID12345"); // @Unique field
s.setCourseCode(1);              // @ForeignKey — course with code=1 must exist
s.setGender("M");
s.setDateOfBirth(new java.sql.Date(...));

dm.begin();
try {
    dm.save(s);
    System.out.println("Student added.");
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();
}
```

---

### All `DataException` Messages from `save()`

| Message | Cause |
|---|---|
| `"Call begin() before save()"` | Connection is null — `begin()` not called |
| `"Invalid data provided, Data required."` | obj's class has no entry in the `statements` map |
| `"Invalid data provided, Data required"` | Class is not a `@Table` POJO (e.g., it's a `@View`) |
| `"Invalid data provided to primary key, Data required"` | Non-auto-increment PK field has null value or no getter |
| `"This record already exists. Please use a unique identifier."` | Non-auto-increment PK value already exists in DB |
| `"Invalid data provided to unique key, Data required"` | A `@Unique` field has null value |
| `"This <value> is already in use. Please try another."` | A `@Unique` field value already exists in DB |
| `"Invalid data provided to foreign key, Data required"` | A `@ForeignKey` field has null value |
| `"Referenced parent record <value> does not exist. Please select a valid entry."` | FK value does not exist in the parent table |

---

## 2. Method: `update(Object obj)`

### Signature
```java
public void update(Object obj) throws DataException
```

### Prerequisites
- `begin()` must have been called — throws `DataException("Call begin() before update()")`.
- `obj` must be a `@Table` POJO with a `@PrimaryKey` field — no PK means no UPDATE.
- The record identified by the PK value must already exist in the database.

### What It Does
Updates an existing database record. The PK field in the POJO identifies **which record to update**. All other non-PK columns are updated to the values in the POJO. Full constraint validation runs before the UPDATE.

---

### Complete Internal Flow

```
Phase 1 — Fetch Existing Record (getByPrimaryKey)
│
│  Statement: "SELECT * FROM table WHERE pk_col = ?"
│  Sets parameter: PK value from obj via getter
│  If PK value is null or getter is null:
│    throws DataException("Invalid data provided to primary key, Data required")
│  Executes query
│  If NO record found:
│    throws DataException("Invalid <fieldName>: <pk_value>")
│    (e.g., "Invalid rollNumber: 99999" — the record doesn't exist)
│  If record found:
│    Populates a temporary POJO object (prevObj) with the existing DB state
│    (This is used for the referential integrity check in Phase 2)
│
▼
Phase 2 — Referential Integrity Check (updateAndDeleteForeignKeyConstrainOnCompleteDB)
│
│  Scans ALL other loaded TableSchemas to find any that have a
│  @ForeignKey referencing THIS table's column.
│
│  For each other table that references this table:
│    Gets the FK column name + the parent column name
│    Gets the parent column's current value from prevObj
│    Executes: "SELECT * FROM child_table WHERE fk_col = <current_value>"
│    If any child record exists that references this record:
│      throws DataException("Unable to update or delete record,
│                           since this record is attached with other child record(s).")
│
│  Why prevObj (not obj)? Because we need the CURRENT value in the DB —
│  obj might have a different FK value that the developer wants to set.
│  The constraint is on the EXISTING stored value being referenced by children.
│
▼
Phase 3 — Unique + PK Validation (uniqueAndPrimaryKeyValidation)
│
│  For EACH @Unique field:
│    Statement: "SELECT col FROM table WHERE col = ? AND pk_col <> ?;"
│    Parameter 1: new unique value from obj (the value being set)
│    Parameter 2: PK value from obj (to exclude THIS record from the check)
│    If another record has the same unique value:
│      throws DataException("This <value> is already in use. Please try another.")
│
│  The AND pk_col <> ? clause is crucial — it allows a record to keep its
│  own unique value unchanged without triggering a false conflict.
│
▼
Phase 4 — Foreign Key Validation
│
│  Same as save() Phase 3:
│  For EACH @ForeignKey field in obj:
│    Checks that the new FK value exists in the parent table
│    throws DataException if parent not found
│
▼
Phase 5 — UPDATE Execution
│
│  Statement: "UPDATE table SET col1=?, col2=?, ... WHERE pk_col=?"
│  All non-PK, non-auto-increment columns are included in SET
│  The PK column appears in the WHERE clause
│  For each column: getter on obj → JDBC type conversion → set on PreparedStatement
│  If any getter returns null → setNull(i, sqlType)
│  Executes: preparedStatement.executeUpdate()
│
▼
Phase 6 — Cache Sync (only if @Cacheable)
│
│  Creates a clone of obj using PojoCopier.copy()
│  Gets PK value from clone
│  Removes old entry: cache.get(objClass).remove(pk)
│  Adds new entry:    cache.get(objClass).put(pk, clonedObj)
│
└── update() returns normally
```

---

### Why the Existing Record is Fetched First (Phase 1)

The framework fetches the existing record (`prevObj`) for two reasons:
1. **Existence verification** — if the PK doesn't exist, there's nothing to update.
2. **Referential integrity check** — the framework needs the current column values (stored in the DB) to check whether any child records reference them. `obj` might carry new values that don't yet match what's in the DB.

---

### Code Examples

**Updating a Course title:**
```java
Course c = new Course();
c.setCode(1);             // PK — identifies which record to update
c.setTitle("Advanced Java"); // new value to set

dm.begin();
try {
    dm.update(c);
    System.out.println("Course updated.");
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();
}
```

**Updating a Student (with FK + unique validation):**
```java
Student s = new Student();
s.setRollNumber(10101);           // PK — must exist in DB
s.setFirstName("Rohit");
s.setLastName("Sharma");
s.setAadharCardNumber("UID12345"); // @Unique — checked against other records only
s.setCourseCode(2);               // @ForeignKey — course 2 must exist
s.setGender("M");
s.setDateOfBirth(existingDate);

dm.begin();
try {
    dm.update(s);
    System.out.println("Student updated.");
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();
}
```

---

### All `DataException` Messages from `update()`

| Message | Cause |
|---|---|
| `"Call begin() before update()"` | Connection is null |
| `"Invalid data provided, Data required."` | Class has no statements entry |
| `"Invalid data provided, Data required"` | Class is not a `@Table` POJO |
| `"Invalid data provided to primary key, Data required"` | PK field is null or no getter |
| `"Invalid <fieldName>: <value>"` | The record with the given PK does not exist in DB |
| `"Unable to update or delete record, since this record is attached with other child record(s)."` | Children in another table reference this record |
| `"Invalid data provided for primary key, Data required"` | PK value is null in the unique+PK validation phase |
| `"Invalid data provided for unique key, Data required"` | A unique field value is null |
| `"This <value> is already in use. Please try another."` | Another record has the same unique value |
| `"The selected <value> does not exist. Please select a valid entry."` | FK parent record not found |

---

## 3. Method: `delete(Class<?> objClass, Object primaryKey)`

### Signature
```java
public void delete(Class<?> objClass, Object primaryKey) throws DataException
```

### Prerequisites
- `begin()` must have been called — throws `DataException("Call begin() before delete()")`.
- `primaryKey` must not be `null` — throws `DataException("Invalid data provided to primary key, Data required")`.
- The class must be a `@Table` POJO with a `@PrimaryKey` field.

### What It Does
Deletes the record identified by `primaryKey` from the database. Before deleting, it verifies the record exists and that no other table's records reference it.

### Key Difference from `save()` and `update()`
`delete()` does not take a POJO object — it takes the **class** and the **raw primary key value** directly. This is a design choice: the developer doesn't need to construct a full POJO just to delete by PK.

---

### Complete Internal Flow

```
Phase 1 — Record Existence Check + Load State
│
│  If the table IS @Cacheable:
│    Looks up the record directly from in-memory cache:
│      obj = cache.get(objClass).get(primaryKey)
│    If not found in cache:
│      throws DataException("Invalid <fieldName>: <primaryKey>")
│    No DB query needed — the cache is authoritative.
│
│  If the table is NOT @Cacheable:
│    Statement: "SELECT * FROM table WHERE pk_col = ?"
│    Sets parameter: primaryKey value directly
│    Executes query
│    If NO record found:
│      throws DataException("Invalid <fieldName>: <primaryKey>")
│    If record found:
│      Populates a temporary obj POJO with the current DB state
│      (This obj is used in Phase 2 for referential integrity check)
│
▼
Phase 2 — Referential Integrity Check (updateAndDeleteForeignKeyConstrainOnCompleteDB)
│
│  Identical to update() Phase 2:
│  Scans ALL other loaded TableSchemas for @ForeignKey fields
│  that reference this table's column.
│
│  For each referencing child table:
│    Gets the current PK column value from obj
│    Executes: "SELECT * FROM child_table WHERE fk_col = <current_pk_value>"
│    If any child record found:
│      throws DataException("Unable to update or delete record,
│                           since this record is attached with other child record(s).")
│
│  This ensures you cannot delete a parent record while child records still exist.
│
▼
Phase 3 — DELETE Execution
│
│  Statement: "DELETE FROM table WHERE pk_col = ?"
│  Sets parameter: PK value from obj (loaded in Phase 1) via getter
│  If getter returns null → setNull(1, sqlType)
│  Executes: preparedStatement.executeUpdate()
│
▼
Phase 4 — Cache Sync (only if @Cacheable)
│
│  Removes the entry from the in-memory cache:
│    cache.get(objClass).remove(primaryKey)
│
└── delete() returns normally
```

---

### The `@Cacheable` Shortcut in `delete()`

For `@Cacheable` tables, `delete()` does **not** query the database to check existence. It checks the **in-memory cache** directly:

```java
if (tableSchema.isCacheable()) {
    obj = cache.get(objClass).get(primaryKey);
    if (obj == null)
        throw new DataException("Invalid " + primaryKeyField.getMethodName() + ": " + primaryKey);
}
```

This is both an optimization and a safety mechanism — if the record is not in the cache, it's treated as non-existent. The referential integrity check still runs against the database (Phase 2), and the actual DELETE still hits the database (Phase 3).

---

### Code Examples

**Deleting a Course by its PK:**
```java
dm.begin();
try {
    dm.delete(Course.class, 5);   // delete course with code = 5
    System.out.println("Course deleted.");
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();
}
```

**Attempting to delete a Course that has Students referencing it:**
```java
dm.begin();
try {
    dm.delete(Course.class, 1);   // course 1 has students referencing it
} catch (DataException de) {
    // Will print: "Unable to update or delete record, since this
    //              record is attached with other child record(s)."
    System.out.println(de.getMessage());
} finally {
    dm.end();
}
```

**Deleting a Student (no children reference it, so succeeds):**
```java
dm.begin();
try {
    dm.delete(Student.class, 10101);  // roll_number = 10101
    System.out.println("Student deleted.");
} catch (DataException de) {
    System.out.println("Error: " + de.getMessage());
} finally {
    dm.end();
}
```

---

### All `DataException` Messages from `delete()`

| Message | Cause |
|---|---|
| `"Call begin() before delete()"` | Connection is null |
| `"Invalid data provided to primary key, Data required"` | `primaryKey` argument is `null` |
| `"Invalid data provided, Data required."` | Class has no statements entry |
| `"Invalid data provided, Data required"` | Class is not a `@Table` POJO |
| `"Invalid <fieldName>: <primaryKey>"` | No record with given PK found (in cache or DB) |
| `"Unable to update or delete record, since this record is attached with other child record(s)."` | Child records in another table reference this record |

---

## 4. Deep Dive: `updateAndDeleteForeignKeyConstrainOnCompleteDB()`

This private method is used by both `update()` and `delete()` before executing the write. It enforces **parent-side referential integrity** — preventing a parent record from being modified or removed while child records still depend on it.

### How It Works

```java
private void updateAndDeleteForeignKeyConstrainOnCompleteDB(
    Object obj, TableSchema tableSchema) throws DataException {

    List<TableSchema> allTables = ORMDataModel.getAllTableInfo();
    for (TableSchema table : allTables) {
        if (tableSchema.equals(table)) continue;  // skip the current table itself

        List<FieldSchema> fkFields = table.getForeignKeyFields();
        for (FieldSchema fkField : fkFields) {

            // Does this FK in 'table' point back to our table?
            if (fkField.getFKParentClass().equals(tableSchema.getTableName())) {

                // Get the parent column name that is referenced
                String fkParentColumn = fkField.getFKParentColumn();
                FieldSchema fs = tableSchema.getFieldByColumnName(fkParentColumn);

                // Get the current value of that column from our obj
                Object value = getFieldValue(obj, obj.getClass(), fs);
                value = formatValue(value);  // wraps strings in quotes, formats dates

                // Check if any child record references it
                String sql = "SELECT * FROM " + table.getTableName()
                           + " WHERE " + fkField.getColumnName() + " = " + value + ";";
                // Execute query
                // If any row found → throw DataException
            }
        }
    }
}
```

### What Makes This Powerful
This check is **dynamic and automatic** — it does not require the developer to know which tables reference which. It scans `ORMDataModel.getAllTableInfo()` (all loaded POJO schemas) at runtime. As long as the relevant tables are loaded by `DataManager`, the relationship is automatically enforced.

> [!IMPORTANT]
> This check only covers tables that are **loaded by DataManager** (i.e., have a POJO class in `src/`). If a referencing table does not have a POJO in `src/`, its child records will not be detected.

---

## 5. Cache Synchronization with `PojoCopier`

For `@Cacheable` tables, every write operation (save, update, delete) must keep the in-memory cache in sync with the database. The key mechanism here is `PojoCopier.copy()`:

### Why Clones are Stored (Not the Original Object)
```java
// After save() or update():
Object clonedObj = objClass.getDeclaredConstructor().newInstance();
PojoCopier.copy(clonedObj, obj);   // deep copy of obj into clonedObj
cache.get(objClass).put(primaryKey, clonedObj);
```

If the **original** `obj` were stored in the cache, the caller could continue to mutate it after `save()` / `update()`, which would corrupt the cached state. By storing a **clone**, the cache is protected — the caller's object and the cached object are independent copies.

`PojoCopier.copy()` works by:
1. Getting all declared fields of the POJO class.
2. For each field, deriving the standard getter and setter names.
3. Calling the getter on the source object.
4. Calling the setter on the target (clone) object with that value.

---

## 6. Side-by-Side Comparison: `save()` vs `update()` vs `delete()`

| Aspect | `save(obj)` | `update(obj)` | `delete(Class, pk)` |
|---|---|---|---|
| Takes object? | Yes — full POJO | Yes — full POJO | No — only class + PK value |
| Requires PK in obj? | Only if not `@AutoIncrement` | Always | No (PK passed separately) |
| PK duplicate check? | Yes (non-auto-increment only) | No (existence check instead) | No |
| Fetches current DB state? | No | Yes (for referential integrity + existence) | Yes (unless `@Cacheable`) |
| Referential integrity check? | No | Yes — checks children | Yes — checks children |
| Unique key check? | Yes (all `@Unique` fields) | Yes (excludes own record via `<>`) | No |
| FK parent check? | Yes — parent must exist | Yes — parent must exist | No |
| SQL executed | `INSERT INTO ... SET ...` | `UPDATE ... SET ... WHERE pk=?` | `DELETE FROM ... WHERE pk=?` |
| Generated key read-back? | Yes (if `@AutoIncrement`) | No | No |
| Cache updated? | Yes — entry added | Yes — entry replaced | Yes — entry removed |

---

## 7. The Validation Sequence — Decision Tree

```
save(obj) called
│
├── Is connection null?            → YES → DataException("Call begin() before save()")
│
├── Is class known to framework?  → NO  → DataException("Invalid data provided")
├── Is class a @Table POJO?       → NO  → DataException("Invalid data provided")
│
├── Is PK auto-incremented?
│   ├── NO  → Run PK uniqueness check
│   │         → PK already exists? → DataException("This record already exists...")
│   └── YES → Skip
│
├── Has @Unique fields?
│   └── YES → For each @Unique field:
│             → Duplicate found?  → DataException("This <val> is already in use...")
│
├── Has @ForeignKey fields?
│   └── YES → For each @ForeignKey field:
│             → Parent not found? → DataException("Referenced parent record...")
│
└── Execute INSERT
    └── Read generated keys (if @AutoIncrement)
    └── Update cache (if @Cacheable)
    └── Return
```

---

*End of Part 5 — Please review and confirm to proceed to Part 6.*

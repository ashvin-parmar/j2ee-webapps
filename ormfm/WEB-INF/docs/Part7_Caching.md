# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 7 — Caching: `@Cacheable` & `queryDS()`

---

> [!NOTE]
> This is **Part 7** of the ORMFM documentation. It covers the in-memory caching system in complete depth — how the cache is built, how it is queried, how it stays in sync with CRUD operations, and when to use it.

---

## 1. What Caching Means in ORMFM

When a POJO class is marked `@Cacheable`, the framework maintains a **complete copy of that table's data in memory** for the entire lifetime of the application. This in-memory copy is called the **cache**.

```
Database Table (on disk)          In-Memory Cache (in JVM heap)
──────────────────────────        ──────────────────────────────────────
│ code │ title              │     Map<Class, Map<PK, POJO>>
├──────┼────────────────────┤     {
│  1   │ Java Programming   │       Course.class → LinkedHashMap {
│  2   │ Data Structures    │         1 → Course(code=1, title="Java Programming")
│  3   │ Operating Systems  │         2 → Course(code=2, title="Data Structures")
└──────┴────────────────────┘         3 → Course(code=3, title="Operating Systems")
                                    }
                                  }
```

Every time a developer calls `queryDS(Course.class)`, the data is returned **directly from this in-memory map** — no database query is executed. Every `save()`, `update()`, and `delete()` on a `@Cacheable` table automatically keeps the map in sync with the actual database.

> [!IMPORTANT]
> `@Cacheable` is intended for **small, stable, frequently-read** tables — such as lookup/reference tables (`course`, `category`, `department`, `status`, etc.). The **entire table** is loaded into heap memory at startup. This is not suitable for large or frequently-changing tables.

---

## 2. The Cache Data Structure

The cache is a static field on `DataManager`:

```java
private static Map<Class<?>, Map<Object, Object>> cache = new HashMap<>();
```

- **Outer map key**: The POJO `Class<?>` object (e.g., `Course.class`).
- **Inner map**: A `LinkedHashMap<Object, Object>` — keyed by the **primary key value** (e.g., an `Integer`), valued by the **POJO instance**.

### Why `LinkedHashMap`?
A `LinkedHashMap` preserves **insertion order** — meaning records appear in `queryDS()` results in the same order they were loaded from the database during initialization (which is the natural `SELECT *` order). This gives consistent, predictable ordering without needing an `ORDER BY`.

```java
tableCache = new LinkedHashMap<>();
// Entries inserted in ResultSet row order
// queryDS() iterates .values() → insertion-order preserved
```

---

## 3. How the Cache is Built — Initialization

At `DataManager.initialize()` time, after building all `StatementDS` objects for a `@Cacheable` table, the framework immediately executes a full SELECT and populates the cache. Here is the exact code path:

```java
// Check: is this table @Cacheable AND does it have a @PrimaryKey?
if (tableSchema.isCacheable()) {
    if (primaryKeyIndex != -1) {            // primaryKeyIndex was found during StatementDS build
        tableCache = new LinkedHashMap<>();

        // Use the pre-built SELECT * statement
        StatementDS selectStatement = map.get("select");
        PreparedStatement ps = connection.prepareStatement(
            selectStatement.getStatement().toString()
        );
        ResultSet resultSet = ps.executeQuery();

        Object instance;
        Object convertedData = null;
        Object data;
        Object primaryKeyObj = null;

        while (resultSet.next()) {
            // Create a new POJO instance for each row
            instance = objClass.getDeclaredConstructor().newInstance();

            // Iterate all columns in SELECT order
            for (int i = 0; i < selectStatement.getResultParamsCount(); i++) {
                try {
                    // Use JDBC getter (e.g., ResultSet.getInt(1), getString(2)...)
                    data = jdbcGetterMethods.get(i).invoke(resultSet, i + 1);

                    // Convert raw JDBC value to correct Java type
                    convertedData = JDBCMethodExtractor.convertToJava(paramsType.get(i), data);

                    // Call POJO setter (e.g., setCode(1), setTitle("Java")...)
                    classSetterMethods.get(i).invoke(instance, convertedData);
                } catch (Exception e) {
                    convertedData = null;
                }

                // Track the PK column value for use as the map key
                if (primaryKeyIndex == i) primaryKeyObj = convertedData;
            }

            // Store: PK → POJO instance
            tableCache.put(primaryKeyObj, instance);
        }

        // Register in the global cache map
        cache.put(objClass, tableCache);
        resultSet.close();
        ps.close();
    }
}
```

### Key Points from the Initialization Code

| Aspect | Detail |
|---|---|
| **Column access** | Uses positional `resultSet.getXxx(i+1)` — not by column name. This is faster than the query API's `getObject(columnName)`. |
| **Type conversion** | `JDBCMethodExtractor.convertToJava()` converts each value to the correct Java type before calling the setter. |
| **PK tracking** | The loop tracks which column index is the PK (`primaryKeyIndex`), so its value can be stored as the map key. |
| **One instance per row** | A fresh POJO instance is created per row via `getDeclaredConstructor().newInstance()`. |
| **Stored directly** | The instance itself (not a clone) is stored in the `LinkedHashMap`. Cloning happens on **retrieval** in `queryDS()`. |

---

## 4. Method: `queryDS(Class objClass)`

### Signature
```java
public Object queryDS(Class objClass) throws DataException
```
Returns `Object` — cast to `List<YourClass>`.

### What It Does
Returns all records for a `@Cacheable` table as a list of POJO objects — **entirely from the in-memory cache, with no database query**.

### Complete Implementation

```java
public Object queryDS(Class objClass) throws DataException {
    Schema s = ORMDataModel.getInfo(objClass);
    if (s == null)
        throw new DataException("Invalid data provided, Data required");

    TableSchema tableSchema;
    if (s instanceof TableSchema) tableSchema = (TableSchema) s;
    else throw new DataException("Invalid data provided, Table required");

    if (!tableSchema.isCacheable())
        throw new DataException(
            objClass.getName() + " is not declared as Cacheable, " +
            "call for query() instead of using queryDS()"
        );

    session().qClass = objClass;
    List<Object> results = new ArrayList<>();

    try {
        for (Object obj : cache.get(objClass).values()) {
            // Clone EACH cached object before adding to results
            Object clonedObj = objClass.getDeclaredConstructor().newInstance();
            PojoCopier.copy(clonedObj, obj);
            results.add(clonedObj);
        }
    } catch (Exception exception) {
        throw new DataException("Unable to load data, try query() method");
    }

    return results;
}
```

### Step-by-Step Flow

```
1. Validate the class has a known schema (DataException if not found)
2. Confirm it is a @Table POJO (DataException if @View)
3. Confirm the table is @Cacheable (DataException if not — tells you to use query() instead)
4. Iterate cache.get(objClass).values()
   → These are the actual cached POJO instances (insertion-order from LinkedHashMap)
5. For each cached object:
   a. Create a fresh blank POJO: objClass.getDeclaredConstructor().newInstance()
   b. Clone all field values: PojoCopier.copy(clonedObj, obj)
   c. Add the clone to results list
6. Return results list
   → No database connection needed — begin() is NOT required for queryDS()
```

> [!IMPORTANT]
> `queryDS()` does **not** require `begin()` to have been called. There is no database access — the data comes entirely from memory. However, calling it within a `begin()`/`end()` block is harmless and is the pattern used in `testingCacheable.java`.

---

## 5. Why Every Result is a Clone — `PojoCopier.copy()`

`queryDS()` creates a **fresh clone** of each cached POJO before returning it. This is critical for cache integrity.

### The Problem Without Cloning

```java
// Without cloning — DANGEROUS
List<Course> courses = (List<Course>) dm.queryDS(Course.class);
courses.get(0).setTitle("Modified!");
// The title in the cache is now "Modified!" — cache is corrupted!
// Every future queryDS() call returns the mutated object.
```

### The Solution — Always Clone on Retrieval

```java
// With cloning — SAFE
Object clonedObj = objClass.getDeclaredConstructor().newInstance();
PojoCopier.copy(clonedObj, obj);   // copies from cached obj → fresh clonedObj
results.add(clonedObj);
// Caller receives clonedObj — modifying it has NO effect on the cache
```

### How `PojoCopier.copy()` Works

```java
public static void copy(Object target, Object source) throws Exception {
    // Ensures both are the same class
    if (!target.getClass().equals(source.getClass()))
        throw new Exception("Target and source are different classes");

    Field[] fields = target.getClass().getDeclaredFields();

    for (Field field : fields) {
        try {
            String fieldName   = field.getName();
            Class<?> fieldType = field.getType();

            // Capitalise first letter: "rollNumber" → "RollNumber"
            String stdName = fieldName.substring(0, 1).toUpperCase()
                           + fieldName.substring(1);

            // Look up setter: "setRollNumber"
            Method setter = target.getClass().getMethod("set" + stdName, fieldType);

            // Look up getter: "getRollNumber"
            Method getter = source.getClass().getMethod("get" + stdName);

            // Copy: target.setRollNumber(source.getRollNumber())
            setter.invoke(target, getter.invoke(source));
        } catch (Exception e) {
            continue;  // silently skip fields with no matching getter/setter
        }
    }
}
```

**Key characteristics of `PojoCopier.copy()`:**
- Works on **all declared fields** of the class — not just `@Column`-annotated ones.
- Silently skips any field that doesn't have a matching `getXxx()` / `setXxx()` method.
- Copies values **shallowly** — for primitive types and `String` (immutable), this is fine. For mutable objects like `java.util.Date`, both source and clone share the same Date instance.
- Does not clone nested objects — ORMFM POJOs only hold primitives, Strings, and java.sql/util Date types, so this shallow copy is sufficient.

---

## 6. Cache Synchronization with CRUD Operations

The three write operations each include a cache-sync step for `@Cacheable` tables. Here is the exact sync logic for each:

### 6.1 After `save()` — Add to Cache

```java
if (tableSchema.isCacheable()) {
    Object clonedObj = objClass.getDeclaredConstructor().newInstance();
    PojoCopier.copy(clonedObj, obj);  // clone the saved object

    // Determine PK value:
    // - For @AutoIncrement: use the generated key already set on obj by the INSERT
    // - For manual PK: use the PK getter on obj
    Object pkValue = getPrimaryKeyValue(obj, tableSchema);

    cache.get(objClass).put(pkValue, clonedObj);  // add to cache
}
```

> A clone is stored (not `obj` itself) — the caller retains `obj` and can mutate it freely after `save()` without affecting the cache.

### 6.2 After `update()` — Replace in Cache

```java
if (tableSchema.isCacheable()) {
    Object clonedObj = objClass.getDeclaredConstructor().newInstance();
    PojoCopier.copy(clonedObj, obj);  // clone the updated object

    Object pkValue = getPrimaryKeyValue(obj, tableSchema);

    // Remove old entry, put new one
    cache.get(objClass).remove(pkValue);
    cache.get(objClass).put(pkValue, clonedObj);
}
```

> Because `LinkedHashMap` preserves insertion order, removing and re-putting an entry moves it to the **end** of the map. This means after an update, the updated record appears last when iterating — insertion order is slightly altered.

### 6.3 After `delete()` — Remove from Cache

```java
if (tableSchema.isCacheable()) {
    cache.get(objClass).remove(primaryKey);  // primaryKey is the argument passed to delete()
}
```

> Simple removal by PK. The `LinkedHashMap` shrinks by one entry. The relative order of remaining entries is preserved.

---

## 7. Cache State Walkthrough — Complete Example

Following the exact sequence in `testingCacheable.java`:

```
[Startup] DataManager.initialize() is called
  → SELECT * FROM student (assume student is @Cacheable with PK roll_number)
  → Cache loaded:
    {10001 → Student(10001, "Rohit", ...), 10002 → Student(10002, "Meena", ...)}

Step 1 — queryDS() before save
  dm.begin();
  List<Student> students = dm.queryDS(Student.class);
  dm.end();
  → Returns clones of: [10001, 10002]
  → No DB query issued

Step 2 — save() a new Student (roll: 10132)
  dm.begin();
  dm.save(student_10132);    // INSERT INTO student ...
  // Cache sync: {10001→..., 10002→..., 10132→clone(student_10132)}

Step 3 — queryDS() after save
  dm.begin();
  students = dm.queryDS(Student.class);
  dm.end();
  → Returns clones of: [10001, 10002, 10132]
  → 10132 is now in results — cache was updated by save()

Step 4 — update() the new Student (change lastName and courseCode)
  dm.begin();
  dm.update(student_10132_modified);  // UPDATE student ...
  // Cache sync: remove 10132 → put 10132→clone(modified)
  // {10001→..., 10002→..., 10132→clone(modified)}

Step 5 — queryDS() after update
  students = dm.queryDS(Student.class);
  → 10132 now shows updated values

Step 6 — delete() the Student
  dm.begin();
  dm.delete(Student.class, 10132);   // DELETE FROM student WHERE roll_number=10132
  // Cache sync: remove 10132
  // {10001→..., 10002→...}

Step 7 — queryDS() after delete
  students = dm.queryDS(Student.class);
  → Back to original 2 records — 10132 is gone
```

---

## 8. Thread Safety of the Cache

The `cache` map itself is a `HashMap` — not thread-safe for concurrent writes. However:

- **Reads** (`queryDS()`) are safe because:
  - `cache.get(objClass)` returns the inner `LinkedHashMap`.
  - Iterating `.values()` is safe as long as no concurrent structural modification happens.
  - Clones are created per-thread in local variables — no shared mutation.

- **Writes** (`save()`, `update()`, `delete()` cache sync) mutate the inner `LinkedHashMap`.
  - In a multi-threaded Tomcat environment, two threads could simultaneously call `save()` on a `@Cacheable` table, causing a concurrent modification.
  - This is an acknowledged limitation (noted in `improvementREADME.md`).
  - **Practical mitigation**: `@Cacheable` is best used for tables that are read frequently but written infrequently (e.g., reference/lookup tables). Concurrent writes to such tables are rare in practice.

> [!WARNING]
> The cache is not thread-safe for concurrent writes. If multiple threads frequently write to a `@Cacheable` table simultaneously, use `query().fire()` instead to read directly from the database, which uses per-thread `Connection` objects and is fully thread-safe.

---

## 9. `queryDS()` vs `query().fire()` vs `view()` — Comparison

| Aspect | `queryDS()` | `query().fire()` | `view()` |
|---|---|---|---|
| Works on | `@Table` + `@Cacheable` only | `@Table` (any) | `@View` only |
| Data source | In-memory `LinkedHashMap` | Live database query | Live database query |
| Requires `begin()`? | No | Yes | Yes |
| Hits the database? | **Never** | Always | Always |
| Supports `where()` / filter? | No — always returns all rows | Yes | No — always returns all rows |
| Supports `orderBy()`? | No | Yes | No |
| Returns clones? | Yes — always clones | No — fresh instances from DB | No — fresh instances from DB |
| Performance | Fastest (no IO) | Standard JDBC overhead | Standard JDBC overhead |
| Data freshness | Reflects last CRUD operation | Always up-to-date from DB | Always up-to-date from DB |
| Error if class not `@Cacheable`? | Yes — `DataException` | No | N/A |

---

## 10. When to Use `@Cacheable`

### ✅ Good Candidates

| Table type | Why it's a good fit |
|---|---|
| Lookup/reference tables | `course`, `department`, `category`, `status` — small, stable, read many times per request |
| Rarely-changing master data | Records added/removed infrequently; stale data is acceptable between updates |
| Tables with < few thousand rows | Entire table fits comfortably in heap memory |
| Tables read on almost every request | Eliminates repeated DB round-trips for the same data |

### ❌ Poor Candidates

| Table type | Why it's a poor fit |
|---|---|
| Transaction/event tables | `order`, `payment`, `log` — large, high-churn, always growing |
| User data tables | `student`, `employee` — potentially large and modified frequently |
| Tables > tens of thousands of rows | Memory pressure; startup time increases |
| Tables with frequent concurrent writes | Cache sync not thread-safe under high write concurrency |

---

## 11. All `DataException` Messages from `queryDS()`

| Message | Cause |
|---|---|
| `"Invalid data provided, Data required"` | Class has no schema (not annotated or not loaded) |
| `"Invalid data provided, Table required"` | Class is a `@View` POJO — `queryDS()` is table-only |
| `"<ClassName> is not declared as Cacheable, call for query() instead of using queryDS()"` | Class is a valid `@Table` POJO but `@Cacheable` was not added to it |
| `"Unable to load data, try query() method"` | Internal failure during cloning (e.g., `PojoCopier` failed, class has no default constructor) |

---

## 12. Adding `@Cacheable` — Step-by-Step Reminder

Since `@Cacheable` is **never generated automatically by `ORMFMTool`**, here is the exact workflow:

```
1. Run ORMFMTool → generates src/your/package/Course.java
   (without @Cacheable)

2. Open src/your/package/Course.java and add @Cacheable manually:

   @Table(name="course")
   @Cacheable        ← add this line
   public class Course { ... }

3. Run ORMFMTool → createJar("school_pojo")
   (recompiles src/ — picks up @Cacheable — packages into dist/school_pojo.jar)

4. In your application:
   DataManager.initialize(new File(...));
   // @Cacheable is now detected — cache is loaded at startup

5. Use queryDS() to read from cache:
   dm.begin();
   List<Course> courses = (List<Course>) dm.queryDS(Course.class);
   dm.end();
```

> [!TIP]
> To verify caching is working, add a print statement before and after `save()` and call `queryDS()` each time — as done in `testingCacheable.java`. You should see the new record appear in the results immediately after `save()` without any additional database query.

---

*End of Part 7 — Please review and confirm to proceed to Part 8.*

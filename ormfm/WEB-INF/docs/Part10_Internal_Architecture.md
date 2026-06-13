# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 10 — Internal Architecture Deep Dive

---

> [!NOTE]
> This is **Part 10** of the ORMFM documentation. It looks inside the framework at the level of individual classes, fields, and method calls — tracing exactly how a `save()` and a `query().fire()` travel through the complete internal stack.

---

## 1. Complete Class Architecture

All 14 classes in the framework divide into four architectural layers:

```
┌─────────────────────────────────────────────────────────────────────┐
│  LAYER 1 — DEVELOPER API                                            │
│   DataManager            ORMFMTool                                  │
│   (runtime CRUD/query)   (code generator / PDF creator)             │
├─────────────────────────────────────────────────────────────────────┤
│  LAYER 2 — SCHEMA MODEL                                             │
│   ORMDataModel           Schema (interface)                         │
│   (registry)             TableSchema    ViewSchema    FieldSchema    │
│                          (table meta)   (view meta)   (column meta)  │
├─────────────────────────────────────────────────────────────────────┤
│  LAYER 3 — STATEMENT DATA STRUCTURES                                │
│   StatementDS                                                        │
│   (pre-compiled SQL + pre-resolved Method references)               │
├─────────────────────────────────────────────────────────────────────┤
│  LAYER 4 — UTILITIES                                                │
│   JDBCMethodExtractor    ORMUtils        PojoCopier                  │
│   (SQL type→Method map)  (type convert)  (POJO cloning)             │
├─────────────────────────────────────────────────────────────────────┤
│  CROSS-CUTTING                                                      │
│   DataException          9 Annotations                              │
│   (checked exception)    (@Table, @View, @Column, etc.)             │
└─────────────────────────────────────────────────────────────────────┘
```

### Class Dependency Map

```
DataManager
  ├── uses: ORMDataModel          (lookup schema at runtime)
  ├── uses: StatementDS           (get pre-built SQL + Method refs)
  ├── uses: JDBCMethodExtractor   (type conversion during CRUD)
  ├── uses: PojoCopier            (clone POJOs for cache)
  ├── uses: ORMUtils              (formatValue, camelCase — indirectly)
  ├── reads: TableSchema          (get field list, PK, FK info)
  ├── reads: ViewSchema           (get field list for views)
  └── reads: FieldSchema          (per-column metadata)

ORMDataModel
  ├── creates: TableSchema / ViewSchema   (from annotation scan)
  ├── creates: FieldSchema                (per @Column field)
  └── reads: all 9 Annotations           (via reflection)

StatementDS
  └── stores: Method refs (from JDBCMethodExtractor + POJO class reflection)

ORMFMTool
  ├── uses: ORMUtils.camelCaseRepresent()
  ├── uses: ORMUtils.jdbcToJavaMappedType()
  ├── uses: DataManager (internally in createPDFDocumentation())
  ├── uses: ORMDataModel (to get all schemas for PDF generation)
  └── reads: TableSchema / ViewSchema / FieldSchema (for PDF content)
```

---

## 2. `StatementDS` — Complete Internal Structure

`StatementDS` is the **pre-compiled package** that holds everything needed to execute one specific SQL operation against one specific POJO class. It is built once at `DataManager` initialization and reused on every operation.

### All Fields

```java
public class StatementDS {
    private StringBuilder statement;           // The SQL string
    private boolean isQuery;                   // false = write, true = read

    // WRITE-side (populated for INSERT/UPDATE/DELETE/validation statements)
    private List<Method> jdbcSetterMethods;    // PreparedStatement.setXxx(i, val)
    private List<Method> classGetterMethods;   // pojo.getXxx() — reads POJO value
    private List<Integer> statementParamsType; // SQL type of each ? parameter

    // READ-side (populated only when isQuery = true)
    private List<Method> jdbcGetterMethods;    // ResultSet.getXxx(i)
    private List<Method> classSetterMethods;   // pojo.setXxx(val) — writes into POJO
    private List<Integer> resultParamsType;    // SQL type of each result column
}
```

### The `isQuery` Flag

`StatementDS` has two operational modes controlled by `isQuery`:

| Mode | `isQuery` | Populated lists | Used for |
|---|---|---|---|
| **Write mode** | `false` (default) | `jdbcSetterMethods`, `classGetterMethods`, `statementParamsType` | INSERT, UPDATE, DELETE, PK/FK/Unique validations |
| **Read mode** | `true` | All write-mode lists + `jdbcGetterMethods`, `classSetterMethods`, `resultParamsType` | SELECT statements (table + view) |

Read mode is activated by calling `setQuery(true)` — this initializes the three read-only lists. Before this call they are `null`. All read-mode getter methods check `if (this.isQuery)` and return `null` if not set.

```java
public void setQuery(boolean isQuery) {
    if (isQuery == true && this.isQuery == false) {
        this.isQuery = true;
        jdbcGetterMethods  = new ArrayList<>();   // only created now
        classSetterMethods = new ArrayList<>();
        resultParamsType   = new ArrayList<>();
    }
}
```

### Two Parameter Counts

```java
getStatementParamsCount()  // = statementParamsType.size()
                           // Number of ? placeholders in the SQL write statement
                           // e.g., INSERT INTO course SET title=? → count = 1

getResultParamsCount()     // = resultParamsType.size()
                           // Number of columns in the SELECT result
                           // e.g., SELECT * FROM course → count = 2 (code, title)
```

### What a Fully Populated `StatementDS` Looks Like

For the `"insert"` statement of `Course.class` (auto-increment PK, one non-PK column `title`):

```
StatementDS {
  statement:           "INSERT INTO course SET title=?"
  isQuery:             false

  statementParamsType: [Types.CHAR]           // one ? → CHAR type
  jdbcSetterMethods:   [PS.setString(int,String)]  // PreparedStatement setter
  classGetterMethods:  [Course::getTitle]          // POJO getter to read value

  jdbcGetterMethods:   null  (not a query)
  classSetterMethods:  null
  resultParamsType:    null
}
```

For the `"select"` statement of `Course.class`:

```
StatementDS {
  statement:           "SELECT * FROM course"
  isQuery:             true

  statementParamsType: []  (no ? in SELECT *)
  jdbcSetterMethods:   []
  classGetterMethods:  []

  resultParamsType:    [Types.INTEGER, Types.CHAR]   // code, title
  jdbcGetterMethods:   [RS.getInt(int), RS.getString(int)]
  classSetterMethods:  [Course::setCode, Course::setTitle]
}
```

---

## 3. `ORMDataModel` — The Schema Registry

`ORMDataModel` is a **singleton** that acts as the global schema registry. It is the only place where annotation scanning happens.

### Internal Cache

```java
private static Map<Class<?>, Schema> cache = new HashMap<>();
```

This is separate from `DataManager`'s `cache` (which stores POJO data). This is a **schema metadata cache** — it maps POJO classes to their `TableSchema` or `ViewSchema` objects. Once a class is scanned, its schema is never re-built — subsequent calls return the cached result instantly.

### `getInfo(Class)` — Full Algorithm

```
getInfo(Student.class) called
│
├── Is Student.class already in cache? → return cached schema immediately
│
├── Is @Table present on Student?
│     → YES → schema = new TableSchema(Student.class, "student")
│
├── Is @View present on Student?
│     → YES → schema = new ViewSchema(Student.class, "v_...")
│
├── Neither? → throw DataException("...has no @Table or @View annotation")
│
├── Is @Cacheable present? → schema.setCacheable()
│
├── Get all declared fields: Student.getDeclaredFields()
│     For each field:
│
│       Not @Column present? → SKIP this field entirely
│
│       @Column present:
│         columnName  = @Column.name()
│         fieldName   = field.getName()
│         fieldType   = field.getType()
│         fieldSchema = new FieldSchema(fieldName, columnName, fieldType)
│
│         @PrimaryKey present?    → fieldSchema.setPrimaryKey(true); hasPrimaryKey=true
│         @AutoIncrement present? → fieldSchema.setAutoIncrement(true)
│         @Unique present?        → fieldSchema.setUnique(true)
│         @ForeignKey present?    → fieldSchema.setForeignKey(parent, column)
│
│         @SetterGetter present?  → fieldSchema.setSetterAllowed(true)
│                                   fieldSchema.setGetterAllowed(true)
│         Field is public?        → fieldSchema.setPublicAllowed(true)
│         Neither? → SKIP (private field without @SetterGetter ignored)
│
│         schema.addField(fieldSchema)
│
├── isCacheable() && !hasPrimaryKey?
│     → throw DataException("@Cacheable not allowed on the pojo, which does not have any primary key set.")
│
├── cache.put(Student.class, schema)
└── return schema
```

### Registry Lookup Methods

| Method | Returns |
|---|---|
| `getInfo(Class)` | `Schema` — `TableSchema` or `ViewSchema` for one class |
| `getAllInfo()` | `List<Schema>` — all loaded schemas (tables + views) |
| `getAllTableInfo()` | `List<TableSchema>` — only table schemas (used by FK referential check) |
| `getAllViewInfo()` | `List<ViewSchema>` — only view schemas |

---

## 4. `ORMUtils` — Utility Functions

`ORMUtils` provides three categories of utility functions, all static.

### 4.1 `camelCaseRepresent(String field)` — Full Algorithm

This is called by `ORMFMTool` for converting database column/table names to Java identifiers.

```java
public static String camelCaseRepresent(String field)
```

**Step-by-step trace for `"roll_number"`:**

```
input: "roll_number"
│
├── Skip leading non-letter characters: i=0, 'r' is letter → stop
├── First character: 'r' → lowercase → append 'r'
│   camelCaseField = "r",  i=1
│
├── Loop i=1 to end:
│   i=1: 'o' → letter/digit → append 'o'  → "ro"
│   i=2: 'l' → letter/digit → append 'l'  → "rol"
│   i=3: 'l' → letter/digit → append 'l'  → "roll"
│   i=4: '_' → NOT letter/digit → enter separator block
│          skip all non-letter chars: i=5
│          i=5: 'n' is letter → uppercase → 'N' → append 'N'  → "rollN"
│   i=6: 'u' → letter/digit → append 'u'  → "rollNu"
│   i=7: 'm' → letter/digit → append 'm'  → "rollNum"
│   i=8: 'b' → letter/digit → append 'b'  → "rollNumb"
│   i=9: 'e' → letter/digit → append 'e'  → "rollNumbe"
│   i=10: 'r' → letter/digit → append 'r' → "rollNumber"
│
└── return "rollNumber" ✓
```

**Edge cases:**
- **All non-letter chars** (e.g., `"___"`): returns `"tmp1"`, `"tmp2"`, etc. (unique names).
- **`ROLL_NUMBER`**: First char `R` uppercased → lowercase → `r`; `_` triggers uppercase of next → `rollNumber`.
- **`dateofbirth`** (no separator): → `dateofbirth` (unchanged, no word boundaries).
- **`date_of_birth`**: → `dateOfBirth`.

### 4.2 `jdbcToJavaMappedType(JDBCType)` — SQL to Java Type Mapping

Used by `ORMFMTool` when generating POJO field type declarations:

```java
private static final Map<JDBCType, Class<?>> sqlToJavaTypes = new HashMap<>();
// Loaded in static initializer:
sqlToJavaTypes.put(JDBCType.BIGINT,     Long.class);
sqlToJavaTypes.put(JDBCType.INTEGER,    Integer.class);
sqlToJavaTypes.put(JDBCType.SMALLINT,   Short.class);
sqlToJavaTypes.put(JDBCType.TINYINT,    Byte.class);
sqlToJavaTypes.put(JDBCType.FLOAT,      Double.class);
sqlToJavaTypes.put(JDBCType.DOUBLE,     Double.class);
sqlToJavaTypes.put(JDBCType.DECIMAL,    java.math.BigDecimal.class);
sqlToJavaTypes.put(JDBCType.NUMERIC,    java.math.BigDecimal.class);
sqlToJavaTypes.put(JDBCType.REAL,       Float.class);
sqlToJavaTypes.put(JDBCType.BIT,        Boolean.class);
sqlToJavaTypes.put(JDBCType.DATE,       java.util.Date.class);
sqlToJavaTypes.put(JDBCType.CHAR,       String.class);
sqlToJavaTypes.put(JDBCType.VARCHAR,    String.class);
sqlToJavaTypes.put(JDBCType.LONGVARCHAR,String.class);
```

### 4.3 `wrap(Class)` — Primitive to Wrapper Conversion

```java
public static Class<?> wrap(Class<?> type)
// If type is a primitive (int, long, etc.), returns its wrapper (Integer, Long, etc.)
// If already a reference type, returns as-is
// Example: wrap(int.class) → Integer.class
//          wrap(String.class) → String.class
```

Used when looking up setter methods via reflection — `getMethod("setCode", Integer.class)` succeeds where `getMethod("setCode", int.class)` may not (depending on how the setter is declared).

### 4.4 `parseTo(Class, String)` — HTTP Parameter Parsing

```java
public static Object parseTo(Class parameterType, String parameterValue)
```

Converts a raw `String` (typically from an HTTP request parameter) to a typed Java object. Handles all primitive/wrapper types, `String`, and falls back to `Gson.fromJson()` for complex types. Used for binding servlet request parameters to POJO fields.

### 4.5 `toJSON(Object)` — Object Serialization

```java
public static String toJSON(Object obj)
// Delegates to Gson.toJson(obj)
// Converts any Java object to a JSON string
```

---

## 5. `JDBCMethodExtractor` — The JDBC Bridge

`JDBCMethodExtractor` is a pure utility class with a **static initializer** that resolves all `PreparedStatement` setter and `ResultSet` getter `Method` objects **once at class load time** — never again. These resolved methods are stored in static `HashMap`s and looked up by SQL type integer.

### Static Initializer — All 18 Mappings

```java
static {
    Class<ResultSet>         RS = ResultSet.class;
    Class<PreparedStatement> PS = PreparedStatement.class;

    // INTEGER family
    resultSetGetters.put(Types.INTEGER,   RS.getMethod("getInt",     int.class));
    prepStmtSetters .put(Types.INTEGER,   PS.getMethod("setInt",     int.class, int.class));
    typeConverters  .put(Types.INTEGER,   int.class);

    resultSetGetters.put(Types.SMALLINT,  RS.getMethod("getInt",     int.class));
    prepStmtSetters .put(Types.SMALLINT,  PS.getMethod("setInt",     int.class, int.class));
    // ... TINYINT, BIGINT, FLOAT, REAL, DOUBLE, NUMERIC, DECIMAL,
    //     BOOLEAN, BIT, DATE, TIME, TIMESTAMP, VARCHAR, CHAR, LONGVARCHAR, OTHER
}
```

The three internal maps:

| Map | Key | Value |
|---|---|---|
| `resultSetGetters` | `int` SQL type code | `Method` for `ResultSet.getXxx(int columnIndex)` |
| `preparedStatementSetters` | `int` SQL type code | `Method` for `PreparedStatement.setXxx(int idx, type val)` |
| `typeConverters` | `int` SQL type code | Java primitive/class target type |

### Public Methods

#### `getJDBCGetter(int sqlType)` — ResultSet reader
```java
// Returns: RS.getInt, RS.getString, RS.getDate, RS.getDouble, etc.
// Falls back to RS.getObject for unknown types
return resultSetGetters.getOrDefault(sqlType, resultSetGetters.get(Types.OTHER));
```

#### `getJDBCSetter(int sqlType)` — PreparedStatement writer
```java
// Returns: PS.setInt, PS.setString, PS.setDate, PS.setDouble, etc.
// Falls back to PS.setObject for unknown types
return preparedStatementSetters.getOrDefault(sqlType, preparedStatementSetters.get(Types.OTHER));
```

#### `convertToJDBC(int sqlType, Object value)` — Java → JDBC
Called before `preparedStatement.setXxx()` — converts a Java value (from a POJO getter) to the correct type expected by the JDBC setter:

```java
String s = value.toString();
if (target == int.class)     return Integer.parseInt(s);
if (target == String.class)  return s;
if (target == Date.class)    return Date.valueOf(sdf.format(value));  // "yyyy-MM-dd"
// ... etc.
```

#### `convertToJava(int sqlType, Object value)` — JDBC → Java
Called after `resultSet.getXxx()` — converts a raw JDBC value to the correct Java type before calling a POJO setter:

```java
// Fast path — if already the right type, return immediately:
if (sqlType == Types.INTEGER && value instanceof Integer) return value;

// Conversion path — parse from string representation:
switch (sqlType) {
    case Types.INTEGER: return Integer.parseInt(value.toString());
    case Types.DATE:    return Date.valueOf(value.toString());
    // etc.
}
```

---

## 6. Full Call Stack — `dm.save(student)` Traced

Here is the complete path a single `save()` call takes through the framework:

```
dm.save(student)                                [DataManager]
│
├─ conn() → session().connection                [DataManager → Session]
│    If null → throw DataException
│
├─ student.getClass() → Student.class
├─ statements.get(Student.class)               [static HashMap in DataManager]
│    → Map<String, StatementDS>
│    If null → throw DataException
│
├─ ORMDataModel.getInfo(Student.class)         [ORMDataModel]
│    → returns cached TableSchema for student
│    If ViewSchema → throw DataException
│
├─ tableSchema.getPrimaryKeyField()            [TableSchema]
│    → FieldSchema{methodName="rollNumber", isPrimaryKey=true, ...}
│
│  ── Phase 1: PK Validation ──
│
├─ isPrimaryKeyAutoIncremented()? → false (Student has manual PK)
├─ statementMap.get("primary_key_validation")  → StatementDS
│    statement: "SELECT roll_number FROM student WHERE roll_number=?"
│    jdbcSetterMethods: [PS.setInt(int,int)]
│    classGetterMethods: [Student::getRollNumber]
│    statementParamsType: [Types.INTEGER]
│
├─ connection.prepareStatement(sql)            [JDBC]
├─ classGetterMethods[0].invoke(student)       [Reflection → Student.getRollNumber()]
│    → returns 10101 (the PK value)
├─ JDBCMethodExtractor.convertToJDBC(INTEGER, 10101) → 10101
├─ jdbcSetterMethods[0].invoke(ps, 1, 10101)   → PS.setInt(1, 10101)
├─ ps.executeQuery() → ResultSet
│    If rs.next() is true → record exists → throw DataException
├─ rs.close(); ps.close()
│
│  ── Phase 2: Unique Validation ──
│
├─ statementMap.get("unique_key_validation")   → StatementDS
│    statement: "SELECT aadhar_card_number FROM student WHERE aadhar_card_number=?;"
│    (one sub-statement per @Unique field, separated by ";")
├─ Execute check for aadharCardNumber field
│    classGetterMethods[0].invoke(student)  → "UID12345"
│    JDBCMethodExtractor.convertToJDBC(CHAR, "UID12345") → "UID12345"
│    PS.setString(1, "UID12345")
│    ps.executeQuery() → if rs.next() true → throw DataException("UID12345 is already in use")
│
│  ── Phase 3: FK Validation ──
│
├─ statementMap.get("foreign_key_validation")  → StatementDS
│    statement: "SELECT code FROM course WHERE code=?;"
├─ classGetterMethods[0].invoke(student) → 1 (the courseCode FK value)
│    JDBCMethodExtractor.convertToJDBC(INTEGER, 1) → 1
│    PS.setInt(1, 1)
│    ps.executeQuery() → if NOT rs.next() → throw DataException("Referenced parent record 1 does not exist")
│
│  ── Phase 4: INSERT Execution ──
│
├─ statementMap.get("insert")                  → StatementDS
│    statement: "INSERT INTO student SET roll_number=?, first_name=?, last_name=?,
│                aadhar_card_number=?, course_code=?, gender=?, date_of_birth=?"
│    statementParamsType: [INTEGER, CHAR, CHAR, CHAR, INTEGER, CHAR, DATE]
│    jdbcSetterMethods: [PS.setInt, PS.setString, PS.setString, PS.setString,
│                        PS.setInt, PS.setString, PS.setDate]
│    classGetterMethods: [::getRollNumber, ::getFirstName, ::getLastName,
│                         ::getAadharCardNumber, ::getCourseCode, ::getGender, ::getDateOfBirth]
│
├─ connection.prepareStatement(sql, RETURN_GENERATED_KEYS)
├─ For each column i=0..6:
│    value = classGetterMethods[i].invoke(student)  [Reflection]
│    converted = JDBCMethodExtractor.convertToJDBC(sqlType[i], value)
│    jdbcSetterMethods[i].invoke(ps, i+1, converted) [Reflection → PS.setXxx]
│
├─ ps.executeUpdate()                          [JDBC → SQL sent to DB]
│
│  ── Phase 5: Generated Key + Cache ──
│
├─ generatedKeys = ps.getGeneratedKeys()
│    (for Student with manual PK @AutoIncrement=false → no generated key expected)
│    If no auto-increment → skip
│
├─ tableSchema.isCacheable()? → false (Student not @Cacheable) → skip cache
│
└─ save() returns normally
```

---

## 7. Full Call Stack — `dm.query(Course.class).fire()` Traced

```
dm.query(Course.class)                         [DataManager]
│
├─ ORMDataModel.getInfo(Course.class)          [ORMDataModel]
│    → cached TableSchema for course
│    If null → throw DataException
│    If ViewSchema → throw DataException("Table required")
│
├─ session().qClass     = Course.class         [Session — this thread's only]
├─ session().qStatement = "SELECT * FROM course"
├─ return this                                 [for chaining]
│
│  .fire()                                     [DataManager]
│
├─ conn() → session().connection               [Session]
│    If null → throw DataException("Call begin() before fire()")
│
├─ qClass = session().qClass                   [Course.class]
│    If null → throw DataException("Call query() before fire()")
│
├─ qStatement = "SELECT * FROM course"
│
├─ ORMDataModel.getInfo(Course.class) → TableSchema
│    tableSchema.getAllFields()
│    → [FieldSchema(code/INTEGER), FieldSchema(title/CHAR)]
│
├─ connection.prepareStatement("SELECT * FROM course")  [JDBC]
├─ ps.executeQuery() → ResultSet
│
├─ resultList = new ArrayList<>()
│
├─ While rs.next():
│    instance = Course.getDeclaredConstructor().newInstance()  [Reflection]
│    For each FieldSchema fs in getAllFields():
│      value = rs.getObject(fs.getColumnName())
│      // rs.getObject("code")  → 1
│      // rs.getObject("title") → "Java Programming"
│
│      isSetterAllowed()?  → YES
│        setterName = "set" + capitalize(fs.getMethodName())
│        // "setCode", "setTitle"
│        method = Course.class.getMethod(setterName, fs.getType())
│        method.invoke(instance, value)
│        // course.setCode(1), course.setTitle("Java Programming")
│
│    resultList.add(instance)
│
├─ rs.close(); ps.close()
├─ reset()                 [clears session().qStatement, qClass, whereUsed, orderByUsed]
└─ return resultList       [as Object — caller casts to List<Course>]
```

---

## 8. `DataException` — The Framework's Checked Exception

```java
package com.ashvin.orm.fm.exceptions;

public class DataException extends Exception {
    public DataException(String message) { super(message); }
    public DataException(Throwable cause) { super(cause); }
    public DataException(Exception cause) { super(cause); }
}
```

Three constructors allow:
- **String** — user-friendly message thrown directly (most common).
- **Throwable** / **Exception** — wraps a lower-level exception (e.g., `SQLException`, `ClassNotFoundException`) when the framework catches a system error and re-throws it as a `DataException`.

**Why checked?** Checked exceptions force the developer to handle `DataException` explicitly — either with a `try-catch` or by declaring `throws DataException` on their method. This makes error handling mandatory, which is appropriate for database operations that can fail for many reasons outside the developer's control.

---

## 9. Architecture at a Glance — Data Flow Summary

```
INITIALIZATION TIME (once)                   RUNTIME (per operation)
──────────────────────────                   ──────────────────────────
conf.json                                    Developer: dm.begin()
    ↓                                            ↓
DriverManager.getConnection()                DriverManager.getConnection()
    ↓                                            → stored in ThreadLocal<Session>
DatabaseMetaData (per column)                    ↓
    ↓                                        Developer: dm.save(obj)
JDBCMethodExtractor                              ↓
  .getJDBCSetter() ─────────────────────→   StatementDS (lookup)
  .getJDBCGetter()                              ↓
    ↓                                        classGetterMethods.invoke(obj)
ORMDataModel.getInfo(Class)                      ↓
  (reads @Table, @Column, etc.)             JDBCMethodExtractor.convertToJDBC()
    ↓                                            ↓
FieldSchema list                            jdbcSetterMethods.invoke(ps, i, val)
    ↓                                            ↓
StatementDS built for each                   ps.executeUpdate() → DB
  operation per table/view                       ↓
    ↓                                        PojoCopier.copy() (if @Cacheable)
stored in statements HashMap                     ↓
                                             cache.put(pk, clone)
                                                 ↓
@Cacheable tables: SELECT * → cache          Developer: dm.end()
  → LinkedHashMap<PK, POJO>                      ↓
                                             connection.close()
                                             threadSession.remove()
```

---

*End of Part 10 — Please review and confirm to proceed to Part 11.*

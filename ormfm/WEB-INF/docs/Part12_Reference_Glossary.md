# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 12 — Final Reference & Glossary

---

> [!NOTE]
> This is **Part 12** — the final part of the ORMFM documentation. It is a consolidated quick-reference of the entire framework: every public method, every exception message, every annotation, every term defined — in one place.

---

## 1. `DataManager` — Complete Public API Reference

### Lifecycle Methods

| Method | Signature | Returns | Requires `begin()`? | Notes |
|---|---|---|---|---|
| `initialize()` | `static synchronized void initialize(File dir)` | `void` | No | Call once at startup; `synchronized` |
| `isInitialized()` | `static boolean isInitialized()` | `boolean` | No | Safe to call any time |
| `getDataManager()` | `static DataManager getDataManager()` | `DataManager` | No | Throws if not initialized |
| `begin()` | `void begin()` | `void` | N/A | Opens per-thread JDBC connection |
| `end()` | `void end()` | `void` | Yes | Closes connection + removes Session |
| `reset()` | `void reset()` | `void` | No | Clears query state only; keeps connection |

### Write Methods (Tables only)

| Method | Signature | Returns | Requires `begin()`? | Notes |
|---|---|---|---|---|
| `save()` | `void save(Object obj)` | `void` | Yes | Full validation (PK, Unique, FK) before INSERT |
| `update()` | `void update(Object obj)` | `void` | Yes | Fetches existing record first; full validation |
| `delete()` | `void delete(Class<?> c, Object pk)` | `void` | Yes | Referential integrity check before DELETE |

### Read Methods — Tables

| Method | Signature | Returns | Requires `begin()`? | Notes |
|---|---|---|---|---|
| `query()` | `DataManager query(Class c)` | `DataManager` | No (but `fire()` does) | `SELECT *`; starts chain |
| `select(cols)` | `DataManager select(Class c, String[] cols)` | `DataManager` | No (but `fire()` does) | `SELECT col1, col2, ...`; starts chain |
| `queryDS()` | `Object queryDS(Class c)` | `Object` → `List<T>` | No | Cache read; `@Cacheable` only; no DB hit |

### Read Methods — Views

| Method | Signature | Returns | Requires `begin()`? | Notes |
|---|---|---|---|---|
| `select(view)` | `DataManager select(Class c)` | `DataManager` | Yes | `SELECT *` from view; starts chain |
| `view()` | `Object view(Class c)` | `Object` → `List<T>` | Yes | Immediate `SELECT *`; uses Method refs |

### Fluent Chain Methods

| Method | Signature | Returns | Notes |
|---|---|---|---|
| `where()` | `DataManager where(String col)` | `DataManager` | Appends `WHERE col` or just `col` (if after `and()`/`or()`) |
| `eq()` | `DataManager eq(Object val)` | `DataManager` | Appends `= <formatted_val>` |
| `gt()` | `DataManager gt(Object val)` | `DataManager` | Appends `> <formatted_val>` |
| `lt()` | `DataManager lt(Object val)` | `DataManager` | Appends `< <formatted_val>` |
| `ge()` | `DataManager ge(Object val)` | `DataManager` | Appends `>= <formatted_val>` |
| `le()` | `DataManager le(Object val)` | `DataManager` | Appends `<= <formatted_val>` |
| `ne()` | `DataManager ne(Object val)` | `DataManager` | Appends `!= <formatted_val>` |
| `and()` | `DataManager and()` | `DataManager` | Appends ` AND ` |
| `or()` | `DataManager or()` | `DataManager` | Appends ` OR ` |
| `orderBy()` | `DataManager orderBy(String col)` | `DataManager` | One per chain; ascending only |
| `fire()` | `Object fire()` | `Object` → `List<T>` | Executes query; calls `reset()` after |

---

## 2. `ORMFMTool` — CLI Quick Reference

Run from directory containing `conf.json`:
```bash
java -classpath ../lib/*:lib/*:dist/*:. com.ashvin.orm.fm.ORMFMTool <choice...>
```

| Choice Keyword | Method | Prompt? | Output |
|---|---|---|---|
| `generate_pojo` | `createPojo()` | No | `src/<pkg>/<Table>.java` + `.class` per table |
| `generate_view_pojo` | `createViewPojo()` | No | `src/<pkg>/<View>.java` + `.class` per view |
| `generate_jar` | `createJar(name)` | Yes — JAR name | `dist/<name>.jar` |
| `generate_doc_pdf` | `createPDFDocumentation(name)` | Yes — PDF name | `dist/<name>.pdf` |

---

## 3. All `DataException` Messages — Consolidated

### `initialize()`
| Message | Cause |
|---|---|
| `"Already initialized, can not call again"` | Called more than once |
| `"Configuration file contains directory required"` | `null` or non-existent path |
| `"Configuration(conf.json) file required"` | `conf.json` not in provided dir |
| `"Invalid json configuration file"` | `conf.json` not valid JSON |
| `"Invalid JDBC driver: <class>"` | JDBC driver JAR missing |
| `"@Cacheable not allowed on the pojo, which does not have any primary key set."` | `@Cacheable` + no `@PrimaryKey` |
| `"Multiple primary key are not allowed"` | Two `@PrimaryKey` fields in one class |
| `"No source file available to create JAR file."` | `src/` folder missing in dir |

### `getDataManager()`
| Message | Cause |
|---|---|
| `"Must call initialize along with parent working directory"` | Not yet initialized |

### `begin()`
| Message | Cause |
|---|---|
| *(wraps SQLException)* | JDBC connection failure |

### `save()`
| Message | Cause |
|---|---|
| `"Call begin() before save()"` | Connection null |
| `"Invalid data provided, Data required."` | Class not in `statements` map |
| `"Invalid data provided, Data required"` | Class is `@View`, not `@Table` |
| `"Invalid data provided to primary key, Data required"` | Non-auto-increment PK is null |
| `"This record already exists. Please use a unique identifier."` | PK value already in DB |
| `"Invalid data provided to unique key, Data required"` | `@Unique` field is null |
| `"This <value> is already in use. Please try another."` | Duplicate unique value |
| `"Invalid data provided to foreign key, Data required"` | `@ForeignKey` field is null |
| `"Referenced parent record <value> does not exist. Please select a valid entry."` | FK parent not found |

### `update()`
| Message | Cause |
|---|---|
| `"Call begin() before update()"` | Connection null |
| `"Invalid data provided, Data required."` | Class not in `statements` map |
| `"Invalid data provided, Data required"` | Class is `@View` |
| `"Invalid data provided to primary key, Data required"` | PK field is null |
| `"Invalid <fieldName>: <value>"` | Record with given PK does not exist |
| `"Unable to update or delete record, since this record is attached with other child record(s)."` | Child records reference this record |
| `"Invalid data provided for primary key, Data required"` | PK null in unique+PK check phase |
| `"Invalid data provided for unique key, Data required"` | Unique field null |
| `"This <value> is already in use. Please try another."` | Another record has same unique value |
| `"The selected <value> does not exist. Please select a valid entry."` | FK parent not found |

### `delete()`
| Message | Cause |
|---|---|
| `"Call begin() before delete()"` | Connection null |
| `"Invalid data provided to primary key, Data required"` | `primaryKey` arg is null |
| `"Invalid data provided, Data required."` | Class not in `statements` map |
| `"Invalid data provided, Data required"` | Class is `@View` |
| `"Invalid <fieldName>: <primaryKey>"` | PK not found in cache or DB |
| `"Unable to update or delete record, since this record is attached with other child record(s)."` | Children reference this record |

### `query()` / `select(Class, String[])`
| Message | Cause |
|---|---|
| `"Invalid data provided, Data required"` | Class not known to framework |
| `"Invalid data provided, Table required"` | Class is `@View`, not `@Table` |

### `select(Class)` — View overload
| Message | Cause |
|---|---|
| `"Invalid data provided, Data required"` | Class not known |
| `"Invalid data provided, View required"` | Class is `@Table`, not `@View` |
| `"Call begin() before select()"` | Connection null |

### `view()`
| Message | Cause |
|---|---|
| `"Invalid data provided, Data required"` | Class not known |
| `"Invalid data provided, View required"` | Class is `@Table` |
| `"Call begin() before view()"` | Connection null |

### `queryDS()`
| Message | Cause |
|---|---|
| `"Invalid data provided, Data required"` | Class not known |
| `"Invalid data provided, Table required"` | Class is `@View` |
| `"<ClassName> is not declared as Cacheable, call for query() instead of using queryDS()"` | Class not `@Cacheable` |
| `"Unable to load data, try query() method"` | Clone failure (no default constructor etc.) |

### `fire()`
| Message | Cause |
|---|---|
| `"Call begin() before fire()"` | Connection null |
| `"Call query() before fire()"` | `qClass` is null (no chain started) |
| `"Invalid statement provided to fire()"` | SQL malformed (bad column name etc.) |

### `orderBy()`
| Message | Cause |
|---|---|
| `"Invalid statement, can't use multiple 'ORDER BY' in one statement"` | Called twice in one chain |

### `ORMFMTool.init()`
| Message | Cause |
|---|---|
| `"Configuration file required"` | `conf.json` not in working directory |
| `"Invalid json configuration file"` | Bad JSON |
| `"<driver class>"` *(wraps exception)* | JDBC driver not found |

### `ORMFMTool.createJar()`
| Message | Cause |
|---|---|
| `"No source file available to create JAR file."` | `src/` missing |
| `"Error: JDK required"` | Running with JRE |
| `"ormfm.jar required: (...)"` | `lib/ormfm.jar` missing |
| `"Unable to create JAR file."` | IO failure |

---

## 4. All Annotations — Quick Reference Card

| Annotation | Target | Attributes | Purpose |
|---|---|---|---|
| `@Table` | Class | `name` (String) | Marks a class as mapping to a DB table |
| `@View` | Class | `name` (String) | Marks a class as mapping to a DB view |
| `@Cacheable` | Class | *(none)* | Enables in-memory caching for a `@Table` class |
| `@Column` | Field | `name` (String) | Maps a field to a DB column by name |
| `@PrimaryKey` | Field | *(none)* | Marks the field as the primary key |
| `@AutoIncrement` | Field | *(none)* | Marks PK as auto-generated; excluded from INSERT |
| `@Unique` | Field | *(none)* | Triggers application-layer uniqueness check |
| `@ForeignKey` | Field | `parent` (String table name), `column` (String column name) | Maps FK + enables parent-existence check |
| `@SetterGetter` | Field | *(none)* | Signals framework to use `setXxx()`/`getXxx()` for this field |

### Annotation Combination Rules

| Combination | Valid? | Note |
|---|---|---|
| `@PrimaryKey` + `@AutoIncrement` | ✅ | Most common for auto-generated PKs |
| `@PrimaryKey` alone | ✅ | Manual PK — developer must set value before `save()` |
| `@Unique` + `@PrimaryKey` | ⚠️ | Redundant — PKs are already unique; no functional error but unnecessary |
| `@ForeignKey` + `@Unique` | ✅ | Valid — a FK can also be unique (1:1 relationship) |
| `@Cacheable` + no `@PrimaryKey` | ❌ | `DataException` at `initialize()` |
| `@Cacheable` on `@View` class | ⚠️ | Compiles but has no effect at runtime |
| Two `@PrimaryKey` fields | ❌ | `DataException` at `initialize()` |
| `@Column` without `@SetterGetter` on `private` field | ⚠️ | Field silently excluded from framework operations |
| `@AutoIncrement` without `@PrimaryKey` | ⚠️ | Auto-increment flag set but column may appear in INSERT |

---

## 5. Internal `StatementDS` Key Reference

These are the string keys used in `statements.get(Class).get("key")` inside `DataManager`:

| Key | SQL Pattern | Used in |
|---|---|---|
| `"insert"` | `INSERT INTO t SET c1=?, c2=?, ...` | `save()` |
| `"update"` | `UPDATE t SET c1=?, c2=?, ... WHERE pk=?` | `update()` |
| `"delete"` | `DELETE FROM t WHERE pk=?` | `delete()` |
| `"primary_key_validation"` | `SELECT pk FROM t WHERE pk=?` | `save()` Phase 1 |
| `"get_by_primary_key"` | `SELECT * FROM t WHERE pk=?` | `update()` Phase 1, `delete()` Phase 1 |
| `"unique_key_validation"` | `SELECT col FROM t WHERE col=?;` *(per unique col)* | `save()` Phase 2 |
| `"unique_and_primary_key_validation"` | `SELECT col FROM t WHERE col=? AND pk<>?;` | `update()` Phase 3 |
| `"foreign_key_validation"` | `SELECT pc FROM pt WHERE pc=?;` *(per FK col)* | `save()` Phase 3, `update()` Phase 4 |
| `"get_by_foreign_key"` | `SELECT * FROM t WHERE fk_col=?;` | Internal referential integrity |
| `"select"` | `SELECT * FROM t` (or view) | `query()`, `view()`, cache loading |

---

## 6. `ORMUtils` Method Reference

| Method | Signature | Used for |
|---|---|---|
| `jdbcToJavaMappedType()` | `static Class<?> jdbcToJavaMappedType(JDBCType t)` | SQL type → Java class in `ORMFMTool` |
| `wrap()` | `static Class<?> wrap(Class<?> type)` | Primitive → wrapper (e.g., `int.class` → `Integer.class`) |
| `parseTo()` | `static Object parseTo(Class t, String val)` | String → typed Java value (HTTP params) |
| `parseTo()` | `static Object parseTo(Class t, String val, String from)` | Same + JSON fallback via Gson |
| `toJSON()` | `static String toJSON(Object obj)` | Object → JSON string |
| `camelCaseRepresent()` | `static String camelCaseRepresent(String field)` | DB column/table name → Java camelCase |

---

## 7. Quick Decision — Which Read Method?

```
I want to read data. What method do I use?
│
├── Is it a VIEW class (@View)?
│     ├── Need ORDER BY filtering?
│     │     └── select(ViewClass).orderBy(...).fire()
│     └── Just all rows?
│           └── view(ViewClass)
│
└── Is it a TABLE class (@Table)?
      ├── Is the table @Cacheable?
      │     └── queryDS(TableClass)         ← no DB hit, fastest
      │
      ├── Need WHERE / ORDER BY filtering?
      │     ├── SELECT * ?
      │     │     └── query(TableClass).where(...).orderBy(...).fire()
      │     └── SELECT specific columns?
      │           └── select(TableClass, new String[]{...}).where(...).fire()
      │
      └── Just SELECT * all rows, no filter?
            └── query(TableClass).fire()
```

---

## 8. Glossary

| Term | Definition |
|---|---|
| **`DataManager`** | The singleton runtime class providing all CRUD and query operations. The primary interface for backend developers. |
| **`ORMFMTool`** | The developer tool for generating POJO `.java` files, compiling them, packaging a JAR, and creating a PDF reference document. |
| **POJO** | Plain Old Java Object. A simple Java class with private fields and public getters/setters. In ORMFM, every database table and view maps to one POJO class. |
| **`@Table`** | Annotation declaring a POJO class maps to a database table. Enables full CRUD operations. |
| **`@View`** | Annotation declaring a POJO class maps to a database view. Read-only. |
| **`@Cacheable`** | Annotation enabling in-memory caching for a `@Table` POJO. The entire table is loaded at `initialize()` time and kept in sync with writes. |
| **`@Column`** | Annotation mapping a private field to a database column by its exact column name. |
| **`@PrimaryKey`** | Annotation identifying a field as the table's primary key. Used for WHERE clauses in UPDATE, DELETE, and all validation statements. |
| **`@AutoIncrement`** | Annotation marking the PK as DB-generated. The column is excluded from INSERT; the generated value is read back and set on the POJO after `save()`. |
| **`@Unique`** | Annotation triggering application-layer uniqueness validation in `save()` and `update()`. |
| **`@ForeignKey`** | Annotation mapping a field to a parent table and column. `save()` and `update()` verify the parent exists; `delete()` and `update()` on the parent verify no children reference it. |
| **`@SetterGetter`** | Annotation telling the framework to use standard `getXxx()`/`setXxx()` methods for reading/writing this field. |
| **`Session`** | A `ThreadLocal`-stored inner class of `DataManager` holding one thread's JDBC `Connection`, current query string, and query-state flags. |
| **`ThreadLocal<Session>`** | Java mechanism providing each thread with its own independent copy of the `Session` object. The core of ORMFM's thread safety. |
| **`StatementDS`** | Data structure holding one pre-built SQL string and all pre-resolved `java.lang.reflect.Method` references for one specific operation on one POJO class. |
| **`ORMDataModel`** | The global singleton schema registry mapping POJO `Class<?>` objects to their `TableSchema` or `ViewSchema` metadata. |
| **`TableSchema`** | Runtime metadata object for a `@Table` POJO. Holds the table name, all `FieldSchema` entries, and provides methods for PK, FK, auto-increment, and unique field lookups. |
| **`ViewSchema`** | Runtime metadata object for a `@View` POJO. Simpler than `TableSchema` — only holds the view name and a flat list of `FieldSchema` entries. |
| **`FieldSchema`** | Runtime metadata for a single mapped field. Stores column name, Java field name, Java type, and all constraint flags (`isPrimaryKey`, `isForeignKey`, `isUnique`, etc.). |
| **`JDBCMethodExtractor`** | Utility class with a static initializer that resolves all `PreparedStatement.setXxx()` and `ResultSet.getXxx()` `Method` objects at class load time. |
| **`ORMUtils`** | Utility class providing camelCase conversion, SQL→Java type mapping, primitive→wrapper mapping, string parsing, and Gson JSON serialization. |
| **`PojoCopier`** | Utility class that copies all field values from one POJO instance to another of the same class using reflection. Used to clone POJOs before storing in or returning from the cache. |
| **`DataException`** | The framework's single checked exception class. All framework errors are wrapped or thrown as `DataException`. Developers must handle it with `try-catch` or `throws`. |
| **`conf.json`** | The JSON configuration file read by both `ORMFMTool` and `DataManager` at startup. Contains JDBC credentials and the `package-name` (used only by `ORMFMTool`). |
| **Application-layer constraints** | Validation logic performed by `DataManager` in Java (via SELECT queries) before executing any write SQL. Enforces PK uniqueness, unique field values, FK existence, and referential integrity — independently of database-level constraints. |
| **Referential integrity** | The rule preventing a parent record from being updated or deleted while child records in another table still reference it. Enforced by `updateAndDeleteForeignKeyConstrainOnCompleteDB()`. |
| **`fire()`** | The terminal method in a fluent query chain that executes the built SQL, populates POJO instances from the ResultSet, calls `reset()`, and returns a `List`. |
| **`queryDS()`** | The cache-read method for `@Cacheable` tables. Returns a `List` of cloned POJO instances entirely from in-memory storage — no database query. |
| **`begin()` / `end()` pair** | The transaction boundary for one unit of work. `begin()` opens a JDBC connection; `end()` closes it and removes the thread's `Session`. Always paired in `try-finally`. |

---

## 9. Framework Design Philosophy

ORMFM was built to teach ORM concepts by implementing them from scratch. Each design decision maps directly to a core ORM principle:

| ORMFM Feature | Concept It Teaches |
|---|---|
| `@Table`, `@View`, `@Column` annotations | How ORM frameworks map Java classes to database structures |
| `DatabaseMetaData` scanning at init time | How tools like Hibernate auto-discover schema information |
| `StatementDS` pre-building | Why ORM frameworks are fast — SQL is prepared once, not every call |
| `ThreadLocal<Session>` | How connection management works in multi-threaded servers |
| Application-layer constraint validation | The trade-off between DB-level and application-level validation |
| `@Cacheable` + `LinkedHashMap` | First-level cache concept in JPA/Hibernate |
| `PojoCopier` cloning | Why persistent objects and returned objects should be independent |
| Fluent query builder | How JPQL/HQL/Criteria API chainable queries are implemented |
| `JDBCMethodExtractor` static init | Why reflection-based frameworks resolve methods at startup, not per-call |
| `ORMFMTool` code generation | How tools like `hbm2java` or JPA schema generation work |
| PDF documentation generation | How schema documentation tooling works |

---

## 10. Known Limitations

These are known areas for improvement (consistent with the framework's learning purpose):

| Limitation | Impact | Industry Solution |
|---|---|---|
| No connection pooling | Each `begin()` opens a new JDBC connection — expensive for high-traffic apps | HikariCP, C3P0 |
| Cache writes not thread-safe | Concurrent `save()`/`update()`/`delete()` on `@Cacheable` tables can cause `ConcurrentModificationException` | `ConcurrentHashMap` or `synchronized` block |
| SQL string injection risk in query API | `formatValue()` embeds values directly into SQL string — no `?` placeholders | Parameterized `PreparedStatement` for all queries |
| No `LIMIT` / `OFFSET` support | Cannot paginate query results | Add `limit(n)` / `offset(n)` to fluent chain |
| Single `ORDER BY`, always ascending | Cannot sort descending or by multiple columns | Add `orderByDesc()`, `thenOrderBy()` |
| No `JOIN` in query API | Complex joins require database views | Add a join-building API or named query support |
| No explicit transaction control | `begin()`/`end()` always auto-commits | Add `commit()` / `rollback()` with `setAutoCommit(false)` |
| No lazy loading | All fields loaded on every query | Add `@Lazy` fields that load on demand |
| No `TIMESTAMP` / `TIME` SQL type mapping | Only `DATE` is in the type map | Add `Types.TIMESTAMP` → `java.sql.Timestamp` mapping |
| Logo pending in PDF | `logo.png` loading in PDF marked `[pending]` | Implement `ImageDataFactory.create(logoFile)` from config |

---

## 11. ORMFM vs Industry ORM — Comparison

| Feature | ORMFM | Hibernate / JPA |
|---|---|---|
| Configuration | `conf.json` | `persistence.xml` / Spring Boot `application.properties` |
| POJO generation | `ORMFMTool` → `generate_pojo` | `hbm2java`, `JPA Buddy`, `Spring Data` generators |
| Annotation style | Custom (`@Table`, `@Column`, etc.) | `javax.persistence.*` / `jakarta.persistence.*` |
| Connection management | Manual `begin()` / `end()` | `EntityManager.getTransaction().begin()` / `commit()` |
| Connection pooling | None (one per `begin()`) | Built-in (C3P0, HikariCP) |
| Thread safety | `ThreadLocal<Session>` | `EntityManager` is also per-thread (similar pattern) |
| Caching | Manual `@Cacheable` + `LinkedHashMap` | First-level (Session), Second-level (Ehcache, Redis) |
| Query language | Fluent Java API (SQL column names) | JPQL / HQL (Java field names) |
| Lazy loading | Not supported | Supported (`FetchType.LAZY`) |
| Transactions | Auto-commit per operation | Full `@Transactional` support |
| Schema validation | At startup via `DatabaseMetaData` | `ddl-auto=validate` |
| PDF documentation | Built-in via `createPDFDocumentation()` | Not a standard feature |
| Learning curve | Low — read the source | High — complex specification |

---

## 12. Complete Documentation Index

| Part | Title | What It Covers |
|---|---|---|
| **Part 1** | Introduction & Architecture | Project overview, framework components, package structure, data flow diagram |
| **Part 2** | Annotations Reference | All 9 annotations — purpose, attributes, valid combinations, full examples |
| **Part 3** | ORMFMTool: Code Generation & PDF | `init()`, `createPojo()`, `createViewPojo()`, `createJar()`, `createPDFDocumentation()`, CLI usage |
| **Part 4** | DataManager: Initialization & Lifecycle | `initialize()`, `Session`, `ThreadLocal`, `begin()`, `end()`, `reset()`, Tomcat integration |
| **Part 5** | CRUD Operations | `save()`, `update()`, `delete()` — full validation flows, auto-increment, cache sync |
| **Part 6** | Query API | `query()`, `select()`, `where()`, all comparison operators, `fire()`, `view()`, anti-patterns |
| **Part 7** | Caching: `@Cacheable` & `queryDS()` | Cache structure, initialization loading, `queryDS()`, `PojoCopier`, thread safety limits |
| **Part 8** | View Support | `@View`, `ViewSchema` vs `TableSchema`, `FieldSchema` reference, read-only enforcement |
| **Part 9** | Thread Safety & Multi-threaded Usage | `ThreadLocal` mechanics, `testingThreaded.java` walkthrough, Tomcat threading model |
| **Part 10** | Internal Architecture Deep Dive | All 14 classes, `StatementDS`, `ORMDataModel`, `ORMUtils`, `JDBCMethodExtractor`, full call stacks |
| **Part 11** | Complete Project Setup Guide | End-to-end setup, all test scripts, `testingCorner.sh`, first-time mistakes, checklist |
| **Part 12** | Final Reference & Glossary | Complete API table, all exceptions consolidated, all annotations card, glossary, philosophy |

---

> [!TIP]
> When you move to an industry ORM framework, you will recognise every concept here:
> - `@Table` → `@Entity`
> - `@Column` → `@Column` (same name!)
> - `@PrimaryKey` → `@Id`
> - `@AutoIncrement` → `@GeneratedValue(strategy = GenerationType.IDENTITY)`
> - `@ForeignKey` → `@ManyToOne` + `@JoinColumn`
> - `begin()` / `end()` → `EntityManager.getTransaction().begin()` / `commit()`
> - `queryDS()` → First-level EntityManager cache
> - `fire()` → `TypedQuery.getResultList()`
>
> The concepts are identical. Only the API surface and the level of abstraction differ.

---

*End of Part 12 — ORMFM Documentation Complete.*

---

**Documentation authored by:** Antigravity AI  
**Framework authored by:** Ashvin Parmar  
**Total parts:** 12  
**Coverage:** Complete framework — ORMFMTool, DataManager, all annotations, caching, views, thread safety, internal architecture, project setup, and reference.

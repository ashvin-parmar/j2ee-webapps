# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 1 — Introduction, Architecture & Project Overview

---

> [!NOTE]
> This is **Part 1** of the complete ORMFM documentation. It covers the project background, philosophy, overall architecture, and directory structure. Subsequent parts will cover each feature in deep detail.

---

## 1. What is ORMFM?

**ORMFM** (ORM Framework by Ashvin) is a custom-built **Java-based Object-Relational Mapping (ORM) backend framework**, designed and implemented from scratch as a learning project to understand the internals of production-grade ORM frameworks like Hibernate or EclipseLink.

The framework allows a **backend developer** to:
- Define their complete database schema in any relational database (tested with MySQL).
- Map database tables and views to plain Java POJOs (Plain Old Java Objects) automatically — no manual writing required.
- Perform **CRUD operations** (Create, Read, Update, Delete) on those POJOs using a simple, chainable API — without writing raw SQL.
- Package the generated POJOs as a JAR file for reuse in any Java project.
- Generate a **PDF documentation** of the complete database schema and POJO structure automatically.

> [!IMPORTANT]
> ORMFM is a **backend/server-side library**. It is not a standalone application. It is deployed and used inside a Java web application (e.g., a Tomcat web app), and its `DataManager` class provides the core API to backend developers.

---

## 2. Project Philosophy

This project was built with one driving idea:
> *"Before learning the actual ORM framework, build something similar to understand how it works internally."*

The goal was not to produce a 1:1 copy of Hibernate, but to independently design and implement the core concepts that real ORM frameworks use:

| Real ORM Concept            | ORMFM Equivalent                                |
|-----------------------------|-------------------------------------------------|
| Entity annotation           | `@Table`, `@View`                              |
| Column mapping              | `@Column`                                       |
| Primary key                 | `@PrimaryKey`                                   |
| Auto-generated ID           | `@AutoIncrement`                                |
| Unique constraint           | `@Unique`                                       |
| Foreign key relationship    | `@ForeignKey`                                   |
| Caching / first-level cache | `@Cacheable` (in-memory map, keyed by PK)      |
| Session / transaction       | `begin()` / `end()` / `reset()`                |
| Criteria-style queries      | `query()`, `where()`, `eq()`, `and()`, `fire()` |
| Schema generation tool      | `ORMFMTool` (POJO + JAR + PDF creator)         |
| Session-scoped connection   | `ThreadLocal<Session>` per-thread connection   |

---

## 3. Two Core Components

The framework is divided into two distinct tools that work together:

### 3.1 ORMFMTool — The Developer Tool
A **setup/code-generation tool** used **once** (or whenever the schema changes) by the developer:

- Reads the `conf.json` configuration file.
- Connects to the database using JDBC.
- Introspects `DatabaseMetaData` to discover all tables and views.
- Generates annotated Java POJO source files for every table and view found.
- Compiles those POJOs.
- Packages them into a **JAR file** (`dist/<name>.jar`).
- Generates a **PDF reference document** (`dist/<name>.pdf`) describing all tables, views, columns, constraints, and their POJO mappings.

> [!TIP]
> Think of `ORMFMTool` like running `hibernate-tools` or `jpagen` — it is a **code generator** that reads your live database and produces ready-to-use Java classes.

### 3.2 DataManager — The Runtime API
A **singleton runtime class** used by backend developers in their actual application code:

- Initialized once using `DataManager.initialize(File workingDir)`.
- Returns the singleton instance via `DataManager.getDataManager()`.
- Provides a **session-based** CRUD + query API: `save()`, `update()`, `delete()`, `query()`, `select()`, `view()`, `fire()`, etc.
- Each thread gets its own **database connection** (via `ThreadLocal<Session>`) — making it thread-safe for multi-threaded web environments (e.g., Tomcat servlets).
- Automatically validates **primary key**, **unique key**, and **foreign key** constraints before any write operation.
- Maintains an **in-memory cache** (`@Cacheable` tables) that is kept in sync with every CRUD operation.

---

## 4. Directory Structure

```
WEB-INF/
├── conf.json                         ← Configuration file (JDBC, package name)
├── web.xml                           ← Servlet configuration (for Tomcat deployment)
├── .gitignore
│
├── classes/                          ← Framework source code (the framework itself)
│   ├── create_jar.sh                 ← Shell script to compile and create ormfm.jar
│   └── com/ashvin/orm/fm/
│       ├── ORMFMTool.java            ← POJO generator + JAR creator + PDF generator
│       ├── ORMFMStarter.java         ← (Ignored) Servlet startup helper (use DataManager.initialize() instead)
│       ├── PDFCreationREADME.md      ← Notes on PDF generation dependencies
│       ├── compile.sh                ← Compile script for the framework classes
│       ├── images/
│       │   ├── correct.png           ← Used in PDF generation (✓ checkmark icon)
│       │   └── incorrect.png         ← Used in PDF generation (✗ cross icon)
│       │
│       ├── annotations/              ← All custom annotations
│       │   ├── Table.java            ← @Table — marks a class as a DB table POJO
│       │   ├── View.java             ← @View — marks a class as a DB view POJO
│       │   ├── Column.java           ← @Column — maps a field to a DB column
│       │   ├── PrimaryKey.java       ← @PrimaryKey — marks the primary key field
│       │   ├── AutoIncrement.java    ← @AutoIncrement — marks an auto-incremented PK
│       │   ├── Unique.java           ← @Unique — marks a field with a unique constraint
│       │   ├── ForeignKey.java       ← @ForeignKey — maps a field to a parent table
│       │   ├── Cacheable.java        ← @Cacheable — enables in-memory caching for table
│       │   └── SetterGetter.java     ← @SetterGetter — tells framework to use getter/setter
│       │
│       ├── exceptions/
│       │   └── DataException.java    ← Checked exception thrown by all framework operations
│       │
│       ├── model/                    ← Core runtime model classes
│       │   ├── Schema.java           ← Interface — common contract for TableSchema & ViewSchema
│       │   ├── TableSchema.java      ← Internal representation of a table POJO's metadata
│       │   ├── ViewSchema.java       ← Internal representation of a view POJO's metadata
│       │   ├── FieldSchema.java      ← Internal representation of a single column/field
│       │   ├── StatementDS.java      ← Pre-compiled SQL statement data structure (with method refs)
│       │   ├── ORMDataModel.java     ← Global registry of all loaded Schema objects
│       │   └── DataManager.java      ← THE CORE — Singleton runtime API for all operations
│       │
│       └── utils/                    ← Internal utility classes
│           ├── JDBCMethodExtractor.java  ← Maps SQL types → JDBC getter/setter methods via reflection
│           ├── ORMUtils.java             ← camelCase converter, JDBC↔Java type mapping, JSON util
│           └── PojoCopier.java           ← Deep-copies a POJO to another instance of same class
│
├── testing/                          ← Usage examples and test scripts
│   ├── conf.json                     ← Same config as above (for standalone testing)
│   ├── create_db_schema.sql          ← SQL to create the test database
│   ├── create_table_schema.sql       ← SQL to create test tables (course, student)
│   ├── testingQuery.sql              ← Sample SQL queries
│   ├── testingRule.md                ← Rules/order for running tests
│   ├── toolTest.sh                   ← Runs ORMFMTool: creates POJO + JAR + PDF
│   ├── testingAdd.java               ← Tests save() — Add a Course (auto-increment PK)
│   ├── testingAddStudent.java        ← Tests save() — Add a Student (manual PK, FK, unique)
│   ├── testingUpdate.java            ← Tests update()
│   ├── testingUpdateStudent.java     ← Tests update() on Student
│   ├── testingDelete.java            ← Tests delete()
│   ├── testingDeleteStudent.java     ← Tests delete() with FK constraint check
│   ├── testingView.java              ← Tests select(ViewClass).fire() and view()
│   ├── testingQuery.java             ← Tests query(), select(cols[]), where(), orderBy(), fire()
│   ├── testingCacheable.java         ← Tests @Cacheable + queryDS()
│   ├── testingThreaded.java          ← Multi-thread stress test — proves ThreadLocal isolation
│   ├── test.java                     ← Simple smoke test
│   ├── src/                          ← Generated POJO source files go here
│   ├── dist/                         ← Generated JAR + PDF output goes here
│   └── lib/                          ← Place ormfm.jar here for compilation
│
└── lib/                              ← External JAR dependencies
    (contains: mysql-connector, gson, iText PDF library, etc.)
```

---

## 5. Package Structure (Source)

All framework source files live under the following Java package hierarchy:

```
com.ashvin.orm.fm
│
├── ORMFMTool.java             ← Main tool class
├── ORMFMStarter.java          ← Servlet-based starter (legacy, not recommended)
│
├── annotations/               ← com.ashvin.orm.fm.annotations
│   ├── @Table
│   ├── @View
│   ├── @Column
│   ├── @PrimaryKey
│   ├── @AutoIncrement
│   ├── @Unique
│   ├── @ForeignKey
│   ├── @Cacheable
│   └── @SetterGetter
│
├── exceptions/                ← com.ashvin.orm.fm.exceptions
│   └── DataException
│
├── model/                     ← com.ashvin.orm.fm.model
│   ├── Schema (interface)
│   ├── TableSchema
│   ├── ViewSchema
│   ├── FieldSchema
│   ├── StatementDS
│   ├── ORMDataModel
│   └── DataManager
│
└── utils/                     ← com.ashvin.orm.fm.utils
    ├── JDBCMethodExtractor
    ├── ORMUtils
    └── PojoCopier
```

---

## 6. External Dependencies

ORMFM uses the following external libraries (placed in the `lib/` folder):

| Library              | Purpose                                               |
|----------------------|-------------------------------------------------------|
| **MySQL Connector/J** | JDBC driver for connecting to MySQL database         |
| **Gson (Google)**    | JSON parsing for reading `conf.json` configuration   |
| **iText PDF**        | Generating PDF documentation via `ORMFMTool`         |

> [!NOTE]
> ORMFM is designed to work with **any JDBC-compatible database**. The JDBC driver class and connection URL are configurable in `conf.json`. Only the test setup uses MySQL — the framework itself has no MySQL-specific code.

---

## 7. Configuration File — `conf.json`

Both `ORMFMTool` and `DataManager` read a `conf.json` file from the **current working directory** (or from the directory passed to `DataManager.initialize()`).

**Format:**
```json
{
  "jdbc-driver":   "com.mysql.cj.jdbc.Driver",
  "connection-url": "jdbc:mysql://localhost:3306/your_database",
  "username":      "user",
  "password":      "pass",
  "package-name":  "your.package.name"
}
```

| Key              | Purpose                                                                   |
|------------------|---------------------------------------------------------------------------|
| `jdbc-driver`    | Fully qualified JDBC driver class name (must be on the classpath)        |
| `connection-url` | JDBC connection URL with host, port, and database name                   |
| `username`       | Database username                                                         |
| `password`       | Database password                                                         |
| `package-name`   | Java package name for generated POJOs — **used only by `ORMFMTool`**. `DataManager` does **not** use this value; it recursively traverses the entire `src/` folder from the provided working directory and loads all POJO classes automatically, regardless of package. |

> [!IMPORTANT]
> The `conf.json` file must be placed in the **working directory** (`user.dir`) when using `ORMFMTool`, or in the directory passed to `DataManager.initialize(File dir)` when using the runtime API. The `DataManager` looks specifically for `conf.json` inside the provided directory.

---

## 8. Workflow Overview

Here is the complete developer workflow from database design to using the framework in code:

```
Step 1 — Design Database
    │  Create all tables and views in your SQL database.
    │  Define PKs, FKs, UNIQUE constraints, AUTO_INCREMENT etc.
    ▼

Step 2 — Write conf.json
    │  Set up JDBC credentials + package name.
    ▼

Step 3 — Run ORMFMTool (generate POJOs)
    │  ormfmTool.init()
    │  ormfmTool.createPojo()          → Generates annotated POJO .java files in src/
    │  ormfmTool.createViewPojo()      → Generates annotated view POJO .java files in src/
    ▼

Step 4 — Manually edit generated POJOs (optional but common)
    │  Open the generated .java files in src/
    │  Add any extra annotations by hand, for example:
    │    - @Cacheable on a POJO class to enable in-memory caching
    │  Save the modified files — the src/ folder is now your source of truth.
    ▼

Step 5 — Create JAR from the (possibly modified) src/ folder
    │  ormfmTool.createJar("myPojo")   → Compiles src/ + packages into dist/myPojo.jar
    │  ormfmTool.createPDFDocumentation("myDoc") → Generates dist/myDoc.pdf
    ▼

Step 6 — Add the generated JAR to your project
    │  Copy dist/myPojo.jar to your project's lib/ folder.
    ▼

Step 7 — Use DataManager in your application code
    │  DataManager.initialize(new File("/path/to/conf/dir"));
    │  DataManager dm = DataManager.getDataManager();
    │
    │  dm.begin();
    │  dm.save(myObj);
    │  dm.end();
    ▼

Done — no raw SQL needed in your business logic.
```

---

## 9. Key Design Decisions

### 9.1 ThreadLocal for Session Isolation
Each thread in a multi-threaded environment (such as a Tomcat servlet container) gets its **own** `Session` object, stored using `ThreadLocal<Session>`. This `Session` holds:
- The active `Connection` to the database.
- The current SQL query string being built (`qStatement`).
- The class being queried (`qClass`).
- Flags for `whereUsed` and `orderByUsed` to prevent malformed SQL.

This design means **no connection pooling library is required** — each thread manages its own connection lifecycle via `begin()` / `end()`.

### 9.2 Pre-compiled Statement Data Structures
At initialization time, `DataManager` does all the heavy reflection work **once** and stores the results in pre-built `StatementDS` objects per class. These contain:
- The SQL statement as a `StringBuilder`.
- References to the exact `Method` objects needed to call `PreparedStatement.setXxx()` and `ResultSet.getXxx()` for each column.
- References to the POJO's setter/getter `Method` objects.

This avoids repeated reflection at runtime during each CRUD call, making the framework efficient.

### 9.3 Constraint Validation in Application Layer
ORMFM performs **application-level constraint validation** before executing write statements:
- **Primary key uniqueness** is checked with a SELECT before INSERT (for non-auto-increment PKs).
- **Unique key** values are checked with per-column SELECTs before INSERT and UPDATE.
- **Foreign key** parent records are verified with a SELECT before INSERT and UPDATE.
- **Referential integrity** (blocking delete/update of parent records that are referenced by children) is enforced by scanning all other loaded table schemas.

> [!NOTE]
> This is a deliberate design choice: since the framework is database-agnostic, it enforces constraints at the Java layer, not relying on database-level constraint errors. This gives more meaningful, user-friendly error messages via `DataException`.

### 9.4 Dual Access Mode: Setter/Getter vs. Public Fields
The framework supports two patterns for accessing POJO fields:
- **`@SetterGetter` annotation**: The framework will use `getXxx()` and `setXxx()` methods (standard JavaBean style). This is the recommended pattern.
- **`public` field**: If the field is declared `public` without `@SetterGetter`, the framework accesses it directly via `Field.get()` / `Field.set()`.

Private fields without `@SetterGetter` are **completely ignored** by the framework.

---

*End of Part 1 — Please review and confirm to proceed to Part 2.*

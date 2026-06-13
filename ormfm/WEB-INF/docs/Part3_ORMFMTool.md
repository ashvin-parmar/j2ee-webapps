# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 3 — ORMFMTool: Code Generation & PDF

---

> [!NOTE]
> This is **Part 3** of the ORMFM documentation. It covers `ORMFMTool` in complete detail — what it does, how each method works internally, what it produces, and how to run it. This is the **setup tool** used once per project (or whenever the database schema changes).

---

## 1. What is ORMFMTool?

`ORMFMTool` is a **Java developer tool** — a code generator and documentation creator. Its job is to look at a live database, understand its structure, and produce everything a backend developer needs to use `DataManager`:

| What it produces       | Where it goes         | How                          |
|------------------------|-----------------------|------------------------------|
| Annotated POJO `.java` files | `src/<package>/` | `createPojo()` + `createViewPojo()` |
| Compiled `.class` files      | `src/<package>/` | Happens automatically as part of JAR creation |
| POJO JAR file                | `dist/<name>.jar`    | `createJar()`               |
| PDF reference document       | `dist/<name>.pdf`    | `createPDFDocumentation()`  |

The developer **does not write POJO classes by hand**. `ORMFMTool` reads your live database using `DatabaseMetaData` and generates everything automatically — including all annotations, correct Java types, getters, setters, and constraint markers.

---

## 2. Prerequisites

Before running `ORMFMTool`, the following must be in place:

| Requirement | Details |
|---|---|
| **JDK** (not just JRE) | `ORMFMTool` uses `javax.tools.JavaCompiler` to compile generated `.java` files. This API is only available in the JDK. Running with a plain JRE will cause `createPojo()` and `createJar()` to fail with "Error: JDK required". |
| **`conf.json`** in current working directory | Must contain valid JDBC credentials and `package-name`. |
| **`ormfm.jar`** in `lib/` folder | The generated POJOs import `com.ashvin.orm.fm.annotations.*`. This JAR must be available on the classpath at compile time. |
| **Database running and accessible** | `ORMFMTool` connects to the live database during both `createPojo()` and `createPDFDocumentation()`. |
| **`logo.png`** in the working directory *(optional)* | Used as a header image in the PDF. If missing, the PDF is still created — the logo section is simply skipped. |

---

## 3. Building ORMFMTool from Source

The framework source lives under `classes/com/ashvin/orm/fm/`. To compile and package the framework itself:

**Step 1 — Compile all framework source files:**
```bash
# Run from inside: classes/com/ashvin/orm/fm/
javac -classpath \
  ../../../../../classes/:../../../../../lib/*:../../../../../../../../lib/*:. \
  exceptions/*.java \
  annotations/*.java \
  utils/*.java \
  model/*.java \
  ORMFMTool.java \
  ORMFMStarter.java
```
> This is what `compile.sh` does.

**Step 2 — Package the compiled framework into `ormfm.jar`:**
```bash
# Run from inside: classes/
jar -cvf ../lib/ormfm.jar com
jar -cvf ../testing/lib/ormfm.jar com
```
> This is what `create_jar.sh` does. It places `ormfm.jar` into both `lib/` and `testing/lib/` so both the main project and the testing folder have access to it.

After this, the `ormfm.jar` is ready to be used as a dependency when compiling and running generated POJOs.

---

## 4. Method: `init()`

### Signature
```java
public void init() throws DataException
```

### What It Does
`init()` is always the **first method called** on `ORMFMTool`. It:
1. Reads `System.getProperty("user.dir")` — this is the **current working directory** (where you ran the `java` command from).
2. Looks for `conf.json` in that directory. Throws `DataException("Configuration file required")` if not found.
3. Parses the JSON and loads: `jdbc-driver`, `connection-url`, `username`, `password`, `package-name`.
4. Calls `Class.forName(jdbcDriver)` to verify the JDBC driver is on the classpath. Throws `DataException` if the class is not found.

After `init()` succeeds, the tool is ready to connect to the database.

### Error Cases

| Error | Cause |
|---|---|
| `DataException("Configuration file required")` | `conf.json` not found in current directory |
| `DataException("Invalid json configuration file")` | `conf.json` exists but is not valid JSON |
| `DataException(<driver class name>)` | JDBC driver JAR is missing from the classpath |

### Example
```java
ORMFMTool tool = new ORMFMTool();
tool.init();   // Must always be called first
```

---

## 5. Method: `createPojo()`

### Signature
```java
public final void createPojo() throws DataException
```

### What It Does
Generates **annotated POJO `.java` source files** for every table found in the database, then compiles each one immediately.

### Step-by-Step Internal Flow

```
1. Connect to DB → DriverManager.getConnection(...)
2. Get DatabaseMetaData
3. Query all TABLE-type objects → dbMetaData.getTables(null, null, "%", new String[]{"TABLE"})
4. Ensure src/<package>/ directories exist (creates them if not)
5. Verify ormfm.jar exists in lib/ (throws DataException if not)
6. For each table:
   a. Convert table name → PascalCase Java class name   [see Section 8]
   b. Query primary key columns    → dbMetaData.getPrimaryKeys(...)
   c. Query unique key columns     → dbMetaData.getIndexInfo(..., uniqueOnly=true)
   d. Query foreign key columns    → dbMetaData.getImportedKeys(...)
   e. Query all columns            → dbMetaData.getColumns(...)
   f. Write .java file using RandomAccessFile:
      - package declaration
      - import com.ashvin.orm.fm.annotations.*;
      - @Table(name="<table_name>") class header
      - For each column:
          · @PrimaryKey          (if in PK list)
          · @Unique              (if in UNIQUE index list, excluding PKs)
          · @ForeignKey(...)     (if in FK list)
          · @AutoIncrement       (if IS_AUTOINCREMENT = "YES")
          · @Column(name="<col>")
          · @SetterGetter
          · private <JavaType> <camelCaseFieldName>;
          · public setter method
          · public getter method
      - closing brace
   g. Compile the .java file → JavaCompiler.run(...)
   h. Print result: "compiled successfully." or "compilation failed!"
```

### CamelCase Naming — `ORMUtils.camelCaseRepresent()`

Table names and column names from the database are converted to camelCase Java identifiers. The rules are:

| Input (DB name)       | Output (Java name)  | Notes                                    |
|-----------------------|---------------------|------------------------------------------|
| `roll_number`         | `rollNumber`        | Underscore acts as word boundary         |
| `first_name`          | `firstName`         | Standard snake_case → camelCase          |
| `aadhar_card_number`  | `aadharCardNumber`  | Multiple underscores handled             |
| `dateofbirth`         | `dateofbirth`       | No separator → no change after first char |
| `date_of_birth`       | `dateOfBirth`       | Each `_` triggers next char to uppercase |
| `ROLL_NUMBER`         | `rollNumber`        | First char lowercased, rest follow rule  |

For **class names** (from table names), the same conversion runs first, then the first character is uppercased:
- `course` → `Course`
- `student` → `Student`
- `student_course_view` → `StudentCourseView`

### SQL Type → Java Type Mapping

`ORMUtils.jdbcToJavaMappedType()` maps the JDBC type of each column to the appropriate Java type written in the POJO:

| JDBC Type       | Java Type written in POJO    |
|-----------------|------------------------------|
| `BIGINT`        | `java.lang.Long`             |
| `INTEGER`       | `java.lang.Integer`          |
| `SMALLINT`      | `java.lang.Short`            |
| `TINYINT`       | `java.lang.Byte`             |
| `FLOAT`         | `java.lang.Double`           |
| `DOUBLE`        | `java.lang.Double`           |
| `DECIMAL`       | `java.math.BigDecimal`       |
| `NUMERIC`       | `java.math.BigDecimal`       |
| `REAL`          | `java.lang.Float`            |
| `BIT`           | `java.lang.Boolean`          |
| `DATE`          | `java.util.Date`             |
| `CHAR`          | `java.lang.String`           |
| `VARCHAR`       | `java.lang.String`           |
| `LONGVARCHAR`   | `java.lang.String`           |

### What a Generated POJO Looks Like

For a table defined as:
```sql
CREATE TABLE student (
    roll_number       INT PRIMARY KEY,
    first_name        CHAR(20) NOT NULL,
    last_name         CHAR(20) NOT NULL,
    aadhar_card_number CHAR(20) NOT NULL UNIQUE,
    course_code       INT NOT NULL,
    gender            CHAR(1) NOT NULL,
    date_of_birth     DATE NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course(code)
);
```

`createPojo()` generates exactly this `.java` file:

```java
package your.package.name;

import com.ashvin.orm.fm.annotations.*;

@Table(name="student")
public class Student
{
@PrimaryKey
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
@Unique
@Column(name="aadhar_card_number")
@SetterGetter
private java.lang.String aadharCardNumber;
public void setAadharCardNumber(java.lang.String aadharCardNumber)
{
this.aadharCardNumber=aadharCardNumber;
}
public java.lang.String getAadharCardNumber()
{
return this.aadharCardNumber;
}
@ForeignKey(parent="course",column="code")
@Column(name="course_code")
@SetterGetter
private java.lang.Integer courseCode;
public void setCourseCode(java.lang.Integer courseCode)
{
this.courseCode=courseCode;
}
public java.lang.Integer getCourseCode()
{
return this.courseCode;
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

> [!IMPORTANT]
> Notice the generated POJO does **not** include `@Cacheable` automatically. If you want in-memory caching for a particular table, you must **add `@Cacheable` manually** to the class header after generation — before running `createJar()`.

> [!NOTE]
> The column order in the generated POJO follows the column order returned by `dbMetaData.getColumns()`, which is typically the order they appear in the `CREATE TABLE` statement.

### File System After `createPojo()`

```
testing/
└── src/
    └── your/
        └── package/
            └── name/
                ├── Course.java        ← generated + compiled
                ├── Course.class
                ├── Student.java       ← generated + compiled
                └── Student.class
```

---

## 6. Method: `createViewPojo()`

### Signature
```java
public final void createViewPojo() throws DataException
```

### What It Does
Works identically to `createPojo()`, but queries for **VIEW-type** database objects instead of tables:

```java
dbMetaData.getTables(null, null, "%", new String[]{"VIEW"})
```

The key differences from table POJO generation:

| Aspect | Table POJO (`createPojo`) | View POJO (`createViewPojo`) |
|---|---|---|
| Class annotation | `@Table(name="...")` | `@View(name="...")` |
| PK detection | Yes | No |
| FK detection | Yes | No |
| Unique detection | Yes | No |
| AutoIncrement detection | Yes | No |
| Column iteration | `getColumns()` + constraint checks | `getColumns()` only |
| Operations supported | Full CRUD | Read-only (`select`, `view`) |

The generated view POJO is simpler — every column gets only `@Column` + `@SetterGetter` + the private field + getter/setter. No constraint annotations.

### Example Generated View POJO

For a view `v_student_course` with columns `first_name` (CHAR), `roll_number` (INT), `title` (CHAR):

```java
package your.package.name;

import com.ashvin.orm.fm.annotations.*;

@View(name="v_student_course")
public class VStudentCourse
{
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
@Column(name="roll_number")
@SetterGetter
private java.lang.Integer rollNumber;
// ... setter/getter
@Column(name="title")
@SetterGetter
private java.lang.String title;
// ... setter/getter
}
```

---

## 7. Method: `createJar(String targetJarFileName)`

### Signature
```java
public final void createJar(String targetJarFileName) throws DataException
```

### What It Does
1. **Strips** any spaces and the `.jar` extension from the provided name (e.g., `"my Pojo.jar"` → `"myPojo"`).
2. Falls back to `"generated_pojo"` if the name is blank after cleaning.
3. Verifies that `src/<package>/` exists and contains files.
4. Verifies `ormfm.jar` is present in `lib/`.
5. **Recompiles every `.java` file** in `src/<package>/` using `JavaCompiler` — this picks up any manual changes you made to the generated files (such as adding `@Cacheable`).
6. Creates `dist/` directory if it doesn't exist.
7. Walks `src/` recursively and packages every `.class` file into a JAR file at `dist/<name>.jar`.

### JAR Packaging Internals
- Uses `JarOutputStream` to write entries.
- Relative paths inside the JAR are computed from the `src/` folder root (e.g., `your/package/name/Student.class`).
- Only `.class` files are included — not `.java` source files.

### File System After `createJar()`

```
testing/
├── src/
│   └── your/package/name/
│       ├── Course.java
│       ├── Course.class         ← recompiled (picks up @Cacheable if you added it)
│       ├── Student.java
│       └── Student.class
└── dist/
    └── myPojo.jar               ← your.package.name.Course + Student inside
```

### The Correct Order

```
createPojo()           →  Generates .java files
createViewPojo()       →  Generates view .java files
  [ Manual editing ]   →  Add @Cacheable (or any other changes) to .java files
createJar("name")      →  Recompiles everything in src/, packages into dist/name.jar
```

> [!IMPORTANT]
> Always run `createJar()` **after** any manual edits to `.java` files. The method recompiles the entire `src/` folder so all your manual changes are included in the final JAR.

### Error Cases

| Error | Cause |
|---|---|
| `DataException("No source file available to create JAR file.")` | `src/` folder or the package subfolder doesn't exist |
| `DataException("Error: JDK required")` | Running with JRE — `JavaCompiler` is not available |
| `DataException("ormfm.jar required: (...)")` | `lib/ormfm.jar` is missing |
| `DataException("Unable to create JAR file.")` | General IO failure during JAR packaging |

---

## 8. Method: `createPDFDocumentation(String pdfFileName)`

### Signature
```java
public void createPDFDocumentation(String pdfFileName) throws DataException
```

### What It Does
Generates a detailed **PDF reference document** describing every table and view POJO that was loaded by `DataManager`. The PDF is saved to `dist/<pdfFileName>.pdf`.

### Internal Flow

```
1. If DataManager is not yet initialized → calls DataManager.initialize(parentWorkingDirectory)
2. Retrieves all TableSchema and ViewSchema objects from ORMDataModel
3. Opens a JDBC connection to the live database (to read extra column metadata for the PDF)
4. Creates a PdfWriter → PdfDocument → Document (using iText library)
5. Loads logo.png from working directory (optional — skipped gracefully if missing)
6. Loads correct.png / incorrect.png from within ormfm.jar classpath (used as ✓/✗ icons)
7. Writes SECTION 1 — "TABLE POJO(s)":
   For each TableSchema:
     - Table row with serial number
     - Inner table:
         · Table Name [DB]
         · POJO Class [fully qualified Java class name]
         · Is Cacheable? [✓ or ✗]
         · Field(s) — nested table per field:
             - Column name [DB]
             - SQL type [DB]        ← live query from DatabaseMetaData
             - Field name [Class]
             - Java type
             - Is nullable?         ← live query from DatabaseMetaData
             - Is primary key?      (shown only if true)
             - Is auto increment?   (shown only if true)
             - Is unique key?       (shown only if true)
             - Foreign key details  (shown only if true — parent table + column)
             - Is public allowed?   [✓ or ✗]
             - Is setter allowed?   [✓ or ✗]
             - Is getter allowed?   [✓ or ✗]
8. Page break
9. Writes SECTION 2 — "VIEW POJO(s)":
   For each ViewSchema:
     - View Name [DB]
     - POJO Class
     - Field(s) — nested table per field:
         - Column name [DB]
         - Field name [Class]
         - Java type
         - Is setter allowed?
         - Is getter allowed?
10. Closes document → PDF written to dist/
```

### PDF Structure (Visual)

```
┌─────────────────────────────────────────────┐
│  [logo]  ORM POJO Document                  │
│          TABLE POJO(s)                      │
├──────┬──────────────────────────────────────┤
│ S.No │ Table                                │
├──────┼──────────────────────────────────────┤
│  1   │ Table Name:    student               │
│      │ Pojo Class:    your.package.Student  │
│      │ Is Cacheable?: ✗                     │
│      │ Field(s):                            │
│      │  ┌───┬───────────────────────────┐   │
│      │  │ # │ Field Details             │   │
│      │  ├───┼───────────────────────────┤   │
│      │  │ 1 │ Column name [DB]: roll_number │
│      │  │   │ SQL type [DB]:    INT         │
│      │  │   │ Field name:       rollNumber  │
│      │  │   │ Java type:        Integer     │
│      │  │   │ Is nullable:      ✗           │
│      │  │   │ Is primary key?:  ✓           │
│      │  │   │ Is setter allowed?: ✓         │
│      │  │   │ Is getter allowed?: ✓         │
│      │  ├───┼──────────────────────────── ─┤
│      │  │ 2 │ Column name [DB]: first_name  │
│      │  │   │ ...                           │
│      │  └───┴───────────────────────────┘   │
├──────┼──────────────────────────────────────┤
│  2   │ Table Name:    course                │
│      │ ...                                  │
├──────┴──────────────────────────────────────┤
│           Creator: Ashvin Parmar            │
└─────────────────────────────────────────────┘

--- page break ---

┌─────────────────────────────────────────────┐
│  [logo]  ORM POJO Document                  │
│          VIEW POJO(s)                       │
│  ...same structure but for views...         │
└─────────────────────────────────────────────┘
```

### iText PDF Library
The PDF is built using the **iText 7** library. Key classes used:
- `PdfWriter`, `PdfDocument`, `Document` — document foundation
- `Table`, `Cell`, `Paragraph`, `Text` — nested table layout
- `Image`, `ImageDataFactory` — logo and icons
- `PdfFontFactory` with `StandardFonts.TIMES_BOLD` / `TIMES_ROMAN` — typography
- `ColorConstants.BLUE` — header cell background color
- `AreaBreak(AreaBreakType.NEXT_PAGE)` — page separator between TABLE and VIEW sections

---

## 9. Running ORMFMTool — The `main()` Method

`ORMFMTool` has a `main()` method that lets you run it directly from the command line. It accepts one or more **choice keywords** as arguments:

### Syntax
```bash
java -classpath <classpath> com.ashvin.orm.fm.ORMFMTool <choice1> [choice2] ...
```

### Valid Choices

| Keyword               | Method Called                  | Interactive Input? |
|-----------------------|--------------------------------|--------------------|
| `generate_pojo`       | `createPojo()`                 | No                 |
| `generate_view_pojo`  | `createViewPojo()`             | No                 |
| `generate_jar`        | `createJar(inputName)`         | Yes — prompts for JAR file name |
| `generate_doc_pdf`    | `createPDFDocumentation(name)` | Yes — prompts for PDF file name |

Multiple choices can be passed in one command and they execute **in order**.

### Example Commands

```bash
# Generate both table and view POJOs in one shot:
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_pojo generate_view_pojo

# Create the JAR (will prompt for name):
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_jar

# Create the PDF documentation (will prompt for name):
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_doc_pdf
```

---

## 10. The `toolTest.sh` Walkthrough

The `testing/toolTest.sh` script demonstrates the full ORMFMTool workflow in one shell script:

```bash
# Step 1 — Generate table + view POJOs together
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_pojo generate_view_pojo

# Step 2 — Create the JAR (prompts for name)
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_jar

# Step 3 — Create the PDF documentation (prompts for name)
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_doc_pdf
```

> [!IMPORTANT]
> The classpath includes three sources: `../lib/*` (the ormfm.jar from the classes directory), `lib/*` (local lib folder with MySQL/iText JARs), and `dist/*` (previously generated POJO JARs — needed by `createPDFDocumentation()` which internally initializes `DataManager` to load all POJO schemas).

---

## 11. Complete File System — Before and After

### Before running `ORMFMTool`:
```
testing/
├── conf.json
├── lib/
│   └── ormfm.jar          ← must be here already
├── logo.png               ← optional, for PDF header
└── (no src/ or dist/ yet)
```

### After `createPojo()` + `createViewPojo()`:
```
testing/
├── src/
│   └── your/
│       └── package/
│           └── name/
│               ├── Course.java
│               ├── Course.class
│               ├── Student.java
│               ├── Student.class
│               ├── VStudentCourse.java    ← view POJO
│               └── VStudentCourse.class
└── ...
```

### After manual edit + `createJar("school_pojo")`:
```
testing/
├── src/
│   └── your/package/name/
│       ├── Course.java           ← may contain @Cacheable now
│       ├── Course.class          ← recompiled
│       └── ...
└── dist/
    └── school_pojo.jar           ← ready to use in any project
```

### After `createPDFDocumentation("school_doc")`:
```
testing/
└── dist/
    ├── school_pojo.jar
    └── school_doc.pdf            ← complete schema reference
```

---

## 12. Common Errors and Fixes

| Error Message | Root Cause | Fix |
|---|---|---|
| `Configuration file required` | `conf.json` not found in current working directory | Ensure you run the `java` command from the directory that contains `conf.json` |
| `Invalid json configuration file` | `conf.json` has a syntax error | Validate the JSON using any JSON validator |
| `Invalid JDBC driver: <class>` | JDBC driver JAR not on classpath | Add the JDBC driver JAR to the `-classpath` argument |
| `ormfm.jar required: (...)` | `lib/ormfm.jar` missing | Copy `ormfm.jar` to the `lib/` folder in your working directory |
| `Error: JDK required` | Running with JRE, not JDK | Switch to a JDK installation (`javac` must be available) |
| `File '...' compilation failed!` | Generated POJO has a compile error | Usually caused by an unsupported SQL type not in the type mapping table — check the column type |
| `No source file available to create JAR file.` | `src/` folder is empty or missing | Run `createPojo()` and/or `createViewPojo()` first |
| `Unable to create <name> file.` (PDF) | iText JARs missing or `DataManager` failed to initialize | Ensure all iText JARs are on the classpath and the database is accessible |

---

*End of Part 3 — Please review and confirm to proceed to Part 4.*

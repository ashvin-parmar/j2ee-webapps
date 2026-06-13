# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 11 — Complete Project Setup Guide

---

> [!NOTE]
> This is **Part 11** of the ORMFM documentation. It is a complete, practical end-to-end guide for setting up a new project with ORMFM — from database creation to running your first query. All steps use the `testing/` folder of the framework as the reference example.

---

## Overview — What You Will Build

By the end of this guide, you will have:

```
myproject/
├── conf.json                    ← database configuration
├── lib/
│   └── ormfm.jar                ← the framework
│   └── mysql-connector.jar      ← (or your JDBC driver)
│   └── gson-*.jar               ← Gson (required by ORMFMTool)
│   └── itextpdf-*.jar           ← iText (required for PDF generation)
├── logo.png                     ← optional, used in PDF header
├── src/
│   └── your/package/name/
│       ├── Course.java          ← generated + optionally edited
│       ├── Course.class
│       ├── Student.java
│       ├── Student.class
│       └── VStudentCourse.java  ← generated view POJO
└── dist/
    ├── mypojo.jar               ← compiled POJOs, ready to use
    └── mydb_doc.pdf             ← PDF schema reference
```

---

## Step 0 — Build the Framework Itself

> [!IMPORTANT]
> This step is done **once** — by the framework developer. If you already have `ormfm.jar` (from the `lib/` folder), skip to Step 1.

```bash
# 1. Compile all framework source files
#    Run from: classes/com/ashvin/orm/fm/
javac -classpath \
  ../../../../../classes/:../../../../../lib/*:../../../../../../../../lib/*:. \
  exceptions/*.java \
  annotations/*.java \
  utils/*.java \
  model/*.java \
  ORMFMTool.java \
  ORMFMStarter.java

# 2. Package into ormfm.jar
#    Run from: classes/
jar -cvf ../lib/ormfm.jar com
jar -cvf ../testing/lib/ormfm.jar com
```

After this, `ormfm.jar` is placed in both `lib/` and `testing/lib/`.

---

## Step 1 — Create the Database

Create your MySQL database and tables. The `testing/` folder uses this schema:

**`create_db_schema.sql`** — creates the database:
```sql
CREATE DATABASE school;
USE school;
```

**`create_table_schema.sql`** — creates the tables:
```sql
CREATE TABLE course
(
    code  INT PRIMARY KEY AUTO_INCREMENT,
    title CHAR(35)
);

CREATE TABLE student
(
    roll_number        INT PRIMARY KEY,
    first_name         CHAR(20)  NOT NULL,
    last_name          CHAR(20)  NOT NULL,
    aadhar_card_number CHAR(20)  NOT NULL UNIQUE,
    course_code        INT       NOT NULL,
    gender             CHAR(1)   NOT NULL,
    date_of_birth      DATE      NOT NULL,
    FOREIGN KEY (course_code) REFERENCES course(code)
);
```

> [!TIP]
> Notice the design choices that the framework will enforce:
> - `course.code` is `AUTO_INCREMENT` → `@AutoIncrement` in POJO → never set by developer, always returned after `save()`.
> - `student.aadhar_card_number` is `UNIQUE` → `@Unique` in POJO → `save()` and `update()` validate uniqueness.
> - `student.course_code` is a `FOREIGN KEY` → `@ForeignKey` in POJO → `save()` and `update()` check parent exists; `delete()` on `course` blocks if students reference it.

If you have views, create them too:
```sql
CREATE VIEW v_student_course AS
SELECT s.first_name, s.roll_number, s.last_name,
       s.gender, s.course_code, s.aadhar_card_number, s.date_of_birth
FROM student s
JOIN course c ON s.course_code = c.code;
```

---

## Step 2 — Write `conf.json`

Place `conf.json` in your project's working directory (the directory you will run `java` from):

```json
{
    "jdbc-driver"    : "com.mysql.cj.jdbc.Driver",
    "connection-url" : "jdbc:mysql://localhost:3306/school",
    "username"       : "user",
    "password"       : "pass",
    "package-name"   : "testing.school.pojo"
}
```

| Field | Purpose | Used by |
|---|---|---|
| `jdbc-driver` | Fully qualified JDBC driver class name | `ORMFMTool` + `DataManager` |
| `connection-url` | JDBC connection URL with DB name | `ORMFMTool` + `DataManager` |
| `username` | Database username | `ORMFMTool` + `DataManager` |
| `password` | Database password | `ORMFMTool` + `DataManager` |
| `package-name` | Java package for generated POJOs | `ORMFMTool` **only** |

> [!NOTE]
> `DataManager` reads `conf.json` for connection credentials but **ignores `package-name`**. It scans the entire `src/` folder recursively to discover all `.class` files, regardless of package.

---

## Step 3 — Generate POJOs with `ORMFMTool`

Run `ORMFMTool` from your project directory (where `conf.json` lives):

```bash
# Generate table POJOs + view POJOs in one command
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_pojo generate_view_pojo
```

**Expected console output:**
```
Course compiled successfully.
Student compiled successfully.
VStudentCourse compiled successfully.
```

**What was created:**
```
src/
└── testing/
    └── school/
        └── pojo/
            ├── Course.java         ← @Table(name="course")
            ├── Course.class        ← compiled automatically
            ├── Student.java        ← @Table + @PrimaryKey + @Unique + @ForeignKey
            ├── Student.class
            ├── VStudentCourse.java ← @View(name="v_student_course")
            └── VStudentCourse.class
```

If a table's column has an unsupported SQL type, `compilation failed!` is printed for that class. Check the column type against the mapping table in Part 3.

---

## Step 4 — Manually Edit POJOs (Optional)

After generation, open any POJO you want cached in memory and add `@Cacheable`:

**Before (`Course.java` as generated):**
```java
@Table(name="course")
public class Course {
    ...
}
```

**After (manually added `@Cacheable`):**
```java
@Table(name="course")
@Cacheable
public class Course {
    ...
}
```

> [!IMPORTANT]
> `@Cacheable` requires that the class has a `@PrimaryKey` field. `Course` has `@PrimaryKey` on `code` — so this is valid. Trying to add `@Cacheable` to a view POJO or a table with no PK will throw `DataException` at `DataManager.initialize()` time.

You can also add any other custom methods to the POJO at this point:
```java
@Table(name="course")
@Cacheable
public class Course {
    // ... generated fields + getters/setters ...

    // Custom method — safe to add, won't interfere with framework
    @Override
    public String toString() {
        return "Course{code=" + code + ", title='" + title + "'}";
    }
}
```

---

## Step 5 — Package POJOs into a JAR

Run `ORMFMTool` again to recompile the modified `.java` files and package them:

```bash
java -classpath ../lib/*:lib/*:dist/*:. \
     com.ashvin.orm.fm.ORMFMTool \
     generate_jar
```

When prompted, enter a name for the JAR:
```
Enter JAR file name: mypojo
```

**Result:** `dist/mypojo.jar` — contains all compiled POJO classes including your manual changes.

> [!TIP]
> You can also generate the PDF reference document at this point:
> ```bash
> java -classpath ../lib/*:lib/*:dist/*:. \
>      com.ashvin.orm.fm.ORMFMTool \
>      generate_doc_pdf
> ```
> Enter a PDF name when prompted. The PDF is saved to `dist/mydb_doc.pdf`.

---

## Step 6 — Write Your Application Code

### Standalone Java Application

```java
import java.io.*;
import java.util.*;
import java.text.*;
import com.ashvin.orm.fm.model.*;
import com.ashvin.orm.fm.exceptions.*;
import testing.school.pojo.*;

public class MyApp {
    public static void main(String[] args) {
        try {
            // Initialize once — reads conf.json, loads POJOs from src/, builds SQL
            DataManager.initialize(new File(System.getProperty("user.dir")));
            DataManager dm = DataManager.getDataManager();

            // ── Add a course ──────────────────────────────────────────────────
            Course c = new Course();
            c.setTitle("Data Structures");
            // code is @AutoIncrement — do not set it

            dm.begin();
            dm.save(c);
            dm.end();
            System.out.println("Course saved with code: " + c.getCode());

            // ── Add a student ─────────────────────────────────────────────────
            Student s = new Student();
            s.setRollNumber(10101);
            s.setFirstName("Rohit");
            s.setLastName("Shah");
            s.setAadharCardNumber("UID12345");
            s.setCourseCode(c.getCode());  // FK → the course we just created
            s.setGender("M");
            s.setDateOfBirth(new SimpleDateFormat("dd/MM/yy").parse("15/06/01"));

            dm.begin();
            dm.save(s);
            dm.end();
            System.out.println("Student saved with roll: " + s.getRollNumber());

            // ── Query all students ────────────────────────────────────────────
            dm.begin();
            List<Student> students = (List<Student>) dm.query(Student.class).fire();
            dm.end();
            for (Student student : students) {
                System.out.println(student.getFirstName() + " " + student.getLastName());
            }

            // ── Query @Cacheable course (no DB hit) ───────────────────────────
            dm.begin();
            List<Course> courses = (List<Course>) dm.queryDS(Course.class);
            dm.end();

            // ── Query view ────────────────────────────────────────────────────
            dm.begin();
            List<VStudentCourse> joined =
                (List<VStudentCourse>) dm.view(VStudentCourse.class);
            dm.end();

            // ── Update student's course ───────────────────────────────────────
            s.setLastName("Sharma");   // update last name
            dm.begin();
            dm.update(s);
            dm.end();

            // ── Delete student first (FK constraint!) ─────────────────────────
            dm.begin();
            dm.delete(Student.class, s.getRollNumber());
            dm.end();

            // ── Now delete the course (no students reference it anymore) ──────
            dm.begin();
            dm.delete(Course.class, c.getCode());
            dm.end();

        } catch (DataException de) {
            System.out.println("DataManager error: " + de.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
```

### Tomcat Web Application

```java
// In your AppStartupListener.java
public class AppStartupListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            File configDir = new File(sce.getServletContext().getRealPath("/WEB-INF"));
            DataManager.initialize(configDir);
        } catch (DataException de) {
            System.out.println("ORMFM init failed: " + de.getMessage());
        }
    }
}
```

Register in `web.xml`:
```xml
<listener>
    <listener-class>AppStartupListener</listener-class>
</listener>
```

---

## Step 7 — Compile and Run

### Classpath Structure

All `java` commands for your application need three sources on the classpath:

| Classpath Entry | What it provides |
|---|---|
| `../lib/*` | `ormfm.jar` (framework classes) |
| `lib/*` | JDBC driver, Gson, iText JARs |
| `dist/*` | Your generated POJO JAR (`mypojo.jar`) |
| `.` | Current directory (for `.class` files in the current folder) |

### Compile your application class:
```bash
javac -classpath ../lib/*:lib/*:dist/*:. MyApp.java
```

### Run your application:
```bash
java -classpath ../lib/*:lib/*:dist/*:. MyApp
```

---

## Step 8 — Testing Scripts Reference

The `testing/` folder contains a complete set of shell scripts demonstrating every framework operation. Here is a reference for each:

### `testing.sh` — Basic add + query
```bash
java -classpath ../lib/*:dist/*:. testingAdd [course_title]
java -classpath ../lib/*:dist/*:. testing
```
Tests saving a `Course` and reading all courses back.

---

### `testingAdd.sh` — Add a course
```bash
java -classpath ../lib/*:dist/*:. testingAdd "Java Programming"
```
Saves one course. Prints the generated `code` and all current courses.

---

### `testingAddStudent.sh` — Add a student
```bash
java -classpath ../lib/*:dist/*:. testingAddStudent \
     10101 Rohit Shah UID12345 22 M 15/06/01
#    [roll] [first] [last] [aadhar] [course_code] [gender] [dob]
```
Saves a student with full constraint validation (PK unique, aadhar unique, course FK must exist). Then queries all students.

---

### `testingUpdate.sh` — Update a course title
```bash
java -classpath ../lib/*:dist/*:. testingUpdate 22 "Advanced Java"
#                                                [code] [new_title]
```

---

### `testingUpdateStudent.sh` — Update a student
```bash
java -classpath ../lib/*:dist/*:. testingUpdateStudent \
     10101 Rohit Shah UID12345 22 M 15/06/01
```
Updates all fields for roll number `10101`. FK and unique validations run.

---

### `testingDelete.sh` — Delete a course
```bash
java -classpath ../lib/*:dist/*:. testingDelete 22
#                                               [code]
```
Deletes course with code `22`. Fails with `DataException` if any student references it.

---

### `testingDeleteStudent.sh` — Delete a student
```bash
java -classpath ../lib/*:dist/*:. testingDeleteStudent 10001
#                                                      [roll_number]
```

---

### `testingQuery.sh` — Fluent query patterns
```bash
java -classpath ../lib/*:dist/*:. testingQuery
```
Runs 8 query patterns in sequence (4 valid + 4 invalid):

| # | Pattern | Expected |
|---|---|---|
| 1 | `query().where("first_name").eq("Rohit").fire()` | All Rohit students |
| 2 | `select(["first_name","last_name"]).where("first_name").eq("Rohit").fire()` | Only 2 columns populated |
| 3 | `select([...]).where("first_name").eq(...).and().where("course_code").eq(22).fire()` | Two-condition filter |
| 4 | `select([...]).where("first_name").eq(...).orderBy("last_name").fire()` | Ordered by last_name |
| 5 | Same as 1 repeated | Tests state reset after previous `fire()` |
| 6 | `select(["abcd","last_name"])...fire()` | `DataException` — invalid column "abcd" |
| 7 | `.where("first_name").and().where(...)` | `DataException` — missing comparison after `where()` |
| 8 | `.orderBy("last_name").orderBy("first_name")` | `DataException` — duplicate `orderBy()` |

---

### `testingView.sh` — View queries
```bash
java -classpath ../lib/*:dist/*:. testingView
```
Runs 4 view query patterns:

| # | Pattern | Notes |
|---|---|---|
| 1 | `select(V1.class).fire()` | All rows from V1 view |
| 2 | `select(V4.class).orderBy("first_name").fire()` | Ordered view |
| 3 | `view(V1.class)` | Direct immediate query — faster for full result |
| 4 | `view(V4.class)` | Prints all columns directly |

---

### `testingCacheable.sh` — In-memory cache demo
```bash
java -classpath ../lib/*:dist/*:. testingCacheable
```

Runs this sequence (requires `Student` POJO to have `@Cacheable`):

```
1. queryDS(Student.class)          → prints current students from memory
2. save(student_10132)             → INSERT + cache sync
3. queryDS(Student.class)          → student 10132 now visible (no DB query)
4. update(student_10132_modified)  → UPDATE + cache sync
5. queryDS(Student.class)          → updated values visible
6. delete(Student.class, 10132)    → DELETE + cache sync
7. queryDS(Student.class)          → student 10132 gone
```

---

### `testingThreaded.sh` — Thread safety stress test
```bash
java -classpath ../lib/*:dist/*:. testingThreaded
```

15 threads × 30 operations × 3 tests = 1,350 individual operations.  
All must succeed. Final line: `✅  ALL PASSED — ThreadLocal isolation is working.`

---

### `testingCorner.sh` — FK constraint enforcement order

This script demonstrates the **correct deletion order** when FK constraints exist:

```bash
# Course 14 has Student 10001 referencing it.

java ... testingDelete 14          # ❌ FAILS — student 10001 references course 14
java ... testingUpdate 14 "English" # ❌ FAILS — same reason

java ... testingDeleteStudent 10001 # ✅ Delete the student first (no children)
java ... testingUpdate 14 "English" # ✅ Now course 14 can be updated
java ... testingDelete 14           # ✅ Now course 14 can be deleted
```

This demonstrates the referential integrity enforcement at the application layer — `DataManager` checks for child records before allowing any update or delete of a parent.

---

## Step 9 — Common First-Time Mistakes

| Mistake | Symptom | Fix |
|---|---|---|
| Forgot `dm.end()` after an exception | Next `begin()` on that thread opens a new connection — the old one leaks | Always use `try-finally { dm.end(); }` |
| `dm.begin()` not called before `save()` | `DataException("Call begin() before save()")` | Always call `begin()` first |
| Running with JRE instead of JDK | `DataException("Error: JDK required")` from `createJar()` | Switch to a JDK — ensure `javac` is on PATH |
| `conf.json` in wrong directory | `DataException("Configuration file required")` | Run the `java` command from the folder containing `conf.json` |
| POJO JAR not on classpath | `ClassNotFoundException` for your POJO package | Add `dist/mypojo.jar` to the `-classpath` argument |
| `ormfm.jar` missing from `lib/` | `DataException("ormfm.jar required: ...")` from `createPojo()` | Copy `ormfm.jar` to `lib/` in your project directory |
| Added `@Cacheable` without `@PrimaryKey` | `DataException("@Cacheable not allowed...")` at startup | Every `@Cacheable` POJO must have exactly one `@PrimaryKey` field |
| Multiple `@PrimaryKey` fields | `DataException("Multiple primary key are not allowed")` | Only one field may have `@PrimaryKey` per class |
| Used `query()` with a view class | `DataException("Table required")` | Use `select(ViewClass).fire()` or `view(ViewClass)` |
| Used `queryDS()` on non-`@Cacheable` class | `DataException("...is not declared as Cacheable...")` | Add `@Cacheable` to POJO and re-run `createJar()` |
| Deleted parent before children | `DataException("Unable to update or delete...")` | Delete children first, then parent (see `testingCorner.sh`) |
| Invalid column name in `select(cols[])` | `DataException("Invalid statement provided to fire()")` | Use exact DB column names, not Java field names |

---

## Complete Setup Checklist

```
□ Database created and tables defined
□ conf.json written in project working directory
□ JDBC driver JAR present in lib/
□ ormfm.jar present in lib/
□ Gson JAR present in lib/ (needed by ORMFMTool)
□ iText JARs present in lib/ (needed for PDF generation)
□ ORMFMTool run: generate_pojo [+ generate_view_pojo]
□ All POJOs compiled successfully (no "compilation failed!" messages)
□ @Cacheable added manually to desired POJOs (if any)
□ createJar() run — dist/mypojo.jar created
□ Application compiled with correct classpath
□ DataManager.initialize() called once at startup
□ Every begin() paired with end() in try-finally
```

---

*End of Part 11 — Please review and confirm to proceed to Part 12 (Final Reference & Glossary).*

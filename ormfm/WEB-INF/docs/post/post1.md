# Post 1 - What is ORM and Why It Exists

Most Java developers write SQL. Then they write Java code. Then they spend time converting between the two.

You get a row from the database. You manually set each field on a Java object. One column at a time. Every single time.

ORM frameworks solve exactly that problem.

ORM stands for Object-Relational Mapping. It is a layer between your Java code and your database. You work with Java objects. The framework handles the SQL.

You define a class:

```java
class Student {
    int rollNumber;
    String firstName;
    int courseCode;
}
```

The framework maps it to a table automatically. When you save the object, it writes the SQL. When you query, it fills the object back.

Without ORM:
- You write SQL strings everywhere
- You manually loop through ResultSet
- A single column rename breaks everything

With ORM:
- Your object is the table
- SQL is handled internally
- You think in Java, not SQL

ORM is not magic. It is just a very organized layer of abstraction built on top of JDBC.

Understanding how it works internally, not just how to use it, is what separates a good Java developer from a great one.

---

**Image generation prompt:**
`Flat tech illustration. Left side shows a database table with rows and columns. Right side shows a Java class with fields. In the middle, a wide arrow labeled ORM connecting them. Clean white background. Blue and orange color palette. Minimal style.`

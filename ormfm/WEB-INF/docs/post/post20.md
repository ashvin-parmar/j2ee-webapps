# Post 20 - Referential Integrity: Who Is Responsible for Enforcing It?

You have two tables. Course and Student. A student belongs to a course.

```sql
FOREIGN KEY (course_code) REFERENCES course(code)
```

The database will reject any student insert where course_code does not exist. That is referential integrity at the database level.

But there is also the other side: what happens when you try to delete a course that still has students?

The database blocks it with a constraint violation error. Again, ugly. Hard to turn into a clean user message.

So you have a choice:

Let the database enforce it and catch the error. Or check in your application layer first.

Application layer check:

```java
// Before deleting course with code=22
String sql = "SELECT roll_number FROM student WHERE course_code = ?";
// If any results exist, throw a clean exception
throw new DataException("Cannot delete. Students are enrolled in this course.");
```

Now the error is readable. The user knows exactly what went wrong and what they need to do.

The important ordering rule:

When deleting related records, always delete children before parents. Delete students before deleting the course they belong to. The parent cannot be deleted while children reference it.

This is not a bug. This is correct behavior. The database is protecting your data from becoming inconsistent.

Understand your data relationships. Respect the order of operations. And always give your users a message that helps them fix the problem, not a raw SQL error.

---

**Image generation prompt:**
`Diagram showing two tables: Course table on left, Student table on right. FK arrow from Student to Course. Delete arrow pointing at Course blocked by a shield icon labeled constraint. Below, a two step diagram: Step 1 delete Students, Step 2 delete Course with green checkmarks. Flat minimal illustration. Red and green palette. White background.`

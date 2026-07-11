# Post 12 - Database Views: The Most Underused Feature in SQL

Most developers use tables every day. Very few use views the way they should.

A view is a saved SELECT query with a name. You query it like a table. But the data comes from one or more underlying tables.

```sql
CREATE VIEW v_student_details AS
SELECT s.first_name, s.last_name,
       c.title AS course_title
FROM student s
JOIN course c ON s.course_code = c.code;
```

Now you can query:
```sql
SELECT * FROM v_student_details;
```

No JOIN in your application code. The complexity lives in the database where it belongs.

Why views are useful:

1. Simplify complex joins: Your Java code queries one view, not three joined tables.
2. Read-only safety: Views can expose limited data without giving access to full tables.
3. Stable interface: If the underlying table structure changes, you update the view, not every query in the application.
4. Performance: Some databases cache view execution plans.

When to use views:
- Complex multi-table reporting queries
- When you want to expose a joined result as if it were a single table
- When different parts of the app need different projections of the same data

Views are read-only. You cannot INSERT or UPDATE through them in most cases. But for read-heavy reporting, they are exactly the right tool.

Stop writing the same JOIN five times in five different places. Write it once as a view. Query the view everywhere.

---

**Image generation prompt:**
`Diagram showing two tables (student and course) at the bottom with a JOIN arrow between them. Above both tables, a view box labeled v_student_details showing merged columns. Arrow from application pointing to the view, not the tables. Clean flat illustration. Soft purple and blue palette. White background.`

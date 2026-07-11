# Post 8 - Fluent API: The Pattern Behind Query Builders

Have you used code like this?

```java
query(Student.class)
    .where("first_name").eq("Rohit")
    .orderBy("last_name")
    .fire();
```

This is called a fluent API or method chaining. It reads almost like English.

The secret behind it is simple: every method returns this (the same object).

```java
public QueryBuilder where(String column) {
    this.sql.append(" WHERE ").append(column);
    return this; // return same object
}

public QueryBuilder eq(Object value) {
    this.sql.append(" = '").append(value).append("'");
    return this;
}
```

Because each method returns the same object, you can call the next method immediately on the result.

Why is this useful?
1. The code reads naturally
2. Each method does one small thing
3. The final method (fire, execute, build) triggers the actual work
4. Invalid chains fail at a logical point, not silently

Almost all modern Java libraries use this. JDBC, JPA Criteria API, Stream API, StringBuilder.

String.replace().trim().toLowerCase() - that is also method chaining.

When you design a library or internal tool, think about whether a fluent API would make it easier to use. Most of the time, the answer is yes.

Simple pattern. Big impact on readability.

---

**Image generation prompt:**
`Code snippet illustration. Shows method chain query.where().eq().orderBy().fire() with each method highlighted in a different soft color. Arrows connecting each method call. Clean dark background. Minimal monospace font style. Tech illustration.`

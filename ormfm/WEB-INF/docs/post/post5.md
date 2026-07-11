# Post 5 - SQL Injection and Why PreparedStatement Matters

This is a real vulnerability that still exists in production Java code.

```java
String sql = "SELECT * FROM user WHERE name = '" + name + "'";
```

If someone passes this as name:
```
' OR '1'='1
```

The SQL becomes:
```sql
SELECT * FROM user WHERE name = '' OR '1'='1'
```

Which returns every row in the table.

This is SQL injection. And it is completely preventable.

PreparedStatement fixes this:

```java
String sql = "SELECT * FROM user WHERE name = ?";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, name);
```

Here the ? is a placeholder. The value is passed separately. The database driver handles escaping. No matter what the user types, it is treated as data, not SQL.

Why do developers still make this mistake?

Because string concatenation feels fast and easy. And in small projects it seems to work fine. Until it does not.

If you are building a query builder or any dynamic SQL layer in Java, always use PreparedStatement with parameter binding. Store the ? placeholders in your SQL string and collect the values separately.

The rule is simple: never put user input directly into a SQL string.

No exception to this rule.

---

**Image generation prompt:**
`Split screen illustration. Left panel shows red highlighted SQL string concatenation with a warning icon. Right panel shows green PreparedStatement with question mark placeholder and setString call. Clean white background. Red and green color contrast. Flat minimal tech style.`

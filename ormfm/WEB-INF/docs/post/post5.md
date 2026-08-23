# Post 5 - SQL Injection and Why PreparedStatement Matters

Let me show you a line of code that looks completely fine but is actually dangerous.

```java
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

This works perfectly in normal use. If a user types `john`, the query becomes:

```sql
SELECT * FROM users WHERE username = 'john'
```

No problem.

Now imagine someone types this into the input field:

```
' OR '1'='1
```

Your code blindly puts that into the string. The query becomes:

```sql
SELECT * FROM users WHERE username = '' OR '1'='1'
```

`'1'='1'` is always true. So the WHERE condition is always true. The database returns every single row in the table.

One input. All your data exposed.

This is SQL injection. It is not a complex hack. It is just the attacker treating your SQL string as code, not data.

It gets worse. An attacker can also do this:

```
'; DROP TABLE users; --
```

Which becomes:

```sql
SELECT * FROM users WHERE username = ''; DROP TABLE users; --'
```

Depending on the database configuration, that second statement actually runs. Your table is gone.

This has been in the OWASP Top 10 security risks list for over 15 years. And it still happens. Because string concatenation looks harmless until it is not.

The fix is PreparedStatement.

```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement ps = connection.prepareStatement(sql);
ps.setString(1, username);
ResultSet rs = ps.executeQuery();
```

The `?` is a placeholder. The actual value is passed separately using `setString()`.

Here is the key difference: the database receives the SQL structure and the value as two separate things. The value is never interpreted as SQL. No matter what the user types, it is treated as plain text data.

So if someone types `' OR '1'='1`, the database literally searches for a user whose username is the text `' OR '1'='1`. It finds nothing. No breach.

Why do developers still use string concatenation?

Because it is shorter to write. Because it works in testing. Because the data in tests is always clean. The problem only shows up when real users find the edge case. Or when someone is actively looking for it.

Three things to remember:

1. Never put any user input directly into a SQL string
2. Always use `?` placeholders and `setXxx()` methods
3. PreparedStatement also improves performance because the database can cache the execution plan

The rule has no exceptions. If the value comes from outside your code, it goes through a placeholder. Always.

---

**Image generation prompt:**
`Split screen illustration. Left panel labeled Dangerous shows SQL string with username value concatenated directly, red warning border, attacker icon injecting code. Right panel labeled Safe shows PreparedStatement with question mark placeholder and setString call below it, green border, shield icon. Clean white background. Red and green contrast. Flat minimal tech style.`

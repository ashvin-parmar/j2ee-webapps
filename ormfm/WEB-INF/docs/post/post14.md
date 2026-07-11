# Post 14 - Always Close Your Resources: The try-finally Rule

This is one of the most common sources of bugs and memory leaks in Java applications:

```java
Connection conn = dataSource.getConnection();
PreparedStatement ps = conn.prepareStatement(sql);
ResultSet rs = ps.executeQuery();
// ... process ...
rs.close();
ps.close();
conn.close();
```

Looks fine. But what if an exception is thrown during processing?

rs.close() never runs. ps.close() never runs. conn.close() never runs. The connection is leaked. Do this enough times and you run out of connections.

The fix: try-finally

```java
Connection conn = dataSource.getConnection();
try {
    // work
} finally {
    conn.close(); // always runs, even after exception
}
```

Or with try-with-resources (Java 7+):

```java
try (Connection conn = dataSource.getConnection();
     PreparedStatement ps = conn.prepareStatement(sql)) {
    // work
} // auto-closed
```

Try-with-resources calls close() automatically when the block exits, even on exception. The cleanest option.

This applies to everything:
- Database connections
- File streams
- Network sockets
- Any Closeable object

The rule is: if you open it, you close it. Always. In a finally block or with try-with-resources.

Connection leaks are frustrating to debug because the application works fine under light load and breaks only when traffic increases. Do not wait for that lesson.

---

**Image generation prompt:**
`Flow diagram showing two paths. Top path: normal code flow with open and close resource. Bottom path: exception occurs, close is skipped, resource leak shown as dripping faucet icon. Arrow pointing to try-finally block as the solution showing always closes. Clean flat illustration. Blue and red palette. White background.`

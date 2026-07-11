# Post 4 - ThreadLocal in Java: One Object, Different Data Per Thread

Imagine a web server handling 200 requests at the same time.

Every request runs on a different thread. But your service class is a single shared instance.

If you store state in a regular field, all threads read and write to the same variable. That causes data corruption.

ThreadLocal solves this cleanly.

```java
private static final ThreadLocal<Connection> connection =
    ThreadLocal.withInitial(() -> openNewConnection());
```

Every thread calls connection.get() and gets its own independent Connection object. Thread 1 gets Connection A. Thread 2 gets Connection B. They never share.

This is how most ORM frameworks and JDBC wrappers handle database connections. One DataManager instance. But each thread gets its own session.

The most important thing people forget:

```java
threadLocal.remove();
```

If you are using a thread pool (which Tomcat does), threads are reused. If you do not remove the ThreadLocal value after the request is done, the next request picks up stale data from the previous one.

Always clean up after yourself.

Rule of thumb:
- Store request-scoped data in ThreadLocal
- Always remove it when the work is done
- Never store shared, mutable state in a regular field of a singleton

ThreadLocal is one of those things you never appreciate until you debug a production issue where two users were seeing each other's data.

---

**Image generation prompt:**
`Diagram with three parallel vertical lanes labeled Thread-1, Thread-2, Thread-3. Each lane has its own isolated Connection box. A single ThreadLocal box at the top connects to all three lanes with separate arrows. Blue and teal color scheme. Clean white background. Minimal flat style.`

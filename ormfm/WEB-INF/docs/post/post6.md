# Post 6 - Why Opening a New Database Connection Every Time is Expensive

This looks harmless:

```java
Connection conn = DriverManager.getConnection(url, user, pass);
```

But what actually happens when you call this?

1. A TCP socket is opened between your app and the database server
2. A handshake happens
3. Authentication runs
4. The database allocates resources for your session

All of that for one query. Then you close it and do it again for the next one.

In a web application with 50 requests per second, that means 50 new TCP connections every second. The database gets overwhelmed. Latency goes up. Everything slows down.

Connection pooling solves this.

A connection pool opens a fixed number of connections at startup, say 10. When your code needs one, it borrows from the pool. When done, it returns it. No open. No close. Just borrow and return.

Libraries like HikariCP handle this. You configure the pool size and the pool URL. Your code just calls getConnection() and gets a ready-to-use connection from the pool.

The difference in performance is significant. Especially under load.

If you are building any Java application that talks to a database and you are not using a connection pool, you should fix that before anything else.

Connection pools are one of the most underappreciated performance decisions in backend development.

---

**Image generation prompt:**
`Diagram showing two flows. Top flow: App opens new connection for every request, five arrows going to database labeled open, close, open, close. Bottom flow: App borrows from a pool of 5 connections, single pool box in center. Green checkmark on bottom. Red X on top. Clean minimal flat illustration.`

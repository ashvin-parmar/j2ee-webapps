# Post 4 - ThreadLocal in Java: One Object, Different Data Per Thread

Let me explain ThreadLocal simply.

A web server handles many requests at the same time. Each request runs on a separate thread. That is normal.

Now, most services and managers in a Java application are singletons. One instance. Shared by every thread.

Here is the problem.

If that singleton stores data in a regular field:

```java
public class SessionManager {
    private Connection connection; // shared by ALL threads
}
```

Thread 1 sets connection. Thread 2 also sets connection. Thread 1's value is now gone. Both threads are fighting over the same field. This causes data corruption, wrong results, or crashes.

This is the classic multi-threading problem.

ThreadLocal solves it.

ThreadLocal is a container. Every thread that accesses it gets its own separate copy of the value. Same variable name. Different value per thread.

```java
private static final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();

// Thread 1 sets its own copy
threadConnection.set(connectionA);

// Thread 2 sets its own copy
threadConnection.set(connectionB);

// Thread 1 reads back only its own
threadConnection.get(); // returns connectionA

// Thread 2 reads back only its own
threadConnection.get(); // returns connectionB
```

They never interfere. Thread 1 and Thread 2 each have their own version of the data. That is the whole point.

Think of it like a locker room. Everyone uses "locker number 5". But each person opens their own locker number 5. They never see each other's stuff.

Now here is where most people make a mistake.

Web servers like Tomcat use a thread pool. A thread pool does not create a new thread for every request. It reuses the same threads again and again.

So if Thread 1 handles Request A and stores a connection in ThreadLocal, then handles Request B next, it will still have that old connection sitting in ThreadLocal.

Request B picks up leftover data from Request A. Wrong user. Wrong session. Wrong connection.

This is how bugs like "I saw another user's data" happen in production.

The fix is always this:

```java
try {
    threadConnection.set(openNewConnection());
    // do your work
} finally {
    threadConnection.remove(); // clean up before thread goes back to pool
}
```

Always remove the value in a finally block. No exception should skip this step.

To summarize:

- Regular field: one value, shared by all threads (dangerous for per-request data)
- ThreadLocal field: one value per thread (safe for per-request data)
- Thread pool: always call remove() after work is done

ThreadLocal is used heavily inside frameworks. Database sessions, security contexts, request metadata. Anywhere the framework needs to track something per-request without passing it manually through every method call.

Once you understand ThreadLocal, a whole layer of framework magic becomes obvious.

---

**Image generation prompt:**
`Flat clean diagram. Top shows one ThreadLocal variable box. Below it, three separate thread lanes labeled Thread-1, Thread-2, Thread-3. Each thread lane has its own isolated data box connected to the ThreadLocal by a separate arrow. Arrows are color coded per thread. Bottom of each lane shows a remove() call icon after work is done. White background. Blue teal palette. Minimal tech illustration style.`

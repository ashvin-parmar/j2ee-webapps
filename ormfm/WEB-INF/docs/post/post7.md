# Post 7 - The Singleton Pattern in Java Frameworks

Almost every framework you use has a singleton somewhere.

Hibernate has SessionFactory. Spring has ApplicationContext. JDBC wrappers have DataSource.

A singleton is a class that has exactly one instance for the lifetime of the application. All code shares that one instance.

Here is the safe way to implement it in Java:

```java
public class DataManager {
    private static DataManager instance;

    private DataManager() { }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }
}
```

The synchronized keyword ensures that if two threads call getInstance() at the same time, only one creates the instance. The other waits.

Why frameworks use singletons:
- Initialization is expensive (reading config, setting up connections, scanning classes)
- You only want to pay that cost once
- After that, all threads share the same ready-to-use instance

What singletons should NOT store:
- Per-request state (use ThreadLocal for that)
- Mutable data shared across threads without synchronization

Singletons store shared, read-only state after initialization. Per-thread state lives in ThreadLocal.

When you understand this separation, you understand why frameworks like Hibernate say: create SessionFactory once, create Session per request.

SessionFactory is the singleton. Session is the per-thread object.

Same pattern, every time.

---

**Image generation prompt:**
`Flat diagram. One large box at top labeled Singleton Instance. Below it, five arrows going down to five different User Request boxes. The singleton box has a lock icon. Clean minimal style. Dark background with light blue and white text. Tech illustration.`

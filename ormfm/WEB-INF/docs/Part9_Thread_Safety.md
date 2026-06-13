# ORMFM — Object-Relational Mapping Framework
## Complete Developer Documentation
### Part 9 — Thread Safety & Multi-threaded Usage

---

> [!NOTE]
> This is **Part 9** of the ORMFM documentation. It covers how `DataManager` handles concurrent access from multiple threads, what is thread-safe and what is not, and a complete walkthrough of the multi-threaded stress test in `testingThreaded.java`.

---

## 1. The Thread Safety Model — Overview

ORMFM is designed to run in a **multi-threaded environment** such as a Java web server (Tomcat), where many HTTP requests are handled simultaneously, each on its own thread. The framework achieves thread safety through two complementary mechanisms:

| Mechanism | What it protects | How |
|---|---|---|
| `ThreadLocal<Session>` | Per-thread connection, query state | Each thread gets its own `Session` object — no sharing |
| `synchronized` on `initialize()` | One-time startup | Prevents two threads from initializing simultaneously |
| Shared read-only maps | `statements` map, schema registry | Built once at startup, never modified at runtime |

### What IS Thread-Safe

- **CRUD operations** (`save`, `update`, `delete`) — each thread uses its own `Connection` from `ThreadLocal<Session>`.
- **Query operations** (`query`, `select`, `fire`, `view`) — same isolation.
- **Schema metadata** (`statements` map, `ORMDataModel` registry) — read-only after initialization; no synchronization needed.
- **`initialize()`** — `synchronized`, so safe to call from any thread.

### What is NOT Fully Thread-Safe

- **Cache writes** for `@Cacheable` tables — `cache.get(objClass).put(...)` on a `LinkedHashMap` is not synchronized. Concurrent `save()` / `update()` / `delete()` on a `@Cacheable` table from multiple threads can cause `ConcurrentModificationException` in rare high-contention scenarios.

> [!TIP]
> Use `@Cacheable` on lookup/reference tables that are rarely written to — this minimises the chance of concurrent write conflicts. Tables with frequent concurrent writes should be accessed via `query().fire()` exclusively.

---

## 2. `ThreadLocal<Session>` — How It Works

The core of ORMFM's thread safety is this single declaration:

```java
private static final ThreadLocal<Session> threadSession =
    ThreadLocal.withInitial(() -> new Session());
```

### What `ThreadLocal` Does

Java's `ThreadLocal` is a special container where **each thread has its own independent copy** of the stored value. When one thread calls `threadSession.get()`, it gets its own `Session`. Another thread calling `threadSession.get()` at the same moment gets a completely different `Session`. They never see each other's data.

```
JVM Heap Memory
│
├── ThreadLocal<Session> object (shared reference — static field)
│       │
│       ├── Thread-1's internal slot → Session { connection=conn_A, qStatement="..." }
│       ├── Thread-2's internal slot → Session { connection=conn_B, qStatement="..." }
│       ├── Thread-3's internal slot → Session { connection=conn_C, qStatement="..." }
│       └── ...
```

Each `Session` holds:
- `connection` — a **unique JDBC connection** opened by that thread's `begin()` call.
- `qStatement` — the SQL being built in that thread's current query chain.
- `qClass` — the POJO class the current query is for.
- `whereUsed`, `orderByUsed` — flags tracking query chain state for that thread.

### Accessing the Session

Internally, `DataManager` accesses the current thread's session with:

```java
private Session session() {
    return threadSession.get();
}

private Connection conn() {
    return session().connection;
}
```

Every method that needs the connection or query state calls `session()` — which automatically returns **this thread's own Session**. No locks, no synchronization needed.

---

## 3. What Happens Without `ThreadLocal` — The Problem It Solves

To understand why `ThreadLocal` is essential, consider what would happen with a single shared connection:

```
Without ThreadLocal — DANGEROUS (hypothetical):
────────────────────────────────────────────────────

Thread-1:  dm.begin()      → opens Connection shared_conn
Thread-2:  dm.begin()      → overwrites shared_conn with a NEW connection
                             Thread-1's connection is now closed!

Thread-1:  dm.save(course1) → tries to use closed connection → CRASH
                              or uses Thread-2's connection and saves
                              data into Thread-2's transaction

Thread-2:  dm.query(...)    → might execute Thread-1's partially built query string
                              because qStatement is shared
```

With `ThreadLocal`:
```
With ThreadLocal — SAFE (actual behaviour):
───────────────────────────────────────────

Thread-1:  dm.begin()        → Thread-1's Session.connection = conn_A
Thread-2:  dm.begin()        → Thread-2's Session.connection = conn_B
                               (completely separate — Thread-1 unaffected)

Thread-1:  dm.save(course1)  → uses conn_A exclusively
Thread-2:  dm.query(...)     → uses conn_B exclusively
                               (both run simultaneously with zero interference)
```

---

## 4. `initialize()` — The `synchronized` Guard

```java
public static synchronized void initialize(File parentWorkingDirectory)
    throws DataException {
    if (dataManager != null)
        throw new DataException("Already initialized, can not call again");
    ...
    dataManager = new DataManager();
}
```

The `synchronized` keyword ensures that even if two threads call `initialize()` at exactly the same millisecond (e.g., two servlet requests arriving simultaneously at first startup), only **one thread** enters the method at a time. The second thread blocks until the first finishes, then sees `dataManager != null` and throws `DataException` — preventing double initialization.

---

## 5. The Shared Read-Only State — Why It's Safe

After `initialize()` completes, two large data structures are shared across all threads:

```java
// Populated ONCE at startup. Never modified after that.
private static Map<Class<?>, Map<String, StatementDS>> statements = new HashMap<>();
```

Because `statements` is **never modified after initialization**, all threads can read from it simultaneously with no synchronization needed. Java's memory model guarantees that a value written in one thread before a `synchronized` block exits is visible to all threads that subsequently enter any `synchronized` block — and all threads passed through `initialize()` (synchronized) before they can call any other method. So the `statements` map is safely published.

---

## 6. `threadSession.remove()` — Critical for Thread Pool Environments

In `end()`:

```java
public void end() {
    Session s = session();
    try {
        if (s.connection != null) s.connection.close();
    } catch (SQLException e) { /* ignored */ }
    reset();
    threadSession.remove();   // ← CRITICAL
}
```

### Why This Matters

Tomcat maintains a **thread pool** — the same thread object is reused across multiple HTTP requests. Without `threadSession.remove()`:

```
Request 1 arrives → Thread-A handles it → dm.begin() → dm.save() → dm.end()
  end() closes connection but does NOT remove Session from ThreadLocal

Request 2 arrives → Thread-A is reused → dm.begin()
  → session() returns the OLD Session from Request 1
  → The old Session's connection is already closed
  → If checked: s.connection.isClosed() == true → begin() opens new one → OK
  → But qStatement still has leftover state from Request 1 → logic errors!
```

With `threadSession.remove()`, the `Session` is **completely destroyed** at `end()`. When the thread is reused for the next request, `threadSession.get()` creates a brand-new `Session` via `ThreadLocal.withInitial(() -> new Session())`.

---

## 7. `testingThreaded.java` — Complete Walkthrough

This is the stress test that **proves ThreadLocal isolation is working**. Here is a complete explanation of every part.

### Configuration

```java
static final int THREAD_COUNT  = 15;  // 15 simultaneous threads
static final int OPS_PER_THREAD = 30; // each thread does 30 save+query cycles
// Total operations = 15 × 30 × 3 tests = 1,350 individual operations
```

### Counters

```java
static AtomicInteger successCount = new AtomicInteger(0);
static AtomicInteger failCount    = new AtomicInteger(0);
```

`AtomicInteger` is used instead of `int` because multiple threads increment these counters simultaneously. `AtomicInteger.incrementAndGet()` is a single atomic CPU instruction — no synchronization needed.

### The `CountDownLatch` Start Gun Pattern

```java
CountDownLatch startGun = new CountDownLatch(1);   // count = 1
CountDownLatch allDone  = new CountDownLatch(15);  // count = 15

// All 15 threads are submitted to pool and each calls startGun.await()
// → All 15 threads block, waiting for the gun

startGun.countDown();   // count goes 1 → 0 → ALL 15 threads unblock SIMULTANEOUSLY
```

This pattern ensures **maximum contention** — all 15 threads start executing at exactly the same moment. Without this, threads would start one by one as the pool submits them, reducing the overlap window.

```java
allDone.await();   // main thread blocks until all 15 workers call allDone.countDown()
```

### Per-Thread Worker — Three Tests Per Operation Cycle

Each thread runs `runWorker(dm, threadId)` which loops `OPS_PER_THREAD` (30) times. Each iteration runs three tests:

---

#### Test 1 — `save()` Under Concurrency

```java
dm.begin();
Course c = new Course();
c.setTitle("Course-T" + threadId + "-Op" + op);
// e.g., "Course-T3-Op12" → unique title identifying which thread + op
dm.save(c);
dm.end();
saved = c;
successCount.incrementAndGet();
// Prints: [Thread-3] [Op 12] SAVE OK → code=147  title=Course-T3-Op12
```

**What this tests:**
- 15 threads call `dm.begin()` simultaneously — each must open its own independent connection.
- 15 threads call `dm.save()` simultaneously — each INSERT must execute on its own connection.
- Each thread's auto-incremented `code` is returned correctly to that thread's POJO — not mixed up with another thread's result.
- The title `"Course-T<threadId>-Op<op>"` uniquely identifies which thread saved which record — verifiable in the final output.

**If ThreadLocal were broken:** Two threads would share a connection. The second thread's `dm.begin()` would close the first thread's connection mid-save, causing `DataException` or data corruption. `failCount` would increase.

---

#### Test 2 — `query().fire()` Under Concurrency

```java
sleep(50);  // deliberate sleep — lets OTHER threads interleave here

dm.begin();
List<Course> courses = (List<Course>) dm.query(Course.class).fire();
dm.end();
successCount.incrementAndGet();
// Prints: [Thread-3] [Op 12] QUERY OK → 147 courses in DB
```

**What this tests:**
- While Thread-3 is mid-sleep, Threads 1, 2, 4–15 are also executing saves and queries.
- Each thread's `query().fire()` runs on its own connection — the `qStatement` built by `query()` is in that thread's `Session.qStatement` only.
- Results vary per thread (different number of courses visible depending on timing) — that's expected and correct. The key check is that `fire()` succeeds for every thread.

**If `qStatement` were shared:** Thread-A's `where()` call would corrupt Thread-B's partially built query. `fire()` would execute a malformed statement and throw `DataException`.

---

#### Test 3 — Isolation Check (sleep INSIDE transaction)

```java
if (saved != null) {
    dm.begin();
    sleep(20);  // sleep INSIDE an open transaction — maximum overlap window!
    List<Course> courses = (List<Course>) dm.query(Course.class).fire();
    dm.end();

    final int savedCode = saved.getCode();
    boolean found = courses.stream().anyMatch(c -> c.getCode() == savedCode);

    if (found) {
        successCount.incrementAndGet();
        // [Thread-3] [Op 12] ISOLATION CHECK OK — saved code 147 confirmed in DB
    } else {
        failCount.incrementAndGet();
        // [Thread-3] [Op 12] ISOLATION FAIL — code 147 NOT found (connection leaked?)
    }
}
```

**What this tests — the hardest test:**
- Thread-3 opened a connection and is sleeping with it open for 20ms.
- During those 20ms, Thread-1, Thread-7, Thread-14 etc. are also doing `begin()`/`end()` cycles.
- After waking, Thread-3 queries the DB and looks for the specific course code it saved in Test 1.
- The code must be found — proving Thread-3's connection was not stolen, closed, or corrupted by another thread during the 20ms sleep.

**If ThreadLocal were broken:** Thread-X's `begin()` could have closed Thread-3's connection during the sleep. Thread-3's `fire()` would fail on a closed connection (`DataException`), or return results from the wrong connection.

---

### Final Verification — Main Thread

After all 15 workers finish:

```java
System.out.println("Total operations : " + total);
System.out.println("Succeeded        : " + successCount.get());
System.out.println("Failed           : " + failCount.get());

if (failCount.get() == 0)
    System.out.println("✅  ALL PASSED — ThreadLocal isolation is working.");
else
    System.out.println("❌  FAILURES DETECTED — check logs above.");

// Confirms ALL courses are in DB — not just some threads' saves
dm.begin();
List<Course> all = (List<Course>) dm.query(Course.class).fire();
dm.end();
for (Course c : all)
    System.out.println("code=" + c.getCode() + "  title=" + c.getTitle());
```

The final query from the **main thread** reads all courses and prints them. If all 15×30=450 saves succeeded, there should be 450 rows in the `course` table (plus any pre-existing ones). Each row's title like `"Course-T7-Op22"` proves it was saved by Thread-7 on its 22nd operation.

---

## 8. How Tomcat Uses ORMFM — Real-World Threading Model

```
Tomcat Thread Pool (e.g., 200 worker threads)
│
│  HTTP Request arrives → Tomcat assigns it to Thread-47
│
│  Thread-47: CourseServlet.doGet()
│    ├── DataManager dm = DataManager.getDataManager()
│    │     └── Returns singleton — same reference all threads use
│    │
│    ├── dm.begin()
│    │     └── Thread-47's Session.connection = DriverManager.getConnection(...)
│    │         (entirely independent of Thread-22 or Thread-115's connections)
│    │
│    ├── dm.query(Course.class).fire()
│    │     └── Uses Thread-47's Session.connection
│    │         Thread-47's Session.qStatement = "SELECT * FROM course"
│    │
│    └── dm.end()
│          ├── Closes Thread-47's connection
│          └── threadSession.remove() → Thread-47's Session is destroyed
│
│  HTTP Request arrives → Tomcat reuses Thread-47
│
│  Thread-47: StudentServlet.doPost()
│    ├── dm.begin()
│    │     └── threadSession.get() creates NEW Session (previous one removed)
│    │         Thread-47's Session.connection = DriverManager.getConnection(...)
│    └── ...
```

Each HTTP request — regardless of which Tomcat worker thread handles it — gets a completely isolated database session. 200 concurrent requests = 200 independent connections. No interference.

---

## 9. Thread Safety Summary Table

| Scenario | Thread-Safe? | Reason |
|---|---|---|
| Two threads calling `getDataManager()` simultaneously | ✅ Yes | Returns immutable singleton reference |
| Two threads calling `initialize()` simultaneously | ✅ Yes | `synchronized` keyword — only one enters |
| Two threads calling `begin()` simultaneously | ✅ Yes | Each writes to its own `ThreadLocal<Session>` |
| Two threads calling `save()` simultaneously on non-`@Cacheable` table | ✅ Yes | Each uses its own `Connection` |
| Two threads calling `query().fire()` simultaneously | ✅ Yes | Each builds its own `qStatement` in its own `Session` |
| Two threads calling `end()` simultaneously | ✅ Yes | Each closes its own `Session.connection` |
| Two threads calling `save()` simultaneously on `@Cacheable` table | ⚠️ Mostly | DB operation is safe; cache `put()` on `LinkedHashMap` is not synchronized |
| Reading from `statements` map simultaneously | ✅ Yes | Read-only after initialization — no mutation |
| Reading from cache via `queryDS()` simultaneously | ✅ Yes | Reads `.values()` without structural modification |
| Writing to cache via `save()`/`update()`/`delete()` simultaneously | ⚠️ Limited | `LinkedHashMap` mutations not synchronized |
| `reset()` called from two threads simultaneously | ✅ Yes | Each resets its own `Session` fields |

---

## 10. Best Practices for Multi-threaded Usage

```java
// ✅ CORRECT — always use try-finally to guarantee end() is called
DataManager dm = DataManager.getDataManager();
dm.begin();
try {
    List<Course> courses = (List<Course>) dm.query(Course.class).fire();
    // process courses...
} catch (DataException de) {
    // handle error
} finally {
    dm.end();   // connection always closed — thread pool safe
}

// ✅ CORRECT — multiple independent transactions in one thread
dm.begin();
dm.save(courseObj);
dm.end();

dm.begin();
List<Student> students = (List<Student>) dm.query(Student.class).fire();
dm.end();

// ❌ WRONG — never share dm across threads manually
// dm is already a singleton — you don't need to pass it around;
// just call DataManager.getDataManager() in each context

// ❌ WRONG — never call begin() without end()
dm.begin();
dm.save(obj);
// forgot dm.end() → connection leak for this thread
```

---

*End of Part 9 — Please review and confirm to proceed to Part 10.*

# Post 9 - In-Memory Caching in ORM: What It Is and When to Use It

Every time you query the database, there is a cost.

Network round trip. Query parsing. Disk read. Result serialization. All of that for data that might not have changed since the last time you read it.

For some tables, the data barely ever changes. A list of countries. A list of course types. A list of status codes. You read them thousands of times but write to them almost never.

Caching is the answer for these tables.

The idea is simple: load all the rows once at startup and store them in a HashMap. Every read after that is just a HashMap lookup. No database call at all.

```java
Map<Integer, Course> courseCache = new LinkedHashMap<>();
// Load once
courseCache.put(course.getCode(), course);

// Read anytime, no DB hit
Course c = courseCache.get(22);
```

But you have to keep the cache in sync when you write:
- After save: put the new object into the cache
- After update: replace the existing object
- After delete: remove it

The biggest mistake with caching:

Returning the cached object directly. If the caller modifies it, they corrupt the cache. Always return a copy, not the original reference.

When to cache:
- Small tables
- Frequently read, rarely written
- Reference/lookup data

When not to cache:
- Large tables
- Tables with concurrent writes from multiple threads
- Data that changes constantly

Caching is a trade-off. Use it where it makes sense.

---

**Image generation prompt:**
`Diagram showing database on left, in-memory HashMap box in center, and application on right. Arrow from database to cache labeled load once at startup. Arrow from cache to application labeled instant read, no DB. Small sync arrows labeled save, update, delete going from app back to both cache and database. Flat clean illustration, blue green palette.`

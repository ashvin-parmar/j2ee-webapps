# Post 11 - Checked vs Unchecked Exceptions: When to Use Which

Java has two kinds of exceptions and many developers mix them up.

Checked exceptions extend Exception. The compiler forces you to handle them.
Unchecked exceptions extend RuntimeException. The compiler says nothing.

Checked example:
```java
public void save(Object obj) throws DataException {
    // caller MUST handle DataException
}
```

Unchecked example:
```java
public void process() {
    throw new NullPointerException(); // no throws declaration needed
}
```

When to use checked exceptions:

When the caller can reasonably recover from the failure. Database connection failed? File not found? The caller should know about this and decide what to do.

When to use unchecked exceptions:

When the failure is a programming error. NullPointerException. IndexOutOfBoundsException. These should not happen if the code is correct.

For framework design, checked exceptions are usually the right choice for anything involving I/O, network, or database operations. The developer using your framework should not be surprised by a failure that takes the whole application down silently.

The mistake most people make:

Wrapping every checked exception into RuntimeException just to avoid writing try-catch. This hides real errors and makes debugging much harder.

If the operation can fail because of external systems (database, network, file system), make the exception checked. Respect your callers enough to tell them things can go wrong.

---

**Image generation prompt:**
`Split diagram. Left side shows Checked Exception tree from Exception class with database and file icons. Right side shows Unchecked Exception tree from RuntimeException with bug icon. Both trees have clean branch lines. White background. Blue for checked, orange for unchecked. Flat minimal style.`

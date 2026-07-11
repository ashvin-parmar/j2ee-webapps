# Post 17 - Where Should You Validate Uniqueness: Java or Database?

You have a column marked UNIQUE in the database.

If you try to insert a duplicate, the database throws an error.

But that database error is ugly. It is a SQLException with a constraint violation message buried in the stack trace. Your user sees a 500 error page.

You have two options for handling this:

Option 1: Let the database throw and catch the exception.
Option 2: Check for the duplicate in Java before inserting.

Option 1 is less code. But the error message is database-specific and hard to parse. Translating it into a clean user message is messy.

Option 2 looks like this:

```java
// Before INSERT, run:
String sql = "SELECT id FROM user WHERE email = ?";
// If result exists, throw a clean business exception
throw new DataException("This email is already in use.");
```

Now your error message is clear. You control it. You can localize it.

The trade-off:

The Java check adds an extra SELECT query. In most cases that is fine. But under very high concurrency, two threads could pass the check at the same time and both attempt the insert. The database constraint catches the second one.

So the real answer: validate in Java for a clean user experience, AND keep the database constraint as the final guard.

Both layers together. Not one or the other.

---

**Image generation prompt:**
`Two layer diagram. Top layer labeled Application Layer showing Java code with a SELECT check before INSERT. Bottom layer labeled Database Layer showing UNIQUE constraint icon. Both layers connected by arrow labeled double validation. Clean white background. Blue and green palette. Flat minimal style.`

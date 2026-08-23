# Post 14 - Always Close Your Resources: The try-finally Rule

One of the easiest ways to create hidden bugs in Java is forgetting to close resources.
At first, everything seems fine. The application runs without issues.
Then an exception occurs.

FileInputStream input = new FileInputStream("data.txt");
// Read and process the file
input.close();

Looks correct, right?
Not quite.

If an exception is thrown before close() is reached, the stream is never closed. The same problem happens with database connections, sockets, HTTP clients, and many other resources.

The result?
• Memory leaks
• File handles staying open
• Database connection pool exhaustion
• Applications that work perfectly during testing but start failing under production load

The safer approach: try-finally
FileInputStream input = new FileInputStream("data.txt");

try {
    // Read and process the file
} finally {
    input.close();   // Always executes
}

No matter how the try block exits—normally, with a return, or because of an exception—the finally block still runs.

The modern approach: Try-with-Resources (Java 7+)
try (FileInputStream input = new FileInputStream("data.txt")) {
    // Read and process the file
}

When execution leaves the block, Java automatically calls close(), even if an exception occurs.
Cleaner code, Less boilerplate, Fewer resource leaks.

This isn't just for files. The same principle applies to:
- Database connections (Connection, PreparedStatement, ResultSet)
- File streams (FileInputStream, BufferedReader, etc.)
- Network sockets
- HTTP connections
- Any object that implements AutoCloseable

A simple rule that saves hours of debugging: If you open it, you close it.
Small habits like this make applications far more reliable—especially when they move from development to production.

#Java #Programming #SoftwareEngineering #Backend #SpringBoot #JDBC #BestPractices #CleanCode #JavaDeveloper


-------------------------------------

**Image generation prompt:**
`Flow diagram showing two paths. Top path: normal code flow with open and close resource. Bottom path: exception occurs, close is skipped, resource leak shown as dripping faucet icon. Arrow pointing to try-finally block as the solution showing always closes. Clean flat illustration. Blue and red palette. White background.`

# Post 19 - DatabaseMetaData: Reading Your Database Schema From Java

Most developers know how to query data from a database.

Fewer know that you can also query the database schema itself from Java.

DatabaseMetaData is a JDBC class that gives you information about your database structure.

```java
DatabaseMetaData meta = connection.getMetaData();

// Get all tables
ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});

// Get columns of a specific table
ResultSet columns = meta.getColumns(null, null, "student", null);
while (columns.next()) {
    String name = columns.getString("COLUMN_NAME");
    int type = columns.getInt("DATA_TYPE"); // java.sql.Types constant
    boolean nullable = columns.getInt("NULLABLE") == 1;
    int size = columns.getInt("COLUMN_SIZE");
}

// Get primary keys
ResultSet pks = meta.getPrimaryKeys(null, null, "student");

// Get foreign keys
ResultSet fks = meta.getImportedKeys(null, null, "student");
```

You can discover the entire structure of a database at runtime without knowing anything about it in advance.

This is exactly how code generators work. They connect to your database, read the schema through DatabaseMetaData, and output Java classes.

This is also how schema validation works in frameworks. They compare your annotated classes to the actual database structure at startup and warn you if they do not match.

If you ever need to build a tool that works with any database generically, without knowing the schema in advance, DatabaseMetaData is where you start.

JDBC is more capable than most developers realize.

---

**Image generation prompt:**
`Diagram showing JDBC connection arrow pointing to a DatabaseMetaData box. From that box, four output arrows pointing to: Tables list, Columns list, Primary Keys, Foreign Keys. Each output has a small icon. Clean light background. Soft blue palette. Technical minimal flat illustration.`

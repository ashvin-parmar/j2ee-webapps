# Post 13 - The Parallel Lists Code Smell in Java

Have you ever seen code like this?

```java
List<String> columnNames = new ArrayList<>();
List<Integer> sqlTypes = new ArrayList<>();
List<Method> setterMethods = new ArrayList<>();
```

Three lists. All indexed together. If columnNames.get(2) is "first_name", then sqlTypes.get(2) is its SQL type, and setterMethods.get(2) is its setter.

This is called parallel lists. And it is a code smell.

The problem:
- You depend on index i being correct across all three lists
- Adding or removing from one list without updating the others breaks everything
- The relationship between the three pieces of data is invisible in the code
- Every loop is more complex than it needs to be

The fix is simple: create a class that holds all three together.

```java
public class ColumnInfo {
    String columnName;
    int sqlType;
    Method setterMethod;
}

List<ColumnInfo> columns = new ArrayList<>();
```

Now you have one list. Each element carries all the related data. The relationship is explicit. The index dependency is gone.

This is just basic object-oriented thinking: things that belong together should live together.

Parallel lists are often a sign that a class is missing from your design. Ask yourself: what do these lists represent? Give it a name. Make it a class.

Your future self (and your team) will thank you.

---

**Image generation prompt:**
`Before and after illustration. Left side shows three separate parallel lists with index markers showing fragile coupling. Red warning icon. Right side shows a single list of ColumnInfo objects each containing all three fields. Green checkmark. Clean white background. Red and green contrast. Flat minimal code style.`

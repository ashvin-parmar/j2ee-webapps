# Post 10 - POJO Design: The Foundation of Clean Java Code

POJO stands for Plain Old Java Object.

No framework dependency. No special parent class. No interface to implement. Just a class with fields and standard getters and setters.

```java
public class Student {
    private int rollNumber;
    private String firstName;

    public int getRollNumber() { return rollNumber; }
    public void setRollNumber(int rollNumber) { this.rollNumber = rollNumber; }
}
```

This simplicity is the point.

When your data class has no dependencies, you can use it anywhere. Pass it to a framework. Serialize it to JSON. Store it in a database. Send it over a network. The class itself does not care.

Why frameworks love POJOs:
- They can be created with newInstance() (no constructor args needed)
- They can be read with getXxx() by name
- They can be written with setXxx() by name
- They carry data without any behavior that gets in the way

Common POJO mistakes:

1. Adding framework annotations to every POJO: Your POJO should not know about your ORM. That is the ORM's job.

2. No default constructor: Frameworks use reflection to create instances. If you add a constructor with arguments and do not keep the default one, the framework breaks.

3. Business logic inside POJOs: Keep POJOs as data containers. Logic belongs in service classes.

Simple Java classes are powerful precisely because they have no magic. The framework brings the magic. The POJO just holds the data.

---

**Image generation prompt:**
`Flat illustration. Center shows a clean Java class box with private fields and public getter setter methods. Three arrows pointing outward: one to a database icon, one to a JSON bracket icon, one to a network icon. Clean white background. Minimal modern style. Soft blue palette.`

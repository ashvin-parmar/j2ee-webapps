# Post 3 - Java Reflection: How Frameworks Work Without Knowing Your Class

Ever wonder how a framework can call your getter methods without knowing your class at compile time?

That is reflection.

Reflection lets Java code inspect and interact with any class, method, or field at runtime. The framework does not need to import your class. It just needs a reference to it.

Here is a simple example:

```java
Class<?> clazz = Student.class;
Method getter = clazz.getMethod("getFirstName");
Object value = getter.invoke(studentObject); // calls student.getFirstName()
```

No hard dependency. No import. Just the method name as a string and the object to invoke it on.

Frameworks use this for everything:
- Reading field values from your POJO to build SQL
- Writing result set values back into your object
- Calling setters dynamically based on column names

The performance concern:

Reflection is slower than direct method calls. That is why good frameworks resolve Method objects once at startup and store them. Then they reuse those stored Method references on every operation.

This is why initialization takes a moment, but every call after that is fast.

Reflection is the core reason you can write:
```java
dm.save(anyObject);
```

And the framework just handles it, no matter what class you pass.

It is powerful. But like any tool, understanding it prevents misuse.

---

**Image generation prompt:**
`Flat illustration. Center shows a generic Java object. Arrows pointing out to Method.invoke(), Field.get(), and getAnnotation() labels. Each arrow is a different pastel color. White background. Clean minimal tech style. Small code snippet visible in corner.`

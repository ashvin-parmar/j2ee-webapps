# Post 2 - 𝗛𝗼𝘄 𝗝𝗮𝘃𝗮 𝗔𝗻𝗻𝗼𝘁𝗮𝘁𝗶𝗼𝗻𝘀 𝗔𝗰𝘁𝘂𝗮𝗹𝗹𝘆 𝗪𝗼𝗿𝗸
     @Table(name="student")
     @Column(name="roll_number")

You use them, but have you ever wondered what actually happens behind the scenes?
Annotations are metadata. They don't execute any logic themselves. Instead, they provide information that frameworks can read and use.
When you compile your code, annotations are stored in the .class file (if their retention policy allows it). At runtime, frameworks use reflection to inspect your classes and read those annotations.

Here is how you define one:
     @Retention(RetentionPolicy.RUNTIME)
     @Target(ElementType.TYPE)
     public @interface Table {
         String name();
     }

And here is how a framework reads it:
     Table annotation = Student.class.getAnnotation(Table.class);
     String tableName = annotation.name(); // "student"

The framework scans your classes, reads the annotation values, and builds internal metadata that it uses later to generate SQL, configure beans, map entities, and much more.
This is exactly how frameworks like 𝗛𝗶𝗯𝗲𝗿𝗻𝗮𝘁𝗲, 𝗦𝗽𝗿𝗶𝗻𝗴, 𝗮𝗻𝗱 𝗝𝗣𝗔 understand your code.

Three things make this work:
1. @Retention(RUNTIME): Keeps the annotation available at runtime
2. @Target: Defines where the annotation can be applied (class, field, method, etc.)
3. Reflection; Allows the framework to inspect classes and read annotation values dynamically

Annotations don't perform the work, they simply describe your code. The framework reads that description and the real work begins.

#Java #Backend #SpringBoot #Reflection #Annotations

---

**Image generation prompt:**
`Clean tech diagram. A Java source file with @Table annotation highlighted in yellow. Arrow pointing right to a reflection API call showing getAnnotation(). Result box shows tableName = student. Dark navy background, cyan and white text. Flat minimal style.`

---
title: "Understanding Dependency Injection by Building a Custom IoC Container"
description: "How I implemented my own @AutoWired annotation and Inversion of Control container to manage object lifecycles without Spring."
pubDate: "Aug 2026"

tags:
  - "Java"
  - "Dependency Injection"
  - "Software Design"
  - "Architecture"
---

## Introduction

"Dependency Injection" (DI) and "Inversion of Control" (IoC) are two of the most heavily used buzzwords in the Java ecosystem, usually tied directly to the Spring framework. When I first started learning enterprise Java, I knew I was supposed to put `@Autowired` on my fields, but I had no idea how the framework actually instantiated those objects and wired them together.

To truly understand IoC, I decided to build it myself. As part of my custom Java framework **WEBRock**, I created a mini-IoC container that scans for components and resolves dependencies automatically. 

Here is what I learned about how Dependency Injection actually works under the hood.

## The Problem: Tight Coupling

Without DI, classes are responsible for creating their own dependencies. 

```java
public class UserController {
    // Tight coupling: The controller is responsible for creating the service.
    private UserService userService = new UserService();
}
```

This makes testing difficult (you can't easily mock `UserService`) and makes the system rigid. If `UserService` suddenly requires a `DatabaseConnection` in its constructor, you have to go modify every class that instantiates it.

Inversion of Control flips this: the class simply declares what it needs, and the framework (the container) provides it.

## Step 1: The @AutoWired Annotation

First, we need a way to tell the framework which fields require injection. I created a simple `@AutoWired` annotation.

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD) // This can only be applied to fields
public @interface AutoWired {
}
```

Now, the `UserController` looks like this:

```java
public class UserController {
    
    @AutoWired
    private UserService userService;
}
```

Notice that `userService` is never initialized with `new`. If we ran this code right now, any call to `userService` would result in a `NullPointerException`. It's the framework's job to step in before the controller is ever used.

## Step 2: The IoC Container Registry

During the server startup, my framework scans the classpath (the `WEB-INF/classes` folder) to find all the application classes. 

Instead of creating instances willy-nilly, the framework creates a central "Registry" (often called an Application Context in Spring). This is simply a `HashMap` that stores exactly one instance (a singleton) of every discovered service class.

```java
Map<Class<?>, Object> container = new HashMap<>();

// 1. First pass: Instantiate everything (assuming default no-args constructors)
for (Class<?> clazz : scannedClasses) {
    container.put(clazz, clazz.newInstance());
}
```

At this point, we have all our objects in memory, but they aren't connected. `UserController` is in the map, and `UserService` is in the map, but the `UserController`'s field is still null.

## Step 3: Wiring the Dependencies (The Injection Phase)

The magic happens in the second pass. We iterate over every object in our container, look at its fields using Reflection, and check if any of them are annotated with `@AutoWired`.

If we find an annotated field, we go back into our container, grab the instance of the required type, and inject it into the field.

```java
// 2. Second pass: Inject dependencies
for (Object instance : container.values()) {
    Class<?> clazz = instance.getClass();
    
    // Inspect all fields (including private ones)
    for (Field field : clazz.getDeclaredFields()) {
        
        if (field.isAnnotationPresent(AutoWired.class)) {
            // Find the dependency in our container
            Object dependency = container.get(field.getType());
            
            if (dependency != null) {
                // Bypass private visibility to inject the object!
                field.setAccessible(true); 
                field.set(instance, dependency);
            } else {
                throw new RuntimeException("No bean found for type: " + field.getType());
            }
        }
    }
}
```

## The "Aha!" Moment

The `field.setAccessible(true)` line is the absolute crux of how annotation-driven field injection works. Java's reflection API allows you to bypass the `private` access modifier and directly assign a value to the memory address of that field. 

Once this loop completes, the object graph is fully connected. The `UserController` now has a reference to the singleton `UserService`, and it is ready to handle HTTP requests.

## Conclusion

Building my own IoC container was an eye-opening experience. It demystified Spring's `@Autowired` and showed me that a Dependency Injection framework is essentially just:
1. A Map that holds Singleton instances.
2. A Reflection loop that forces references into private fields.

Understanding this mechanism deeply has made me much better at debugging dependency cycles and context initialization errors in large enterprise Spring applications.

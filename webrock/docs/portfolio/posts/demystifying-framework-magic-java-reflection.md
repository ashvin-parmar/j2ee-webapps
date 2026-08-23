---
title: "Demystifying Framework Magic: Building a Front Controller with Java Reflection"
description: "How I used Java Reflection and custom annotations to eliminate web.xml boilerplate and build my own mini Spring-like routing engine."
pubDate: "Jul 2026"

tags:
  - "Java"
  - "Reflection"
  - "Software Architecture"
  - "Backend"
---

## Introduction

If you've used modern Java frameworks like Spring Boot, you've experienced the "magic." You annotate a method with `@GetMapping("/users")`, return an object, and somehow, the framework knows exactly how to intercept the HTTP request, invoke your specific method, and serialize the response into JSON. 

But how does it actually work? 

For a long time, I treated frameworks as a black box. To break that habit, I decided to build my own lightweight Servlet framework called **WEBRock**. In this post, I want to explore one of the most exciting concepts I learned during that project: using Java Reflection to build a dynamic **Front Controller**.

## The Problem with Traditional Servlets

In the old days of J2EE, creating a new web endpoint was tedious. You had to:
1. Write a class extending `HttpServlet`.
2. Override `doGet` or `doPost`.
3. Open `web.xml` and add a `<servlet>` and `<servlet-mapping>` entry.

If you had 50 endpoints, you had 50 Servlets and a massive, unreadable XML file. This tightly coupled your business logic to the HTTP protocol and made maintenance a nightmare.

## Enter the Front Controller

The Front Controller is a design pattern where **a single Servlet** handles all incoming requests for the entire application. Instead of mapping every URL to a different Servlet, you map the root (`/*`) to one master Servlet. This Servlet's job is to look at the requested URL, find the appropriate Java method in your application, execute it, and return the response.

But how does this master Servlet know which method belongs to which URL? That's where custom annotations and Java Reflection come in.

## Step 1: Creating the Annotation

First, we need a way to mark our methods so the framework knows they represent an endpoint. We create a custom annotation, let's call it `@PATH`.

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Retain this at runtime so Reflection can see it
@Retention(RetentionPolicy.RUNTIME) 
// This annotation can only be applied to Methods
@Target(ElementType.METHOD)
public @interface PATH {
    String value(); // The URL path
}
```

Now, a developer can write a plain Java class without extending `HttpServlet`:

```java
public class UserController {
    
    @PATH("/getUser")
    public User getUserData() {
        return new User("Ashvin", "Software Engineer");
    }
}
```

## Step 2: Classpath Scanning and the Routing Map

When the server starts, our framework needs to find all methods annotated with `@PATH`. We can do this using a `ServletContainerInitializer`, which Tomcat runs on startup.

We scan the compiled `.class` files, load them into the JVM, and use Reflection to inspect them. When we find a method with our annotation, we store it in a `Map`.

```java
// A simple map linking a URL string to the specific Method object
Map<String, Method> routingTable = new HashMap<>();
Map<String, Object> controllerInstances = new HashMap<>();

// (Pseudo-code for the scanning phase)
for (Class<?> clazz : scannedClasses) {
    for (Method method : clazz.getDeclaredMethods()) {
        if (method.isAnnotationPresent(PATH.class)) {
            PATH pathAnnotation = method.getAnnotation(PATH.class);
            String url = pathAnnotation.value();
            
            routingTable.put(url, method);
            
            // We also need an instance of the class to invoke the method on later
            if (!controllerInstances.containsKey(clazz.getName())) {
                controllerInstances.put(clazz.getName(), clazz.newInstance());
            }
        }
    }
}
```

## Step 3: The Magic Dispatcher (Front Controller)

Now, we set up our single `HttpServlet` mapped to `/*`. When an HTTP request comes in, it extracts the URL, looks it up in our `routingTable`, and invokes the method dynamically.

```java
@WebServlet("/*")
public class WebRockFrontController extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        String requestUrl = request.getPathInfo(); // e.g., "/getUser"
        
        Method targetMethod = routingTable.get(requestUrl);
        
        if (targetMethod != null) {
            try {
                // Get the instance of the class that owns this method
                Object controller = controllerInstances.get(targetMethod.getDeclaringClass().getName());
                
                // INVOCATION MAGIC: Call the method dynamically
                Object result = targetMethod.invoke(controller);
                
                // Convert the result to JSON and send it back
                String jsonResponse = convertToJson(result);
                response.setContentType("application/json");
                response.getWriter().write(jsonResponse);
                
            } catch (Exception e) {
                response.setStatus(500);
            }
        } else {
            response.setStatus(404);
        }
    }
}
```

## Conclusion

By combining a Front Controller with Java Reflection, we entirely eliminated `web.xml` boilerplate. Developers using this framework can just write plain Java methods, annotate them, and the framework handles the HTTP lifecycle. 

This is exactly what Spring MVC's `DispatcherServlet` does under the hood. Building it from scratch taught me that framework "magic" is just clever architecture leveraging the dynamic capabilities of the language. Next time you use `@GetMapping`, you'll know exactly the kind of reflection heavy-lifting happening behind the scenes!

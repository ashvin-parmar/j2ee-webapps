---
title: "Bridging the Gap: Generating Frontend JavaScript Clients from Backend Java using Reflection"
description: "How I built a mechanism to automatically generate typed JavaScript functions for AJAX calls by introspecting backend Java methods at runtime."
pubDate: "Jul 2026"

tags:
  - "Java"
  - "JavaScript"
  - "Code Generation"
  - "Full Stack"
  - "Web Architecture"
---

## Introduction

One of the most tedious and error-prone parts of full-stack web development is keeping the frontend HTTP client in sync with backend endpoints. If a backend engineer changes an API route from `/getUser` to `/users/get`, or changes a query parameter from `id` to `userId`, the frontend breaks. 

Usually, this is solved by tools like Swagger (OpenAPI) and code generators like OpenAPI Generator. But while building **WEBRock**—my custom Servlet-based framework—I wanted to see if I could automate this synchronization without relying on external schemas or build steps. 

My goal: What if the backend could just write the JavaScript file itself?

## The Concept: Backend-Driven Code Generation

Since WEBRock already uses Java Reflection to scan for methods annotated with `@PATH` to build a routing table, it already knows *everything* about the API. For any given endpoint, the framework knows:
1. The URL path.
2. The expected parameters (thanks to reflection on the method signature).
3. The expected return type.

If we have all this metadata in memory on the server, we can dynamically generate a JavaScript string that contains `fetch()` or `XMLHttpRequest` wrappers tailored specifically to those endpoints.

## Step 1: Extracting Metadata via Reflection

Let's say we have a service in Java:

```java
public class StudentService {
    
    @PATH("/getStudent")
    public Student fetchStudent(int rollNumber) {
        // Business logic...
        return new Student(rollNumber, "Ashvin");
    }
}
```

During startup, the framework scans this class. Using the `java.lang.reflect.Method` API, we can extract the path and the parameter names (note: Java 8+ requires compiling with the `-parameters` flag to retain parameter names at runtime).

## Step 2: Generating the JavaScript String

Once we have the metadata, we can map it to a JavaScript template. For the `fetchStudent` method, our generator might build a string like this:

```java
StringBuilder js = new StringBuilder();
js.append("class StudentService {\n");

for (Method method : serviceMethods) {
    String url = method.getAnnotation(PATH.class).value();
    String paramName = method.getParameters()[0].getName(); // e.g. "rollNumber"
    String methodName = method.getName();
    
    js.append(String.format("  static %s(%s) {\n", methodName, paramName));
    js.append(String.format("    return fetch('%s?%s=' + %s)\n", url, paramName, paramName));
    js.append("      .then(res => res.json());\n");
    js.append("  }\n");
}

js.append("}\n");
```

## Step 3: Serving the Virtual JavaScript File

Here's the trick: we don't save this string to a `.js` file on the hard drive. Instead, we keep it in memory and serve it dynamically via a dedicated Servlet endpoint.

We create a special endpoint, say `/jsFile?name=StudentService.js`. When the browser requests this file:

```html
<script src="/jsFile?name=StudentService.js"></script>
```

Our framework intercepts the request, looks up the generated JavaScript string for `StudentService`, and writes it directly to the `HttpServletResponse` with the content type `application/javascript`.

## The Result: Zero-Boilerplate AJAX

On the frontend, a developer can now write:

```javascript
StudentService.fetchStudent(101).then(student => {
    console.log(student.name); // Outputs: "Ashvin"
});
```

There is no URL hardcoding. There is no manual `fetch` configuration. If the backend developer renames the method or changes the parameter, the generated JS updates instantly on the next server restart, and the browser always receives the correct, up-to-date client code.

## Conclusion

By leveraging the JVM's introspection capabilities, I turned the backend into a dynamic asset server for the frontend. While modern microservices usually rely on OpenAPI contracts, implementing this generation at runtime inside a monolith taught me a valuable lesson: your code is just data, and you can use that data to write more code.

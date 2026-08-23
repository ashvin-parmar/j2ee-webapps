---
title: "WEBRock: Architecting a Custom Java Servlet Framework from Scratch"
description: "A deep dive into WEBRock, my custom-built MVC web framework on top of Java Servlets. Featuring annotation-driven routing, auto-wiring, classpath scanning, and automated JS client generation."
pubDate: "Jun 2026"

tags:
  - "Java"
  - "Servlets"
  - "Reflection"
  - "MVC"
  - "Code Generation"

github: "https://github.com/ashvin-parmar/webrock"

featured: true
category: "frameworks"
---

## Overview

**WEBRock** is a lightweight, custom-built MVC web framework built entirely on top of the standard Java Servlet API. I embarked on this project to pull back the curtain on enterprise Java frameworks like Spring Boot and Spring MVC. Instead of treating these powerful tools as black boxes, I wanted to understand their internal mechanics by building them myself.

This framework is the culmination of my deep dive into Java Reflection, the Servlet Container lifecycle, and Inversion of Control (IoC). By abstracting away the boilerplate of raw `HttpServletRequest` and `HttpServletResponse` handling, WEBRock provides a modern, annotation-driven developer experience. It goes beyond simple routing, offering automated dependency injection, runtime generation of JavaScript client code, and dynamic PDF API documentation.

This case study breaks down the complete architecture and the core engineering challenges I solved to bring WEBRock to life.

## Problem Statement

In classic J2EE web applications, the development process is heavily constrained by boilerplate configuration. Every new endpoint requires:
1. Creating a new class that extends `HttpServlet`.
2. Overriding `doGet`, `doPost`, etc.
3. Manually mapping the Servlet to a URL in a massive `web.xml` file.
4. Writing repetitive code to parse HTTP parameters, convert them into Java objects, and serialize the response back to JSON.

This results in tightly coupled code that is difficult to maintain and test. WEBRock was designed to solve this by providing a centralized **Front Controller** that dynamically maps requests to standard Java methods using custom annotations (`@PATH`, `@POJO`, `@AutoWired`), entirely eliminating the need for `web.xml` servlet mappings.

## Architecture

WEBRock relies on a centralized dispatch mechanism and an in-memory routing table built at startup.

```
+---------------------------------------------------+
|               HTTP Request / Response             |
+---------------------------------------------------+
|            WebRock (Front Controller)             |
|  - Intercepts all '/*' requests                   |
|  - Looks up route in memory                       |
|  - Parses parameters / JSON payload               |
+---------------------------------------------------+
|            Reflection & Annotation Engine         |
|  - @PATH: Locates the target method               |
|  - @AutoWired: Injects dependencies (IoC)         |
|  - @SecuredAccess: Validates sessions/auth        |
+---------------------------------------------------+
|              Developer's Java Services            |
|  (Standard POJOs without Servlet boilerplate)     |
+---------------------------------------------------+
```

## Key Technical Contributions

### 1. Dynamic Classpath Scanning & Routing (`@PATH`)

To eliminate `web.xml`, I utilized the `ServletContainerInitializer` interface. When the Apache Tomcat server starts, WEBRock's `WebRockStarter` class scans the `WEB-INF/classes` directory. It loads every compiled class into the JVM and uses Java Reflection to inspect them for the `@PATH` annotation.

```java
// Example of a WEBRock Service Method
public class UserController {

    @PATH(value = "/getUser", method = "GET")
    public User getUser(int id) {
        return database.findUser(id);
    }
}
```

During startup, the framework maps `/getUser` to this specific `Method` object and stores it in an in-memory `HashMap`. When the Front Controller receives a request, it performs an $O(1)$ lookup in this map and dynamically invokes the method via `targetMethod.invoke(instance, args)`.

### 2. Custom Dependency Injection (`@AutoWired`)

I built a custom Inversion of Control (IoC) container to manage object lifecycles. After the framework instantiates the service classes during startup, it performs a second pass to resolve dependencies.

If a field is annotated with `@AutoWired`, the framework looks up the required type in its registry. It then uses Reflection to bypass private visibility (`field.setAccessible(true)`) and injects the singleton instance directly into the object.

```java
public class OrderService {

    @AutoWired
    private PaymentGateway paymentGateway; // Injected automatically at startup!

    // ...
}
```

This taught me exactly how Spring context initialization works and how object graphs are constructed dynamically.

### 3. Automated JavaScript Client Generation

One of the most innovative features of WEBRock is its ability to write frontend code. Keeping AJAX calls in sync with backend routes is notoriously error-prone.

Because WEBRock has full knowledge of all endpoints in memory via Reflection, I wrote a generator that outputs typed ES6 JavaScript classes for every service.

When the browser requests a specific script tag:
```html
<script src="jsFile?name=UserController.js"></script>
```

The framework intercepts this, generates the Javascript on-the-fly, and serves it directly. The generated code looks like this:

```javascript
class UserController {
    static getUser(id) {
        return fetch('/getUser?id=' + id).then(res => res.json());
    }
}
```

Developers get zero-boilerplate, auto-updating frontend clients with zero extra build steps.

### 4. Dynamic PDF API Documentation

Documentation often goes stale the moment the code changes. To solve this, I integrated the **iText** PDF library directly into the startup sequence.

When Tomcat boots up, WEBRock analyzes the routing table, extracts the expected parameters, return types, and security constraints (`@SecuredAccess`), and generates a beautifully formatted `webrockDOC.pdf` file. This ensures the API documentation is always 100% accurate and mathematically tied to the compiled code.

### 5. Intelligent Data Binding (`@POJO`)

Instead of forcing developers to manually parse `request.getParameter()`, WEBRock uses reflection to inspect the target method's parameters. If it sees an `@POJO` annotation, it intercepts the incoming HTTP form data or JSON payload, instantiates the Java object, populates its fields via setter methods, and passes the fully formed object into the method.

## What I Learned

- **The Intricacies of Java Reflection:** I gained a profound understanding of how to inspect classes, methods, and fields at runtime. I also learned about the performance implications and why caching this metadata in `HashMaps` during startup is critical.
- **IoC and Dependency Injection:** Building an `@AutoWired` container from scratch demystified how Spring manages application contexts and taught me how to handle circular dependencies and singleton lifecycles.
- **Code as Data:** Generating Javascript and PDF files directly from backend method signatures showed me the immense power of treating code as queryable data. It drastically reduced boilerplate and human error.
- **Advanced Servlet APIs:** By building a framework on top of Servlets, I learned the low-level HTTP handling—streams, buffers, content types, and container initializers—that modern abstractions usually hide.

## Stack

| Layer       | Technology           |
|-------------|----------------------|
| Language    | Java 8+              |
| Core API    | Java Servlet API 4.0 |
| Frontend    | JavaScript (ES6)     |
| Build Tool  | Maven                |
| Platform    | Apache Tomcat / Linux|
| PDF Engine  | iText                |
| Data Format | JSON (via Gson/Jackson equivalents) |

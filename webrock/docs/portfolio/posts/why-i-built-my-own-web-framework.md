---
title: "Why I Built My Own Java Web Framework Instead of Using Spring Boot"
description: "Frameworks like Spring Boot are powerful, but they often feel like magic. I decided to build my own MVC web framework from scratch to understand exactly how the magic works."
pubDate: "Aug 2026"

tags:
  - "Java"
  - "Architecture"
  - "Learning"
  - "Framework Design"
  - "Spring"
---

## Introduction

If you ask any Java developer today how to build a web application, the answer is almost universally: **Spring Boot**. 

Spring is an incredible piece of engineering. You put `@RestController` on a class, drop an `@Autowired` on a field, and suddenly you have a fully functional API with dependency injection, transaction management, and JSON serialization. But for a long time, this felt like magic to me. When things worked, it was great. When things broke—like a silent failure in the Application Context or a mysterious `NullPointerException` on an injected bean—I was left digging through stack traces I didn't understand.

I realized that relying on a framework without understanding its underlying mechanics limits your growth as an engineer. To break out of the "black box" mentality, I decided to build my own Servlet-based MVC web framework from scratch. I called it **WEBRock**.

Here is why I did it, and what building a framework taught me about enterprise software design.

## 1. Escaping the Boilerplate Trap

Before learning Spring, I learned the standard J2EE Servlet API. Traditional Servlets are powerful but extremely verbose. Every single endpoint requires a new class, overriding `doGet` or `doPost`, and manually extracting parameters from the `HttpServletRequest`.

Worse, you have to register every Servlet in a massive XML file (`web.xml`). If you have 50 endpoints, you have 50 classes and a bloated configuration file. 

I wanted to achieve the clean, annotation-driven routing of Spring MVC (`@GetMapping`) but using only the native Java Servlet API.

## 2. Re-inventing the Front Controller

The first major hurdle was eliminating the need for `web.xml`. I learned about the **Front Controller** pattern—a design where a single master Servlet intercepts *every* incoming HTTP request (mapped to `/*`).

But how does this master Servlet know where to send the request? 

The answer is **Java Reflection**. I created a custom `@PATH` annotation. During the server boot process (using `ServletContainerInitializer`), WEBRock scans the compiled `.class` files in the project. It looks for any method annotated with `@PATH`, extracts the URL, and stores a reference to that `Method` object in an in-memory `HashMap`.

When a request arrives, the Front Controller simply extracts the URL, does an $O(1)$ lookup in the map, and invokes the target method dynamically. Suddenly, the "magic" of Spring routing made perfect sense—it's just an intelligent caching of Reflection data!

## 3. Demystifying Dependency Injection

The next challenge was object management. I didn't want developers using WEBRock to instantiate objects with `new Keyword()`, because that creates tight coupling. I needed to build my own Inversion of Control (IoC) container.

I created an `@AutoWired` annotation. On server startup, after building the routing table, the framework does a second pass. It instantiates singleton instances of all service classes and stores them in a registry. Then, it uses Reflection to scan for fields annotated with `@AutoWired`. 

Using `field.setAccessible(true)`, the framework injects the singleton dependencies directly into the private fields. Building this taught me exactly how Spring's Application Context resolves object graphs and dependency cycles. It isn't magic; it's just a well-orchestrated startup sequence.

## 4. Taking It Further: Code Generation

Once I had the routing table in memory, I realized I possessed complete metadata about the entire backend API. I knew every URL, every required parameter, and every HTTP method. 

I decided to push the framework further than just routing. I wrote a module that takes this metadata and dynamically generates **ES6 JavaScript client code**. When a frontend developer requests a specific JS file, WEBRock serves a dynamically generated string of `fetch()` wrappers. 

This completely eliminated the need to manually write AJAX calls on the frontend, ensuring the client and server were always perfectly in sync.

## The Biggest Takeaway

Building WEBRock took immense effort, and I have no illusions of it replacing Spring Boot in production. But that wasn't the goal.

The goal was to peek behind the curtain. By building a framework from the ground up, I learned the low-level details of the HTTP lifecycle, the performance implications of Java Reflection, and the power of Aspect-Oriented design. 

Now, when I use Spring Boot, I don't see magic anymore. I see a highly polished version of the exact same patterns I built myself. And when a bug occurs deep inside the container, I know exactly where to look.

If you ever feel like your tools are doing too much of your thinking for you, I highly recommend trying to rebuild them. The lessons learned will fundamentally change how you write code.

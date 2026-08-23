---
title: "Securing APIs at Scale: Implementing Annotation-Based Authorization"
description: "How I built a global interceptor mechanism using a @SecuredAccess annotation to protect API endpoints without duplicating authentication logic."
pubDate: "Aug 2026"

tags:
  - "Java"
  - "Security"
  - "Web Architecture"
  - "Framework Design"
---

## Introduction

In any web application, certain endpoints must be public (like `/login` or `/signup`), while others must be protected (like `/getUserData` or `/checkout`). 

If you aren't using a framework, you usually end up with code that looks like this at the beginning of *every single* protected Servlet:

```java
HttpSession session = request.getSession(false);
if (session == null || session.getAttribute("USER_ID") == null) {
    response.setStatus(401); // Unauthorized
    return;
}
// Proceed with business logic...
```

This is a massive violation of the DRY (Don't Repeat Yourself) principle. If your session validation logic changes, you have to update it in dozens of files. 

While building **WEBRock**, my custom Java framework, I wanted a cleaner solution. I wanted to apply security at the method level using a simple annotation, similar to Spring Security's `@PreAuthorize`.

## The @SecuredAccess Annotation

The goal is to let developers flag protected methods explicitly, while leaving unannotated methods public by default. 

First, I created the annotation:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SecuredAccess {
    // We could add roles here later, e.g., String[] roles() default {};
}
```

Now, a developer can secure a route simply by attaching this annotation to the method:

```java
public class UserController {
    
    // This is public
    @PATH("/login")
    public boolean login(String username, String password) {
        // logic...
    }
    
    // This is protected!
    @PATH("/profile")
    @SecuredAccess
    public UserProfile getProfile(int userId) {
        // logic...
    }
}
```

## Implementing the Global Interceptor

Because WEBRock uses a **Front Controller** pattern (a single master `HttpServlet` that intercepts all incoming requests), we have a perfect chokepoint to enforce security globally.

When a request arrives, the Front Controller looks up the target method in the routing table. Before it attempts to bind parameters or invoke the method, it checks for the `@SecuredAccess` annotation.

```java
// Inside the Front Controller's service() method
Method targetMethod = routingTable.get(requestUrl);

if (targetMethod != null) {
    
    // --- SECURITY INTERCEPTOR ---
    if (targetMethod.isAnnotationPresent(SecuredAccess.class)) {
        
        HttpSession session = request.getSession(false);
        
        // If there is no session, or the user isn't logged in, block the request
        if (session == null || session.getAttribute("loggedInUser") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized access\"}");
            return; // Halt execution! Do not invoke the target method.
        }
    }
    // --- END SECURITY INTERCEPTOR ---
    
    // Proceed to parameter binding and method invocation
    invokeMethod(targetMethod, request, response);
}
```

## The Benefits of Aspect-Oriented Design

This approach is a lightweight form of Aspect-Oriented Programming (AOP). We have separated a cross-cutting concern (authorization) from our core business logic.

The benefits are immediate:
1. **No Code Duplication**: The session validation logic exists in exactly one place (the Front Controller).
2. **Readability**: Developers looking at the `UserController` instantly know which routes are protected just by glancing at the annotations.
3. **Fail-Safe Security**: Because the Front Controller enforces the rule before the method is even invoked, it is impossible for a developer to accidentally execute protected business logic while forgetting to check the session state.

## Conclusion

Building a custom security interceptor taught me that framework security isn't magic—it's just a combination of centralized routing and runtime metadata. 

By pushing the authentication logic up into the framework layer, the underlying application code remains clean, focused, and purely concerned with business rules.

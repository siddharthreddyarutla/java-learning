### 🔹 Web Server (High level)

A **web server**:

* Listens on a **port (8080/80)**
* Accepts **HTTP requests**
* Sends **HTTP responses**

### 🔹 **Apache Tomcat**

Tomcat is:

* A **Servlet container**
* NOT a full Java EE server
* Used by Spring Boot by default

Tomcat responsibilities:

* Accept HTTP connections
* Manage **threads**
* Convert HTTP → Servlet API
* Call **Servlets**

---

## 🧵 2️⃣ Thread model in Tomcat (Blocking I/O)

### Traditional Tomcat (Servlet stack)

```
1 request = 1 thread
```

Flow:

```
Client
 → Tomcat thread pool
   → One thread is assigned
     → That thread is blocked until response is sent
```

If thread is busy:

* DB call
* External API
* Sleep / wait

👉 **Thread is blocked**

---

## 📦 3️⃣ What is a Servlet?

### 🔹 Servlet

A **Servlet** is a Java class that:

* Receives HTTP requests
* Produces HTTP responses

```java
public interface Servlet {
    void service(ServletRequest req, ServletResponse res);
}
```

Examples:

* `HttpServlet`
* `DispatcherServlet` (Spring)

---

## 🚦 4️⃣ DispatcherServlet (Heart of Spring MVC)

![Image](https://terasolunaorg.github.io/guideline/5.2.0.RELEASE/en/_images/RequestLifecycle.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1200/0%2A1iuj1QRg0zBPrAc0.png)

![Image](https://justforchangesake.wordpress.com/wp-content/uploads/2014/05/spring-request-lifecycle.jpg)

### 🔹 DispatcherServlet

It is:

* A **Front Controller**
* A **Servlet**
* Entry point of **Spring MVC**

👉 **Every request goes through it**

### Responsibilities:

1. Find the controller
2. Call interceptors
3. Call controller method
4. Resolve response
5. Return response

---

## 🔀 5️⃣ Complete Request Flow (Spring MVC)

```
Client
 ↓
Tomcat (Thread allocated)
 ↓
Filter(s)
 ↓
DispatcherServlet
 ↓
HandlerMapping
 ↓
Interceptor (preHandle)
 ↓
Controller
 ↓
Interceptor (postHandle)
 ↓
ViewResolver / ResponseBody
 ↓
Interceptor (afterCompletion)
 ↓
Response → Client
```

---

## 🧱 6️⃣ Filters vs Interceptors (VERY IMPORTANT)

### 🔹 Filters (Servlet level)

📌 **Part of Servlet specification (javax.servlet)**
📌 Runs **before Spring**

```java
public class AuthFilter implements Filter {
    public void doFilter(...) {}
}
```

### Where filter lies:

```
Client → Tomcat → FILTER → DispatcherServlet
```

### Use cases:

* Authentication (JWT)
* Logging
* CORS
* Request/Response modification
* Compression

✅ Works for **all servlets**
❌ Does NOT know about controllers

---

### 🔹 Interceptors (Spring-specific)

📌 **Part of Spring MVC**
📌 Runs **inside Spring**

```java
public class AuthInterceptor implements HandlerInterceptor
```

### Where handlerInterceptor lies:

```
DispatcherServlet → INTERCEPTOR → Controller
```

### Use cases:

* Authorization
* Request validation
* Business logging
* Metrics
* User context

✅ Knows controller & handler
❌ Spring MVC only

---

## 🧠 7️⃣ Filter vs Interceptor (Interview Table)

| Aspect              | Filter        | Interceptor     |
| ------------------- | ------------- | --------------- |
| Belongs to          | Servlet spec  | Spring          |
| Executes            | Before Spring | Inside Spring   |
| Aware of controller | ❌ No          | ✅ Yes           |
| Use case            | Infra-level   | App-level       |
| Works with          | All servlets  | Only Spring MVC |

💡 **Interview line:**

> “Filters are web-container level, interceptors are framework level.”

---

## 🔁 8️⃣ Interceptor Lifecycle Methods

```java
public interface HandlerInterceptor {
    boolean preHandle(...)
    void postHandle(...)
    void afterCompletion(...)
}
```

### What each does:

#### `preHandle`

* Before controller
* Can block request

```java
return false; // stops request
```

#### `postHandle`

* After controller
* Before response is rendered

#### `afterCompletion`

* After response
* Cleanup, logging

---

## 📡 9️⃣ SOAP APIs vs REST APIs

### 🔹 SOAP

* XML only
* Heavy
* Strict contract (WSDL)
* Older enterprise systems

```
SOAP Envelope
  → Header
  → Body
```

### 🔹 REST (Spring Boot default)

* JSON
* Lightweight
* HTTP methods
* Flexible

📌 Spring Boot supports SOAP, but REST is preferred today.

---

## 🧵 🔄 10️⃣ Modern Threading: NIO (Non-Blocking)

![Image](https://media.geeksforgeeks.org/wp-content/uploads/20220420215510/Nio.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AclDJ293GZQ9sIgb5krt95Q.gif)

![Image](https://miro.medium.com/1%2Ay8OTPaojQ9uRkxZK0Adc3Q.png)

### Traditional (Blocking)

```
1 request = 1 thread
```

### NIO / Event Loop

```
Few threads handle many requests
```

Flow:

* Request registers event
* Thread is freed
* Callback executes later

👉 Much better scalability

---

## ⚡ 1️⃣1️⃣ Spring WebFlux (Reactive)

### Spring MVC

* Blocking
* Thread-per-request
* Uses Tomcat (Servlet)

### Spring WebFlux

* Non-blocking
* Event loop
* Uses Netty

```java
Mono<Response> getUser() {}
Flux<User> getUsers() {}
```

### When to use WebFlux:

* High concurrency
* Streaming
* Async APIs
* IO-heavy apps

❌ NOT for CPU-heavy tasks

---

## 🧠 1️⃣2️⃣ Where Filters & Interceptors fit in Reactive?

| Stack          | Filter         | Interceptor                       |
| -------------- | -------------- | --------------------------------- |
| Spring MVC     | Servlet Filter | HandlerInterceptor                |
| Spring WebFlux | WebFilter      | WebFilter / HandlerFilterFunction |

---

## 🧩 1️⃣3️⃣ End-to-End Summary (MEMORIZE THIS)

```
Client
 ↓
Tomcat (Thread / NIO)
 ↓
Filter (Security, Logging)
 ↓
DispatcherServlet
 ↓
HandlerMapping
 ↓
Interceptor (Auth, Metrics)
 ↓
Controller
 ↓
Service → DB / API
 ↓
Response
```

---

## 🧠 Final Interview One-Liners

* **Tomcat** is a Servlet container
* **Servlet** handles HTTP
* **DispatcherServlet** is Spring’s front controller
* **Filter** works before Spring
* **Interceptor** works inside Spring
* **Spring MVC** is blocking
* **Spring WebFlux** is non-blocking
* **Reactive ≠ Faster**, it’s **more scalable**

---
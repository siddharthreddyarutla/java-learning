
## 🧠 Big Picture First (mental model)

![Image](https://miro.medium.com/0%2ACdg8CBPWokYfi9WV.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AN2UsFp7KGpAAD6UcPmLUUQ.png)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/0%2A1iuj1QRg0zBPrAc0.png)

```
Raw HTTP (bytes)
 ↓
Tomcat Coyote (HTTP engine)
 ↓
Servlet Container
 ↓
Filters
 ↓
DispatcherServlet (Spring)
 ↓
HandlerMapping
 ↓
HandlerAdapter
 ↓
Controller method
 ↓
Response
```

Now let’s zoom into each piece 👇

---

## 1️⃣ Tomcat is a Servlet Container — so what is **Coyote**?

### 🔹 **Apache Tomcat** has **multiple internal layers**

Tomcat is NOT just one thing. Internally, it has **connectors + container**.

### 🔹 What is Coyote?

**Coyote is Tomcat’s HTTP engine (Connector)**.

👉 Its job:

* Accept **raw HTTP requests** (bytes)
* Parse:

    * HTTP method
    * Headers
    * URI
    * Body
* Convert them into:

  ```
  HttpServletRequest
  HttpServletResponse
  ```

📌 **Coyote does NOT know about Spring, Controllers, or Servlets logic**

Think of it like this:

> **Coyote = Translator from HTTP → Java objects**

---

### 🧩 Internal Tomcat Layers

```
[Coyote]
  ↓
[Engine]
  ↓
[Host]
  ↓
[Context]
  ↓
[Wrapper (Servlet)]
```

* **Coyote** → Network + HTTP
* **Container** → Servlet lifecycle & execution

---

## 2️⃣ If DispatcherServlet exists, why do we need OTHER Servlets?

### 🔹 Servlet basics

A **Servlet** is just a Java class that can handle requests:

```java
public interface Servlet {
    void service(ServletRequest req, ServletResponse res);
}
```

### Common servlets:

* `DispatcherServlet` (Spring MVC)
* `DefaultServlet` (static resources)
* `JspServlet` (JSP rendering)
* Custom servlets (rare today)

---

### 🔹 What does DispatcherServlet do differently?

### 🔹 **DispatcherServlet**

DispatcherServlet is:

* A **Servlet**
* A **Front Controller**
* The **entry point of Spring MVC**

Instead of handling logic itself, it:

* Delegates request handling to Spring components

📌 **Without DispatcherServlet, Spring MVC does not exist**

---

### 🔁 Multiple Servlets can coexist

Example:

```
/api/*        → DispatcherServlet
/static/*     → DefaultServlet
/jsp/*        → JspServlet
```

So:

* DispatcherServlet ≠ only servlet
* It just handles **Spring MVC requests**

---

## 3️⃣ How does request reach DispatcherServlet?

### 🔹 Servlet mapping (VERY IMPORTANT)

In Spring Boot:

```java
DispatcherServlet → mapped to "/"
```

Meaning:

> All requests go to DispatcherServlet **unless explicitly excluded**

Flow:

```
Coyote
 ↓
Servlet Container
 ↓
DispatcherServlet
```

---

## 4️⃣ What is HandlerMapping? (This is the brain)

Once the request reaches DispatcherServlet, the **real magic starts**.

### 🔹 What is HandlerMapping?

> **HandlerMapping decides WHICH controller method should handle the request**

It maps:

```
HTTP Method + URL
        ↓
Controller method
```

Example:

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {}
```

HandlerMapping says:

```
GET /users/10 → getUser()
```

---

### 🔹 Types of HandlerMapping (important for interviews)

Spring has multiple implementations:

1. **RequestMappingHandlerMapping** ✅ (MOST IMPORTANT)
2. SimpleUrlHandlerMapping
3. BeanNameUrlHandlerMapping

👉 In modern Spring MVC:

* **RequestMappingHandlerMapping** is used almost always

---

## 5️⃣ Step-by-step: How endpoint is routed to controller

Let’s trace this **exactly** 👇

### Request:

```
GET /api/users/10
```

---

### Step 1: DispatcherServlet receives request

```java
doDispatch(HttpServletRequest req, HttpServletResponse res)
```

---

### Step 2: DispatcherServlet asks HandlerMappings

```java
HandlerExecutionChain chain =
    handlerMapping.getHandler(request);
```

HandlerMapping:

* Scans all controllers at startup
* Builds a map like:

```
(GET, /api/users/{id}) → UserController#getUser()
```

---

### Step 3: HandlerExecutionChain is created

Contains:

* Controller method
* Interceptors (if any)

---

### Step 4: HandlerAdapter is selected

Different handlers need different adapters.

For `@Controller`:

* `RequestMappingHandlerAdapter`

```java
adapter.handle(request, response, handler);
```

📌 **DispatcherServlet never calls controller directly**

---

### Step 5: Controller method is invoked

```java
getUser(10)
```

Spring handles:

* Path variables
* Request body
* Headers
* Validation
* Conversion

---

### Step 6: Response processing

* `@ResponseBody` → JSON
* ViewResolver → JSP / Thymeleaf
* HTTP status applied

---

### Step 7: Response sent back

```
Controller
 ↓
DispatcherServlet
 ↓
Servlet Container
 ↓
Coyote
 ↓
Client
```

---

## 6️⃣ Where do Filters and Interceptors fit?

### 🔹 Filters (Servlet-level)

```
Coyote
 ↓
Filter
 ↓
DispatcherServlet
```

Used for:

* JWT auth
* CORS
* Logging
* Compression

---

### 🔹 Interceptors (Spring-level)

```
DispatcherServlet
 ↓
Interceptor (preHandle)
 ↓
Controller
 ↓
Interceptor (postHandle)
 ↓
Interceptor (afterCompletion)
```

Used for:

* Authorization
* Metrics
* Request context
* Business logging

---

## 7️⃣ Blocking vs NIO — where does it change?

### Traditional Tomcat (Blocking)

```
1 request = 1 thread
```

Thread waits for:

* DB
* API
* IO

---

### Tomcat NIO (still Servlet-based)

* Uses non-blocking sockets
* BUT once request enters Servlet:

  ```
  Thread is still blocked
  ```

So:

> Tomcat NIO ≠ Fully non-blocking application

---

## 8️⃣ True Non-Blocking (Reactive)

### Spring WebFlux + Netty

```
Event Loop
 ↓
Non-blocking handlers
 ↓
Mono / Flux
```

No:

* Servlet
* DispatcherServlet (MVC one)

Instead:

* WebHandler
* WebFilter
* Reactive pipeline

---

## 🧠 Final “Interview Gold” Summary

* **Coyote** parses HTTP → Java objects
* **Servlet Container** manages servlet lifecycle
* **DispatcherServlet** is Spring MVC front controller
* **HandlerMapping** maps URL → controller method
* **HandlerAdapter** invokes controller safely
* **Filters** run before Spring
* **Interceptors** run inside Spring
* **Tomcat NIO** improves IO, not app-level blocking
* **Reactive (WebFlux)** is truly non-blocking

---

## 🎯 One-liners you can say confidently

* “Coyote is Tomcat’s HTTP engine.”
* “DispatcherServlet delegates requests; it doesn’t handle business logic.”
* “HandlerMapping is responsible for routing URLs to controller methods.”
* “HandlerAdapter abstracts controller invocation.”
* “Servlet stack is blocking; reactive stack is event-driven.”

---

If you want next, I can:

* Draw **exact sequence diagram**
* Show **DispatcherServlet source code flow**
* Explain **why HandlerAdapter exists**
* Compare **Spring MVC vs WebFlux internals**
* Explain **thread pools & tuning in Tomcat**

Just tell me 🚀

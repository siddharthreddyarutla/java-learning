## 📘 Overview

This class `TrackingIdInterceptor` is a **Spring Boot `HandlerInterceptor`** that automatically attaches a **tracking ID** (unique identifier) to every incoming HTTP request — and ensures that the same ID appears in your logs, making it easier to **trace a request** across services and logs.

---

## ⚙️ Step-by-Step Explanation

### **1️⃣ Class Definition**

```java
@Component
@Slf4j
public class TrackingIdInterceptor implements HandlerInterceptor
```

* `@Component` — registers this class as a Spring Bean, so Spring can automatically add it to the interceptor chain.
* `@Slf4j` — Lombok annotation that provides a `log` object for logging (uses `org.slf4j.Logger`).
* `HandlerInterceptor` — interface in Spring MVC that allows you to **intercept HTTP requests** before they reach the controller or after they complete.

---

## **2️⃣ Constant for Header**

```java
private static final String TRACKING_ID_HEADER = "X-PH-Tracking-ID";
```

* This defines a **custom HTTP header name**.
* Example header in a request:

  ```
  X-PH-Tracking-ID: 123e4567-e89b-12d3-a456-426614174000
  ```

---

## **3️⃣ preHandle() — Before Controller Execution**

```java
@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
```

* Called **before** the controller method runs.
* Used here to check if a **tracking ID** is already present in the request.

#### Logic inside:

```java
String trackingId = request.getHeader(TRACKING_ID_HEADER);
```

* Reads the custom header `X-PH-Tracking-ID` from the incoming HTTP request.

---

### **Case 1: Tracking ID exists**

```java
if (trackingId != null && !trackingId.isEmpty()) {
    MDC.put(TRACKING_ID_HEADER, trackingId);
    log.trace("Added tracking id MDC");
}
```

* If client already sent a tracking ID, store it inside the **MDC** (Mapped Diagnostic Context).
* This means that **every log entry from this thread** will automatically include that tracking ID.

---

### **Case 2: Tracking ID not present**

```java
else {
    MDC.put(TRACKING_ID_HEADER, UUID.randomUUID().toString());
    log.trace("There is not tracking id present in the request");
}
```

* If header is missing, generate a **new unique tracking ID (UUID)**.
* Store it in MDC.
* This ensures that **every request always has a unique ID** for tracing.

---

### **Return `true`**

```java
return true;
```

* Tells Spring to continue with the request — i.e., don’t block the request.

---

## **4️⃣ postHandle() — After Controller Execution (Before View Rendering)**

```java
@Override
public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
```

* Currently **empty** — you could use it for adding the tracking ID to the response header if needed, like:

  ```java
  response.addHeader(TRACKING_ID_HEADER, MDC.get(TRACKING_ID_HEADER));
  ```
* Called after controller method runs but before sending the response.

---

## **5️⃣ afterCompletion() — After the Request Completes**

```java
@Override
public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception)
```

* Runs **after the request is fully completed** (view rendered, response sent).
* Here, you’re cleaning up MDC:

  ```java
  MDC.remove(TRACKING_ID_HEADER);
  ```
* This prevents **memory leaks** or **tracking ID bleeding** between requests (since MDC is thread-local).

---

## 💡 What is **MDC** (Mapped Diagnostic Context)?

> **MDC** stands for **Mapped Diagnostic Context**, part of **SLF4J / Logback / Log4j** logging frameworks.

### 👉 In short:

It’s a **thread-local map** that holds diagnostic information (like tracking IDs, user IDs, session IDs) for logging.

Every log line automatically includes these MDC values — which helps you **trace logs** related to a specific request or user.

### Example:

```java
MDC.put("X-PH-Tracking-ID", "abc-123");
log.info("Processing request");
```

With proper Logback config, your log file might look like:

```
2025-10-09 14:35:12 INFO [X-PH-Tracking-ID=abc-123] Processing request
```

Even if multiple requests are processed in parallel threads, each one gets its **own tracking ID** because MDC is **thread-local**.

---

## 🧩 Why is this useful?

1. ✅ **End-to-end traceability** across microservices.
   You can pass this tracking ID to downstream APIs (e.g., via headers).

2. ✅ **Easy debugging.**
   You can filter logs by the tracking ID in Kibana / Splunk / ELK Stack.

3. ✅ **Consistent logging context** — even async processes can attach this MDC.

---

## 🧠 Summary

| Method              | Purpose                        | Key Action                                               |
| ------------------- | ------------------------------ | -------------------------------------------------------- |
| `preHandle()`       | Before controller              | Reads header or generates new tracking ID, stores in MDC |
| `postHandle()`      | After controller (before view) | (Optional) Add tracking ID to response                   |
| `afterCompletion()` | After request completes        | Removes tracking ID from MDC to avoid memory leaks       |
| **MDC**             | Mapped Diagnostic Context      | Thread-local storage for log context (like tracking ID)  |

---

### ✅ Example of how it helps in logs

Without MDC:

```
INFO - Processing request for /users
INFO - Completed request
```

With MDC:

```
INFO [X-PH-Tracking-ID=12345] - Processing request for /users
INFO [X-PH-Tracking-ID=12345] - Completed request
```
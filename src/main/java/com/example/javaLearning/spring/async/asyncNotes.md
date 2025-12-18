# Async


# 📘 Notes: @Async Hidden Thread Problem (Spring Boot)

## 🔹 What is `@Async`?

* Runs a method **asynchronously** in a separate thread.
* Caller does NOT wait for the method to finish.
* Needs `@EnableAsync` to work.

---

## 🔹 How @Async Works Internally

### 1️⃣ **Proxy Mechanism**

* Spring creates a **proxy** around beans containing `@Async` methods.
* When the method is called, proxy submits it to a **TaskExecutor (thread pool)**.

### 2️⃣ **ThreadPoolTaskExecutor**

* By default: `SimpleAsyncTaskExecutor` (creates unlimited threads → not good).
* Best practice: Configure custom executor with pool size, queue size, thread name prefix.

---

## 🔹 Example Behavior

Without `@Async` → blocking
With `@Async` → non-blocking, runs in background thread

---

# ⚠️ Hidden Traps & Rules Beginners Miss

## 1️⃣ Must Use `@EnableAsync`

Without it → `@Async` does NOT work, method runs normally.

---

## 2️⃣ Only Works on **public methods**

* Proxy can intercept only public methods.
* `private`, `protected`, or `package-private` = **won’t run async**.

---

## 3️⃣ **Self-invocation does NOT trigger @Async**

**Most common mistake**

```java
public void a() {
    b(); // calling @Async method inside same class = NOT async
}
```

* Internal calls bypass Spring proxy.
* Fix: Call async method from a **different bean**.

---

## 4️⃣ Always define your own thread pool

Best practice:

```java
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

  @Override
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(100);
    executor.setThreadNamePrefix("AsyncThread-");
    executor.initialize();
    return executor;
  }
}
```

Gives better:

* thread management
* debugging using custom thread names
* performance control

---

## 5️⃣ Exceptions in async methods are swallowed

Need custom exception handler:

```java
public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler()
```

Otherwise errors disappear.

---

## 6️⃣ Choose correct return types

* `void` → fire-and-forget
* `CompletableFuture<T>` → async result + chaining

---

## 7️⃣ Avoid shared mutable state

Async threads running simultaneously can cause race conditions.

Use:

* `AtomicInteger`
* immutable data
* thread-safe collections

---

# ✔ Final Key Takeaways

* `@Async` works ONLY with **public methods**, from **another bean**, with **@EnableAsync** enabled.
* Configure your **own thread pool** for production.
* Be careful about **self-invocation**, **exceptions**, and **shared state**.
* Use **thread name prefixes** to debug easily.

---

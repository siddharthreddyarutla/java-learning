# Semaphore

> A semaphore is a synchronization tool used in multithreading and concurrent programming to control access to a shared resource.


A semaphore maintains a counter that represents the number of permits available. Threads must acquire a permit before accessing the resource and release it afterward.

## Summary of What Semaphore Does

1. Controls concurrency with a fixed number of permits (i.e., allowed threads).
2. Threads that acquire a permit proceed.
3. If no permit is available, the thread is blocked until a permit is released.


Ex:
```java
Semaphore semaphore = new Semaphore(3);  // Allows 3 threads at a time
```

* Thread 1 → acquires permit ✅
* Thread 2 → acquires permit ✅
* Thread 3 → acquires permit ✅
* Thread 4 → waits ❌ (blocked until one of the above releases)

## Common Methods

| Method               | Description                                                           |
| -------------------- | --------------------------------------------------------------------- |
| `acquire()`          | Acquires a permit. Waits if none are available.                       |
| `release()`          | Releases a permit, increasing the count.                              |
| `tryAcquire()`       | Tries to acquire a permit without waiting. Returns `true` or `false`. |
| `availablePermits()` | Returns the number of currently available permits.                    |

# 🧠 Redis Cache with Spring Annotations (MUST-KNOW)

Spring Cache = **abstraction**
Redis = **cache provider**

```
@Cacheable / @CachePut / @CacheEvict
        ↓
Spring Cache
        ↓
Redis (via Lettuce)
```

---

## 1️⃣ Enable Redis Cache (baseline)

```java
@EnableCaching
@SpringBootApplication
public class App { }
```

Without this → annotations do nothing.

---

## 2️⃣ @Cacheable (READ CACHE)

### What it does

* Checks cache **before** method execution
* If value exists → method **NOT called**
* If not → method runs and result is cached

### Example

```java
@Cacheable(value = "users", key = "#id")
public User getUser(Long id) {
    return userRepository.findById(id);
}
```

### Actual flow

1. Redis GET `users::1`
2. If HIT → return cached value
3. If MISS → DB call → SET Redis

👉 Interview line:

> `@Cacheable` avoids method execution on cache hit.

---

## 3️⃣ @CachePut (UPDATE CACHE)

### What it does

* **Always executes** the method
* Updates cache with returned value

### Example

```java
@CachePut(value = "users", key = "#user.id")
public User updateUser(User user) {
    return userRepository.save(user);
}
```

### Use when

* Updating DB
* Want cache to reflect latest data

👉 Interview trap:
❌ `@CachePut` does NOT skip method execution

---

## 4️⃣ @CacheEvict (DELETE CACHE)

### What it does

* Removes entry from cache
* Method may or may not execute first

### Example (evict one key)

```java
@CacheEvict(value = "users", key = "#id")
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}
```

### Evict all entries

```java
@CacheEvict(value = "users", allEntries = true)
```

👉 Interview line:

> `@CacheEvict` ensures cache consistency after deletes.

---

## 5️⃣ @Caching (MULTIPLE CACHE OPS)

Used when:

* One method affects multiple caches

```java
@Caching(
  evict = {
    @CacheEvict(value = "users", key = "#id"),
    @CacheEvict(value = "userDetails", key = "#id")
  }
)
public void deleteUser(Long id) {
    userRepository.deleteById(id);
}
```

---

## 6️⃣ Cache Key Generation (VERY IMPORTANT)

### Default key

* All method parameters combined

### Explicit key (recommended)

```java
key = "#id"
```

### SpEL examples

```java
key = "#user.id"
key = "#root.methodName + ':' + #id"
```

👉 Interview trap:
❌ Poor key design → cache collisions

---

## 7️⃣ Condition & Unless (INTERVIEW FAVORITE)

### condition → before execution

```java
@Cacheable(value="users", key="#id", condition="#id > 0")
```

### unless → after execution

```java
@Cacheable(value="users", key="#id", unless="#result == null")
```

👉 Interview line:

> `condition` controls cache usage before method execution, while `unless` controls whether the result is cached.

---

## 8️⃣ TTL with Redis Cache (IMPORTANT)

TTL is **NOT** in annotation ❌
It’s configured in **RedisCacheManager**.

```java
@Bean
RedisCacheManager cacheManager(RedisConnectionFactory factory) {
    RedisCacheConfiguration config =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10));

    return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
}
```

👉 Interview trap:
❌ TTL cannot be set per method (by default)

---

## 9️⃣ Serialization (CRITICAL)

### Default

* JDK serialization ❌ (slow)

### Recommended

* JSON (Jackson)

```java
RedisCacheConfiguration.defaultCacheConfig()
  .serializeValuesWith(
    RedisSerializationContext.SerializationPair
      .fromSerializer(new GenericJackson2JsonRedisSerializer())
  );
```

👉 Interview line:

> Serialization choice significantly impacts Redis memory and latency.

---

## 🔟 Cache Consistency (SYSTEM DESIGN SIGNAL)

### Common mistakes

❌ Update DB, forget cache
❌ Multiple services updating same cache

### Best practice

* Write DB first
* Then update or evict cache
* Redis is **not source of truth**

---

## 11️⃣ Cache Stampede Protection (IMPORTANT)

Annotations alone ❌
Mitigate by:

* TTL + jitter
* `@Cacheable(sync = true)` (local lock)

```java
@Cacheable(value="users", key="#id", sync=true)
```

👉 Interview line:

> `sync=true` prevents multiple threads from loading the same cache key concurrently.

---

## 12️⃣ Common Interview Traps (ANNOTATIONS)

| Question                              | Correct Answer |
| ------------------------------------- | -------------- |
| Does @Cacheable always call method?   | ❌ No           |
| Does @CachePut skip method execution? | ❌ No           |
| Can TTL be set in annotation?         | ❌ No           |
| Does RedisCacheManager handle TTL?    | ✅ Yes          |
| Is cache transaction-aware?           | ❌ No           |

---

## 🧠 ONE PERFECT INTERVIEW ANSWER (ANNOTATIONS)

> In Spring Boot, Redis caching is commonly implemented using Spring Cache annotations like `@Cacheable`, `@CachePut`, and `@CacheEvict`. These annotations provide declarative caching, while Redis acts as the cache store via Spring Data Redis and Lettuce. TTL, serialization, and cache consistency must be configured carefully for production use.

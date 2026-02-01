# **Spring Boot Performance Metrics - Complete Guide**

---

## **Table of Contents**
1. [JVM Memory Management & Garbage Collection](#1-jvm-memory-management--garbage-collection)
2. [Thread Management](#2-thread-management)
3. [CPU Metrics](#3-cpu-metrics)
4. [Tomcat/Servlet Metrics](#4-tomcatservlet-metrics)
5. [Database Connection Pool Metrics](#5-database-connection-pool-metrics)
6. [HTTP Request Metrics](#6-http-request-metrics)
7. [System Metrics](#7-system-metrics)
8. [Custom Application Metrics](#8-custom-application-metrics)
9. [Performance Tuning Guidelines](#9-performance-tuning-guidelines)
10. [Monitoring Best Practices](#10-monitoring-best-practices)

---

## **1. JVM Memory Management & Garbage Collection**

### **1.1 JVM Memory Structure**

```
┌─────────────────────────────────────────────────────────┐
│                    JVM Memory                            │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │              HEAP MEMORY                        │    │
│  ├────────────────────────────────────────────────┤    │
│  │  Young Generation                               │    │
│  │  ┌──────────┬──────────┬──────────┐           │    │
│  │  │  Eden    │ Survivor │ Survivor │           │    │
│  │  │  Space   │  S0 (From)│ S1 (To) │           │    │
│  │  └──────────┴──────────┴──────────┘           │    │
│  │                                                 │    │
│  │  Old Generation (Tenured)                      │    │
│  │  ┌──────────────────────────────────┐         │    │
│  │  │  Long-lived objects              │         │    │
│  │  └──────────────────────────────────┘         │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │           NON-HEAP MEMORY                       │    │
│  ├────────────────────────────────────────────────┤    │
│  │  Metaspace (Java 8+)                           │    │
│  │  - Class metadata                               │    │
│  │  - Method data                                  │    │
│  │                                                 │    │
│  │  Code Cache                                     │    │
│  │  - JIT compiled code                           │    │
│  │                                                 │    │
│  │  Compressed Class Space                        │    │
│  └────────────────────────────────────────────────┘    │
│                                                          │
│  ┌────────────────────────────────────────────────┐    │
│  │           NATIVE MEMORY                         │    │
│  │  - Thread stacks                                │    │
│  │  - Direct ByteBuffers                          │    │
│  │  - JNI allocations                             │    │
│  └────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────┘
```

---

### **1.2 Heap Memory Regions Explained**

#### **A. Young Generation (Minor GC occurs here)**

**1. Eden Space:**
- **Purpose:** All new objects are allocated here first
- **Size:** Typically 80% of Young Generation
- **Lifecycle:** Objects are created → survive GC → move to Survivor
- **Example:**
  ```java
  User user = new User(); // Created in Eden Space
  ```

**2. Survivor Space (S0 and S1):**
- **Purpose:** Holds objects that survived Minor GC
- **Mechanism:** Objects move between S0 ↔ S1 with each GC
- **Age Tracking:** Each survival increments object's age counter
- **Promotion Threshold:** After ~15 survivals → promoted to Old Gen

**Survivor Space Flow:**
```
Initial State:
Eden: [obj1, obj2, obj3]
S0 (From): [empty]
S1 (To): [empty]

After GC #1:
Eden: [empty]
S0: [obj1, obj2] (age=1)
S1: [empty]

After GC #2:
Eden: [obj4, obj5]
S0: [empty]
S1: [obj1, obj2] (age=2)

After GC #3:
Eden: [empty]
S0: [obj1, obj2] (age=3), [obj4]
S1: [empty]

... continues until age >= 15 → Old Gen
```

#### **B. Old Generation (Tenured)**

- **Purpose:** Long-lived objects
- **Triggers:**
    - Objects surviving 15 Minor GCs
    - Large objects that don't fit in Eden
- **GC Type:** Major GC / Full GC (expensive!)
- **Example Objects:**
    - Singleton beans
    - Connection pools
    - Cache objects
    - Static variables

#### **C. Metaspace (Java 8+, replaced PermGen)**

- **Purpose:** Stores class metadata, method information
- **Dynamic:** Auto-expands (unlike PermGen)
- **Contents:**
    - Class structures
    - Method bytecode
    - Runtime constant pool
    - Annotations

---

### **1.3 Garbage Collection Types**

#### **Minor GC (Young Generation)**
- **Frequency:** Very frequent (every few seconds)
- **Duration:** Fast (milliseconds)
- **Trigger:** Eden Space is full
- **Algorithm:** Copy survivors to S0/S1
- **Impact:** Low (application pause < 100ms)

#### **Major GC (Old Generation)**
- **Frequency:** Rare (minutes/hours)
- **Duration:** Slow (seconds)
- **Trigger:** Old Gen is full
- **Impact:** HIGH (application pause > 1 second)

#### **Full GC (Entire Heap + Metaspace)**
- **Frequency:** Very rare
- **Duration:** Very slow
- **Trigger:**
    - Old Gen full
    - Metaspace full
    - Explicit `System.gc()` call
- **Impact:** CRITICAL (application freeze)

---

### **1.4 GC Algorithms**

#### **1. Serial GC** (`-XX:+UseSerialGC`)
```
Best for: Single-core, small apps
Pause Time: High
Throughput: Low
Use Case: Development machines
```

#### **2. Parallel GC** (`-XX:+UseParallelGC`) - **Default in Java 8**
```
Best for: Multi-core, batch processing
Pause Time: Moderate
Throughput: High
Use Case: Background jobs, analytics
```

#### **3. CMS (Concurrent Mark Sweep)** (`-XX:+UseConcMarkSweepGC`) - **Deprecated**
```
Best for: Low-latency applications
Pause Time: Low
Throughput: Moderate
Use Case: Web applications (legacy)
```

#### **4. G1 GC (Garbage First)** (`-XX:+UseG1GC`) - **Default in Java 9+**
```
Best for: Large heaps (>4GB), balanced apps
Pause Time: Predictable (target: 200ms)
Throughput: High
Use Case: Modern web applications, microservices
```

#### **5. ZGC** (`-XX:+UseZGC`) - **Java 11+**
```
Best for: Ultra-low latency (<10ms pauses)
Heap Size: Up to 16TB
Pause Time: <10ms (even for huge heaps)
Use Case: Real-time trading, gaming servers
```

#### **6. Shenandoah GC** (`-XX:+UseShenandoahGC`) - **Java 12+**
```
Best for: Low-latency, large heaps
Pause Time: <10ms
Concurrent: Yes (most work concurrent)
Use Case: Similar to ZGC
```

---

### **1.5 Key GC Metrics from Actuator**

```bash
# Memory Metrics
jvm.memory.used              # Current memory used
jvm.memory.committed         # Memory guaranteed by OS
jvm.memory.max               # Maximum memory (-Xmx)

# Heap Breakdown
jvm.memory.used{area="heap",id="PS Eden Space"}
jvm.memory.used{area="heap",id="PS Survivor Space"}
jvm.memory.used{area="heap",id="PS Old Gen"}

# Non-Heap
jvm.memory.used{area="nonheap",id="Metaspace"}
jvm.memory.used{area="nonheap",id="Code Cache"}
jvm.memory.used{area="nonheap",id="Compressed Class Space"}

# GC Metrics
jvm.gc.pause                 # GC pause duration
jvm.gc.pause{action="end of minor GC"}
jvm.gc.pause{action="end of major GC"}

jvm.gc.memory.allocated      # Memory allocated since last GC
jvm.gc.memory.promoted       # Memory promoted to Old Gen
jvm.gc.max.data.size         # Max Old Gen size
jvm.gc.live.data.size        # Old Gen size after Full GC

# GC Counts
jvm.gc.pause_count           # Number of GC events
```

---

### **1.6 Memory Lifecycle Example**

```java
@RestController
public class UserController {
    
    // This object lives in Old Gen (singleton)
    @Autowired
    private UserService userService;
    
    @GetMapping("/users")
    public List<User> getUsers() {
        // Step 1: List object created in Eden Space
        List<User> users = new ArrayList<>();
        
        // Step 2: User objects created in Eden
        for (int i = 0; i < 1000; i++) {
            User user = new User(); // Eden Space
            users.add(user);
        }
        
        // Step 3: If Minor GC occurs before return:
        //   - 'users' list survives → S0 (age=1)
        //   - Individual User objects survive → S0
        
        // Step 4: Return to client
        return users; // After response, eligible for GC
        
        // Step 5: Next Minor GC:
        //   - If 'users' still referenced → S1 (age=2)
        //   - If dereferenced → garbage collected
    }
}
```

---

### **1.7 GC Tuning Parameters**

```bash
# Heap Size
-Xms2g                 # Initial heap size
-Xmx4g                 # Maximum heap size
-XX:NewRatio=2         # Old:Young ratio (1:2)
-XX:SurvivorRatio=8    # Eden:Survivor ratio (8:1:1)

# Young Generation
-Xmn1g                 # Fixed Young Gen size
-XX:MaxTenuringThreshold=15  # Max age before promotion

# Metaspace
-XX:MetaspaceSize=256m
-XX:MaxMetaspaceSize=512m

# GC Algorithm
-XX:+UseG1GC           # Use G1 GC
-XX:MaxGCPauseMillis=200  # Target max pause time

# GC Logging (Java 8)
-XX:+PrintGCDetails
-XX:+PrintGCDateStamps
-Xloggc:/var/log/gc.log

# GC Logging (Java 9+)
-Xlog:gc*:file=/var/log/gc.log:time,uptime,Level,tags

# Monitoring
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/var/log/heapdump.hprof
```

---

## **2. Thread Management**

### **2.1 Thread Types in Spring Boot**

```
┌─────────────────────────────────────────────────┐
│           Thread Categories                     │
├─────────────────────────────────────────────────┤
│                                                  │
│  1. HTTP Request Threads (Tomcat/Jetty)        │
│     - http-nio-8080-exec-1                     │
│     - http-nio-8080-exec-2                     │
│     - ... (default pool: 200 threads)          │
│                                                  │
│  2. Async/Task Executor Threads                │
│     - task-1, task-2, ...                      │
│     - @Async methods                           │
│                                                  │
│  3. Scheduled Task Threads                     │
│     - scheduling-1                              │
│     - @Scheduled methods                        │
│                                                  │
│  4. Database Connection Threads                │
│     - HikariPool-1-connection-1                │
│                                                  │
│  5. JVM Internal Threads                       │
│     - GC threads                                │
│     - Compiler threads                          │
│     - Signal dispatcher                         │
│                                                  │
│  6. Application Threads                        │
│     - Custom ExecutorService threads           │
│                                                  │
└─────────────────────────────────────────────────┘
```

---

### **2.2 Thread States**

```java
public enum Thread.State {
    NEW,           // Created but not started
    RUNNABLE,      // Running or ready to run
    BLOCKED,       // Waiting for monitor lock
    WAITING,       // Waiting indefinitely (Object.wait())
    TIMED_WAITING, // Waiting with timeout (Thread.sleep())
    TERMINATED     // Completed execution
}
```

**Visual Example:**
```
Thread Lifecycle:
NEW → RUNNABLE ⟷ TIMED_WAITING
         ↓           ↑
         ↓           ↑
      BLOCKED ←──────┘
         ↓
    TERMINATED
```

---

### **2.3 Thread Metrics from Actuator**

```bash
# Thread Count
jvm.threads.live           # Currently active threads
jvm.threads.daemon         # Daemon threads (background)
jvm.threads.peak           # Peak thread count since JVM start
jvm.threads.states         # Threads by state (RUNNABLE, BLOCKED, etc.)

# Thread Dump
GET /actuator/threaddump   # Full thread dump with stack traces
```

**Example Thread Dump Analysis:**
```json
{
  "threads": [
    {
      "threadName": "http-nio-8080-exec-5",
      "threadId": 45,
      "threadState": "RUNNABLE",
      "stackTrace": [
        "java.net.SocketInputStream.socketRead0(Native Method)",
        "com.mysql.cj.protocol.ReadAheadInputStream.read(...)",
        "com.yourapp.UserRepository.findById(...)"
      ]
    },
    {
      "threadName": "HikariPool-1-housekeeper",
      "threadId": 23,
      "threadState": "TIMED_WAITING",
      "stackTrace": [
        "java.lang.Thread.sleep(Native Method)",
        "com.zaxxer.hikari.pool.HikariPool$HouseKeeper.run(...)"
      ]
    }
  ]
}
```

---

### **2.4 Thread Pool Configuration**

#### **Tomcat Thread Pool (application.yml):**
```yaml
server:
  tomcat:
    threads:
      max: 200              # Max threads (default: 200)
      min-spare: 10         # Min idle threads
    max-connections: 10000  # Max connections in queue
    accept-count: 100       # Max queue size when all threads busy
    connection-timeout: 20000  # Connection timeout (ms)
```

#### **Async Task Executor:**
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);      // Min threads
        executor.setMaxPoolSize(50);       // Max threads
        executor.setQueueCapacity(100);    // Queue size
        executor.setThreadNamePrefix("async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
```

---

### **2.5 Thread Metrics Interpretation**

| Metric | Good Range | Warning | Critical |
|--------|------------|---------|----------|
| `jvm.threads.live` | < 100 | 100-500 | > 500 |
| `jvm.threads.peak` | < 200 | 200-800 | > 800 |
| BLOCKED threads | 0-2 | 3-10 | > 10 |
| WAITING threads | < 20% | 20-40% | > 40% |

---

## **3. CPU Metrics**

### **3.1 CPU Metrics from Actuator**

```bash
# System-wide CPU usage
system.cpu.usage         # Overall system CPU (0.0 - 1.0)
system.cpu.count         # Number of CPU cores

# Process-specific CPU
process.cpu.usage        # JVM process CPU usage
process.uptime           # JVM uptime in seconds
process.start.time       # JVM start timestamp

# Load Average (Unix/Linux)
system.load.average.1m   # 1-minute load average
```

---

### **3.2 CPU Usage Interpretation**

```
CPU Usage Scale: 0.0 to 1.0 (or 0% to 100%)

system.cpu.usage = 0.75  →  75% of total system CPU
process.cpu.usage = 0.45 →  45% of CPU used by JVM

Example on 4-core machine:
- system.cpu.usage = 0.50 → 2 cores fully utilized
- process.cpu.usage = 0.25 → JVM using 1 core
```

**Load Average:**
```
system.load.average.1m = 2.5 on 4-core machine
→ Average 2.5 processes waiting for CPU
→ Healthy if < number of cores (4)
→ Warning if > cores (indicates CPU saturation)
```

---

### **3.3 High CPU Troubleshooting**

**Step 1: Identify Thread Using CPU**
```bash
# Get thread dump
curl http://localhost:8080/actuator/threaddump > threads.json

# Or use jstack
jstack <pid> > thread_dump.txt
```

**Step 2: Analyze Thread States**
```bash
# Count threads by state
grep "java.lang.Thread.State" thread_dump.txt | sort | uniq -c

# Example output:
#  150 java.lang.Thread.State: RUNNABLE
#   30 java.lang.Thread.State: TIMED_WAITING
#    5 java.lang.Thread.State: BLOCKED
```

**Common CPU Issues:**
1. **Infinite Loops**
   ```java
   while (true) {
       // Missing break or sleep
   }
   ```

2. **Heavy Computation**
   ```java
   for (int i = 0; i < 1000000; i++) {
       complexCalculation();  // No caching
   }
   ```

3. **Excessive GC**
   ```bash
   # Check GC frequency
   jvm.gc.pause_count  # If > 100/minute → memory leak
   ```

---

## **4. Tomcat/Servlet Metrics**

### **4.1 Tomcat Architecture**

```
┌────────────────────────────────────────────────────┐
│                  Tomcat Server                      │
├────────────────────────────────────────────────────┤
│                                                     │
│  ┌──────────────────────────────────────────────┐ │
│  │            Connector (Port 8080)              │ │
│  │  - Accepts HTTP connections                  │ │
│  │  - Protocol Handler (HTTP/1.1, HTTP/2)       │ │
│  └──────────────────────────────────────────────┘ │
│                        ↓                            │
│  ┌──────────────────────────────────────────────┐ │
│  │         Thread Pool (Executor)                │ │
│  │  ┌────┬────┬────┬────┬─────────┬────┐       │ │
│  │  │T1  │T2  │T3  │T4  │   ...   │T200│       │ │
│  │  └────┴────┴────┴────┴─────────┴────┘       │ │
│  │  - Default: 10 min, 200 max threads          │ │
│  └──────────────────────────────────────────────┘ │
│                        ↓                            │
│  ┌──────────────────────────────────────────────┐ │
│  │         Request Queue (acceptCount)           │ │
│  │  [Request1] [Request2] ... [RequestN]        │ │
│  │  - Max: 100 (default)                         │ │
│  └──────────────────────────────────────────────┘ │
│                        ↓                            │
│  ┌──────────────────────────────────────────────┐ │
│  │              Servlet Container                │ │
│  │  - DispatcherServlet                          │ │
│  │  - Spring MVC Controllers                     │ │
│  └──────────────────────────────────────────────┘ │
│                                                     │
└────────────────────────────────────────────────────┘
```

---

### **4.2 Tomcat Metrics from Actuator**

```bash
# Thread Pool Metrics
tomcat.threads.busy         # Currently processing requests
tomcat.threads.current      # Current thread count
tomcat.threads.config.max   # Max thread pool size

# Connection Metrics
tomcat.sessions.active.current      # Active HTTP sessions
tomcat.sessions.active.max          # Max concurrent sessions
tomcat.sessions.created             # Total sessions created
tomcat.sessions.expired             # Expired sessions
tomcat.sessions.rejected            # Rejected sessions (pool full)

# Request Processing
tomcat.global.request.count         # Total requests processed
tomcat.global.request.max           # Max processing time
tomcat.global.error                 # Request errors

# Data Transfer
tomcat.global.sent                  # Bytes sent
tomcat.global.received              # Bytes received
```

---

### **4.3 HTTP Servlet Request Flow**

```
Client Request Flow:
─────────────────────

1. Client sends HTTP request
   ↓
2. Tomcat Connector receives on port 8080
   ↓
3. Check: Are threads available?
   │
   ├─ YES → Assign thread from pool
   │   ↓
   │   4. Thread processes request
   │   ↓
   │   5. DispatcherServlet routes to @Controller
   │   ↓
   │   6. Business logic executes
   │   ↓
   │   7. Response sent back
   │   ↓
   │   8. Thread returns to pool
   │
   └─ NO → Request queued (up to acceptCount=100)
       │
       ├─ Queue has space → Wait for thread
       │
       └─ Queue full → Connection REJECTED (HTTP 503)
```

---

### **4.4 Tomcat Performance Tuning**

```yaml
server:
  tomcat:
    threads:
      max: 200                    # Max worker threads
      min-spare: 10               # Min idle threads
    max-connections: 10000        # Max concurrent connections
    accept-count: 100             # Queue size when all busy
    connection-timeout: 20000     # Socket timeout (ms)
    
    # Advanced tuning
    max-http-header-size: 8192    # Max HTTP header size
    max-swallow-size: 2097152     # Max request body to discard
    
    accesslog:
      enabled: true
      pattern: '%h %l %u %t "%r" %s %b %D'  # %D = response time (ms)
```

**Calculation Example:**
```
If:
- max-connections = 10000
- threads.max = 200
- accept-count = 100

Then:
- Max 200 requests processing simultaneously
- Up to 10000 TCP connections can be open
- If all 200 threads busy, next 100 requests queue
- Request #301 gets HTTP 503 (Service Unavailable)
```

---

### **4.5 Servlet Metrics Interpretation**

| Scenario | Metrics | Action |
|----------|---------|--------|
| **Healthy** | `tomcat.threads.busy < 50%`<br>`tomcat.sessions.rejected = 0` | None |
| **High Load** | `tomcat.threads.busy > 80%`<br>`tomcat.global.request.max > 5000ms` | Scale horizontally<br>Optimize slow endpoints |
| **Thread Starvation** | `tomcat.threads.busy = max`<br>`tomcat.sessions.rejected > 0` | Increase `threads.max`<br>Check for blocking code |
| **Memory Leak** | `tomcat.sessions.active` growing<br>`jvm.memory.used` growing | Check session timeout<br>Heap dump analysis |

---

## **5. Database Connection Pool Metrics (HikariCP)**

### **5.1 HikariCP Architecture**

```
┌─────────────────────────────────────────────────────┐
│              HikariCP Connection Pool                │
├─────────────────────────────────────────────────────┤
│                                                      │
│  Pool Configuration:                                │
│  - minimumIdle: 10                                  │
│  - maximumPoolSize: 20                              │
│  - connectionTimeout: 30000ms                       │
│  - idleTimeout: 600000ms (10min)                    │
│                                                      │
│  ┌────────────────────────────────────────────────┐│
│  │         Active Connections (In Use)             ││
│  │  [C1: executing query]                          ││
│  │  [C2: transaction in progress]                  ││
│  │  [C3: fetching results]                         ││
│  │  ... (hikaricp.connections.active)              ││
│  └────────────────────────────────────────────────┘│
│                                                      │
│  ┌────────────────────────────────────────────────┐│
│  │         Idle Connections (Ready)                ││
│  │  [C4: waiting] [C5: waiting] [C6: waiting]      ││
│  │  ... (hikaricp.connections.idle)                ││
│  └────────────────────────────────────────────────┘│
│                                                      │
│  ┌────────────────────────────────────────────────┐│
│  │         Pending Requests (Queue)                ││
│  │  [Thread waiting for connection...]            ││
│  │  ... (hikaricp.connections.pending)             ││
│  └────────────────────────────────────────────────┘│
│                                                      │
│  Total = Active + Idle ≤ maximumPoolSize            │
│                                                      │
└─────────────────────────────────────────────────────┘
```

---

### **5.2 HikariCP Metrics from Actuator**

```bash
# Connection Pool Size
hikaricp.connections.active      # Connections currently in use
hikaricp.connections.idle        # Idle connections ready for use
hikaricp.connections.total       # Total connections (active + idle)
hikaricp.connections.max         # Maximum pool size
hikaricp.connections.min         # Minimum idle connections

# Wait/Timeout Metrics
hikaricp.connections.pending     # Threads waiting for connection
hikaricp.connections.timeout     # Connection acquisition timeouts
hikaricp.connections.acquire     # Time to acquire connection (histogram)

# Connection Lifecycle
hikaricp.connections.creation    # Time to create new connection
hikaricp.connections.usage       # Connection usage time
```

---

### **5.3 HikariCP Configuration**

```yaml
spring:
  datasource:
    hikari:
      # Pool Sizing
      minimum-idle: 10               # Min idle connections
      maximum-pool-size: 20          # Max total connections
      
      # Timeouts
      connection-timeout: 30000      # Max wait for connection (ms)
      idle-timeout: 600000           # Max idle time before eviction (10 min)
      max-lifetime: 1800000          # Max connection lifetime (30 min)
      
      # Performance
      auto-commit: true              # Auto-commit transactions
      connection-test-query: "SELECT 1"  # Validation query
      validation-timeout: 5000       # Validation timeout
      
      # Leak Detection
      leak-detection-threshold: 60000  # Warn if connection held > 60s
      
      # Pool Name
      pool-name: "MyAppHikariPool"
```

**Sizing Formula:**
```
Optimal Pool Size = (Number of CPU cores × 2) + Number of Disk Spindles

Example:
- 4 CPU cores
- 1 SSD (count as 1)
→ (4 × 2) + 1 = 9 connections

For cloud databases (AWS RDS, etc.):
- Start with 10-20 connections
- Monitor hikaricp.connections.active
- If active ≈ max frequently → increase pool size
```

---

### **5.4 Connection Pool Issues**

#### **Issue 1: Connection Pool Exhausted**
```
Symptoms:
- hikaricp.connections.active = maximumPoolSize
- hikaricp.connections.pending > 0
- hikaricp.connections.timeout increasing
- Logs: "Connection is not available, request timed out after 30000ms"

Causes:
1. Slow queries holding connections
2. Missing @Transactional boundaries
3. Connection leaks (not closed)

Fix:
- Increase pool size temporarily
- Optimize slow queries
- Enable leak detection:
  leak-detection-threshold: 30000
```

#### **Issue 2: Connection Leaks**
```java
// BAD: Connection leak
public List<User> getUsers() {
    Connection conn = dataSource.getConnection();
    // Query execution
    return users;  // Connection never closed!
}

// GOOD: Proper resource management
public List<User> getUsers() {
    try (Connection conn = dataSource.getConnection()) {
        // Query execution
        return users;  // Auto-closed
    }
}

// BEST: Use Spring JDBC Template or JPA
@Autowired
private JdbcTemplate jdbcTemplate;

public List<User> getUsers() {
    return jdbcTemplate.query("SELECT * FROM users", 
        new UserRowMapper());  // Connection managed automatically
}
```

---

### **5.5 Database Query Performance**

```bash
# Spring Data JPA Metrics (if enabled)
spring.data.repository.invocations  # Repository method calls

# Custom Metrics (add via AOP)
db.query.time{query="findUserById"}
db.query.count{query="findUserById"}
```

**Enable JPA Metrics:**
```yaml
spring:
  jpa:
    show-sql: true               # Log SQL queries
    properties:
      hibernate:
        generate_statistics: true  # Enable Hibernate stats
        format_sql: true          # Pretty-print SQL
```

---

## **6. HTTP Request Metrics**

### **6.1 HTTP Metrics from Actuator**

```bash
# Request Count & Timing
http.server.requests                # Total requests
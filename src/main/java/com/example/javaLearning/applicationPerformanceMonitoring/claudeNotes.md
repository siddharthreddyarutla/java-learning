## **How Prometheus & Grafana Monitor Applications:**

### **Architecture Flow:**
```
Java App (Spring Boot Actuator + Micrometer)
    ↓ (exposes /actuator/prometheus endpoint)
Prometheus (scrapes metrics every 15s)
    ↓ (stores time-series data)
Grafana (queries Prometheus & visualizes)
```

---

## **1. Setup Spring Boot Actuator + Micrometer**

### **Add Dependencies:**
```xml
<dependencies>
    <!-- Spring Boot Actuator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    
    <!-- Micrometer Prometheus Registry -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-registry-prometheus</artifactId>
    </dependency>
    
    <!-- Optional: For JVM metrics -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-core</artifactId>
    </dependency>
</dependencies>
```

---

## **2. Configure application.properties / application.yml**

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: "*"  # Expose all endpoints (or specify: health,info,metrics,prometheus)
      base-path: /actuator
  
  endpoint:
    health:
      show-details: always
    metrics:
      enabled: true
    prometheus:
      enabled: true
  
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: production
    
server:
  port: 8080
```

**OR in application.properties:**
```properties
# Expose actuator endpoints
management.endpoints.web.exposure.include=*
management.endpoints.web.base-path=/actuator
management.endpoint.health.show-details=always
management.endpoint.metrics.enabled=true
management.endpoint.prometheus.enabled=true

# Enable Prometheus metrics
management.metrics.export.prometheus.enabled=true
management.metrics.tags.application=${spring.application.name}

server.port=8080
```

---

## **3. Access Metrics Endpoints**

### **Available Endpoints:**

| Endpoint | Description | Example |
|----------|-------------|---------|
| `/actuator` | Lists all available endpoints | http://localhost:8080/actuator |
| `/actuator/health` | Application health status | http://localhost:8080/actuator/health |
| `/actuator/metrics` | List of available metrics | http://localhost:8080/actuator/metrics |
| `/actuator/metrics/{metric}` | Specific metric details | http://localhost:8080/actuator/metrics/jvm.memory.used |
| `/actuator/prometheus` | **Prometheus format metrics** | http://localhost:8080/actuator/prometheus |
| `/actuator/threaddump` | Thread dump | http://localhost:8080/actuator/threaddump |
| `/actuator/heapdump` | Download heap dump | http://localhost:8080/actuator/heapdump |
| `/actuator/env` | Environment properties | http://localhost:8080/actuator/env |

---

## **4. Key Metrics Available**

### **JVM Metrics:**
```bash
# Memory
curl http://localhost:8080/actuator/metrics/jvm.memory.used
curl http://localhost:8080/actuator/metrics/jvm.memory.max
curl http://localhost:8080/actuator/metrics/jvm.memory.committed

# Heap vs Non-Heap
curl http://localhost:8080/actuator/metrics/jvm.memory.used?tag=area:heap
curl http://localhost:8080/actuator/metrics/jvm.memory.used?tag=area:nonheap

# Garbage Collection
curl http://localhost:8080/actuator/metrics/jvm.gc.pause
curl http://localhost:8080/actuator/metrics/jvm.gc.memory.allocated

# Threads
curl http://localhost:8080/actuator/metrics/jvm.threads.live
curl http://localhost:8080/actuator/metrics/jvm.threads.daemon
curl http://localhost:8080/actuator/metrics/jvm.threads.peak

# CPU
curl http://localhost:8080/actuator/metrics/system.cpu.usage
curl http://localhost:8080/actuator/metrics/process.cpu.usage

# Classes
curl http://localhost:8080/actuator/metrics/jvm.classes.loaded
```

### **System Metrics:**
```bash
# Disk Space
curl http://localhost:8080/actuator/metrics/disk.free
curl http://localhost:8080/actuator/metrics/disk.total

# Uptime
curl http://localhost:8080/actuator/metrics/process.uptime
```

### **HTTP Metrics:**
```bash
# HTTP Server Requests
curl http://localhost:8080/actuator/metrics/http.server.requests

# With filters
curl "http://localhost:8080/actuator/metrics/http.server.requests?tag=uri:/api/users&tag=status:200"
```

### **Database Metrics (HikariCP):**
```bash
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
curl http://localhost:8080/actuator/metrics/hikaricp.connections.idle
curl http://localhost:8080/actuator/metrics/hikaricp.connections.max
curl http://localhost:8080/actuator/metrics/hikaricp.connections.pending
```

---

## **5. Prometheus Format (What Prometheus Scrapes)**

```bash
curl http://localhost:8080/actuator/prometheus
```

**Output Example:**
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="PS Eden Space",} 2.5165824E7
jvm_memory_used_bytes{area="heap",id="PS Survivor Space",} 0.0
jvm_memory_used_bytes{area="heap",id="PS Old Gen",} 1.8874368E7

# HELP system_cpu_usage The "recent cpu usage" for the whole system
# TYPE system_cpu_usage gauge
system_cpu_usage 0.15

# HELP jvm_threads_live The current number of live threads
# TYPE jvm_threads_live gauge
jvm_threads_live 23.0

# HELP hikaricp_connections_active Active connections
# TYPE hikaricp_connections_active gauge
hikaricp_connections_active{pool="HikariPool-1",} 5.0
```

---

## **6. Configure Prometheus to Scrape Your App**

### **prometheus.yml:**
```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
        labels:
          application: 'shift-schedule-service'
          environment: 'production'
```

---

## **7. Custom Metrics (Optional)**

### **Add Custom Metrics to Your Code:**
```java
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

@Service
public class BreakConfigService {

    private final Counter breakConfigCreatedCounter;
    private final Timer breakConfigSaveTimer;

    public BreakConfigService(MeterRegistry registry) {
        this.breakConfigCreatedCounter = Counter.builder("break.config.created")
            .description("Number of break configs created")
            .tag("service", "break-management")
            .register(registry);
        
        this.breakConfigSaveTimer = Timer.builder("break.config.save.time")
            .description("Time taken to save break config")
            .register(registry);
    }

    public void createBreakConfig(BreakConfig config) {
        Timer.Sample sample = Timer.start();
        
        try {
            // Your save logic
            repository.save(config);
            breakConfigCreatedCounter.increment();
        } finally {
            sample.stop(breakConfigSaveTimer);
        }
    }
}
```

---

## **8. Quick Testing Commands**

### **Check if Actuator is Running:**
```bash
curl http://localhost:8080/actuator
```

### **Get JVM Memory:**
```bash
curl http://localhost:8080/actuator/metrics/jvm.memory.used | jq
```

### **Get Thread Count:**
```bash
curl http://localhost:8080/actuator/metrics/jvm.threads.live | jq
```

### **Get CPU Usage:**
```bash
curl http://localhost:8080/actuator/metrics/system.cpu.usage | jq
```

### **Get All Metrics in Prometheus Format:**
```bash
curl http://localhost:8080/actuator/prometheus
```

---

## **9. Grafana Dashboard Setup**

Once Prometheus is scraping your app:

1. **Add Prometheus as Data Source in Grafana**
2. **Import JVM Dashboard**: Dashboard ID `4701` or `11378`
3. **Query Examples:**

```promql
# CPU Usage
100 - (avg by (instance) (irate(process_cpu_seconds_total[5m])) * 100)

# Memory Usage
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# Thread Count
jvm_threads_live

# HTTP Request Rate
rate(http_server_requests_seconds_count[1m])

# Database Connections
hikaricp_connections_active
```

---

## **10. Security Considerations**

### **Restrict Actuator Endpoints in Production:**
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

### **Add Security (Spring Security):**
```java
@Configuration
public class ActuatorSecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .requestMatcher(EndpointRequest.toAnyEndpoint())
            .authorizeRequests()
                .requestMatchers(EndpointRequest.to("health", "info")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasRole("ADMIN")
            .and()
            .httpBasic();
    }
}
```
# Jackson

# 📘 Jackson – Deep Notes for Backend Engineers

---

## 1️⃣ What Jackson Really Is (Beyond “JSON Library”)

Jackson is **not one tool**, it’s a **JSON processing framework** built in **layers**.

At its core:

* Jackson is a **token-based JSON engine**
* Everything else (POJOs, annotations, ObjectMapper) is built **on top of streaming**

### Key design goals:

✔ High performance
✔ Low memory usage
✔ Flexible mapping
✔ Pluggable architecture

---

## 2️⃣ Jackson Architecture (Very Important)

```
JSON
 ↓
Streaming API (core)
 ↓
Data Binding (ObjectMapper)
 ↓
Annotations & Modules
```

You must understand this stack to use Jackson correctly.

---

## 3️⃣ Core Modules (Foundational Knowledge)

### 3.1 jackson-core (Streaming API)

This is the **lowest-level API**.

#### Main classes:

* `JsonFactory`
* `JsonParser`
* `JsonGenerator`
* `JsonToken`

### How it works:

Jackson reads JSON as a **stream of tokens**:

```json
{
  "name": "John",
  "age": 30
}
```

Tokens:

```
START_OBJECT
FIELD_NAME (name)
VALUE_STRING (John)
FIELD_NAME (age)
VALUE_NUMBER (30)
END_OBJECT
```

### Example (low-level parsing)

```java
JsonParser parser = new JsonFactory().createParser(json);
while (parser.nextToken() != null) {
    System.out.println(parser.currentToken());
}
```

✔ Fast
✔ Minimal memory
❌ Verbose
❌ Manual handling

👉 Used when:

* Large files
* Streaming data
* Kafka / log processing
* Performance-critical systems

---

## 4️⃣ jackson-databind (Most Used Module)

This is what **99% of applications use**.

### Core class: `ObjectMapper`

It:

* Uses Streaming API internally
* Uses Reflection
* Uses Annotations

---

### 4.1 Serialization (Java → JSON)

```java
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(obj);
```

Supported outputs:

* `String`
* `byte[]`
* `File`
* `OutputStream`

---

### 4.2 Deserialization (JSON → Java)

```java
User user = mapper.readValue(json, User.class);
```

Jackson:

1. Reads JSON tokens
2. Matches fields
3. Calls constructor / setters
4. Applies annotations

---

## 5️⃣ Tree Model (Intermediate Level)

Jackson can represent JSON as a **tree**, similar to DOM.

### Core classes:

* `JsonNode`
* `ObjectNode`
* `ArrayNode`

### Example:

```java
JsonNode root = mapper.readTree(json);
String name = root.get("name").asText();
```

### When to use:

✔ Unknown JSON structure
✔ Partial updates
✔ Dynamic JSON
✔ Schema-less APIs

---

## 6️⃣ jackson-annotations (Customization Layer)

Annotations control **how mapping works**.

### Common annotations (must know)

| Annotation              | Purpose               |
| ----------------------- | --------------------- |
| `@JsonProperty`         | Rename field          |
| `@JsonIgnore`           | Ignore field          |
| `@JsonInclude`          | Include/exclude nulls |
| `@JsonFormat`           | Date/time format      |
| `@JsonCreator`          | Custom constructor    |
| `@JsonValue`            | Custom enum output    |
| `@JsonIgnoreProperties` | Ignore unknown fields |

---

### Example

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public record BreakConfigRecord(
    @JsonProperty("break_type")
    String breakType,
    LocalTime startTime
) {}
```

---

## 7️⃣ Jackson + Records (Modern Java)

Jackson treats records as:

* Immutable POJOs
* Constructor-based mapping

### How deserialization works:

1. Match JSON keys to record components
2. Call canonical constructor

### Validation example:

```java
public record TimeRange(LocalTime start, LocalTime end) {
    public TimeRange {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid range");
        }
    }
}
```

---

## 8️⃣ Jackson + Spring Boot (Very Important)

Spring Boot:

* Auto-configures `ObjectMapper`
* Registers common modules automatically

### Default Spring modules:

* `JavaTimeModule`
* `ParameterNamesModule`
* `Jdk8Module`

You usually **do NOT need**:

```java
new ObjectMapper()
```

Instead inject:

```java
@Autowired
ObjectMapper objectMapper;
```

---

## 9️⃣ Customizing ObjectMapper (Production Grade)

### Global configuration

```java
@Bean
ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return mapper;
}
```

---

## 🔟 Jackson Modules (Advanced)

Jackson is **extensible via modules**.

### Common modules:

| Module              | Purpose           |
| ------------------- | ----------------- |
| `JavaTimeModule`    | Java 8 date/time  |
| `Jdk8Module`        | Optional, Streams |
| `AfterburnerModule` | Performance       |
| `KotlinModule`      | Kotlin            |
| `AvroModule`        | Avro              |
| `XmlMapper`         | XML               |
| `YAMLMapper`        | YAML              |

---

## 1️⃣1️⃣ Custom Serialization / Deserialization

### Custom serializer

```java
public class TimeSerializer extends JsonSerializer<LocalTime> {
    @Override
    public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeString(value.toString());
    }
}
```

### Registering

```java
SimpleModule module = new SimpleModule();
module.addSerializer(LocalTime.class, new TimeSerializer());
mapper.registerModule(module);
```

---

## 1️⃣2️⃣ Jackson & Enums (Common Interview Topic)

### Default:

```java
enum Status { ACTIVE, INACTIVE }
```

JSON:

```json
"ACTIVE"
```

### Custom value:

```java
enum Status {
    ACTIVE("A"),
    INACTIVE("I");

    @JsonValue
    private final String code;
}
```

---

## 1️⃣3️⃣ Performance Considerations (Very Important)

✔ Reuse `ObjectMapper`
✔ Avoid tree model for large JSON
✔ Use Streaming API for big files
✔ Avoid deep object graphs
✔ Prefer records / immutables

---

## 1️⃣4️⃣ Common Pitfalls (Seen in Production)

❌ Creating ObjectMapper per request
❌ Ignoring unknown fields without intention
❌ Using entities directly in APIs
❌ Lazy-loading JPA entities during serialization
❌ Not handling timezones

---

## 1️⃣5️⃣ Jackson vs Gson vs Moshi (Quick)

| Feature       | Jackson   | Gson    |
| ------------- | --------- | ------- |
| Speed         | ⭐⭐⭐⭐      | ⭐⭐      |
| Streaming     | Yes       | Limited |
| Records       | Yes       | Partial |
| Spring Boot   | Default   | ❌       |
| Extensibility | Very High | Low     |

---

## 1️⃣6️⃣ Real-World Usage Pattern (Recommended)

```text
Controller
 ↓
Record DTO (Jackson)
 ↓
Service
 ↓
Entity (JPA)
```

Never:

```text
Entity → Controller → JSON
```

---

## 1️⃣7️⃣ Interview One-Liner (Very Strong)

> “Jackson is a layered JSON processing framework built on a high-performance streaming API, with data binding and annotations providing flexible, annotation-driven object mapping. Spring Boot uses it as the default serializer.”

---

## 1️⃣8️⃣ TL;DR Cheat Sheet

✔ Streaming API = core
✔ ObjectMapper = façade
✔ Records = best DTOs
✔ Annotations = control mapping
✔ Modules = extensibility
✔ Spring Boot auto-configures everything

---
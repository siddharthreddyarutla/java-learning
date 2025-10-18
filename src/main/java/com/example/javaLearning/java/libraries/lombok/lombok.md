# Lombok
 
> Purpose: To drastically reduce boilerplate code like getters, setters, constructors, toString, equals, and hashCode methods.


**Why it’s essential:**
Java is notorious for verbose classes. Lombok uses annotations to generate this code at compile time, making your code cleaner, more readable, and easier to maintain.


### Key Annotations:

1. **@Data:** Generates getters, setters, toString, equals, and hashCode for all fields.
2. **@Getter / @Setter:** Generates getters/setters.
3. @NoArgsConstructor, @AllArgsConstructor, @RequiredArgsConstructor: Generates constructors.
4. **@Builder:** Implements the Builder pattern for object creation.
5. **@Slf4j:** Injects a logger named log.


### Before Lombok:

```java
public class User {
    private Long id;
    private String name;
    private String email;

    // Getters and Setters for all fields... (30+ lines of code)
    // toString... 
    // equals and hashCode...
}
```


### After lombok

```java
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    // That's it! All methods are auto-generated.
}
```
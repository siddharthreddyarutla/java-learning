# MapStruct

> Purpose: To generate type-safe and fast mapping code between Java beans (e.g., between a DTO and an Entity).

Yes, using MapStruct's `@Mapper` annotation is **generally preferred** and considered a good practice for DTO-Entity mapping. Here's why:

## Advantages of MapStruct

**Performance**
- Generates code at compile-time (no reflection)
- As fast as hand-written mapping code
- Much faster than reflection-based libraries (ModelMapper, Dozer)

**Type Safety**
- Compile-time error checking
- IDE support with autocompletion
- Catches mapping errors early

**Maintainability**
- Clear, readable mapping definitions
- Easy to customize mappings
- Generated code is debuggable

**Less Boilerplate**
- Automatic mapping for same-named fields
- Reduces manual setter/getter code

## Basic Example

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User entity);
    User toEntity(UserDTO dto);
    List<UserDTO> toDTOList(List<User> entities);
}
```

## When to Use It

✅ **Use MapStruct when:**
- Working with Spring Boot applications
- Need high performance mapping
- Have complex mapping requirements
- Want compile-time safety

⚠️ **Consider alternatives when:**
- Very simple 1-2 field mappings (manual might be simpler)
- Mapping logic is extremely dynamic
- Project already uses another mapper consistently

## Best Practices

**Separate concerns** - Don't expose entities directly through REST APIs, always use DTOs

**Use component model** - Set `componentModel = "spring"` for Spring dependency injection

**Custom mappings** - Use `@Mapping` for field name differences or transformations

**Avoid bidirectional references** - Handle circular dependencies carefully with `@Mapping(target = "field", ignore = true)`

MapStruct is widely adopted in enterprise Java applications and is definitely interview-worthy knowledge!
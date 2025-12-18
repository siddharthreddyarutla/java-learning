# 📘 **Hibernate Notes (Simple + Interview Ready)**

![Image](https://www.tutorialspoint.com/hibernate/images/hibernate_architecture.jpg?utm_source=chatgpt.com)

![Image](https://media.geeksforgeeks.org/wp-content/uploads/HBArchi.png?utm_source=chatgpt.com)

![Image](https://www.researchgate.net/publication/313263324/figure/fig3/AS%3A457696605413378%401486134782128/EntityManager-Based-Hibernate-JPA-Architecture.png?utm_source=chatgpt.com)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1342/0%2AegdvZ73hgY3eME5D?utm_source=chatgpt.com)

![Image](https://miro.medium.com/v2/resize%3Afit%3A1400/1%2AeV3wCAgtwFkyQFS58lf01w.png?utm_source=chatgpt.com)

![Image](https://www.baeldung.com/wp-content/uploads/2019/11/transition-persistence-context.png?utm_source=chatgpt.com)

---

# ⭐ 1. **What Is Hibernate?**

Hibernate is a **Java ORM (Object Relational Mapping) framework** that maps **Java objects → Database tables**.

It removes the need to write most SQL manually by generating queries based on your entity mappings.

Hibernate implements **JPA (Java Persistence API)**, which is a standard specification.

---

# ⭐ 2. **Why Do Developers Use Hibernate?**

### ✔ Avoid writing SQL for CRUD

### ✔ Manages database connections

### ✔ Handles transactions

### ✔ Lazy loading of relationships

### ✔ First-level and second-level caching

### ✔ Converts Java objects ←→ DB tables automatically

### ✔ Database-independent (works with many DBs)

---

# ⭐ 3. **How Hibernate Works (Internal Flow)**

This follows the *behind-the-scenes* steps from your code to the database.

## 3.1 Application Start

* Hibernate loads configuration (`hibernate.cfg.xml` or Spring Boot properties).
* Builds:

    * `SessionFactory` (heavy, created once)
    * Connection pool
    * Metadata mappings for all `@Entity` classes

---

## 3.2 Entity Mapping

Hibernate scans all classes annotated with:

```java
@Entity
@Table(name="USER")
```

It maps:

* class → table
* fields → columns
* relationships (OneToMany, ManyToOne, etc.)
* id generation (AUTO, IDENTITY, SEQUENCE)

---

## 3.3 SessionFactory → Session → Persistence Context

### **SessionFactory**

* Created once per application
* Stores metadata, caching, SQL generation rules

### **Session / EntityManager**

* Created per request/transaction
* Manages persistence context

### **Persistence Context (1st Level Cache)**

* Stores entities loaded during the session
* Guarantees identity: same entity instance within transaction

---

## 3.4 Hibernate Query Execution

### Two types of queries:

1. **HQL/JPQL**
2. **Native SQL**

Hibernate translates JPQL → SQL → sends to DB.

---

## 3.5 Lazy Loading (Proxies)

If you write:

```java
@OneToMany(fetch = LAZY)
List<Order> orders;
```

Hibernate:

* Loads User
* Creates proxy for orders
* Executes SQL only when `user.getOrders()` is accessed.

---

## 3.6 Dirty Checking

Hibernate tracks all entity changes.

Example:

```java
User u = session.find(User.class, 1);
u.setName("John");
```

Hibernate automatically:

* Compares snapshots
* Generates `UPDATE` at `flush()`

You do **not** need explicit update calls.

---

## 3.7 Flush

Flush sends SQL to DB, but **does NOT commit**.

Hibernate flushes:

* before commit
* before executing a JPQL query
* when manually calling `session.flush()`

---

## 3.8 Transaction & Commit

Spring handles transactions with `@Transactional`:

1. Begin transaction
2. Hibernate operations
3. Flush (SQL executed)
4. Commit
5. Close session

After commit, objects become **detached**.

---

# ⭐ 4. Common Hibernate Components

| Component               | Purpose                     |
| ----------------------- | --------------------------- |
| **EntityManager**       | API for CRUD operations     |
| **Session**             | Hibernate’s core interface  |
| **SessionFactory**      | Heavy object with metadata  |
| **Persistence Context** | 1st level cache             |
| **TransactionManager**  | Handles commit & rollback   |
| **Proxies**             | Lazy loading                |
| **Dialect**             | SQL generation rules for DB |

---

# ⭐ 5. What Databases Does Hibernate Support?

Hibernate works with **almost every SQL database**.

### ✔ Fully Supported

| Database       | Dialect Example                  |
| -------------- | -------------------------------- |
| **MySQL**      | `MySQL5Dialect`, `MySQL8Dialect` |
| **PostgreSQL** | `PostgreSQLDialect`              |
| **Oracle**     | `Oracle12cDialect`               |
| **SQL Server** | `SQLServerDialect`               |
| **MariaDB**    | `MariaDBDialect`                 |
| **DB2**        | `DB2Dialect`                     |
| **H2**         | `H2Dialect`                      |
| **SQLite**     | Community dialects               |
| **Sybase**     | Supported                        |
| **Derby**      | `DerbyDialect`                   |

Hibernate is **not limited to MySQL** — it works with almost all relational databases.

### ❌ Not Supported

* NoSQL DBs like MongoDB, Cassandra (use Spring Data Mongo instead)

---

# ⭐ 6. Essential Hibernate Annotations

## Entity & Table

```java
@Entity
@Table(name="users")
```

## Primary Key

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```

## Relations

```java
@OneToMany(mappedBy="user", fetch = LAZY)
private List<Order> orders;

@ManyToOne
@JoinColumn(name="user_id")
private User user;
```

---

# ⭐ 7. Hibernate Lifecycle Summary

![Image](https://media.geeksforgeeks.org/wp-content/uploads/20210626212614/GFGHibernateLifecycle.png?utm_source=chatgpt.com)

![Image](https://www.mysoftkey.com/wp-content/uploads/2017/07/hibernate-object-lifecycle.png?utm_source=chatgpt.com)

![Image](https://thorben-janssen.com/wp-content/uploads/2020/07/Lifecycle-Model-1024x576.png?utm_source=chatgpt.com)

![Image](https://miro.medium.com/1%2AohA1KFKz9lSKMFvCZ2TBtg.png?utm_source=chatgpt.com)

1. **Transient** – new object, not associated with session
2. **Persistent** – after `save()` or `find()`
3. **Dirty Checking** – Hibernate monitors changes
4. **Flush** – SQL sent
5. **Commit** – DB commit
6. **Detached** – session closed

---

# ⭐ 8. Advantages & Disadvantages

## ✔ Advantages

* Less SQL
* Relationship handling
* Auto dirty checking
* Caching improves performance
* DB-independent due to dialects

## ✘ Disadvantages

* Harder debugging when performance issues arise
* Lazy loading pitfalls
* Complex joins may require native SQL

---

# ⭐ 9. When to Use Hibernate

Use Hibernate when:

* You have many entity relationships
* CRUD operations are dominant
* You want DB independence
* You want caching & lazy loading

Avoid Hibernate when:

* You need extremely complex SQL tuning
* You need NoSQL

---

# ⭐ 10. Short Summary (Exam / Interview Ready)

* Hibernate is a **JPA-based ORM** mapping Java objects to DB tables.
* Uses **Session**, **SessionFactory**, **Persistence Context**, **Dirty Checking**.
* SQL generated by Hibernate using **dialects** for DB portability.
* Provides **lazy loading**, **caching**, **transaction management**, **entity lifecycle**.
* Works with **almost all relational databases**.

---

If you want, I can also provide:

✅ A one-page cheatsheet
✅ Hibernate interview Q&A
✅ Real-world examples (lazy loading, caching, dirty checking)
✅ Diagrams for revision

Would you like those?

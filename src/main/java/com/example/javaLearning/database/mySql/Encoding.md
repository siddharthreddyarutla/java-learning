# ✅ **utf8mb4 vs utf8mb3 (MySQL)**

## 🔹 **1. Meaning**

### **utf8mb3**

* **“UTF-8 MySQL 3-byte”**
* Supports **maximum 3 bytes per character**.
* Can store most common languages: English, Hindi, Chinese, Japanese, etc.
* ❌ Cannot store **4-byte characters** like:

    * Emojis (😊🔥❤️)
    * Some rare symbols
    * Certain CJK characters

### **utf8mb4**

* **“UTF-8 MySQL 4-byte”**
* Supports **full UTF-8**, up to 4 bytes per character.
* ✔ Can store ALL Unicode characters, including emoji.

**In short:**
`utf8mb3` = partial UTF-8
`utf8mb4` = full UTF-8

---

## 🔹 **2. Why MySQL Created utf8mb3 Initially**

MySQL’s original “utf8” implementation was incomplete (3 bytes only).
Real UTF-8 needs **up to 4 bytes per character**.

So MySQL later introduced `utf8mb4` as the **correct full UTF-8 charset**.

---

## 🔹 **3. Current Status**

* `utf8mb3` (usually shown as `utf8`) is **deprecated**.
* MySQL recommends **utf8mb4** for all new applications.

Example MySQL warning:

> utf8 is an alias for utf8mb3 and will be removed in a future MySQL version.

---

# 🔥 **4. When/Where We Use Them**

### ✔ **Use utf8mb4 for practically everything**:

* Web apps (Java/Spring Boot, Angular, Python apps)
* APIs receiving emoji data
* User-generated content (names, comments, messages)
* Any modern DB schema

**Especially important for:**

* Storing user names with emojis (e.g., "John 😊")
* Storing WhatsApp/Slack-type messages
* Social media fields
* Logs or metadata with symbols

### ✔ **Use utf8mb3 only when:**

* Supporting **legacy MySQL systems**
* Database size is extremely sensitive (rare case)
* You are forced to for backwards compatibility

---

# 🔹 **5. Example: What happens if you use utf8mb3 and save emoji?**

```sql
INSERT INTO users (name) VALUES ('Alex 😊');
```

If your column/table/database is `utf8mb3`, you’ll get:

```
Incorrect string value: '\xF0\x9F\x98\x8A'
```

Switching to utf8mb4 solves it.

---

# 🔹 **6. How to convert utf8mb3 to utf8mb4**

### Database level:

```sql
ALTER DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Table level:

```sql
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Column level:

```sql
ALTER TABLE users MODIFY name VARCHAR(255)
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

---

# 📝 Summary Table

| Feature            | utf8mb3 | utf8mb4 |
| ------------------ | ------- | ------- |
| Max bytes per char | 3       | 4       |
| True UTF-8 support | ❌ No    | ✔ Yes   |
| Emoji support      | ❌ No    | ✔ Yes   |
| Deprecated         | ✔ Yes   | ❌ No    |
| Recommended        | ❌ No    | ✔ Yes   |

---

# 🚀 **Final Recommendation (for your Java + MySQL projects):**

Use:

```
character_set_server = utf8mb4
collation_server = utf8mb4_unicode_ci
```

Because:

* Spring Boot handles emojis fine with utf8mb4
* Angular can send emoji characters in JSON
* Modern MySQL versions default to utf8mb4

---
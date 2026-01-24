# Module 1 — Single Responsibility Principle (SRP)

## Definition (short)

**A class should have only one reason to change.** One class = one responsibility / job.

## Why SRP matters

* Reduces coupling of unrelated behavior.
* Keeps classes small & focused.
* Easier to test and reuse.
* Minimizes risk when changing behavior (if saving logic changes, only the saver changes).

## Real-world analogy

A **restaurant**:

* Chef cooks (one responsibility)
* Waiter serves (another responsibility)
* Cashier handles payments (another responsibility)

If the chef also handled payments, changing payment logic forces the chef’s code to change — bad separation.

---

## Example — BAD (violates SRP)

```java
// Single class doing many things
public class ReportManager {
    public String generateReport() {
        // create report contents
        return "REPORT CONTENT";
    }

    public void saveToFile(String report, String fileName) {
        // file I/O logic
    }

    public void sendEmail(String report, String email) {
        // email sending logic
    }

    public void audit(String action) {
        // write audit log somewhere
    }
}
```

Problems:

* `ReportManager` handles report creation, persistence, email, and auditing → many reasons to change.

---

## Example — GOOD (SRP applied)

```java
// Report content generator
public class ReportGenerator {
    public String generateReport() {
        // single responsibility: prepare content
        return "REPORT CONTENT";
    }
}

// Persistence responsibility
public interface ReportRepository {
    void save(String content, String fileName);
}

public class FileReportRepository implements ReportRepository {
    @Override
    public void save(String content, String fileName) {
        // file I/O
    }
}

// Email responsibility
public interface EmailService {
    void send(String to, String subject, String body);
}

public class SmtpEmailService implements EmailService {
    @Override
    public void send(String to, String subject, String body) {
        // smtp send
    }
}

// Auditing responsibility
public class AuditService {
    public void audit(String action) {
        // audit logic
    }
}

// Orchestrator that composes responsibilities (still small)
public class ReportService {
    private final ReportGenerator generator;
    private final ReportRepository repository;
    private final EmailService emailService;
    private final AuditService auditService;

    public ReportService(ReportGenerator generator,
                         ReportRepository repository,
                         EmailService emailService,
                         AuditService auditService) {
        this.generator = generator;
        this.repository = repository;
        this.emailService = emailService;
        this.auditService = auditService;
    }

    public void createAndDistributeReport(String fileName, String recipient) {
        String report = generator.generateReport();
        repository.save(report, fileName);
        emailService.send(recipient, "Report", report);
        auditService.audit("Report created: " + fileName);
    }
}
```

Why this is better:

* Each class has a **single reason to change**.
* You can replace the `ReportRepository` (e.g., DB) without touching `ReportGenerator`.
* Easier to unit test `ReportGenerator` in isolation.

---

## Unit-testing note (how SRP helps testing)

* To test report generation, mock `ReportRepository`, `EmailService`, `AuditService`, or test `ReportGenerator` alone.
* Smaller classes mean fewer dependencies to mock and fewer behaviors per test.

---

## Refactor steps you can practice (mini exercise)

Given a class that mixes business logic and I/O (for example, a `UserManager` that both updates user data and writes logs to a file), do:

1. Extract business logic to `UserService`.
2. Extract persistence to `UserRepository`.
3. Extract logging to `AuditService` or use a logging framework adapter.
4. Wire them together via constructor injection.

Try this on a small class in your project — it should reduce the number of tests and simplify each test.

---

## SRP — Code smells & when SRP is violated

* A class with > \~200 lines and multiple unrelated private methods.
* Methods in the same class operate on different abstractions (HTTP + DB + formatting + calculation).
* Frequent commits that touch the same class for different reasons (UI change + DB change).
* Tests that are hard to write because of many setup dependencies.

---

## Interview questions (SRP)

1. What is the Single Responsibility Principle? Give a Java example where SRP is violated and how you’d fix it.
2. How does SRP improve testability?
3. Can a class have more than one method and still follow SRP? Explain.
4. Give a scenario where splitting a class might be a bad idea (over-splitting). How do you decide?

---

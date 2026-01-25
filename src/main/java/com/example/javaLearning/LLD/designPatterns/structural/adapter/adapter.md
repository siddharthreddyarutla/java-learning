# Module 7 — Adapter Pattern

---

## 1) Definition & Intent

The **Adapter Pattern** allows objects with **incompatible interfaces** to work together by providing a **wrapper** that translates one interface into another.

👉 Think of it as a **bridge** between a client and an external system / legacy code.

---

## 2) Problem it solves

* When you have an existing class (legacy or third-party) that doesn’t match the interface your code expects.
* Instead of modifying the old class (which might not even be possible), you **wrap** it with an Adapter that makes it “look like” the expected interface.

---

## 3) Example without Adapter (problem)

```java
// Target interface (what client expects)
interface MediaPlayer {
    void play(String fileName);
}

// Adaptee (incompatible interface - legacy class)
class AdvancedMediaPlayer {
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }
}
```

Client wants to call `play("song.mp4")` but the legacy player only supports `playMp4()`.

❌ Direct use won’t work because interfaces don’t match.

---

## 4) ✅ Adapter Pattern applied

```java
// Target interface
interface MediaPlayer {
    void play(String fileName);
}

// Adaptee
class AdvancedMediaPlayer {
    public void playMp4(String fileName) {
        System.out.println("Playing MP4 file: " + fileName);
    }
}

// Adapter makes Adaptee compatible with Target
class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;

    public MediaAdapter(AdvancedMediaPlayer advancedPlayer) {
        this.advancedPlayer = advancedPlayer;
    }

    @Override
    public void play(String fileName) {
        advancedPlayer.playMp4(fileName); // adapts call
    }
}

// Client code
public class Demo {
    public static void main(String[] args) {
        MediaPlayer player = new MediaAdapter(new AdvancedMediaPlayer());
        player.play("song.mp4"); // works seamlessly
    }
}
```

✅ Client can now call `play()` without caring about `playMp4()`.

---

## 5) Real-world analogy

Think of a **travel power adapter**:

* Your laptop plug (client) expects a certain socket type.
* The hotel’s wall socket (adaptee) is different.
* The **adapter** converts between the two so they work together.

---

## 6) When to use

* Integrating with **legacy code** that you can’t modify.
* Using **third-party libraries** with a different interface.
* Migrating between APIs where new code expects a different contract.

---

## 7) Types of Adapters

* **Object Adapter** (uses composition — like above).
* **Class Adapter** (uses multiple inheritance — not directly possible in Java, but can be simulated with interfaces + extends).

---

## 8) Interview questions

1. What is the Adapter Pattern and why do we need it?
2. Difference between **Adapter** and **Facade** pattern?
3. Can you give a real-world example where you’ve used Adapter in a project?
4. What’s the difference between **object adapter** and **class adapter**?
5. How does Adapter follow the principle of **composition over inheritance**?

---

## 9) Exercises

1. Suppose you have a `PaymentGateway` interface with `pay(amount)`. You have a legacy `OldPaymentProcessor` with `makePayment(amount)`. Write an Adapter.
2. Create a `Shape` interface (`draw()`) and an old `LegacyRectangle` class (`drawRectangle(x, y, width, height)`). Write an Adapter so `LegacyRectangle` can be used as `Shape`.
3. In your earlier **Report example**, imagine a legacy `CSVExporter` that has a different method (`exportToCsvFile(reportData)`), adapt it to your standard `ReportExporter` interface.


## 10) Pros and cons

### 1. Pros:

- Single Responsibility Principle. You can separate the interface or data conversion code from the primary business logic of the program.
- Open/Closed Principle. You can introduce new types of adapters into the program without breaking the existing client code, as long as they work with the adapters through the client interface

### 2. Cons:

- The overall complexity of the code increases because you need to introduce a set of new interfaces and classes. Sometimes it’s simpler just to change the service class so that it matches the rest of your code.
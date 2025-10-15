### 🧠 **Strategy Pattern — Short & Simple Explanation**

**Definition:**
Strategy pattern lets you **change an algorithm’s behavior at runtime** by encapsulating each algorithm (strategy) in a separate class and making them interchangeable.

**Purpose:**
➡️ Define a family of algorithms,
➡️ Encapsulate each one,
➡️ Let the client choose which one to use dynamically.

**Example (Java):**

```java
interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) { System.out.println("Paid " + amount + " using Credit Card"); }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(int amount) { System.out.println("Paid " + amount + " using PayPal"); }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    void setPaymentStrategy(PaymentStrategy strategy) { this.paymentStrategy = strategy; }
    void checkout(int amount) { paymentStrategy.pay(amount); }
}

public class StrategyExample {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart();
        cart.setPaymentStrategy(new CreditCardPayment());
        cart.checkout(100);   // Paid 100 using Credit Card
        cart.setPaymentStrategy(new PayPalPayment());
        cart.checkout(200);   // Paid 200 using PayPal
    }
}
```

**In short:**

> 🏗️ Strategy pattern = “Same task, different ways — switch anytime.”

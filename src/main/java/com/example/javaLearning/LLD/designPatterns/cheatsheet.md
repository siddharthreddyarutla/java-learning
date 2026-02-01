# Cheat sheet

## 1. Creational design pattern


### 1. Singleton:

- Only once instance of class created which provides global point of access to it


### 2. Factory method:

- Provides interface to create objects and let subclasses decide which type of objects are created
- Uses **Abstract class** and **inheritance**

### 3. Abstract factory method:

- Family of related or dependent objects without specifying the concrete classes
- Using **interface** and **inheritance**

### 4. Builder

- Builder class is created inside the main class and defining all the methods and combination of each field creation with the constructor

### 5. Prototype

- Instead of creating objects from scratch (new keyboard) can be cloned or copied
- Uses **abstract class**

## 2. Structural design pattern

### 1. Adapter

- Allows to create objects with incompatable interfaces to work together by providing wrapper
- Uses **Composition** and **interface**

### 2. Bridge

- lets you split large class or set of closely related objects into 2 separate implementations/hierarchies
- It is mainly used to tackle exponential growth of sub classes.
- uses **abstract class** and **composition**

### 3. Composite data structure

- Composite pattern it to build tree structures, where both leaf and composite objects implement the same interface, allowing clients to treat them uniformly.
- Uses **composition** and **interface**

### 4. Decorator

- Add or remove responsibilities (features/behaviors) to an object dynamically at runtime, without changing its original class or creating tons of subclasses.
- Uses **Composition**, **abstract class** and **interface**

### 5. Facade

- Facade provides a simple, unified interface to a set of complex subsystems.
- **Composition** + **delegation**
  
- Think of:
  - Restaurant waiter = Facade 
  - Kitchen, chef, billing = Subsystems


### 6. Flyweight

- The Flyweight Pattern is used to minimize memory usage by sharing common (intrinsic) abstractState between multiple objects instead of creating duplicates.
- Flyweight minimizes memory usage by sharing immutable objects instead of creating new ones.


### 7. Proxy

- The Proxy Pattern provides a surrogate or placeholder for another object to control access to it.
- Uses composition


## 3. Behavioral design pattern

### 1. Chain of responsibility

- The Chain of Responsibility (CoR) pattern lets you pass a request along a chain of handlers, where each handler decides either to process the request or pass it to the next handler in the chain.
- uses **Abstract class** and **composition**

### 2. Interpreter

- The Interpreter Pattern defines a way to evaluate language grammar or expressions by representing them as objects and interpreting them.
- uses **interface**

### 3. Iterator

- Iterator pattern lets you traverse elements of a collection one by one without exposing how that collection is implemented.

### 4. Memento

- Think of it like an “undo” feature in editors or games.
- uses **Composition**


### 5. Observer

- pub sub
- publisher:
  - extends ApplicationEvent
  - constructs ApplicationEventPublisher
- Consumer:
  - Component class with @EventListner annotation
  

### 6. State

- The State Pattern allows an object to change its behavior when its internal State changes, as if the object has changed its class.
- Uses **interface** and **composition**


### 7. Strategy

- Strategy pattern lets you change an algorithm’s behavior at runtime by encapsulating each algorithm (strategy) in a separate class and making them interchangeable.
- “Same task, different ways — switch anytime
- uses **composition**, **inheritance** and **interface**

### 8. Template method

- The Template Method Pattern defines the skeleton (outline) of an algorithm in a base (abstract) class and lets subclasses override specific steps without changing the algorithm’s overall structure.
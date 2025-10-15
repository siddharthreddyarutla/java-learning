package com.example.javaLearning.designPatterns.creational.singleton;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.locks.ReentrantLock;

public class Singleton {

  public static void main(String[] args)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
      IllegalAccessException {

    // Creating or initializing it once
    DatabaseConfig.initializeDatabaseConfig();

    // Testing it with multiple threads should return same object (same hashcode)

    Runnable task = () -> {
      DatabaseConfig databaseConfig = DatabaseConfig.initializeDatabaseConfig();
      System.out.println("HashCode: " + databaseConfig.hashCode());
    };

    Thread t1 = new Thread(task);
    Thread t2 = new Thread(task);
    Thread t3 = new Thread(task);

    t1.start();
    t2.start();
    t3.start();

    try {
      t1.join();
      t2.join();
      t3.join();
    } catch (Exception e) {
      System.out.println(e);
    }

    /* Possible ways to violate singleton by using reflection and serialisation
     This is the reflection example to break singleton to achieve complete singleton check
     singletonViolations */
    DatabaseConfigSingleton databaseConfigSingleton = DatabaseConfigSingleton.getInstance();
    Constructor<DatabaseConfigSingleton> newConstructor =
        DatabaseConfigSingleton.class.getDeclaredConstructor();
    newConstructor.setAccessible(true);
    DatabaseConfigSingleton databaseConfigSingletonNew = newConstructor.newInstance();
    System.out.println(databaseConfigSingleton == databaseConfigSingletonNew);
    System.out.println("old hashcode: " + databaseConfigSingleton.hashCode());
    System.out.println("new hashcode: " + databaseConfigSingletonNew.hashCode());
  }

  public static class SimpleSingleton implements Serializable {
    private final static SimpleSingleton INSTANCE = new SimpleSingleton();

    private SimpleSingleton() {
    }

    public static SimpleSingleton getInstance() {
      return INSTANCE;
    }
  }


  /**
   * This can also be singleton but bit more boiler plate code
   */
  public static class DatabaseConfig {

    private static DatabaseConfig databaseConfig;

    private static final ReentrantLock lock = new ReentrantLock();

    private DatabaseConfig() {
    }

    public static DatabaseConfig initializeDatabaseConfig() {
      if (null == databaseConfig) {
        lock.lock();
        try {
          if (null == databaseConfig) {
            System.out.println(
                "Single instance is created by thread: " + Thread.currentThread().getName());
            databaseConfig = new DatabaseConfig();
          }
        } catch (Exception e) {
          System.out.println(e);
        } finally {
          lock.unlock();
        }
      }
      return databaseConfig;
    }
  }


  /**
   * simple singleton approach by Initialization-on-demand holder idiom
   */
  public static class DatabaseConfigSingleton {
    private static final DatabaseConfigSingleton INSTANCE = new DatabaseConfigSingleton();

    private DatabaseConfigSingleton() {
    }

    public static DatabaseConfigSingleton getInstance() {
      return INSTANCE;
    }
  }
}

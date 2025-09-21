package com.example.javaLearning.solidPrinciples.DIP;


public class DIP {

  public static void main(String[] args) {

    System.out.println("Violating DIP");
    Application application = new Application();
    application.start();

    System.out.println("--------------------------");
    System.out.println("DIP impl");

    ApplicationDIP mysqlApp = new ApplicationDIP(new MySqlDatabaseDIP());
    mysqlApp.start();

    ApplicationDIP mssqlApp = new ApplicationDIP(new MsSqlDatabase());
    mssqlApp.start();
  }

  public static class MySQLDatabase {
    public void connect() {
      System.out.println("Connected to MySQL Database");
    }
  }


  public static class Application {
    private MySQLDatabase database = new MySQLDatabase(); // tightly coupled ❌

    public void start() {
      database.connect();
    }
  }


  public interface Database {
    void connect();
  }


  public static class MySqlDatabaseDIP implements Database {

    @Override
    public void connect() {
      System.out.println("Connected to my sql database");
    }
  }


  public static class MsSqlDatabase implements Database {

    @Override
    public void connect() {
      System.out.println("Connected to ms sql database");
    }
  }


  public static class ApplicationDIP {

    private Database database;

    public ApplicationDIP(Database database) {
      this.database = database;
    }

    public void start() {
      database.connect();
    }
  }
}


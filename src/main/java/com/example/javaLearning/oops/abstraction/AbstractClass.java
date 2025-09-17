package com.example.javaLearning.oops.abstraction;

public class AbstractClass {

  public static void main(String[] args) {

    DatabaseHandler mongoHandler = new MongoHandler();

    mongoHandler.fetchData();
    mongoHandler.establishConnection();
    mongoHandler.prepareStatement();

    System.out.println("----------");

    DatabaseHandler mysqlHandler = new MysqlHandler();

    mysqlHandler.fetchData();
    mysqlHandler.establishConnection();
    mysqlHandler.prepareStatement();
  }

  public static abstract class DatabaseHandler {

    // Can have fields
    private final Long connectionTimeout = 1L;

    // Can have constructors
    public DatabaseHandler() {

    }

    // abstract method
    public abstract String connectionUrl();

    public abstract String connectionProperties();

    // Concrete methods
    public void fetchData() {
      System.out.println(connectionProperties());
      System.out.println("DatabaseHandler: Fetched data successfully");
    }

    public void establishConnection() {
      System.out.println(connectionUrl());
      System.out.println("DatabaseHandler: Connection successful");
    }

    public void prepareStatement() {
      System.out.println("DatabaseHandler: Prepared a statement");
    }
  }


  // Either abstract methods should be override or can create another abstract class
  public static class MysqlHandler extends DatabaseHandler {

    private final Long connectionTimeout = 2L;

    public MysqlHandler() {

    }

    @Override
    public String connectionUrl() {
      return "Mysql connection url";
    }

    @Override
    public String connectionProperties() {
      return "mysql connection properties";
    }

    @Override
    public void prepareStatement() {
      System.out.println("MysqlHandler: Prepared a statement");
    }
  }


  public static class MongoHandler extends DatabaseHandler {

    @Override
    public String connectionUrl() {
      return "Mongo connection url";
    }

    @Override
    public String connectionProperties() {
      return "Mongo connection properties";
    }

    @Override
    public void establishConnection() {
      System.out.println(connectionUrl());
      System.out.println("MongoHandler: Connection successful");
    }
  }
}

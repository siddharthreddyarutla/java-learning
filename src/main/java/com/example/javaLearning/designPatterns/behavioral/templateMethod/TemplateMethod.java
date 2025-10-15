package com.example.javaLearning.designPatterns.behavioral.templateMethod;


import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class TemplateMethod {

  public static void main(String[] args) {

    MysqlConnection connection = new MysqlConnection();
    connection.isConnectionSuccessful();

    System.out.println("---------------------------");

    MssqlConnection connection1 = new MssqlConnection();
    connection1.isConnectionSuccessful();
  }

  public interface DatabaseConnection {

    boolean isConnectionSuccessful();
  }


  public static abstract class AbstractDatabaseConnection implements DatabaseConnection {

    protected abstract Properties connectionProperties();

    @Override
    public boolean isConnectionSuccessful() {
      Properties properties = connectionProperties();
      System.out.println("Got props for connection");
      try (Connection connection = DriverManager.getConnection("url", properties)) {
        if (connection.isValid(1)) {
          System.out.println("Connection is successful");
          return true;
        } else {
          System.out.println("Connection is unsuccessful");
          return false;
        }
      } catch (Exception e) {
        System.out.println(e);
      }
      return true;
    }
  }


  public static class MysqlConnection extends AbstractDatabaseConnection {

    @Override
    protected Properties connectionProperties() {
      System.out.println("Request came fetch connection properties for mysql");
      return new Properties();
    }
  }


  public static class MssqlConnection extends AbstractDatabaseConnection {

    @Override
    protected Properties connectionProperties() {
      System.out.println("Request came fetch connection properties for mssql");
      return new Properties();
    }
  }
}

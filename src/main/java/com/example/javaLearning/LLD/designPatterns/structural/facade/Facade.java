package com.example.javaLearning.LLD.designPatterns.structural.facade;

import lombok.RequiredArgsConstructor;

public class Facade {

  public static void main(String[] args) {

    DatabaseFacade mysqlFacade =
        new DatabaseFacade(new DatabaseConnection(), new StatementPreparer(),
            new ResultExtractor());

    mysqlFacade.executeQuery();
  }

  @RequiredArgsConstructor
  public static class DatabaseFacade {

    private final DatabaseConnection databaseConnection;
    private final StatementPreparer statementPreparer;
    private final ResultExtractor resultExtractor;

    public void executeQuery() {
      System.out.println("Executing given query......");
      databaseConnection.createConnection();
      statementPreparer.createPrepareStatement();
      resultExtractor.extractResults();
      System.out.println("Query execution is done");
    }
  }


  public static class DatabaseConnection {

    public void createConnection() {
      System.out.println("DatabaseConnection has been established");
    }
  }


  public static class StatementPreparer {

    public void createPrepareStatement() {
      System.out.println("Statement was prepared");
    }
  }


  public static class ResultExtractor {

    public void extractResults() {
      System.out.println("Result set was extracted");
    }
  }
}

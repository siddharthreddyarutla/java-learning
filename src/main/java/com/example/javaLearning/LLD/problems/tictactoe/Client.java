package com.example.javaLearning.LLD.problems.tictactoe;

import com.example.javaLearning.LLD.problems.tictactoe.enums.GameStatus;
import com.example.javaLearning.LLD.problems.tictactoe.orchestrator.Orchestrator;

public class Client {

  public static void main(String[] args) {

    Orchestrator orchestrator = new Orchestrator();

    orchestrator.initializeGame();

    GameStatus status = orchestrator.startGame();

    switch (status) {
      case WIN -> System.out.println(orchestrator.getWinner().getName() + " has won");
      case DRAW -> System.out.println("Draw match");
    }

    System.out.println("Status: " +  status);
  }
}

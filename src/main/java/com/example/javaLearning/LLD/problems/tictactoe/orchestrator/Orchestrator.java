package com.example.javaLearning.LLD.problems.tictactoe.orchestrator;

import com.example.javaLearning.LLD.problems.tictactoe.entity.Board;
import com.example.javaLearning.LLD.problems.tictactoe.enums.GameStatus;
import com.example.javaLearning.LLD.problems.tictactoe.enums.PieceType;
import com.example.javaLearning.LLD.problems.tictactoe.entity.Player;
import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

@Getter
public class Orchestrator {

  private Integer boardSize = 3;
  private Board board;
  Queue<Player> playerQueue = new LinkedList<>();
  Player winner;

  public void initializeGame() {

    Player player1 = new Player("Player1", PieceType.X);

    Player player2 = new Player("Player2", PieceType.O);

    playerQueue.offer(player1);
    playerQueue.offer(player2);

    board = new Board(boardSize, new PieceType[boardSize][boardSize]);

  }

  public GameStatus startGame() {

    while (true) {
      Player currenyPlayer = playerQueue.peek();

      if (null == currenyPlayer) {
        break;
      }

      board.printBoard();

      System.out.println("Player: " + currenyPlayer.getName() + " turn, Enter {row,col}: ");

      Scanner scanner = new Scanner(System.in);

      String string = scanner.nextLine();

      String[] inputString = string.split(",");

      if (!board.hasFreeSpace()) {
        return GameStatus.DRAW;
      }

      int row = Integer.parseInt(inputString[0]);
      int col = Integer.parseInt(inputString[1]);

      if (row >= boardSize || col >= boardSize) {
        System.out.println("Please enter the row or col in bound...");
        continue;
      }

      if (!board.isSpotFree(row, col)) {
        System.out.println("Spot is already occupied, please try with other position");
        continue;
      }

      board.placePiece(row, col, currenyPlayer.getPieceType());

      if (board.isGameOver(row, col, currenyPlayer.getPieceType())) {
        board.printBoard();
        winner = currenyPlayer;
        return GameStatus.WIN;
      }

      playerQueue.offer(playerQueue.poll());

    }

    return null;
  }
}

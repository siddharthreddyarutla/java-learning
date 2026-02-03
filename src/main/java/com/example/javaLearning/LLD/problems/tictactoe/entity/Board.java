package com.example.javaLearning.LLD.problems.tictactoe.entity;

import com.example.javaLearning.LLD.problems.tictactoe.enums.PieceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Board {

  private Integer size;
  private PieceType[][] board;

  public void printBoard() {
    if (null != board) {
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          System.out.print((null != board[i][j] ? board[i][j] + " " : "  ") + " |");
        }
        System.out.println();
      }
    }
  }

  public boolean hasFreeSpace() {
    if (null != board) {
      for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
          if (board[i][j] == null) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public boolean isSpotFree(int i, int j) {
    if (null != board && null == board[i][j]) {
      return true;
    }
    return false;
  }

  public void placePiece(int row, int col, PieceType pieceType) {
    board[row][col] = pieceType;
  }


  public boolean isGameOver(int row, int col, PieceType pieceType) {

    boolean isRowMatch = true, isColMatch = true, isDiagonalMatch = true, isCrossDiagonalMatch =
        true;
    // Horizontal
    for (int i = 0; i < size; i++) {
      if (null == board[row][i] || board[row][i] != pieceType) {
        isRowMatch = false;
        break;
      }
    }

    // vertical
    for (int i = 0; i < size; i++) {
      if (null == board[i][col] || board[i][col] != pieceType) {
        isColMatch = false;
        break;
      }
    }

    // diagonal
    for (int i = 0, j = 0; i < size; i++, j++) {
      if (null == board[i][j] || board[i][j] != pieceType) {
        isDiagonalMatch = false;
        break;
      }
    }

    for (int i = 0, j = size - 1; i < size; i++, j--) {
      if (null == board[i][j] || board[i][j] != pieceType) {
        isCrossDiagonalMatch = false;
        break;
      }
    }
    return isRowMatch || isColMatch || isDiagonalMatch || isCrossDiagonalMatch;
  }
}

package com.example.javaLearning.LLD.problems.tictactoe.entity;

import com.example.javaLearning.LLD.problems.tictactoe.enums.PieceType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Player {

  private String name;
  private PieceType pieceType;
}

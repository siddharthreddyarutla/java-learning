package com.example.javaLearning.LLD.designPatterns.behavioral.iterator;

import java.util.Arrays;
import java.util.List;

public class Iterator {


  public static void main(String[] args) {

    List<String> alphabets = Arrays.asList("A", "B", "C", "D");
    java.util.Iterator<String> iterator = alphabets.iterator();

    while (iterator.hasNext()) {
      System.out.println(iterator.next() + " ");
    }
  }
}

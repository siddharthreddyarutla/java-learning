package com.example.javaLearning.java.libraries.jackson;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public class jackson {

  public static void main(String[] args) throws IOException {

    String json = "{ \"name\": \"John\", \"age\": 30 }";

    JsonParser jsonParser = new JsonFactory().createParser(json);
    while (jsonParser.nextToken() != null) {
      System.out.println("Token: " + jsonParser.getCurrentToken());
      System.out.println("Value: " + jsonParser.getText());
    }
  }
}

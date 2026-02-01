package com.example.javaLearning.LLD.designPatterns.behavioral.memento;

import java.util.Stack;

public class Memento {

  public static void main(String[] args) {

    TextEditor editor = new TextEditor();
    History history = new History();

    editor.addText("Hello ");
    history.save(editor.save()); // save state 1

    editor.addText("World!");
    history.save(editor.save()); // save state 2

    editor.addText("again!");
    history.save(editor.save()); // save state 3

    System.out.println("Current text: " + editor.text);

    // Undo (restore previous state)
    editor.restore(history.undo());
    System.out.println("After undo: " + editor.text);

    // Undo again
    editor.restore(history.undo());
    System.out.println("After second undo: " + editor.text);
  }

  // Memento
  public static class TextMemento {

    private final String text;

    public TextMemento(String text) {
      this.text = text;
    }

    public String getTextMemento() {
      return this.text;
    }
  }


  // Organiser
  public static class TextEditor {

    private String text = "";

    public void addText(String text) {
      this.text += text;
    }

    public TextMemento save() {
      return new TextMemento(text);
    }

    public void restore(TextMemento textMemento) {
      if (null != textMemento) {
        this.text = textMemento.getTextMemento();
      }
    }
  }


  // cate taker
  public static class History {

    private final Stack<TextMemento> historyOfMemento = new Stack<>();

    public void save(TextMemento textMemento) {
      historyOfMemento.push(textMemento);
    }

    public TextMemento undo() {
      if (historyOfMemento.size() <= 1) {
        return historyOfMemento.peek();
      }
      historyOfMemento.pop();
      return historyOfMemento.peek();
    }
  }
}

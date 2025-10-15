package com.example.javaLearning.designPatterns.behavioral.memento;

import java.util.Stack;

public class Memento {

  public static void main(String[] args) {

    TextEditor editor = new TextEditor();
    TextOrganizer history = new TextOrganizer();

    editor.addText("Hello ");
    history.save(editor.save()); // save state 1

    editor.addText("World!");
    history.save(editor.save()); // save state 2

    System.out.println("Current text: " + editor.text);

    // Undo (restore previous state)
    editor.restore(history.undoAll());
    System.out.println("After undo: " + editor.text);

    // Undo again
    editor.restore(history.undoAll());
    System.out.println("After second undo: " + editor.text);
  }

  public static class TextMemento {

    private final String text;

    public TextMemento(String text) {
      this.text = text;
    }

    public String getTextMemento() {
      return this.text;
    }
  }


  public static class TextEditor {

    private String text = "";

    public void addText(String text) {
      this.text += text;
    }

    public TextMemento save() {
      return new TextMemento(text);
    }

    public void restore(TextMemento textMemento) {
      this.text = textMemento.getTextMemento();
    }
  }


  public static class TextOrganizer {

    private final Stack<TextMemento> historyOfMemento = new Stack<>();

    public void save(TextMemento textMemento) {
      historyOfMemento.push(textMemento);
    }

    public TextMemento undoAll() {
      if (!historyOfMemento.isEmpty()) {
        return historyOfMemento.pop();
      }
      return null;
    }
  }
}

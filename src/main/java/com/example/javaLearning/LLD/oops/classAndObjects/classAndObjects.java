package com.example.javaLearning.LLD.oops.classAndObjects;

public class classAndObjects {

  public static void main(String[] args) {

    Book book = new Book("Sid", 100L, "Auto biography");
    System.out.println(book.toString());
  }

  public static class Book {

    private String author;
    private String title;
    private Long price;

    public Book(String author, Long price, String title) {
      this.author = author;
      this.price = price;
      this.title = title;
    }

    @Override
    public String toString() {
      return "Book{" + "author='" + author + '\'' + ", title='" + title + '\'' + ", price=" + price
          + '}';
    }
  }
}

package com.example.javaLearning.LLD.designPatterns.structural.proxy;

public class Proxy {

  // security proxy
  public static void main(String[] args) {

    FileProxy fileProxy = new FileProxy("test.png");
    fileProxy.viewFile("ADMIN");
    fileProxy.viewFile("USER");
  }

  public static class File {

    public final String fileName;

    public File(String fileName) {
      this.fileName = fileName;
    }

    public void viewFile() {
      System.out.println("File is viewed and file name is: " + fileName);
    }
  }


  public static class FileProxy {

    public final String fileName;
    private File file;

    public FileProxy(String fileName) {
      this.fileName = fileName;
    }

    public void viewFile(String role) {
      if (role == "ADMIN") {
        this.file = new File(fileName);
        file.viewFile();
      } else {
        System.out.println("Access denied");
      }
    }
  }
}

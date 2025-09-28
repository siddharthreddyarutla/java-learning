package com.example.javaLearning.designPatterns.Creational.builder;

import lombok.Builder;
import lombok.ToString;

public class BuilderPattern {

  public static void main(String[] args) {

    System.out.println("Using lombok......");
    User user =
        User.builder().firstName("Sid").lastName("arutla").age(24).email("#gmail.com").phone("123")
            .occupation("software").build();
    System.out.println(user.toString());

    System.out.println("---------------------------");
    System.out.println("Using custom builder class");
    UserClass userClass =
        new UserClass.UserClassCustomBuilder().firstName("Sid").lastName("arutla").age(24)
            .email("#gmail.com").phone("123").occupation("software").build();
    System.out.println(userClass.toString());
  }

  // Using lombok builder to avoid boilerplate code
  @Builder
  @ToString
  public static class User {
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phone;
    private String occupation;
  }


  // Instead of using lombok
  public static class UserClass {
    private String firstName;
    private String lastName;
    private int age;
    private String email;
    private String phone;
    private String occupation;

    private UserClass(UserClassCustomBuilder userClassCustomBuilder) {
      this.firstName = userClassCustomBuilder.firstName;
      this.lastName = userClassCustomBuilder.lastName;
      this.age = userClassCustomBuilder.age;
      this.email = userClassCustomBuilder.email;
      this.phone = userClassCustomBuilder.phone;
      this.occupation = userClassCustomBuilder.occupation;
    }


    public static class UserClassCustomBuilder {
      private String firstName;
      private String lastName;
      private int age;
      private String email;
      private String phone;
      private String occupation;


      public UserClassCustomBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
      }

      public UserClassCustomBuilder lastName(String lastName) {
        this.firstName = firstName;
        return this;
      }

      public UserClassCustomBuilder age(int age) {
        this.age = age;
        return this;
      }

      public UserClassCustomBuilder email(String email) {
        this.email = email;
        return this;
      }

      public UserClassCustomBuilder phone(String phone) {
        this.phone = phone;
        return this;
      }

      public UserClassCustomBuilder occupation(String occupation) {
        this.occupation = firstName;
        return this;
      }

      public UserClass build() {
        return new UserClass(this);
      }
    }

    @Override
    public String toString() {
      return "User [firstName=" + firstName + ", lastName=" + lastName + ", age=" + age + ", email="
          + email + ", phone=" + phone + ", occupation=" + occupation + "]";
    }
  }
}

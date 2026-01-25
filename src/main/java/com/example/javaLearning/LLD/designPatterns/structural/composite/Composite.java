package com.example.javaLearning.LLD.designPatterns.structural.composite;

import java.util.ArrayList;
import java.util.List;

public class Composite {

  public static void main(String[] args) {

    Manager revanth = new Manager("Revanth");
    Manager rahul = new Manager("Rahul");
    Manager mayuresh = new Manager("Mayuresh");

    Employee employee = new Employee(10_000L);
    Employee employee1 = new Employee(20_000L);
    Employee employee2 = new Employee(30_000L);

    revanth.addSalary(employee);
    mayuresh.addSalary(employee1);
    mayuresh.addSalary(employee2);
    rahul.addSalary(mayuresh);
    rahul.addSalary(revanth);

    System.out.println("Total rahul salary: " + rahul.totalSalary());
    rahul.printSalary();
  }


  // Component
  public interface CalculateSalary {

    Long totalSalary();

    void printSalary();
  }


  // Leaf
  public static class Employee implements CalculateSalary {

    public final Long salary;

    public Employee(Long salary) {
      this.salary = salary;
    }

    @Override
    public Long totalSalary() {
      return salary;
    }

    @Override
    public void printSalary() {
      System.out.println("     - Employee salary: " + this.salary);
    }
  }


  // Composite
  public static class Manager implements CalculateSalary {
    private final String name;

    List<CalculateSalary> calculateSalaryList = new ArrayList<>();

    public Manager(String name) {
      this.name = name;
    }

    public void addSalary(CalculateSalary calculateSalary) {
      calculateSalaryList.add(calculateSalary);
    }

    public void removeSalary(CalculateSalary calculateSalary) {
      calculateSalaryList.remove(calculateSalary);
    }

    @Override
    public Long totalSalary() {
      Long amount = 0L;
      for (CalculateSalary calculateSalary : calculateSalaryList) {
        amount += calculateSalary.totalSalary();
      }
      return amount;
    }

    @Override
    public void printSalary() {
      System.out.println("+ Manager name: " + this.name);
      for (CalculateSalary calculateSalary : calculateSalaryList) {
        calculateSalary.printSalary();
      }
    }
  }
}


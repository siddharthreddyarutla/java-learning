package com.example.javaLearning.designPatterns.behavioral.strategy;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

public class Strategy {

  public static void main(String[] args) {

    ValidationStrategy<Long> validationStrategy =
        new ValidationStrategy<>(new CustomerValidation<Long>());
    System.out.println(validationStrategy.validate(1L));

    validationStrategy.updateValidationStrategy(new UserValidation<Void>());
    System.out.println(validationStrategy.validate(null));

    System.out.println("----------Register Strategy----------");

    Map<ValidationType, Validation<?>> validationMap = new HashMap<>();
    validationMap.put(ValidationType.CUSTOMER_VALIDATION, new CustomerValidation<>());
    validationMap.put(ValidationType.USER_VALIDATION, new UserValidation<>());
    validationMap.put(ValidationType.OWNER, new OwnerValidation<>());
    ValidationStrategyRegister<Long> validationStrategyRegister =
        new ValidationStrategyRegister<>(validationMap);
    System.out.println(validationStrategyRegister.validate(ValidationType.CUSTOMER_VALIDATION, 1L));
    System.out.println(validationStrategyRegister.validate(ValidationType.OWNER, null));
  }

  public enum StatusCode {
    SUCCESS,
    DATA_VALIDATION_FAILED
  }


  public enum ValidationType {
    CUSTOMER_VALIDATION,
    USER_VALIDATION,
    ROLE,
    LEAD,
    OWNER
  }


  public interface Validation<T> {

    StatusCode validate(T value);
  }


  public static class ValidationStrategy<T> implements Validation<T> {

    private Validation validation;

    private ValidationStrategy() {

    }

    public ValidationStrategy(Validation validation) {
      this.validation = validation;
    }

    public void updateValidationStrategy(Validation validation) {
      this.validation = validation;
    }

    @Override
    public StatusCode validate(T value) {
      return validation.validate(value);
    }
  }


  public static class CustomerValidation<T> implements Validation<T> {

    @Override
    public StatusCode validate(T value) {
      System.out.println("Validating customer scope");
      if (null != value) {
        return StatusCode.SUCCESS;
      }
      return StatusCode.DATA_VALIDATION_FAILED;
    }
  }


  public static class UserValidation<T> implements Validation<T> {

    @Override
    public StatusCode validate(T value) {
      System.out.println("Validating user scope");
      // some conditional based checking
      if (null != value) {
        return StatusCode.SUCCESS;
      }
      return StatusCode.DATA_VALIDATION_FAILED;
    }
  }


  public static class OwnerValidation<T> implements Validation<T> {

    @Override
    public StatusCode validate(T value) {
      System.out.println("Validating owner scope");
      if (null != value) {
        return StatusCode.SUCCESS;
      }
      return StatusCode.DATA_VALIDATION_FAILED;
    }
  }


  @Configuration
  public class validationConfiguration {

    @Bean("ValidationMap")
    public Map<ValidationType, Validation> validationMapConfig() {
      Map<ValidationType, Validation> validationMap = new HashMap<>();
      validationMap.put(ValidationType.CUSTOMER_VALIDATION, new CustomerValidation<>());
      validationMap.put(ValidationType.USER_VALIDATION, new UserValidation<>());
      validationMap.put(ValidationType.OWNER, new OwnerValidation<>());
      return validationMap;
    }
  }


  public static class ValidationStrategyRegister<T> {

    private final Map<ValidationType, Validation<?>> validationMap;

    public ValidationStrategyRegister(Map<ValidationType, Validation<?>> validationMap) {
      this.validationMap = validationMap;
    }

    public StatusCode validate(ValidationType validationType, T value) {
      Validation<T> v = (Validation<T>) validationMap.get(validationType);
      return v.validate(value);
    }
  }
}

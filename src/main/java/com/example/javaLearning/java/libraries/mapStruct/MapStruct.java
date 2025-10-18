package com.example.javaLearning.java.libraries.mapStruct;

import lombok.Builder;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

public class MapStruct {

  public static void main(String[] args) {

    MapperImpl mapper = new MapperImpl(UserMapper.INSTANCE);
    mapper.call();
  }

  public static class MapperImpl {

    @Autowired
    private final UserMapper userMapper;

    public MapperImpl(UserMapper userMapper) {
      this.userMapper = userMapper;
    }

    public void call() {

      UserDTO userDTO = UserDTO.builder().id(1L).firstName("Sid").age(23L).status("ACTIVE").build();
      User user = userMapper.toUser(userDTO);
      System.out.println("User: " + user);

      System.out.println("User dto: " + userMapper.toUserDto(user));

      System.out.println("Basic user dto: " + userMapper.toBasicUserDto(user));
    }
  }


  @Data
  @Builder
  public static class User {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private Long age;

    private String address;

    private String mobile;

    private String status;
  }


  @Data
  @Builder
  public static class UserDTO {

    private Long id;

    private String firstName;

    private String middleName;

    private String lastName;

    private Long age;

    private String address;

    private String mobile;

    private String status;
  }


  @Data
  public static class BasicUserDto {

    private String firstName;

    private Long age;
  }


  @Mapper(componentModel = "spring")
  public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User toUser(UserDTO userDTO);

    UserDTO toUserDto(User user);

    BasicUserDto toBasicUserDto(User user);
  }
}

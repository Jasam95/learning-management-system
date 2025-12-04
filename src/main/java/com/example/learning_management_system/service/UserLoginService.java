package com.example.learning_management_system.service;

import com.example.learning_management_system.dto.UserDto;
import com.example.learning_management_system.entity.User;

import java.util.List;

public interface UserLoginService {

        void createUser(UserDto userDto , String roles);

        UserDto findUserByEmail(String email);

        List<UserDto> findAllUsers();


    User findByEmail(String name);

    long countByRoles(String roles);
}

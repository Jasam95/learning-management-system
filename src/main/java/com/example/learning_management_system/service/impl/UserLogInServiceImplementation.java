package com.example.learning_management_system.service.impl;


import com.example.learning_management_system.dto.UserDto;
import com.example.learning_management_system.entity.Role;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.RoleRepository;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.UserLoginService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@AllArgsConstructor
public class UserLogInServiceImplementation implements UserLoginService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;

    private ModelMapper modelMapper;
    private PasswordEncoder passwordEncoder;


    @Override
    public void createUser(UserDto userDto , String roleParticipant) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setFullName(userDto.getFullName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Role role = createRoleIfNotExist(roleParticipant);

        user.setRoles(Arrays.asList(role));
        userRepository.save(user);
    }

    // Create role if it does not exist
    public Role createRoleIfNotExist(String roleUser) {
        return roleRepository.findByName(roleUser)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleUser);
                    return roleRepository.save(role);
                });
    }

    @Override
    public UserDto findUserByEmail(String email) {
        User findUser =  userRepository.findByEmail(email).orElseThrow();
        return modelMapper.map(findUser, UserDto.class);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDto.class)).toList();
    }

    @Override
    public User findByEmail(String email) {

        return userRepository.findByEmail(email).orElseThrow();
    }

    @Override
    public long countByRoles(String student) {
        return userRepository.countByRoleName(student);
    }
}
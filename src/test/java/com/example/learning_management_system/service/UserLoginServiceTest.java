package com.example.learning_management_system.service;

import com.example.learning_management_system.dto.UserDto;
import com.example.learning_management_system.entity.Role;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.RoleRepository;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.impl.UserLogInServiceImplementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserLogInServiceImplementationTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserLogInServiceImplementation service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------------------------
    @Test
    void createUser_shouldSaveNewUser_whenEmailNotUsed() {

        // ARRANGE
        UserDto dto = new UserDto();
        dto.setEmail("test@mail.com");
        dto.setPassword("123");
        dto.setFullName("Test User");

        when(userRepository.existsByEmail("test@mail.com"))
                .thenReturn(false);

        User mappedUser = new User();
        when(modelMapper.map(dto, User.class)).thenReturn(mappedUser);
        when(passwordEncoder.encode("123")).thenReturn("ENCODED");

        Role role = new Role();
        role.setName("ROLE_STUDENT");

        when(roleRepository.findByName("ROLE_STUDENT"))
                .thenReturn(Optional.of(role));

        // ACT
        service.createUser(dto, "ROLE_STUDENT");

        // ASSERT
        assertEquals("ENCODED", mappedUser.getPassword());
        verify(userRepository).save(mappedUser);
    }

    // --------------------------------------------------------------------
    @Test
    void createUser_shouldThrowException_whenEmailAlreadyExists() {

        // ARRANGE
        UserDto dto = new UserDto();
        dto.setEmail("duplicate@mail.com");

        when(userRepository.existsByEmail("duplicate@mail.com"))
                .thenReturn(true);

        // ACT + ASSERT
        assertThrows(IllegalArgumentException.class,
                () -> service.createUser(dto, "ROLE_STUDENT"));
    }

    // --------------------------------------------------------------------
    @Test
    void createUser_shouldCreateRoleIfNotExists() {

        // ARRANGE
        UserDto dto = new UserDto();
        dto.setEmail("test@mail.com");
        dto.setPassword("secret");

        when(userRepository.existsByEmail(any())).thenReturn(false);

        User mapped = new User();
        when(modelMapper.map(dto, User.class)).thenReturn(mapped);
        when(passwordEncoder.encode("secret")).thenReturn("ENCODED");

        when(roleRepository.findByName("ROLE_INSTRUCTOR"))
                .thenReturn(Optional.empty());

        Role newRole = new Role();
        newRole.setName("ROLE_INSTRUCTOR");

        when(roleRepository.save(any(Role.class)))
                .thenReturn(newRole);

        // ACT
        service.createUser(dto, "ROLE_INSTRUCTOR");

        // ASSERT
        verify(roleRepository).save(any(Role.class));
        verify(userRepository).save(mapped);
        assertEquals("ENCODED", mapped.getPassword());
    }

    // --------------------------------------------------------------------
    @Test
    void findUserByEmail_shouldReturnMappedDto() {

        // ARRANGE
        User user = new User();
        user.setEmail("x@mail.com");

        UserDto dto = new UserDto();
        dto.setEmail("x@mail.com");

        when(userRepository.findByEmail("x@mail.com"))
                .thenReturn(Optional.of(user));

        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        // ACT
        UserDto result = service.findUserByEmail("x@mail.com");

        // ASSERT
        assertEquals("x@mail.com", result.getEmail());
    }

    // --------------------------------------------------------------------
    @Test
    void findAllUsers_shouldReturnListOfDtos() {

        // ARRANGE
        User user = new User();
        user.setEmail("a@mail.com");

        when(userRepository.findAll()).thenReturn(List.of(user));

        UserDto dto = new UserDto();
        dto.setEmail("a@mail.com");

        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        // ACT
        List<UserDto> result = service.findAllUsers();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals("a@mail.com", result.get(0).getEmail());
    }

    // --------------------------------------------------------------------
    @Test
    void findByEmail_shouldReturnUser() {

        // ARRANGE
        User user = new User();
        user.setEmail("z@mail.com");

        when(userRepository.findByEmail("z@mail.com"))
                .thenReturn(Optional.of(user));

        // ACT
        User result = service.findByEmail("z@mail.com");

        // ASSERT
        assertEquals("z@mail.com", result.getEmail());
    }

    // --------------------------------------------------------------------
    @Test
    void countByRoles_shouldReturnCount() {

        when(userRepository.countByRoleName("ROLE_STUDENT"))
                .thenReturn(10L);

        long count = service.countByRoles("ROLE_STUDENT");

        assertEquals(10L, count);
        verify(userRepository).countByRoleName("ROLE_STUDENT");
    }
}

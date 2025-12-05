package com.example.learning_management_system.repository;

import com.example.learning_management_system.entity.Role;
import com.example.learning_management_system.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByEmail_shouldReturnUser() {

        User user = new User();
        user.setFullName("Test User");
        user.setEmail("user@test.com");
        user.setPassword("pwd");
        userRepository.saveAndFlush(user);

        assertThat(userRepository.findByEmail("user@test.com")).isPresent();
    }

    @Test
    void existsByEmail_shouldReturnTrue() {
        User user = new User();
        user.setFullName("User 2");
        user.setEmail("exists@test.com");
        user.setPassword("pwd");
        userRepository.saveAndFlush(user);

        assertThat(userRepository.existsByEmail("exists@test.com")).isTrue();
    }

    @Test
    void countByRoleName_shouldReturnCorrectCount() {

        Role studentRole = new Role();
        studentRole.setName("ROLE_STUDENT");
        roleRepository.saveAndFlush(studentRole);

        User user1 = new User();
        user1.setEmail("s1@test.com");
        user1.setPassword("p");
        user1.setFullName("s1");
        user1.getRoles().add(studentRole);
        userRepository.saveAndFlush(user1);

        User user2 = new User();
        user2.setEmail("s2@test.com");
        user2.setPassword("p");
        user2.setFullName("s2");
        user2.getRoles().add(studentRole);
        userRepository.saveAndFlush(user2);

        long count = userRepository.countByRoleName("ROLE_STUDENT");

        assertThat(count).isEqualTo(2);
    }
}

package com.example.learning_management_system.controller;

import com.example.learning_management_system.dto.UserDto;
import com.example.learning_management_system.repository.CourseRepository;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.UserLoginService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AdminUserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserLoginService userLoginService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private EnrollmentService enrollmentService;
    @Mock
    private CourseService courseService;

    @InjectMocks
    private AdminUserController adminUserController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(adminUserController).build();
    }

    // ============================================
    // TEST SAVE STUDENT
    // ============================================
    @Test
    void testSaveStudent_ShouldCallCreateUserWithStudentRole() throws Exception {

        mockMvc.perform(post("/admin/students/save")
                        .param("fullName", "Test Student")
                        .param("email", "student@example.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/students"));

        verify(userLoginService, times(1))
                .createUser(
                        org.mockito.ArgumentMatchers.any(UserDto.class),
                        eq("ROLE_STUDENT")
                );
    }


    // ============================================
    // TEST SAVE INSTRUCTOR
    // ============================================
    @Test
    void testSaveInstructor_ShouldCallCreateUserWithInstructorRole() throws Exception {

        mockMvc.perform(post("/admin/instructors/save")
                        .param("fullName", "Test Instructor")
                        .param("email", "instructor@example.com")
                        .param("password", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/instructors"));

        verify(userLoginService, times(1))
                .createUser(
                        org.mockito.ArgumentMatchers.any(UserDto.class),
                        eq("ROLE_INSTRUCTOR")
                );
    }
}


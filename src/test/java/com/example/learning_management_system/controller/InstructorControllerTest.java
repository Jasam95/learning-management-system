package com.example.learning_management_system.controller;


import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.InstructorService;
import com.example.learning_management_system.service.UserLoginService;
import com.example.learning_management_system.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InstructorControllerTest {

    @Mock private InstructorService instructorService;
    @Mock private UserRepository userRepository;
    @Mock private UserLoginService userLoginService;
    @Mock private CourseService courseService;
    @Mock private EnrollmentService enrollmentService;

    @Mock private Model model;
    @Mock private Principal principal;
    @Mock private UserDetails userDetails;

    @InjectMocks
    private InstructorController instructorController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------------------------
    @Test
    void instructorDashboard_shouldShowCourses() {

        // ARRANGE
        User instructor = new User();
        instructor.setEmail("inst@test.com");

        when(principal.getName()).thenReturn("inst@test.com");
        when(userLoginService.findByEmail("inst@test.com")).thenReturn(instructor);
        when(courseService.getCoursesByInstructor(instructor)).thenReturn(List.of(new Course()));

        // ACT
        String view = instructorController.instructorDashboard(model, principal);

        // ASSERT
        assertEquals("instructor/dashboard", view);
        verify(model).addAttribute(eq("courses"), any());
    }

    // --------------------------------------------------------------------
    @Test
    void listCourses_shouldReturnInstructorCourses() {

        // ARRANGE
        User instructor = new User();
        instructor.setId(10L);

        when(userDetails.getUsername()).thenReturn("inst@test.com");
        when(userRepository.findByEmail("inst@test.com")).thenReturn(Optional.of(instructor));
        when(instructorService.getCoursesForInstructor(instructor)).thenReturn(List.of(new Course()));

        // ACT
        String view = instructorController.listCourses(userDetails, model);

        // ASSERT
        assertEquals("instructor/courses", view);
        verify(model).addAttribute(eq("courses"), any());
    }

    // --------------------------------------------------------------------
    @Test
    void viewCourseContent_shouldReturnContentList() {

        // ARRANGE
        User instructor = new User();
        instructor.setEmail("inst@test.com");

        Course course = new Course();
        course.setId(1L);

        when(userDetails.getUsername()).thenReturn("inst@test.com");
        when(userRepository.findByEmail("inst@test.com")).thenReturn(Optional.of(instructor));
        when(instructorService.getCourseIfBelongsToInstructor(1L, instructor)).thenReturn(course);
        when(instructorService.getContentsForCourse(course)).thenReturn(List.of(new CourseContent()));

        // ACT
        String view = instructorController.viewCourseContent(1L, userDetails, model);

        // ASSERT
        assertEquals("instructor/course-content", view);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute(eq("contents"), any());
    }

    // --------------------------------------------------------------------
    @Test
    void viewEnrolledStudents_shouldReturnStudentsIfInstructorOwnsCourse() {

        // ARRANGE
        User instructor = new User();
        instructor.setId(10L);

        Course course = new Course();
        course.setInstructor(instructor);

        when(principal.getName()).thenReturn("inst@test.com");
        when(userLoginService.findByEmail("inst@test.com")).thenReturn(instructor);
        when(courseService.getCourseById(99L)).thenReturn(Optional.of(course));
        when(enrollmentService.getEnrollmentsForCourse(course)).thenReturn(List.of(new Enrollment()));

        // ACT
        String view = instructorController.viewEnrolledStudents(99L, principal, model);

        // ASSERT
        assertEquals("instructor/students", view);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute(eq("enrollments"), any());
    }
}


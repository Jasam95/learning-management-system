package com.example.learning_management_system.controller;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.UserLoginService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StudentControllerTest {

    @Mock private CourseService courseService;
    @Mock private EnrollmentService enrollmentService;
    @Mock private UserLoginService userService;

    @Mock private Model model;
    @Mock private Principal principal;

    @InjectMocks
    private StudentController studentController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------------------------
    @Test
    void viewAllCourses_shouldReturnCoursesPage() {

        // ARRANGE
        User student = new User();
        student.setEmail("stud@test.com");

        Course c1 = new Course();
        when(principal.getName()).thenReturn("stud@test.com");
        when(userService.findByEmail("stud@test.com")).thenReturn(student);
        when(courseService.getAllCourses()).thenReturn(List.of(c1));
        when(enrollmentService.isEnrolled(student, c1)).thenReturn(false);

        // ACT
        String view = studentController.viewAllCourses(model, principal);

        // ASSERT
        assertEquals("students/courses", view);
        verify(model).addAttribute(eq("courses"), any());
    }

    // --------------------------------------------------------------------
    @Test
    void showEnrollPage_shouldShowPageIfNotEnrolled() {

        // ARRANGE
        User student = new User();
        student.setId(1L);

        Course course = new Course();

        when(principal.getName()).thenReturn("stud@test.com");
        when(userService.findByEmail("stud@test.com")).thenReturn(student);
        when(courseService.getCourseById(10L)).thenReturn(Optional.of(course));
        when(enrollmentService.isEnrolled(student, course)).thenReturn(false);

        // ACT
        String view = studentController.showEnrollPage(10L, model, principal);

        // ASSERT
        assertEquals("students/enroll-page", view);
        verify(model).addAttribute("course", course);
    }

    // --------------------------------------------------------------------
    @Test
    void confirmEnroll_shouldEnrollFreeCourse() {

        // ARRANGE
        User student = new User();
        Course course = new Course();
        course.setPriceType(Course.PriceType.FREE);

        when(principal.getName()).thenReturn("stud@test.com");
        when(userService.findByEmail("stud@test.com")).thenReturn(student);
        when(courseService.getCourseById(10L)).thenReturn(Optional.of(course));

        // ACT
        String view = studentController.confirmEnroll(10L, principal, model);

        // ASSERT
        assertEquals("redirect:/students/course/10/content", view);
        verify(enrollmentService).enrollStudent(student, course, true);
    }

    // --------------------------------------------------------------------
    @Test
    void viewCourseContent_shouldReturnContentIfEnrolled() {

        // ARRANGE
        User student = new User();
        Course course = new Course();

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);

        when(principal.getName()).thenReturn("stud@test.com");
        when(userService.findByEmail("stud@test.com")).thenReturn(student);
        when(courseService.getCourseById(10L)).thenReturn(Optional.of(course));
        when(enrollmentService.getEnrollment(student, course)).thenReturn(Optional.of(enrollment));

        // ACT
        String view = studentController.viewCourseContent(10L, principal, model);

        // ASSERT
        assertEquals("students/course-content", view);
        verify(model).addAttribute("course", course);
    }
}


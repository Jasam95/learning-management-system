package com.example.learning_management_system.controller;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.UserLoginService;
import com.example.learning_management_system.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.Model;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class AdminCourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;
    @Mock
    private UserLoginService userLoginService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EnrollmentService enrollmentService;

    @InjectMocks
    private AdminCourseController adminCourseController;

    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        model = mock(Model.class);
    }

    @Test
    void listCourses_shouldReturnCoursesView_andAddCoursesToModel() {

        // ARRANGE
        List<Course> courses = List.of(new Course());
        when(courseService.getAllCourses()).thenReturn(courses);

        // ACT
        String viewName = adminCourseController.listCourses(model);

        // ASSERT
        assertEquals("admin/courses", viewName);
        verify(model).addAttribute("courses", courses);
    }

    @Test
    void adminDashboard_shouldReturnDashboardView_andAddCounts() {

        // ARRANGE
        when(userLoginService.findAllUsers()).thenReturn(List.of());
        when(courseService.countCourses()).thenReturn(5L);
        when(enrollmentService.countEnrollments()).thenReturn(10L);

        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("admin@test.com");

        // ACT
        String view = adminCourseController.adminDashboard(model, user);

        // ASSERT
        assertEquals("admin/dashboard", view);
        verify(model).addAttribute(eq("totalInstructors"), anyInt());
        verify(model).addAttribute(eq("totalStudents"), anyInt());
        verify(model).addAttribute("totalCourses", 5L);
        verify(model).addAttribute("totalEnrollments", 10L);
    }

    @Test
    void saveCourse_shouldSaveCourse_andRedirect() {

        // ARRANGE
        User admin = new User();
        admin.setEmail("admin@test.com");

        User instructor = new User();
        instructor.setId(5L);

        Course course = new Course();

        UserDetails user = mock(UserDetails.class);
        when(user.getUsername()).thenReturn("admin@test.com");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(userRepository.findById(5L)).thenReturn(Optional.of(instructor));

        // ACT
        String view = adminCourseController.saveCourse(course, 5L, user);

        // ASSERT
        assertEquals("redirect:/admin/courses", view);
        verify(courseService).saveCourse(course);
        assertEquals(admin, course.getCreatedByAdmin());
        assertEquals(instructor, course.getInstructor());
    }


    @Test
    void deleteCourse_shouldDelete_andRedirect() {

        // ACT
        String view = adminCourseController.deleteCourse(7L);

        // ASSERT
        assertEquals("redirect:/admin/courses", view);
        verify(courseService).deleteCourse(7L);
    }


    @Test
    void viewCourse_shouldReturnView_andAddCourseToModel() {

        // ARRANGE
        Course c = new Course();
        when(courseService.getCourseById(10L)).thenReturn(Optional.of(c));

        // ACT
        String view = adminCourseController.viewCourse(10L, model);

        // ASSERT
        assertEquals("admin/course-view", view);
        verify(model).addAttribute("course", c);
    }


    @Test
    void manageCourseStudents_shouldReturnStudentListView() {

        // ARRANGE
        Course course = new Course();
        when(courseService.getCourseById(10L)).thenReturn(Optional.of(course));

        when(enrollmentService.getEnrollmentsForCourse(course))
                .thenReturn(List.of());

        // ACT
        String view = adminCourseController.manageCourseStudents(10L, model);

        // ASSERT
        assertEquals("admin/course-students", view);
        verify(model).addAttribute("course", course);
        verify(model).addAttribute(eq("enrollments"), any());
    }



}


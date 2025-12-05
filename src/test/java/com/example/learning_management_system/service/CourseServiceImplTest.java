package com.example.learning_management_system.service;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Role;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.CourseRepository;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.impl.CourseServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------------------------------------------------------
    @Test
    void getAllCourses_shouldReturnAllCourses() {

        // ARRANGE
        when(courseRepository.findAll()).thenReturn(List.of(new Course()));

        // ACT
        List<Course> courses = courseService.getAllCourses();

        // ASSERT
        assertEquals(1, courses.size());
        verify(courseRepository).findAll();
    }

    // ---------------------------------------------------------------
    @Test
    void getCourseById_shouldReturnCourse() {

        // ARRANGE
        Course course = new Course();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // ACT
        Optional<Course> result = courseService.getCourseById(1L);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(course, result.get());
        verify(courseRepository).findById(1L);
    }

    // ---------------------------------------------------------------
    @Test
    void saveCourse_shouldSaveCourse() {

        // ARRANGE
        Course course = new Course();

        // ACT
        courseService.saveCourse(course);

        // ASSERT
        verify(courseRepository).save(course);
    }

    // ---------------------------------------------------------------
    @Test
    void deleteCourse_shouldDeleteById() {

        // ACT
        courseService.deleteCourse(5L);

        // ASSERT
        verify(courseRepository).deleteById(5L);
    }

    // ---------------------------------------------------------------
    @Test
    void getAllInstructors_shouldReturnUsersWithInstructorRole() {

        // ARRANGE
        User instructor = new User();
        Role r = new Role();
        r.setName("ROLE_INSTRUCTOR");
        instructor.setRoles(List.of(r));

        User student = new User();
        Role s = new Role();
        s.setName("ROLE_STUDENT");
        student.setRoles(List.of(s));

        when(userRepository.findAll()).thenReturn(List.of(instructor, student));

        // ACT
        List<User> result = courseService.getAllInstructors();

        // ASSERT
        assertEquals(1, result.size());
        assertEquals(instructor, result.get(0));
        verify(userRepository).findAll();
    }

    // ---------------------------------------------------------------
    @Test
    void countCourses_shouldReturnRepositoryCount() {

        // ARRANGE
        when(courseRepository.count()).thenReturn(10L);

        // ACT
        long count = courseService.countCourses();

        // ASSERT
        assertEquals(10L, count);
        verify(courseRepository).count();
    }

    // ---------------------------------------------------------------
    @Test
    void getCoursesByInstructor_shouldReturnCourses() {

        // ARRANGE
        User instructor = new User();
        Course c1 = new Course();
        when(courseRepository.findByInstructor(instructor)).thenReturn(List.of(c1));

        // ACT
        List<Course> result = courseService.getCoursesByInstructor(instructor);

        // ASSERT
        assertEquals(1, result.size());
        verify(courseRepository).findByInstructor(instructor);
    }
}

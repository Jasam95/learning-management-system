package com.example.learning_management_system.service;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.EnrollmentRepository;
import com.example.learning_management_system.service.impl.EnrollmentServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EnrollmentServiceImplTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------------------------
    @Test
    void isEnrolled_shouldReturnTrue() {
        // ARRANGE
        User student = new User();
        Course course = new Course();

        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.of(new Enrollment()));

        // ACT
        boolean result = enrollmentService.isEnrolled(student, course);

        // ASSERT
        assertTrue(result);
    }

    // --------------------------------------------------------------------
    @Test
    void isEnrolled_shouldReturnFalse() {
        // ARRANGE
        User student = new User();
        Course course = new Course();

        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.empty());

        // ACT
        boolean result = enrollmentService.isEnrolled(student, course);

        // ASSERT
        assertFalse(result);
    }

    // --------------------------------------------------------------------
    @Test
    void enrollStudent_shouldCreateNewEnrollment_whenNotExists() {
        // ARRANGE
        User student = new User();
        Course course = new Course();

        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.empty());

        // ACT
        enrollmentService.enrollStudent(student, course, true);

        // ASSERT
        verify(enrollmentRepository).save(any(Enrollment.class));
    }

    // --------------------------------------------------------------------
    @Test
    void enrollStudent_shouldNotCreateNew_whenAlreadyEnrolled() {

        // ARRANGE
        User student = new User();
        Course course = new Course();

        Enrollment existing = new Enrollment();
        existing.setPaid(true); // IMPORTANT FIX

        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.of(existing));

        // ACT
        enrollmentService.enrollStudent(student, course, true);

        // ASSERT
        verify(enrollmentRepository, never()).save(any());
    }


    // --------------------------------------------------------------------
    @Test
    void enrollStudent_shouldUpgradeFromFreeToPaid() {
        // ARRANGE
        User student = new User();
        Course course = new Course();

        Enrollment existing = new Enrollment();
        existing.setPaid(false); // previously free

        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.of(existing));

        // ACT
        enrollmentService.enrollStudent(student, course, true);

        // ASSERT
        assertTrue(existing.isPaid());
        assertNotNull(existing.getPaidAt());
        verify(enrollmentRepository).save(existing);
    }

    // --------------------------------------------------------------------
    @Test
    void getEnrollment_shouldReturnEnrollment() {
        // ARRANGE
        User student = new User();
        Course course = new Course();

        Enrollment e = new Enrollment();

        when(enrollmentRepository.findByStudentAndCourse(student, course))
                .thenReturn(Optional.of(e));

        // ACT
        Optional<Enrollment> result = enrollmentService.getEnrollment(student, course);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals(e, result.get());
    }

    // --------------------------------------------------------------------
    @Test
    void getEnrollmentsForStudent_shouldReturnList() {
        // ARRANGE
        User student = new User();
        when(enrollmentRepository.findByStudent(student))
                .thenReturn(List.of(new Enrollment()));

        // ACT
        List<Enrollment> result = enrollmentService.getEnrollmentsForStudent(student);

        // ASSERT
        assertEquals(1, result.size());
    }

    // --------------------------------------------------------------------
    @Test
    void getEnrollmentsForCourse_shouldReturnList() {
        // ARRANGE
        Course course = new Course();
        when(enrollmentRepository.findByCourse(course))
                .thenReturn(List.of(new Enrollment()));

        // ACT
        List<Enrollment> result = enrollmentService.getEnrollmentsForCourse(course);

        // ASSERT
        assertEquals(1, result.size());
    }

    // --------------------------------------------------------------------
    @Test
    void deleteEnrollment_shouldCallRepository() {
        // ACT
        enrollmentService.deleteEnrollment(9L);

        // ASSERT
        verify(enrollmentRepository).deleteById(9L);
    }

    // --------------------------------------------------------------------
    @Test
    void countEnrollments_shouldReturnCount() {
        // ARRANGE
        when(enrollmentRepository.count()).thenReturn(15L);

        // ACT
        long result = enrollmentService.countEnrollments();

        // ASSERT
        assertEquals(15L, result);
    }

    // --------------------------------------------------------------------
    @Test
    void getCoursesByStudent_shouldMapEnrollmentsToCourses() {
        // ARRANGE
        User student = new User();
        Course c1 = new Course();
        Course c2 = new Course();

        Enrollment e1 = new Enrollment();
        e1.setCourse(c1);
        Enrollment e2 = new Enrollment();
        e2.setCourse(c2);

        when(enrollmentRepository.findByStudent(student))
                .thenReturn(List.of(e1, e2));

        // ACT
        List<Course> result = enrollmentService.getCoursesByStudent(student);

        // ASSERT
        assertEquals(List.of(c1, c2), result);
    }
}

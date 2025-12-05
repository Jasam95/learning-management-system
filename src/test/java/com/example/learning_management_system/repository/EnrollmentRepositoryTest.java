package com.example.learning_management_system.repository;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@Transactional
class EnrollmentRepositoryTest {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void saveAndFind_shouldWork() {

        User student = new User();
        student.setFullName("Student A");
        student.setEmail("stud@test.com");
        student.setPassword("pwd");
        student = userRepository.saveAndFlush(student);

        Course course = new Course();
        course.setTitle("Python");
        course.setDescription("Basic Python");
        course = courseRepository.saveAndFlush(course);

        Enrollment e = new Enrollment();
        e.setStudent(student);
        e.setCourse(course);
        e.setEnrolledOn(LocalDateTime.now());
        enrollmentRepository.saveAndFlush(e);

        // Test methods
        assertThat(enrollmentRepository.findByStudent(student)).hasSize(1);
        assertThat(enrollmentRepository.findByCourse(course)).hasSize(1);
        assertThat(enrollmentRepository.findByStudentAndCourse(student, course)).isPresent();
    }
}

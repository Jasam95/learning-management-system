package com.example.learning_management_system.repository;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
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
class CourseContentRepositoryTest {

    @Autowired
    private CourseContentRepository contentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByCourse_shouldWork() {

        // Create instructor
        User instructor = new User();
        instructor.setFullName("Instructor One");
        instructor.setEmail("instructor@test.com");
        instructor.setPassword("pass");
        instructor = userRepository.saveAndFlush(instructor);

        // Create course
        Course course = new Course();
        course.setTitle("Java Basics");
        course.setDescription("Learn Java");
        course.setInstructor(instructor);
        course = courseRepository.saveAndFlush(course);

        // Create content
        CourseContent content = new CourseContent();
        content.setTitle("Intro PDF");
        content.setContentType(CourseContent.ContentType.PDF);
        content.setContentUrl("/uploads/doc.pdf");
        content.setUploadedAt(LocalDateTime.now());
        content.setCourse(course);

        contentRepository.saveAndFlush(content);

        // Test repository method
        List<CourseContent> fetched = contentRepository.findByCourse(course);

        assertThat(fetched).hasSize(1);
        assertThat(fetched.get(0).getTitle()).isEqualTo("Intro PDF");
        assertThat(fetched.get(0).getCourse().getId()).isEqualTo(course.getId());
    }
}

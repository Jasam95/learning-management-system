package com.example.learning_management_system.repository;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@Transactional
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindByInstructor_shouldWork() {

        User instructor = new User();
        instructor.setFullName("Instructor");
        instructor.setEmail("inst@test.com");
        instructor.setPassword("123");
        instructor = userRepository.saveAndFlush(instructor);

        Course c = new Course();
        c.setTitle("Spring Boot");
        c.setDescription("Backend course");
        c.setInstructor(instructor);

        courseRepository.saveAndFlush(c);

        List<Course> fetched = courseRepository.findByInstructor(instructor);

        assertThat(fetched).hasSize(1);
        assertThat(fetched.get(0).getTitle()).isEqualTo("Spring Boot");
    }

    @Test
    void deleteCourse_shouldRemoveRecord() {
        Course c = new Course();
        c.setTitle("Delete Test");
        c.setDescription("Testing delete");
        courseRepository.saveAndFlush(c);

        courseRepository.deleteById(c.getId());

        assertThat(courseRepository.findById(c.getId())).isEmpty();
    }
}

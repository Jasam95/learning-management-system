package com.example.learning_management_system.repository;



import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {

    List<CourseContent> findByCourse(Course course);
}


package com.example.learning_management_system.repository;


import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByInstructor(User instructor);

    List<Course> findByPriceType(Course.PriceType priceType);

    List<Course> findByCreatedByAdmin(User admin);
}


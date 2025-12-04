package com.example.learning_management_system.service;


import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.User;
import java.util.List;
import java.util.Optional;

public interface CourseService {
    List<Course> getAllCourses();
    Optional<Course> getCourseById(Long id);
    void saveCourse(Course course);
    void deleteCourse(Long id);
    List<User> getAllInstructors();

    long countCourses();

    List<Course> getCoursesByInstructor(User instructor);


}


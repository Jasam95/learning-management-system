package com.example.learning_management_system.service.impl;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.CourseRepository;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);

    }

    @Override
    public void saveCourse(Course course) {
        courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    @Override
    public List<User> getAllInstructors() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase("ROLE_INSTRUCTOR")))
                .collect(Collectors.toList());
    }

    @Override
    public long countCourses() {
        return courseRepository.count();
    }

    @Override
    public List<Course> getCoursesByInstructor(User instructor) {
        return courseRepository.findByInstructor(instructor);
    }

}


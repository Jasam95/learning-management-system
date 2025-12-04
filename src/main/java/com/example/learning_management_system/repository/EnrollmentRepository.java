package com.example.learning_management_system.repository;


import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudent(User student);

    List<Enrollment> findByCourse(Course course);

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);




}

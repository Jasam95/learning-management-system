package com.example.learning_management_system.service;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    boolean isEnrolled(User student, Course course);
    /**
     * Enroll student in course.
     *
     * @param student the student user
     * @param course  the course
     * @param paid    whether the enrollment is paid (true) or free (false)
     */
    void enrollStudent(User student, Course course, boolean paid);

    Optional<Enrollment> getEnrollment(User student, Course course);

    List<Enrollment> getEnrollmentsForStudent(User student);

    List<Enrollment> getEnrollmentsForCourse(Course course);

    void deleteEnrollment(Long enrollmentId);

    long countEnrollments();

    List<Course> getCoursesByStudent(User student);
}


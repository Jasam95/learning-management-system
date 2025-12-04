package com.example.learning_management_system.service.impl;



import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.EnrollmentRepository;
import com.example.learning_management_system.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    @Override
    public boolean isEnrolled(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course).isPresent();
    }

    @Override
    @Transactional
    public void enrollStudent(User student, Course course, boolean paid) {
        // avoid duplicate enrollment
        Optional<Enrollment> existing = enrollmentRepository.findByStudentAndCourse(student, course);
        if (existing.isPresent()) {
            // optionally update paid flag if previously free and now paid
            Enrollment e = existing.get();
            if (!e.isPaid() && paid) {
                e.setPaid(true);
                e.setPaidAt(LocalDateTime.now());
                enrollmentRepository.save(e);
                return;
            }
            return;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrolledOn(LocalDateTime.now());
        enrollment.setPaid(paid);
        if (paid) {
            enrollment.setPaidAt(LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);
    }

    @Override
    public Optional<Enrollment> getEnrollment(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course);
    }

    @Override
    public List<Enrollment> getEnrollmentsForStudent(User student) {
        return enrollmentRepository.findByStudent(student);
    }

    @Override
    public List<Enrollment> getEnrollmentsForCourse(Course course) {
        return enrollmentRepository.findByCourse(course);
    }

    @Override
    public void deleteEnrollment(Long enrollmentId) {
         enrollmentRepository.deleteById( enrollmentId);

    }

    @Override
    public long countEnrollments() {
        return enrollmentRepository.count();
    }

    @Override
    public List<Course> getCoursesByStudent(User student) {
        return enrollmentRepository.findByStudent(student)
                .stream()
                .map(Enrollment::getCourse).toList();
    }
}


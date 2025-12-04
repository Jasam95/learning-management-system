package com.example.learning_management_system.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(nullable = false)
    private LocalDateTime enrolledOn = LocalDateTime.now();

    // indicates whether the student paid for the course (true) or free enrollment (true for free too)
    @Column(nullable = false)
    private boolean paid = false;

    // optional: when payment completed
    private LocalDateTime paidAt;
}


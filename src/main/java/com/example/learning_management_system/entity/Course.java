package com.example.learning_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    private PriceType priceType; // FREE or PAID

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;  // Assigned by admin

    @ManyToOne
    @JoinColumn(name = "created_by_admin_id")
    private User createdByAdmin; // Admin who created the course

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseContent> contents = new ArrayList<>();

    @Transient
    private boolean enrolled;


    public enum PriceType {
        FREE,
        PAID
    }

}

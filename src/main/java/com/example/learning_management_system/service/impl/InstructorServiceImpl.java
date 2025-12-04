package com.example.learning_management_system.service.impl;



import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.CourseContentRepository;
import com.example.learning_management_system.repository.CourseRepository;
import com.example.learning_management_system.service.FileStorageService;
import com.example.learning_management_system.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final CourseRepository courseRepository;
    private final CourseContentRepository contentRepository;
    private final FileStorageService fileStorageService;

    @Override
    public List<Course> getCoursesForInstructor(User instructor) {
        return courseRepository.findByInstructor(instructor);
    }

    @Override
    public Course getCourseIfBelongsToInstructor(Long courseId, User instructor) {
        Optional<Course> opt = courseRepository.findById(courseId);
        if (opt.isEmpty()) return null;
        Course course = opt.get();
        if (course.getInstructor() == null || course.getInstructor().getId()!= instructor.getId()) {
            return null;
        }
        return course;
    }

    @Override
    public List<CourseContent> getContentsForCourse(Course course) {
        return contentRepository.findByCourse(course);
    }

//    @Override
//    @Transactional
//    public CourseContent uploadContent(Long courseId, String title, CourseContent.ContentType contentType, MultipartFile file, User instructor) throws IOException {
//        Course course = getCourseIfBelongsToInstructor(courseId, instructor);
//        if (course == null) {
//            throw new IllegalArgumentException("Course not found or not assigned to instructor");
//        }
//
//        // store file
//        String savedPath = fileStorageService.storeFile(file, courseId,contentType);
//
//
//        CourseContent content = CourseContent.builder()
//                .title(title != null && !title.isBlank() ? title : file.getOriginalFilename())
//                .contentType(contentType)
//                .contentUrl(savedPath)
//                .course(course)
//                .build();
//
//        CourseContent saved = contentRepository.save(content);
//        course.getContents().add(saved);
//        courseRepository.save(course); // update relationship
//        return saved;
//    }

    @Override
    @Transactional
    public CourseContent uploadContent(Long courseId, String title, CourseContent.ContentType contentType, MultipartFile file, User instructor) throws IOException {
        Course course = getCourseIfBelongsToInstructor(courseId, instructor);
        if (course == null) {
            throw new IllegalArgumentException("Course not found or not assigned to instructor");
        }

        // Save file and get web path
        String savedPath = fileStorageService.storeFile(file, courseId, contentType);

        // Create manually instead of builder
        CourseContent content = new CourseContent();
        content.setTitle((title != null && !title.isBlank()) ? title : file.getOriginalFilename());
        content.setContentType(contentType);
        content.setContentUrl(savedPath);
        content.setUploadedAt(LocalDateTime.now());
        content.setCourse(course);

        // Save content
        CourseContent saved = contentRepository.save(content);

        // Optionally update course
        course.getContents().add(saved);
        courseRepository.save(course);

        return saved;
    }


    @Override
    @Transactional
    public void deleteContent(Long contentId, User instructor) {
        Optional<CourseContent> opt = contentRepository.findById(contentId);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Content not found");
        }
        CourseContent content = opt.get();
        Course course = content.getCourse();

        if (course == null || course.getInstructor().getId() != instructor.getId()) {
            throw new SecurityException("Not authorized to delete this content");
        }

        // delete file from disk
        try {
            fileStorageService.deleteFile(content.getContentUrl());
        } catch (Exception ex) {
            // log, but continue deletion of DB record
            ex.printStackTrace();
        }

        // remove from course list (if loaded) and delete
        contentRepository.delete(content);
    }
}


package com.example.learning_management_system.service;



import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
import com.example.learning_management_system.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface InstructorService {
    List<Course> getCoursesForInstructor(User instructor);
    Course getCourseIfBelongsToInstructor(Long courseId, User instructor);
    List<CourseContent> getContentsForCourse(Course course);
    CourseContent uploadContent(Long courseId, String title, CourseContent.ContentType contentType, MultipartFile file, User instructor) throws IOException;
    void deleteContent(Long contentId, User instructor);
}


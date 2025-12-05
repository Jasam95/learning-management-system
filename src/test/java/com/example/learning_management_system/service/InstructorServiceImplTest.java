package com.example.learning_management_system.service;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.CourseContentRepository;
import com.example.learning_management_system.repository.CourseRepository;
import com.example.learning_management_system.service.impl.InstructorServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class InstructorServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private CourseContentRepository contentRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private MultipartFile multipartFile;

    @InjectMocks
    private InstructorServiceImpl instructorService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // -------------------------------------------------------------------
    @Test
    void getCoursesForInstructor_shouldReturnInstructorCourses() {
        User instructor = new User();
        Course course = new Course();

        when(courseRepository.findByInstructor(instructor))
                .thenReturn(List.of(course));

        List<Course> result = instructorService.getCoursesForInstructor(instructor);

        assertEquals(1, result.size());
        verify(courseRepository).findByInstructor(instructor);
    }

    // -------------------------------------------------------------------
    @Test
    void getCourseIfBelongsToInstructor_shouldReturnCourse() {
        User instructor = new User();
        instructor.setId(1L);

        Course course = new Course();
        course.setInstructor(instructor);

        when(courseRepository.findById(10L))
                .thenReturn(Optional.of(course));

        Course result = instructorService.getCourseIfBelongsToInstructor(10L, instructor);

        assertNotNull(result);
        assertEquals(course, result);
    }

    // -------------------------------------------------------------------
    @Test
    void getCourseIfBelongsToInstructor_shouldReturnNull_ifUnauthorized() {
        User instructor = new User();
        instructor.setId(1L);

        User anotherInstructor = new User();
        anotherInstructor.setId(2L);

        Course course = new Course();
        course.setInstructor(anotherInstructor);

        when(courseRepository.findById(10L)).thenReturn(Optional.of(course));

        Course result = instructorService.getCourseIfBelongsToInstructor(10L, instructor);

        assertNull(result);
    }

    // -------------------------------------------------------------------
    @Test
    void getCourseIfBelongsToInstructor_shouldReturnNull_ifCourseNotFound() {
        when(courseRepository.findById(10L)).thenReturn(Optional.empty());

        Course result = instructorService.getCourseIfBelongsToInstructor(10L, new User());

        assertNull(result);
    }

    // -------------------------------------------------------------------
    @Test
    void getContentsForCourse_shouldReturnContentList() {
        Course course = new Course();
        CourseContent content = new CourseContent();

        when(contentRepository.findByCourse(course))
                .thenReturn(List.of(content));

        List<CourseContent> result = instructorService.getContentsForCourse(course);

        assertEquals(1, result.size());
        verify(contentRepository).findByCourse(course);
    }

    // -------------------------------------------------------------------
    @Test
    void uploadContent_shouldSaveFileAndCreateContent() throws IOException {

        // ARRANGE
        User instructor = new User();
        instructor.setId(1L);

        Course course = new Course();
        course.setId(10L);
        course.setInstructor(instructor);

        CourseContent.ContentType type = CourseContent.ContentType.IMAGE;

        when(courseRepository.findById(10L))
                .thenReturn(Optional.of(course));

        when(multipartFile.getOriginalFilename())
                .thenReturn("file.png");

        when(fileStorageService.storeFile(multipartFile, 10L, type))
                .thenReturn("/uploads/images/file.png");

        CourseContent saved = new CourseContent();
        saved.setId(99L);

        when(contentRepository.save(any(CourseContent.class)))
                .thenReturn(saved);

        // ACT
        CourseContent result = instructorService.uploadContent(10L, "Title", type, multipartFile, instructor);

        // ASSERT
        assertNotNull(result);
        verify(fileStorageService).storeFile(multipartFile, 10L, type);
        verify(contentRepository).save(any(CourseContent.class));
        verify(courseRepository).save(course);
    }

    // -------------------------------------------------------------------
    @Test
    void uploadContent_shouldThrow_ifNotInstructorCourse() {
        User instructor = new User(); instructor.setId(1L);
        User anotherTeacher = new User(); anotherTeacher.setId(2L);

        Course course = new Course();
        course.setInstructor(anotherTeacher);

        when(courseRepository.findById(10L))
                .thenReturn(Optional.of(course));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> instructorService.uploadContent(10L, "T", CourseContent.ContentType.IMAGE, multipartFile, instructor)
        );

        assertEquals("Course not found or not assigned to instructor", ex.getMessage());
    }

    // -------------------------------------------------------------------
    @Test
    void deleteContent_shouldDeleteContentSuccessfully() throws Exception {

        // ARRANGE
        User instructor = new User();
        instructor.setId(1L);

        Course course = new Course();
        course.setInstructor(instructor);

        CourseContent content = new CourseContent();
        content.setId(55L);
        content.setContentUrl("/uploads/x.png");
        content.setCourse(course);

        when(contentRepository.findById(55L)).thenReturn(Optional.of(content));

        // ACT
        instructorService.deleteContent(55L, instructor);

        // ASSERT
        verify(fileStorageService).deleteFile("/uploads/x.png");
        verify(contentRepository).delete(content);
    }

    // -------------------------------------------------------------------
    @Test
    void deleteContent_shouldThrow_ifContentNotFound() {
        when(contentRepository.findById(55L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> instructorService.deleteContent(55L, new User())
        );

        assertEquals("Content not found", ex.getMessage());
    }

    // -------------------------------------------------------------------
    @Test
    void deleteContent_shouldThrow_ifUnauthorized() {
        User instructor = new User(); instructor.setId(1L);

        User anotherInstructor = new User(); anotherInstructor.setId(2L);
        Course course = new Course(); course.setInstructor(anotherInstructor);

        CourseContent content = new CourseContent();
        content.setCourse(course);

        when(contentRepository.findById(55L)).thenReturn(Optional.of(content));

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> instructorService.deleteContent(55L, instructor)
        );

        assertEquals("Not authorized to delete this content", ex.getMessage());
    }
}

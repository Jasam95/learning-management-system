package com.example.learning_management_system.controller;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.CourseContent;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.InstructorService;
import com.example.learning_management_system.service.UserLoginService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class InstructorController {



    private final InstructorService instructorService;
    private final UserRepository userRepository;
    private final UserLoginService userLoginService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;


    @GetMapping("/instructor/dashboard")
    public String instructorDashboard(Model model, Principal principal) {

        User instructor = userLoginService.findByEmail(principal.getName());

        model.addAttribute("courses", courseService.getCoursesByInstructor(instructor));

        return "instructor/dashboard";
    }

//    @GetMapping("/course/{courseId}/students")
//    public String viewEnrolledStudents(
//            @PathVariable Long courseId,
//            Principal principal,
//            Model model) {
//
//        User instructor = userLoginService.findByEmail(principal.getName());
//        Course course = courseService.getCourseById(courseId).orElse(null);
//
//        if (course == null || course.getInstructor().getId() != instructor.getId()) {
//            return "error/403";
//        }
//
//        model.addAttribute("course", course);
//        model.addAttribute("enrollments", enrollmentService.getEnrollmentsForCourse(course));
//
//        return "instructor/enrolled-students";
//    }


    // list assigned courses
    @GetMapping("/instructor/courses")
    public String listCourses(@AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        User instructor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        List<Course> courses = instructorService.getCoursesForInstructor(instructor);
        model.addAttribute("courses", courses);
        return "instructor/courses";
    }

    // view course content and upload form
    @GetMapping("/instructor/course/{id}/content")
    public String viewCourseContent(@PathVariable Long id,
                                    @AuthenticationPrincipal UserDetails userDetails,
                                    Model model) {
        User instructor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        Course course = instructorService.getCourseIfBelongsToInstructor(id, instructor);
        if (course == null) {
            return "error/403"; // or redirect with message
        }
        List<CourseContent> contents = instructorService.getContentsForCourse(course);
        model.addAttribute("course", course);
        model.addAttribute("contents", contents);
        model.addAttribute("contentTypes", CourseContent.ContentType.values());
        return "instructor/course-content";
    }

    // upload content
    @PostMapping("/instructor/course/{id}/upload")
    public String uploadContent(@PathVariable Long id,
                                @RequestParam("title") String title,
                                @RequestParam("contentType") CourseContent.ContentType contentType,
                                @RequestParam("file") MultipartFile file,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User instructor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            instructorService.uploadContent(id, title, contentType, file, instructor);
            return "redirect:/instructor/course/" + id + "/content";
        } catch (IllegalArgumentException | IOException e) {
            model.addAttribute("error", e.getMessage());
            return "instructor/course-content";
        }
    }

    // delete content
    @GetMapping("/instructor/course/{courseId}/content/delete/{contentId}")
    public String deleteContent(@PathVariable Long courseId,
                                @PathVariable Long contentId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User instructor = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        try {
            instructorService.deleteContent(contentId, instructor);
        } catch (SecurityException | IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "error/403"; // or show error page
        }
        return "redirect:/instructor/course/" + courseId + "/content";
    }

    @GetMapping("/instructor/course/{courseId}/students")
    public String viewEnrolledStudents(@PathVariable Long courseId,
                                       Principal principal,
                                       Model model) {

        User instructor = userLoginService.findByEmail(principal.getName());
        Course course = courseService.getCourseById(courseId).orElse(null);

        if (course == null) {
            model.addAttribute("error", "Course not found");
            return "error/404";
        }

        // ensure instructor owns the course
        if (course.getInstructor().getId() != (instructor.getId())) {
            model.addAttribute("error", "You are not authorized to view this course.");
            return "error/403";
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForCourse(course);

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollments);

        return "instructor/students";  // thymeleaf view
    }

}





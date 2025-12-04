package com.example.learning_management_system.controller;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.UserLoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/students")
public class StudentController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final UserLoginService userService;

    @GetMapping("/courses")
    public String viewAllCourses(Model model, Principal principal) {

        User student = userService.findByEmail(principal.getName());
        List<Course> courses = courseService.getAllCourses();

        for (Course c : courses) {
            c.setEnrolled(enrollmentService.isEnrolled(student, c));
        }

        model.addAttribute("courses", courses);
        return "students/courses";
    }

    @GetMapping("/course/{courseId}/enroll-page")
    public String showEnrollPage(
            @PathVariable Long courseId,
            Model model,
            Principal principal
    ) {
        User student = userService.findByEmail(principal.getName());
        Course course = courseService.getCourseById(courseId).orElse(null);

        if (course == null) {
            model.addAttribute("error", "Course not found");
            return "error/404";
        }

        if (enrollmentService.isEnrolled(student, course)) {
            return "redirect:/students/course/" + courseId + "/content";
        }

        model.addAttribute("course", course);
        return "students/enroll-page";
    }

    @PostMapping("/course/{courseId}/enroll")
    public String confirmEnroll(
            @PathVariable Long courseId,
            Principal principal,
            Model model
    ) {
        User student = userService.findByEmail(principal.getName());
        Course course = courseService.getCourseById(courseId).orElse(null);

        if (course == null) {
            model.addAttribute("error", "Course not found");
            return "error/404";
        }

        if (course.getPriceType() == Course.PriceType.FREE) {
            enrollmentService.enrollStudent(student, course, true);
            return "redirect:/students/course/" + courseId + "/content";
        }

        return "redirect:/students/course/" + courseId + "/payment";
    }

    @GetMapping("/course/{courseId}/payment")
    public String paymentPage(@PathVariable Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId).orElse(null);

        if (course == null) {
            model.addAttribute("error", "Course not found");
            return "error/404";
        }

        model.addAttribute("course", course);
        return "students/payment";
    }

    @PostMapping("/course/{courseId}/payment/success")
    public String paymentSuccess(
            @PathVariable Long courseId,
            Principal principal
    ) {
        User student = userService.findByEmail(principal.getName());
        Course course = courseService.getCourseById(courseId).orElseThrow();

        enrollmentService.enrollStudent(student, course, true);
        return "redirect:/students/course/" + courseId + "/content";
    }

    @GetMapping("/course/{courseId}/content")
    public String viewCourseContent(
            @PathVariable Long courseId,
            Principal principal,
            Model model
    ) {
        User student = userService.findByEmail(principal.getName());
        Course course = courseService.getCourseById(courseId).orElse(null);

        if (course == null) {
            model.addAttribute("error", "Course not found");
            return "error/404";
        }

        Enrollment enrollment = enrollmentService.getEnrollment(student, course)
                .orElse(null);

        if (enrollment == null) {
            return "redirect:/students/courses?error=not-enrolled";
        }

        model.addAttribute("course", course);
        model.addAttribute("contents", course.getContents());
        return "students/course-content";
    }

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("student", student);
        return "students/dashboard";
    }

    @GetMapping("/my_courses")
    public String myCourses(Model model, Principal principal) {
        User student = userService.findByEmail(principal.getName());
        model.addAttribute("courses",
                enrollmentService.getEnrollmentsForStudent(student)
                        .stream().map(Enrollment::getCourse).toList());
        return "students/my-courses";
    }

}

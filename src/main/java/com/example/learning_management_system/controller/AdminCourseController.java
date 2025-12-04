package com.example.learning_management_system.controller;

import com.example.learning_management_system.dto.UserDto;
import com.example.learning_management_system.entity.Enrollment;
import com.example.learning_management_system.exception.CourseNotFoundException;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.UserLoginService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor
public class AdminCourseController {

    private final CourseService courseService;
    private final UserLoginService userLoginService;
    private final UserRepository userRepository;
    private final EnrollmentService enrollmentService;


    @GetMapping({"/admin/courses"})
    public String listCourses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "admin/courses";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model,
                                 @AuthenticationPrincipal UserDetails userDetails) {


        List<UserDto> instructor = userLoginService.findAllUsers().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("ROLE_INSTRUCTOR")))
                .toList();

        List<UserDto> student = userLoginService.findAllUsers().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> r.getName().equals("ROLE_STUDENT")))
                .toList();

        model.addAttribute("totalInstructors", instructor.size());
        model.addAttribute("totalStudents", student.size());
        model.addAttribute("totalCourses", courseService.countCourses());
        model.addAttribute("totalEnrollments", enrollmentService.countEnrollments());

        return "admin/dashboard";
    }

    @GetMapping("/admin/courses/create")
    public String showCreateForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("instructors", courseService.getAllInstructors());
        model.addAttribute("priceTypes", Course.PriceType.values());
        return "admin/course-form";
    }

    @PostMapping("/admin/courses/save")
    public String saveCourse(@ModelAttribute("course") Course course,
                             @RequestParam("instructorId") Long instructorId,
                             @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        User instructor = userRepository.findById(instructorId).orElseThrow();

        course.setCreatedByAdmin(admin);
        course.setInstructor(instructor);
        courseService.saveCourse(course);

        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/edit/{id}")
    public String editCourse(@PathVariable Long id, Model model) {

        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with ID " + id + " not found"));

        model.addAttribute("course", course);
        model.addAttribute("instructors", courseService.getAllInstructors());
        model.addAttribute("priceTypes", Course.PriceType.values());
        return "admin/course-form";
    }

    @GetMapping("/admin/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/courses";
    }

    @GetMapping("/admin/courses/view/{id}")
    public String viewCourse(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course with ID " + id + " not found"));

        model.addAttribute("course", course);
        return "admin/course-view";
    }

    @GetMapping("/admin/courses/{courseId}/students")
    public String manageCourseStudents(@PathVariable Long courseId, Model model) {

        Course course = courseService.getCourseById(courseId).orElse(null);

        if (course == null) {
            model.addAttribute("error", "Course not found");
            return "error/404";
        }

        List<Enrollment> enrollments = enrollmentService.getEnrollmentsForCourse(course);

        model.addAttribute("course", course);
        model.addAttribute("enrollments", enrollments);

        return "admin/course-students";
    }

    @PostMapping("/admin/courses/{courseId}/students/{enrollmentId}/remove")
    public String removeEnrollment(@PathVariable Long courseId,
                                   @PathVariable Long enrollmentId) {

        enrollmentService.deleteEnrollment(enrollmentId);

        return "redirect:/admin/courses/" + courseId + "/students";
    }


}



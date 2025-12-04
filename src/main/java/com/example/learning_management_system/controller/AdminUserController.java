package com.example.learning_management_system.controller;

import com.example.learning_management_system.dto.UserDto;
import com.example.learning_management_system.entity.Course;
import com.example.learning_management_system.entity.Role;
import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.CourseRepository;
import com.example.learning_management_system.repository.RoleRepository;
import com.example.learning_management_system.repository.UserRepository;
import com.example.learning_management_system.service.CourseService;
import com.example.learning_management_system.service.EnrollmentService;
import com.example.learning_management_system.service.UserLoginService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminUserController {

    private final UserLoginService userLogInService;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentService enrollmentService;
    private final CourseService courseService;

    // --- STUDENTS ---
    @GetMapping("/students")
    public String listStudents(Model model) {
        List<User> students = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream()
                        .anyMatch(r -> "ROLE_STUDENT".equalsIgnoreCase(r.getName())))
                .collect(Collectors.toList());
        model.addAttribute("students", students);
        return "admin/students";
    }

    @GetMapping("/students/create")
    public String showCreateStudent(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("actionUrl", "/admin/students/save");
        return "register";
    }

    @PostMapping("/students/save")
    public String saveStudent(@Valid @ModelAttribute("userDto") UserDto userDto,
                RedirectAttributes redirectAttrs,
                BindingResult result,
                Model model) {
            if (result.hasErrors()) {
                return "register";
            }
            try {
                String roles = "ROLE_STUDENT";
                userLogInService.createUser(userDto ,roles);
                redirectAttrs.addFlashAttribute("registered", true);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("error", ex.getMessage());
                return "register";
            }
            return "redirect:/admin/students";
    }


    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/students";
    }


    @GetMapping("/students/view/{id}")
    public String viewStudent(@PathVariable Long id, Model model) {

        User student = userRepository.findById(id).orElseThrow();

        // registered courses
        List<Course> registered = enrollmentService.getCoursesByStudent(student);

        // all courses
        List<Course> allCourses = courseService.getAllCourses();

        // not registered courses
        List<Course> notRegistered =
                allCourses.stream()
                        .filter(c -> registered.stream().noneMatch(r -> r.getId().equals(c.getId())))
                        .toList();

        model.addAttribute("student", student);
        model.addAttribute("registeredCourses", registered);
        model.addAttribute("notRegisteredCourses", notRegistered);

        return "admin/student-view";
    }


    // --- INSTRUCTORS ---
    @GetMapping("/instructors")
    public String listInstructors(Model model) {
        List<User> instructors = userRepository.findAll().stream()
                .filter(u -> u.getRoles().stream().anyMatch(r -> "ROLE_INSTRUCTOR".equalsIgnoreCase(r.getName())))
                .collect(Collectors.toList());
        model.addAttribute("instructors", instructors);
        return "admin/instructors";
    }

    @GetMapping("/instructors/create")
    public String showCreateInstructor(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("actionUrl", "/admin/instructors/save");
        return "register";
    }

    @PostMapping("/instructors/save")
    public String saveInstructor(@Valid @ModelAttribute("userDto") UserDto userDto,
                                 RedirectAttributes redirectAttrs,
                                 BindingResult result,
                                 Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            String roles = "ROLE_INSTRUCTOR";
            userLogInService.createUser(userDto ,roles);
            redirectAttrs.addFlashAttribute("registered", true);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
        return "redirect:/admin/instructors";
    }


    @GetMapping("/instructors/delete/{id}")
    public String deleteInstructor(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/instructors";
    }

    @GetMapping("/instructors/view/{id}")
    public String viewInstructor(@PathVariable Long id, Model model) {
        User instructor = userRepository.findById(id).orElseThrow();
        List<Course> courses = courseRepository.findByInstructor(instructor);

        model.addAttribute("courses", courses);
        model.addAttribute("instructor", instructor);
        return "admin/instructor-view";
    }
}

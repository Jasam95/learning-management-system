package com.example.learning_management_system.controller;

import com.example.learning_management_system.dto.UserDto;


import com.example.learning_management_system.service.UserLoginService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;



@Controller
@AllArgsConstructor
public class AuthController {

    private UserLoginService userLogInService;


    @GetMapping({"/", "/index"})
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("actionUrl", "/register");
        model.addAttribute("isAdminCreate",false);
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("userDto") UserDto userDto,
                             RedirectAttributes redirectAttrs,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            String roles = "ROLE_ADMIN";
            userLogInService.createUser(userDto ,roles);
            redirectAttrs.addFlashAttribute("registered", true);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "register";
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String userEmail =userDetails.getUsername();
        UserDto userDto =userLogInService.findUserByEmail(userEmail);
        model.addAttribute("user", userDto);
        model.addAttribute("role", userDetails.getAuthorities().toString());
        return "profile";
    }

}

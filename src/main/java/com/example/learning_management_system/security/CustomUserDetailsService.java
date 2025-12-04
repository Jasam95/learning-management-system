package com.example.learning_management_system.security;


import com.example.learning_management_system.entity.User;
import com.example.learning_management_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            User user = userRepository.findByEmail(email).orElseThrow();

            Set<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
            System.out.println(authorities.stream().map(GrantedAuthority::getAuthority));

            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),    // username
                    user.getPassword(), // password
                    authorities         // Roles (ROLE_ADMIN , ROLES_STUDENT)
            );
        }
}


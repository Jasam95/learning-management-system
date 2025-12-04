package com.example.learning_management_system.exception;

public class CourseNotFoundException extends RuntimeException{

        public CourseNotFoundException(String message) {
            super(message);
        }

}

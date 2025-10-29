package it.objectmethod.demo.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application class for the Library Management System.
 * 
 * This is the entry point of the Spring Boot application that provides
 * REST API services for managing books and library members.
 *
 * @author Library Management System
 * @version 1.0
 */
@SpringBootApplication
public class Application {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}

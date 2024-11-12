package main.java.controller;

import main.java.service.BookingSystem;
import java.util.Scanner;

public class RequestHandler {
    private BookingSystem bookingSystem;
    private Scanner scanner;

    public  RequestHandler(BookingSystem bookingSystem) {
        this.bookingSystem = bookingSystem;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the Transport Booking System");
        boolean running = true;

        while (running) {
            System.out.println("\nPlease select an option:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. View Transports");
            System.out.println("4. View Destinations");
            System.out.println("5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    viewTransports();
                    break;
                case 4:
                    viewDestinations();
                    break;
                case 5:
                    running = false;
                    System.out.println("Exiting the system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        boolean success = bookingSystem.registerUser(username, email, password);
        if (success) {
            System.out.println("Registration successful!");
        } else {
            System.out.println("Registration failed. User may already exist.");
        }
    }
    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (bookingSystem.checkLoginCredentials(username, password)) {
            System.out.println("Login successful!");
            // Further logic for logged-in users can be added here
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private void viewTransports() {
        System.out.println("\n--- Available Transports ---");
        bookingSystem.getAllTransports().forEach(System.out::println);
    }

    private void viewDestinations() {
        System.out.println("\n--- Available Destinations ---");
        bookingSystem.getLocations().forEach(System.out::println);
    }
}
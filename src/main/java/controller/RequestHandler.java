package main.java.controller;

import main.java.model.Person;
import main.java.service.BookingSystem;

public class RequestHandler {

    private final BookingSystem bookingSystem;

    public RequestHandler(BookingSystem system) {this.bookingSystem = system;}

    public void registerAsCostumer(String name, String email, String password) {
        boolean success = bookingSystem.registerUser(name, email, password, false);
        if (success) {
            System.out.println("Registration successful! Welcome, "+name+"!");
        } else {
            System.out.println("Registration failed. User may already exist.");
        }
    }

    public void registerAsAdministrator(String name, String email, String password) {
        boolean success = bookingSystem.registerUser(name, email, password, true);
        if (success) {
            System.out.println("Admin registration successful! Welcome, "+name+"!");
        } else {
            System.out.println("Registration failed. User may already exist.");
        }
    }

    public Person login(String email, String password) {
        Person loggedin = bookingSystem.checkLoginCredentials(email, password);
        if (loggedin != null) {
            System.out.println("Login successful! Welcome, "+loggedin.getUsername()+"!");
            return loggedin;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return null;
        }
    }

    public void viewAllTransports() {
        StringBuilder out = new StringBuilder("Available transports:\n");
        bookingSystem.getAllTransports().forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    public void viewAllDestinations() {
        StringBuilder out = new StringBuilder("Available destinations:\n");
        bookingSystem.getLocations().forEach(location -> out.append(location.toString()).append("\n"));
        System.out.println(out);

    }


    /*
    register
    login
    exit

    viewBalance
    addBalance
    buyTicket
    viewTickets
    cancelTicket
    addLocation
    addBusTransport
    addTrainTransport
    cancelTransport
    showAllTickets
     */

}
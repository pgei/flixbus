package main.java.controller;

import main.java.model.Administrator;
import main.java.model.Costumer;
import main.java.model.Person;
import main.java.model.Ticket;
import main.java.service.BookingSystem;

import java.util.List;

public class RequestHandler {

    private final BookingSystem bookingSystem;

    public RequestHandler(BookingSystem system) {this.bookingSystem = system;}

    public void registerAsCostumer(String name, String email, String password) {
        boolean success = bookingSystem.registerUser(name, email, password, false);
        if (success) {
            System.out.println("Registration as costumer successful! Welcome, "+name+"!");
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
        StringBuilder out = new StringBuilder("--- Available transports ---\n");
        bookingSystem.getAllTransports().forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    public void filterByLocation(int origin, int destination) {
        StringBuilder out = new StringBuilder("--- Available transports from ");
        if (origin == -1) {
            out.append("any location to ");
        } else {
            out.append("location with ID ").append(origin).append(" to ");
        }
        if (destination == -1) {
            out.append("any location ---\n");
        } else {
            out.append("location with ID ").append(destination).append(" ---\n");
        }
        bookingSystem.getTransportsFilteredByLocation(origin, destination).forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    public void filterByPrice(int price) {
        StringBuilder out = new StringBuilder("--- Available transports with a maximum price of "+price+" Euro ---\n");
        bookingSystem.getTransportsFilteredByPrice(price).forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    public void viewAllDestinations() {
        StringBuilder out = new StringBuilder("--- Available destinations ---\n");
        bookingSystem.getLocations().forEach(location -> out.append(location.toString()).append("\n"));
        System.out.println(out);
    }

    public void viewBalance(Costumer costumer) {
        System.out.println("You have a total of "+ bookingSystem.getBalance(costumer)+ " Euros in your account.");
    }

    public void addBalance(Costumer costumer, int amount) {
        bookingSystem.addBalance(costumer, amount);
        System.out.println("Added "+ amount+ " Euros to your acccount, you now have a total of "+ bookingSystem.getBalance(costumer)+" Euros.");
    }

    public void viewTickets(Costumer costumer) {
        List<Ticket> list = bookingSystem.getALlTickets(costumer);
        System.out.println("Here is a list of all tickets you have reserved at this moment:\n\n");
        list.forEach(ticket -> System.out.println(ticket.toString()));
    }

    public void buyTicket(Costumer costumer, int transportid, int ticketclass) {
        boolean success = bookingSystem.createTicket(costumer, transportid, ticketclass);
        if (success) {
            System.out.println("Successfully reserved a ticket on Transport with Id "+transportid+"!");
        } else {
            System.out.println("Could not reserve a ticket, check the transport Id for correctness. Also there could be no seats left or your account balance is too low.");
        }
    }

    public void cancelTicket(Costumer costumer, int ticketid) {
        boolean success = bookingSystem.removeTicket(costumer, ticketid);
        if (success) {
            System.out.println("Successfully cancelled ticket with TicketNr "+ticketid+"!");
        } else {
            System.out.println("Could not cancel the ticket, please check that the ticket is yours and the TicketNr. is correct.");
        }
    }

    public void addLocation(Administrator admin, String street, String city) {
        boolean success = bookingSystem.createLocation(admin, street, city);
        if (success) {
            System.out.println("Successfully created new location!");
        } else {
            System.out.println("Could not create the location, you are no administrator!");
        }
    }

    public void addBusTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) {
        boolean success = bookingSystem.createBusTransport(admin, originid, destinationid, year, month, day, hourd, mind, houra, mina, capacity);
        if (success) {
            System.out.println("Successfully created new Bustransport!");
        } else {
            System.out.println("Could not create the transport, either a location ID is wrong or you are not an administrator.");
        }
    }

    public void addTrainTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) {
        boolean success = bookingSystem.createTrainTransport(admin, originid, destinationid, year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
        if (success) {
            System.out.println("Successfully created new Traintransport!");
        } else {
            System.out.println("Could not create the transport, either a location ID is wrong or you are not an administrator.");
        }
    }

    public void cancelTransport(Administrator admin, int transportid) {
        boolean success = bookingSystem.removeTransport(admin, transportid);
        if (success) {
            System.out.println("Successfully removed transport with ID "+transportid+". Costumers have been refunded.");
        } else {
            System.out.println("Could not cancel the transport, either the transport ID is wrong or you are not the administrator that created this transport.");
        }
    }

    public void showAllTickets(Administrator admin, int transportid) {
        List<Ticket> list = bookingSystem.getAllTransportTickets(admin, transportid);
        if (list != null) {
            System.out.println("Here is a list of all tickets reserved on transport with ID "+ transportid +":\n\n");
            list.forEach(ticket -> System.out.println(ticket.toStringReducedView()));
        } else {
            System.out.println("Cannot show tickets, either the transport ID is wrong or you are not an administrator.");
        }
    }
}
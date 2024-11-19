package main.java.controller;

import main.java.model.*;
import main.java.service.BookingSystem;

import java.util.List;

/**
 * Der RequestHandler dient als Controller, um Benutzeranfragen zu verarbeiten und diese an das BookingSystem
 * weiterzuleiten. Er bietet Methoden zur Verwaltung von Registrierungen, Anmeldungen, Anzeige von Informationen,
 * Sortierung und anderen Aktionen im Buchungssystem.
 */
public class RequestHandler {

    private final BookingSystem bookingSystem;

    /**
     * Konstruktor, um den RequestHandler mit einem BookingSystem zu initialisieren.
     *
     * @param system das BookingSystem, mit dem der RequestHandler kommuniziert.
     */
    public RequestHandler(BookingSystem system) {
        this.bookingSystem = system;
    }

    /**
     * Registriert einen Benutzer als Kunde.
     *
     * @param name     der Name des Benutzers.
     * @param email    die E-Mail-Adresse des Benutzers.
     * @param password das Passwort des Benutzers.
     */
    public void registerAsCostumer(String name, String email, String password) {
        boolean success = bookingSystem.registerUser(name, email, password, false);
        if (success) {
            System.out.println("Registration as costumer successful! Welcome, " + name + "!");
        } else {
            System.out.println("Registration failed. User may already exist.");
        }
    }

    /**
     * Registriert einen Benutzer als Administrator.
     *
     * @param name     der Name des Benutzers.
     * @param email    die E-Mail-Adresse des Benutzers.
     * @param password das Passwort des Benutzers.
     */
    public void registerAsAdministrator(String name, String email, String password) {
        boolean success = bookingSystem.registerUser(name, email, password, true);
        if (success) {
            System.out.println("Admin registration successful! Welcome, " + name + "!");
        } else {
            System.out.println("Registration failed. User may already exist.");
        }
    }

    /**
     * Führt die Anmeldung eines Benutzers durch.
     *
     * @param email    die E-Mail-Adresse des Benutzers.
     * @param password das Passwort des Benutzers.
     * @return das angemeldete Benutzerobjekt oder null, falls die Anmeldedaten ungültig sind.
     */
    public Person login(String email, String password) {
        Person loggedin = bookingSystem.checkLoginCredentials(email, password);
        if (loggedin != null) {
            System.out.println("Login successful! Welcome, " + loggedin.getUsername() + "!");
            return loggedin;
        } else {
            System.out.println("Invalid credentials. Please try again.");
            return null;
        }
    }

    /**
     * Zeigt alle verfügbaren Transporte an.
     */
    public void viewAllTransports() {
        StringBuilder out = new StringBuilder("--- View transports ---\n");
        bookingSystem.getAllTransports().forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Filtert die verfügbaren Transporte basierend auf Start- und Zielorten.
     *
     * @param origin      die ID des Startortes (-1 für beliebig).
     * @param destination die ID des Zielortes (-1 für beliebig).
     */
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

    /**
     * Filtert verfügbare Transporte basierend auf dem maximalen Preis.
     *
     * @param price der maximale Preis in Euro.
     */
    public void filterByPrice(int price) {
        StringBuilder out = new StringBuilder("--- Available transports with a maximum price of " + price + " Euro ---\n");
        bookingSystem.getTransportsFilteredByPrice(price).forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Sortiert und gibt die Transporte nach Datum aus.
     *
     * @return die Liste der nach Datum sortierten Transporte.
     */
    public List<Transport> sortTransportsByDate() {
        return bookingSystem.getTransportsSortedByDate();
    }

    /**
     * Sortiert und gibt die Transporte basierend auf der Dauer (absteigend) aus.
     *
     * @return die Liste der nach Dauer sortierten Transporte.
     */
    public List<Transport> sortTransportsByDuration() {
        return bookingSystem.getTransportsSortedByDuration();
    }

    /**
     * Zeigt alle verfügbaren Zielorte an.
     */
    public void viewAllDestinations() {
        StringBuilder out = new StringBuilder("--- Available destinations ---\n");
        bookingSystem.getLocations().forEach(location -> out.append(location.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Zeigt das Guthaben eines Kunden an.
     *
     * @param costumer der Kunde, dessen Guthaben angezeigt werden soll.
     */
    public void viewBalance(Costumer costumer) {
        System.out.println("You have a total of " + bookingSystem.getBalance(costumer) + " Euros in your account.");
    }

    /**
     * Fügt Guthaben zum Konto eines Kunden hinzu.
     *
     * @param costumer der Kunde, dem Guthaben hinzugefügt wird.
     * @param amount   der hinzuzufügende Betrag in Euro.
     */
    public void addBalance(Costumer costumer, int amount) {
        bookingSystem.addBalance(costumer, amount);
        System.out.println("Added " + amount + " Euros to your acccount, you now have a total of " + bookingSystem.getBalance(costumer) + " Euros.");
    }

    /**
     * Zeigt alle Tickets eines Kunden an.
     *
     * @param costumer der Kunde, dessen Tickets angezeigt werden sollen.
     */
    public void viewTickets(Costumer costumer) {
        List<Ticket> list = bookingSystem.getALlTickets(costumer);
        System.out.println("Here is a list of all tickets you have reserved at this moment:\n\n");
        list.forEach(ticket -> System.out.println(ticket.toString()));
    }

    /**
     * Kauft ein Ticket für einen bestimmten Transport und eine bestimmte Klasse.
     *
     * @param costumer    der Kunde, der das Ticket kauft.
     * @param transportid die ID des Transports.
     * @param ticketclass die Ticketklasse (z.B. 1. oder 2. Klasse).
     */
    public void buyTicket(Costumer costumer, int transportid, int ticketclass) {
        boolean success = bookingSystem.createTicket(costumer, transportid, ticketclass);
        if (success) {
            System.out.println("Successfully reserved a ticket on Transport with Id " + transportid + "!");
        } else {
            System.out.println("Could not reserve a ticket, check the transport Id for correctness. Also there could be no seats left or your account balance is too low.");
        }
    }

    /**
     * Storniert ein Ticket eines Kunden.
     *
     * @param costumer der Kunde, der das Ticket stornieren möchte.
     * @param ticketid die ID des Tickets.
     */
    public void cancelTicket(Costumer costumer, int ticketid) {
        boolean success = bookingSystem.removeTicket(costumer, ticketid);
        if (success) {
            System.out.println("Successfully cancelled ticket with TicketNr " + ticketid + "!");
        } else {
            System.out.println("Could not cancel the ticket, please check that the ticket is yours and the TicketNr. is correct.");
        }
    }

    /**
     * Fügt einen neuen Ort hinzu.
     *
     * @param admin  der Administrator, der den Ort hinzufügt.
     * @param street die Straße des Ortes.
     * @param city   die Stadt des Ortes.
     */
    public void addLocation(Administrator admin, String street, String city) {
        boolean success = bookingSystem.createLocation(admin, street, city);
        if (success) {
            System.out.println("Successfully created new location!");
        } else {
            System.out.println("Could not create the location, you are no administrator!");
        }
    }

    /**
     * Fügt einen neuen Bus-Transport hinzu.
     *
     * @param admin         der Administrator, der den Transport hinzufügt.
     * @param originid      die ID des Startortes.
     * @param destinationid die ID des Zielortes.
     * @param year          das Jahr der Abfahrt.
     * @param month         der Monat der Abfahrt.
     * @param day           der Tag der Abfahrt.
     * @param hourd         die Stunde der Abfahrt.
     * @param mind          die Minute der Abfahrt.
     * @param houra         die Stunde der Ankunft.
     * @param mina          die Minute der Ankunft.
     * @param capacity      die Kapazität des Busses.
     */
    public void addBusTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) {
        boolean success = bookingSystem.createBusTransport(admin, originid, destinationid, year, month, day, hourd, mind, houra, mina, capacity);
        if (success) {
            System.out.println("Successfully created new Bustransport!");
        } else {
            System.out.println("Could not create the transport, either a location ID is wrong or you are not an administrator.");
        }
    }

    /**
     * Fügt einen neuen Zug-Transport hinzu.
     *
     * @param admin          der Administrator, der den Transport hinzufügt.
     * @param originid       die ID des Startortes.
     * @param destinationid  die ID des Zielortes.
     * @param year           das Jahr der Abfahrt.
     * @param month          der Monat der Abfahrt.
     * @param day            der Tag der Abfahrt.
     * @param hourd          die Stunde der Abfahrt.
     * @param mind           die Minute der Abfahrt.
     * @param houra          die Stunde der Ankunft.
     * @param mina           die Minute der Ankunft.
     * @param firstcapacity  die Kapazität der 1. Klasse.
     * @param secondcapacity die Kapazität der 2. Klasse.
     */
    public void addTrainTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) {
        boolean success = bookingSystem.createTrainTransport(admin, originid, destinationid, year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
        if (success) {
            System.out.println("Successfully created new Traintransport!");
        } else {
            System.out.println("Could not create the transport, either a location ID is wrong or you are not an administrator.");
        }
    }

    /**
     * Storniert einen Transport.
     *
     * @param admin       der Administrator, der den Transport storniert.
     * @param transportid die ID des Transports.
     */
    public void cancelTransport(Administrator admin, int transportid) {
        boolean success = bookingSystem.removeTransport(admin, transportid);
        if (success) {
            System.out.println("Successfully removed transport with ID " + transportid + ". Costumers have been refunded.");
        } else {
            System.out.println("Could not cancel the transport, either the transport ID is wrong or you are not the administrator that created this transport.");
        }
    }

    /**
     * Zeigt alle Tickets für einen bestimmten Transport an.
     *
     * @param admin       der Administrator, der die Tickets anzeigen möchte.
     * @param transportid die ID des Transports.
     */
    public void showAllTickets(Administrator admin, int transportid) {
        List<Ticket> list = bookingSystem.getAllTransportTickets(admin, transportid);
        if (list != null) {
            System.out.println("Here is a list of all tickets reserved on transport with ID " + transportid + ":\n\n");
            list.forEach(ticket -> System.out.println(ticket.toStringReducedView()));
        } else {
            System.out.println("Cannot show tickets, either the transport ID is wrong or you are not an administrator.");
        }
    }
}
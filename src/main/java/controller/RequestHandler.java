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
     * @param system BookingSystem, mit dem der RequestHandler kommuniziert.
     */
    public RequestHandler(BookingSystem system) {
        this.bookingSystem = system;
    }

    /**
     * Registriert einen Benutzer als Kunde.
     *
     * @param name     Name des Benutzers.
     * @param email    E-Mail-Adresse des Benutzers.
     * @param password Passwort des Benutzers.
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
     * @param name     Name des Benutzers.
     * @param email    E-Mail-Adresse des Benutzers.
     * @param password Passwort des Benutzers.
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
     * @param email     E-Mail-Adresse des Benutzers.
     * @param password  Passwort des Benutzers.
     * @return          angemeldetes Person-Objekt oder null, falls die Anmeldedaten ungültig sind.
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
     * Zeigt alle Transporte im Repository an.
     */
    public void viewAllTransports() {
        StringBuilder out = new StringBuilder("--- View transports ---\n");
        bookingSystem.getAllTransports().forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Filtert die verfügbaren Transporte basierend auf Start- und Zielorten.
     *
     * @param origin      ID des Startortes (-1 für beliebig).
     * @param destination ID des Zielortes (-1 für beliebig).
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
     * @param price maximaler Preis in Euro.
     */
    public void filterByPrice(int price) {
        StringBuilder out = new StringBuilder("--- Available transports with a maximum price of " + price + " Euro ---\n");
        bookingSystem.getTransportsFilteredByPrice(price).forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Gibt Transporte in aufsteigender Reihenfolge sortiert nach Datum, und zweitrangig Abfahrtsuhrzeit, aus.
     *
     */
    public void sortTransportsByDate() {
        StringBuilder out = new StringBuilder("--- Transports sorted by date (and departure time) ---\n");
        bookingSystem.getTransportsSortedByDate().forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Sortiert und gibt die Transporte basierend auf der Dauer in aufsteigender Reihenfolge aus.
     *
     */
    public void sortTransportsByDuration() {
        StringBuilder out = new StringBuilder("--- Transports sorted by duration (ascending) ---\n");
        bookingSystem.getTransportsSortedByDuration().forEach(transport -> out.append(transport.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Zeigt alle verfügbaren Zielorte im Repository an.
     */
    public void viewAllDestinations() {
        StringBuilder out = new StringBuilder("--- Available destinations ---\n");
        bookingSystem.getLocations().forEach(location -> out.append(location.toString()).append("\n"));
        System.out.println(out);
    }

    /**
     * Zeigt das Guthaben eines Kunden an.
     *
     * @param costumer Kunde, dessen Guthaben angezeigt werden soll.
     */
    public void viewBalance(Costumer costumer) {
        System.out.println("You have a total of " + bookingSystem.getBalance(costumer) + " Euros in your account.");
    }

    /**
     * Fügt Guthaben zum Konto eines Kunden hinzu.
     *
     * @param costumer Kunde, dem Guthaben hinzugefügt wird.
     * @param amount   hinzuzufügender Betrag in Euro.
     */
    public void addBalance(Costumer costumer, int amount) {
        bookingSystem.addBalance(costumer, amount);
        System.out.println("Added " + amount + " Euros to your acccount, you now have a total of " + bookingSystem.getBalance(costumer) + " Euros.");
    }

    /**
     * Zeigt alle Tickets eines Kunden an.
     *
     * @param costumer Kunde, dessen Tickets angezeigt werden sollen.
     */
    public void viewTickets(Costumer costumer) {
        List<Ticket> list = bookingSystem.getALlTickets(costumer);
        System.out.println("Here is a list of all tickets you have reserved at this moment:\n\n");
        list.forEach(ticket -> System.out.println(ticket.toString()));
    }

    /**
     * Kauft ein Ticket für einen bestimmten Transport und eine bestimmte Klasse.
     *
     * @param costumer      Kunde, der das Ticket kauft.
     * @param transportid   ID des Transports.
     * @param ticketclass   Ticketklasse (z.B. 1. oder 2. Klasse).
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
     * @param costumer  Kunde, der das Ticket stornieren möchte.
     * @param ticketid  ID des Tickets.
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
     * @param admin  Administrator, der den Ort hinzufügt.
     * @param street Straße des Ortes.
     * @param city   Stadt des Ortes.
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
     * @param admin         Administrator, der den Transport hinzufügt.
     * @param originid      ID des Startortes.
     * @param destinationid ID des Zielortes.
     * @param year          Jahr der Abfahrt.
     * @param month         Monat der Abfahrt.
     * @param day           Tag der Abfahrt.
     * @param hourd         Stunde der Abfahrt.
     * @param mind          Minute der Abfahrt.
     * @param houra         Stunde der Ankunft.
     * @param mina          Minute der Ankunft.
     * @param capacity      Kapazität des Busses.
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
     * @param admin          Administrator, der den Transport hinzufügt.
     * @param originid       ID des Startortes.
     * @param destinationid  ID des Zielortes.
     * @param year           Jahr der Abfahrt.
     * @param month          Monat der Abfahrt.
     * @param day            Tag der Abfahrt.
     * @param hourd          Stunde der Abfahrt.
     * @param mind           Minute der Abfahrt.
     * @param houra          Stunde der Ankunft.
     * @param mina           Minute der Ankunft.
     * @param firstcapacity  Kapazität der 1. Klasse.
     * @param secondcapacity Kapazität der 2. Klasse.
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
     * @param admin       Administrator, der den Transport storniert.
     * @param transportid ID des Transports.
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
     * @param admin       Administrator, der die Tickets anzeigen möchte.
     * @param transportid ID des Transports.
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

    /**
     * Zeigt Orte basierend auf der Gesamtanzahl der gebuchten Tickets, die an einem Ort starten oder enden, an.
     */
    public void showLocationsByTotalTickets() {
        StringBuilder out = new StringBuilder("--- Locations sorted by total tickets ---\n");
        bookingSystem.getLocationsByTotalTickets().forEach(location -> out.append(location.toString()).append("\n"));
        System.out.println(out);
    }

}
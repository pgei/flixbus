package main.java.controller;

import main.java.exceptions.BusinessLogicException;
import main.java.exceptions.DatabaseException;
import main.java.exceptions.EntityNotFoundException;
import main.java.exceptions.ValidationException;
import main.java.model.*;
import main.java.service.BookingSystem;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

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
     * @param name                  Name des Benutzers.
     * @param email                 E-Mail-Adresse des Benutzers.
     * @param password              Passwort des Benutzers.
     * @throws ValidationException  Wenn gegebene E-Mail-Adresse nicht korrektes Format hat
     */
    public void registerAsCostumer(String name, String email, String password) throws ValidationException {
        try {
            if (Pattern.matches("[A-Za-z.0-9]*@[A-Za-z.0-9]*",email)) {
                bookingSystem.registerUser(name, email, password, false);
                System.out.println("Registration as costumer successful! Welcome, " + name + "!");
            } else {
                throw new ValidationException("ValidationException: Entered email is not of correct format!");
            }
        } catch (BusinessLogicException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Registration was not possible due to error in database operations!");
        }
    }

    /**
     * Registriert einen Benutzer als Administrator.
     *
     * @param name     Name des Benutzers.
     * @param email    E-Mail-Adresse des Benutzers.
     * @param password Passwort des Benutzers.
     * @throws ValidationException  Wenn gegebene E-Mail-Adresse nicht korrektes Format hat
     */
    public void registerAsAdministrator(String name, String email, String password) throws ValidationException {
        try {
            if (Pattern.matches("[A-Za-z.0-9]*@[A-Za-z.0-9]*",email)) {
                bookingSystem.registerUser(name, email, password, true);
                System.out.println("Administrator registration successful! Welcome, " + name + "!");
            } else {
                throw new ValidationException("ValidationException: Entered email is not of correct format!");
            }
        } catch (BusinessLogicException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Registration was not possible due to error in database operations!");
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
        try {
            Person loggedin = bookingSystem.checkLoginCredentials(email, password);
            System.out.println("Login successful! Welcome, " + loggedin.getUsername() + "!");
            return loggedin;
        } catch (EntityNotFoundException | BusinessLogicException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Login was not possible due to error in database operations!");
        }
        return null;
    }

    /**
     * Zeigt alle Transporte im Repository an.
     */
    public void viewAllTransports() {
        try {
            StringBuilder out = new StringBuilder("--- View transports ---\n");
            bookingSystem.getAllTransports().forEach(transport -> out.append(transport.toString()).append("\n"));
            System.out.println(out);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Fetching transports was not possible due to error in database operations!");
        }
    }

    /**
     * Filtert die verfügbaren Transporte basierend auf Start- und Zielorten.
     *
     * @param origin                ID des Startortes (-1 für beliebig).
     * @param destination           ID des Zielortes (-1 für beliebig).
     */
    public void filterByLocation(int origin, int destination) {
        try {
            List<Location> locations = bookingSystem.getLocations();
            AtomicBoolean originexists = new AtomicBoolean(false);
            AtomicBoolean destinationexists = new AtomicBoolean(false);
            StringBuilder originname = new StringBuilder();
            StringBuilder destinationname = new StringBuilder();
            locations.forEach(location -> {
                if ((int) location.getId() == origin) {
                    originexists.set(true);
                    originname.append(location.getStreet()).append(", ").append(location.getCity());
                }
                if ((int) location.getId() == destination) {
                    destinationexists.set(true);
                    destinationname.append(location.getStreet()).append(", ").append(location.getCity());
                }
            });
            if ((originexists.get() && destinationexists.get()) || (originexists.get() && destination == -1) || (destinationexists.get() && origin == -1) || (origin == -1 && destination == -1)) {
                StringBuilder out = new StringBuilder("--- Available transports from ");
                if (origin == -1) {
                    out.append("any location to ");
                } else {
                    out.append("location ").append(originname).append(" to ");
                }
                if (destination == -1) {
                    out.append("any location ---\n");
                } else {
                    out.append("location ").append(destinationname).append(" ---\n");
                }
                bookingSystem.getTransportsFilteredByLocation(origin, destination).forEach(transport -> out.append(transport.toString()).append("\n"));
                System.out.println(out);
            } else if (originexists.get()) {
                throw new EntityNotFoundException("EntityNotFoundException: There does not exist any location with the entered destination ID!");
            } else if (destinationexists.get()) {
                throw new EntityNotFoundException("EntityNotFoundException: There does not exist any location with the entered origin ID!");
            } else {
                throw new EntityNotFoundException("EntityNotFoundException: There do not exist any locations with the entered origin and destination IDs!");
            }
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Filtering transports was not possible due to error in database operations!");
        }
    }

    /**
     * Filtert verfügbare Transporte basierend auf dem maximalen Preis.
     *
     * @param price maximaler Preis in Euro.
     * @throws ValidationException Wenn negativer Preis eingegeben wurde
     */
    public void filterByPrice(int price) throws ValidationException {
        if (price < 0) {
            throw new ValidationException("ValidationException: Cannot search for transports with negative price!");
        } else {
            try {
                StringBuilder out = new StringBuilder("--- Available transports with a maximum price of " + price + " Euro ---\n");
                bookingSystem.getTransportsFilteredByMaxPrice(price).forEach(transport -> out.append(transport.toString()).append("\n"));
                System.out.println(out);
            } catch (DatabaseException e) {
                System.err.println(e.getMessage());
                //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
                System.out.println("DatabaseException: Filtering transports was not possible due to error in database operations!");
            }
        }

    }

    /**
     * Gibt Transporte in aufsteigender Reihenfolge sortiert nach Datum, und zweitrangig Abfahrtsuhrzeit, aus.
     *
     */
    public void sortTransportsByDate() {
        try {
            StringBuilder out = new StringBuilder("--- Transports sorted by date (and departure time) ---\n");
            bookingSystem.getTransportsSortedByDateAscending().forEach(transport -> out.append(transport.toString()).append("\n"));
            System.out.println(out);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Sorting transports was not possible due to error in database operations!");
        }
    }

    /**
     * Sortiert und gibt die Transporte basierend auf der Dauer in aufsteigender Reihenfolge aus.
     *
     */
    public void sortTransportsByDuration() {
        try {
            StringBuilder out = new StringBuilder("--- Transports sorted by duration (ascending) ---\n");
            bookingSystem.getTransportsSortedByDurationAscending().forEach(transport -> out.append(transport.toString()).append("\n"));
            System.out.println(out);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Sorting transports was not possible due to error in database operations!");
        }
    }

    /**
     * Zeigt alle verfügbaren Zielorte im Repository an.
     */
    public void viewAllDestinations() {
        try {
            StringBuilder out = new StringBuilder("--- Available destinations ---\n");
            bookingSystem.getLocations().forEach(location -> out.append(location.toString()).append("\n"));
            System.out.println(out);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Fetching destinations was not possible due to error in database operations!");
        }

    }

    /**
     * Zeigt das Guthaben eines Kunden an.
     *
     * @param costumer Kunde, dessen Guthaben angezeigt werden soll.
     */
    public void viewBalance(Costumer costumer) {
        try {
            System.out.println("You have a total of " + bookingSystem.getBalance(costumer) + " Euros in your account.");
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Fetching balance was not possible due to error in database operations!");
        }
    }

    /**
     * Fügt Guthaben zum Konto eines Kunden hinzu.
     *
     * @param costumer              Kunde, dem Guthaben hinzugefügt wird.
     * @param amount                Hinzuzufügender Betrag in Euro.
     * @throws ValidationException  Wenn negativer Geldbetrag eingegeben wurde
     */
    public void addBalance(Costumer costumer, int amount) throws ValidationException {
        if (amount < 0) {
            throw new ValidationException("ValidationException: A negative amount does not increase the balance!");
        } else {
            try {
                bookingSystem.addBalance(costumer, amount);
                System.out.println("Added " + amount + " Euros to your acccount, you now have a total of " + bookingSystem.getBalance(costumer) + " Euros.");
            } catch (EntityNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (DatabaseException e) {
                System.err.println(e.getMessage());
                //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
                System.out.println("DatabaseException: Fetching balance was not possible due to error in database operations!");
            }
        }
    }

    /**
     * Zeigt alle Tickets eines Kunden an.
     *
     * @param costumer Kunde, dessen Tickets angezeigt werden sollen.
     */
    public void viewTickets(Costumer costumer) {
        try {
            List<Ticket> list = bookingSystem.getALlTickets(costumer);
            System.out.println("Here is a list of all tickets you have reserved at this moment:\n\n");
            list.forEach(ticket -> {
                System.out.println(ticket.toString());
                bookingSystem.getAllTransports().forEach(transport -> {
                    if ((int) transport.getId() == ticket.getTransport()) {
                        System.out.print(transport.toString());
                    }
                });
                System.out.println("\n+++++++++++++++++++++++++++++++++++++++++\n");
            });
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Fetching tickets from repository was not possible due to error in database operations!");
        }
    }

    /**
     * Kauft ein Ticket für einen bestimmten Transport und eine bestimmte Klasse.
     *
     * @param costumer      Kunde, der das Ticket kauft.
     * @param transportid   ID des Transports.
     * @param ticketclass   Ticketklasse (z.B. 1. oder 2. Klasse).
     * @throws ValidationException  Wenn kein Transport mit der gegebenen ID existiert oder keine korrekte Ticketklasse angegeben wurde
     */
    public void buyTicket(Costumer costumer, int transportid, int ticketclass) throws ValidationException {
        try {
            if (ticketclass == 1 || ticketclass == 2) {
                bookingSystem.createTicket(costumer, transportid, ticketclass);
                System.out.println("Successfully reserved a ticket on Transport with Id " + transportid + "!");
            } else throw new ValidationException("ValidationException: No correct ticket class was entered!");
        } catch (BusinessLogicException | EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Buying ticket was not possible due to error in database operations!");
        }
    }

    /**
     * Storniert ein Ticket eines Kunden.
     *
     * @param costumer  Kunde, der das Ticket stornieren möchte.
     * @param ticketid  ID des Tickets.
     */
    public void cancelTicket(Costumer costumer, int ticketid) {
        try {
            bookingSystem.removeTicket(costumer, ticketid);
            System.out.println("Successfully cancelled ticket with TicketNr " + ticketid + "!");
        } catch (BusinessLogicException | EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Cancelling ticket was not possible due to error in database operations!");
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
        try {
            bookingSystem.createLocation(admin, street, city);
            System.out.println("Successfully created new location!");
        } catch (BusinessLogicException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Creating location was not possible due to error in database operations!");
        }
    }

    /**
     * Fügt einen neuen Bus-Transport hinzu.
     *
     * @param admin                 Administrator, der den Transport hinzufügt.
     * @param originid              ID des Startortes.
     * @param destinationid         ID des Zielortes.
     * @param year                  Jahr der Abfahrt.
     * @param month                 Monat der Abfahrt.
     * @param day                   Tag der Abfahrt.
     * @param hourd                 Stunde der Abfahrt.
     * @param mind                  Minute der Abfahrt.
     * @param houra                 Stunde der Ankunft.
     * @param mina                  Minute der Ankunft.
     * @param capacity              Kapazität des Busses.
     * @throws ValidationException  Wenn Kapazität, Monat, Tag, Abfahrtszeit oder Ankunftszeit nicht innerhalb erwartetem Intervall liegt
     */
    public void addBusTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) throws ValidationException {
        if (capacity <= 0) throw new ValidationException("ValidationException: Capacity cannot be zero or less!");
        if (!(0 < month && month < 13)) throw new ValidationException("ValidationException: Month must be in range 1 to 12!");
        if (!(0 < day && day < 32)) throw new ValidationException("ValidationException: Day must be in range 1 to 31!");
        if (!(0 <= hourd && hourd < 24)) throw new ValidationException("ValidationException: Departure hour must be in range 0 to 23!");
        if (!(0 <= houra && houra < 24)) throw new ValidationException("ValidationException: Arrival hour must be in range 0 to 23!");
        if (!(0 <= mind && mind < 60)) throw new ValidationException("ValidationException: Departure minute must be in range 0 to 59!");
        if (!(0 <= mina && mina < 60)) throw new ValidationException("ValidationException: Arrival minute must be in range 0 to 59!");
        try {
            bookingSystem.createBusTransport(admin, originid, destinationid, year, month, day, hourd, mind, houra, mina, capacity);
            System.out.println("Successfully created new bus transport!");
        } catch (BusinessLogicException | EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Creating bus transport was not possible due to error in database operations!");
        }
    }

    /**
     * Fügt einen neuen Zug-Transport hinzu.
     *
     * @param admin                 Administrator, der den Transport hinzufügt.
     * @param originid              ID des Startortes.
     * @param destinationid         ID des Zielortes.
     * @param year                  Jahr der Abfahrt.
     * @param month                 Monat der Abfahrt.
     * @param day                   Tag der Abfahrt.
     * @param hourd                 Stunde der Abfahrt.
     * @param mind                  Minute der Abfahrt.
     * @param houra                 Stunde der Ankunft.
     * @param mina                  Minute der Ankunft.
     * @param firstcapacity         Kapazität der 1. Klasse.
     * @param secondcapacity        Kapazität der 2. Klasse.
     * @throws ValidationException  Wenn Kapazität, Monat, Tag, Abfahrtszeit oder Ankunftszeit nicht innerhalb erwartetem Intervall liegt
     */
    public void addTrainTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) throws ValidationException {
        if (firstcapacity < 0) throw new ValidationException("ValidationException: First class capacity cannot be negative!");
        if (secondcapacity <= 0) throw new ValidationException("ValidationException: Second class capacity cannot be zero or less!");
        if (!(0 < month && month < 13)) throw new ValidationException("ValidationException: Month must be in range 1 to 12!");
        if (!(0 < day && day < 32)) throw new ValidationException("ValidationException: Day must be in range 1 to 31!");
        if (!(0 <= hourd && hourd < 24)) throw new ValidationException("ValidationException: Departure hour must be in range 0 to 23!");
        if (!(0 <= houra && houra < 24)) throw new ValidationException("ValidationException: Arrival hour must be in range 0 to 23!");
        if (!(0 <= mind && mind < 60)) throw new ValidationException("ValidationException: Departure minute must be in range 0 to 59!");
        if (!(0 <= mina && mina < 60)) throw new ValidationException("ValidationException: Arrival minute must be in range 0 to 59!");
        try {
            bookingSystem.createTrainTransport(admin, originid, destinationid, year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
            System.out.println("Successfully created new train transport!");
        } catch (BusinessLogicException | EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Creating train transport was not possible due to error in database operations!");
        }
    }

    /**
     * Storniert einen Transport.
     *
     * @param admin       Administrator, der den Transport storniert.
     * @param transportid ID des Transports.
     */
    public void cancelTransport(Administrator admin, int transportid) {
        try {
            bookingSystem.removeTransport(admin, transportid);
            System.out.println("Successfully removed transport with ID " + transportid + ". Costumers have been refunded.");
        } catch (BusinessLogicException | EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Cancelling transport was not possible due to error in database operations!");
        }
    }

    /**
     * Zeigt alle Tickets für einen bestimmten Transport an.
     *
     * @param admin       Administrator, der die Tickets anzeigen möchte.
     * @param transportid ID des Transports.
     */
    public void showAllTickets(Administrator admin, int transportid) {
        try {
            List<Ticket> list = bookingSystem.getAllTransportTickets(admin, transportid);
            if (!list.isEmpty()) {
                System.out.println("Here is a list of all tickets reserved on transport with ID " + transportid + ":\n\n");
                list.forEach(ticket -> System.out.println(ticket.toStringReducedView()));
            } else {
                System.out.println("No tickets are reserved on transport with ID " + transportid + " at this moment!\n");
            }
        } catch (BusinessLogicException | EntityNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Fetching tickets was not possible due to error in database operations!");
        }
    }

    /**
     * Zeigt Orte basierend auf der Gesamtanzahl der gebuchten Tickets, die an einem Ort starten oder enden, an.
     */
    public void showLocationsByTotalTickets() {
        try {
            StringBuilder out = new StringBuilder("--- Locations sorted by total tickets (descending) ---\n");
            bookingSystem.getLocationsSortedDescendingByTotalTickets().forEach(location -> out.append(location.toString()).append("\n"));
            System.out.println(out);
        } catch (DatabaseException e) {
            System.err.println(e.getMessage());
            //Exception-Message könnte an dieser Stelle in einen Error-Log geschrieben werden, da Nutzer nicht die Details sehen sollte
            System.out.println("DatabaseException: Calculating the statistics was not possible due to error in database operations!");
        }
    }

}
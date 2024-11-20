package main.java;

import main.java.controller.RequestHandler;
import main.java.model.*;
import main.java.repository.FileRepository;
import main.java.repository.IRepository;
import main.java.repository.InMemoryRepository;
import main.java.service.BookingSystem;

import java.util.Scanner;

/**
 * Die Klasse App stellt die Benutzeroberfläche für das Transport-Buchungssystem bereit.
 * Sie interagiert mit dem Benutzer über die Konsole und bietet Funktionen wie Registrierung,
 * Anmeldung, Ansehen und Filtern von Transporten sowie das Verwalten von Tickets und Orten.
 */
public class App {

    /**
     * Variable die festlegt, mit welcher Art von Repository gearbeitet werden soll
     * <ul>
     *     <li>0: InMemoryRepository</li>
     *     <li>1: FileRepository</li>
     *     <li>2: DBRepository</li>
     * </ul>
     */
    private final static int repositorySource = 1;
    private final RequestHandler requestHandler;
    private Person person;

    /**
     * Konstruktor der Klasse App.
     *
     * @param requestHandler der RequestHandler, der die Anfragen der App bearbeitet.
     */
    public App(RequestHandler requestHandler) {this.requestHandler = requestHandler;}

    /**
     * Startet die Anwendung und zeigt die Hauptmenüs basierend auf dem Benutzerstatus an.
     */
    public void start() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("++ Welcome to the Transport Booking System! ++");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            switch (this.person) {
                case null -> running = startScreen(scanner);
                case Costumer costumer -> costumerScreen(scanner);
                case Administrator administrator -> administratorScreen(scanner);
                default -> {
                    System.out.println("Error: User of unexpected class "+this.person.getClass());
                    System.out.println("Shutting down the system.");
                    running = false;
                }
            }

        }
    }

    /**
     * Zeigt den Startbildschirm für Gäste an, um Optionen wie Registrierung und Anmeldung auszuwählen.
     *
     * @param scanner Scanner-Objekt zum Lesen der Benutzereingaben.
     * @return Wahr, wenn die App weiterhin laufen soll, false, wenn sie beendet werden soll
     */
    private boolean startScreen(Scanner scanner) {
        System.out.println("\n++++++++++++++++++++++++++++");
        System.out.println("Please select an option:");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. View transports");
        System.out.println("4. Sort transports by date");
        System.out.println("5. Sort transports by duration");
        System.out.println("6. Filter transports by location");
        System.out.println("7. Filter transports by price");
        System.out.println("8. View destinations");
        System.out.println("9. Exit");
        System.out.println("++++++++++++++++++++++++++++");
        try {int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    register(scanner);
                    break;
                case 2:
                    login(scanner);
                    break;
                case 3:
                    this.requestHandler.viewAllTransports();
                    break;
                case 4:
                    this.requestHandler.sortTransportsByDate();
                    break;
                case 5:
                    this.requestHandler.sortTransportsByDuration();
                    break;
                case 6:
                    filterTransportsByLocation(scanner);
                    break;
                case 7:
                    this.requestHandler.filterByPrice(readFilterByPrice(scanner));
                    break;
                case 8:
                    this.requestHandler.viewAllDestinations();
                    break;
                case 9:
                    System.out.println("Exiting the system. Goodbye!");
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please try again.");
            return true;
        }
    }

    /**
     * Zeigt das Menü für Kunden an, nachdem sie sich angemeldet haben.
     *
     * @param scanner Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    private void costumerScreen(Scanner scanner) {
        System.out.println("\n++++++++++++++++++++++++++++");
        System.out.println("Please select an option:");
        System.out.println("1. View transports");
        System.out.println("2. Sort transports by date");
        System.out.println("3. Sort transports by duration");
        System.out.println("4. Filter transports by location");
        System.out.println("5. Filter transports by price");
        System.out.println("6. View destinations");
        System.out.println("7. View balance");
        System.out.println("8. Add balance");
        System.out.println("9. View all reserved tickets");
        System.out.println("10. Buy ticket");
        System.out.println("11. Cancel ticket");
        System.out.println("12. Logout");
        System.out.println("++++++++++++++++++++++++++++");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    this.requestHandler.viewAllTransports();
                    break;
                case 2:
                    this.requestHandler.sortTransportsByDate();
                    break;
                case 3:
                    this.requestHandler.sortTransportsByDuration();
                    break;
                case 4:
                    filterTransportsByLocation(scanner);
                    break;
                case 5:
                    this.requestHandler.filterByPrice(readFilterByPrice(scanner));
                    break;
                case 6:
                    this.requestHandler.viewAllDestinations();
                    break;
                case 7:
                    this.requestHandler.viewBalance((Costumer) this.person);
                    break;
                case 8:
                    this.requestHandler.addBalance((Costumer) this.person, readBalanceAddition(scanner));
                    break;
                case 9:
                    this.requestHandler.viewTickets((Costumer) this.person);
                    break;
                case 10:
                    buyTicketRequest(scanner);
                    break;
                case 11:
                    this.requestHandler.cancelTicket((Costumer) this.person, readCancelTicketRequest(scanner));
                    break;
                case 12:
                    this.person = null;
                    System.out.println("Logout successful!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Zeigt das Menü für Administratoren an, nachdem sie sich angemeldet haben.
     *
     * @param scanner Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    private void administratorScreen(Scanner scanner) {
        System.out.println("\n++++++++++++++++++++++++++++");
        System.out.println("Please select an option:");
        System.out.println("1. View transports");
        System.out.println("2. Sort transports by date");
        System.out.println("3. Sort transports by duration");
        System.out.println("4. Filter transports by location");
        System.out.println("5. Filter transports by price");
        System.out.println("6. View destinations");
        System.out.println("7. Add location");
        System.out.println("8. Add transport");
        System.out.println("9. Cancel transport");
        System.out.println("10. View all tickets booked on a specific transport");
        System.out.println("11. View locations sorted by total tickets");
        System.out.println("12. Logout");
        System.out.println("++++++++++++++++++++++++++++");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    this.requestHandler.viewAllTransports();
                    break;
                case 2:
                    this.requestHandler.sortTransportsByDate();
                    break;
                case 3:
                    this.requestHandler.sortTransportsByDuration();
                    break;
                case 4:
                    filterTransportsByLocation(scanner);
                    break;
                case 5:
                    this.requestHandler.filterByPrice(readFilterByPrice(scanner));
                    break;
                case 6:
                    this.requestHandler.viewAllDestinations();
                    break;
                case 7:
                    addLocation(scanner);
                    break;
                case 8:
                    addTransport(scanner);
                    break;
                case 9:
                    this.requestHandler.cancelTransport((Administrator) this.person, readCancelTransportRequest(scanner));
                    break;
                case 10:
                    this.requestHandler.showAllTickets((Administrator) this.person, readTransportId(scanner));
                    break;
                case 11:
                    this.requestHandler.showLocationsByTotalTickets();
                    break;
                case 12:
                    this.person = null;
                    System.out.println("Logout successful!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid choice. Please try again.");
        }
    }

    /**
     * Registriert einen neuen Benutzer, entweder als Kunde oder als Administrator.
     *
     * @param scanner Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    private void register(Scanner scanner) {
        System.out.println("\n--- Register ---");
        System.out.println("Enter username: ");
        String username = scanner.nextLine();
        System.out.println("Enter email: ");
        String email = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        System.out.println("Should this be an administrator account? (Yes/No)");
        String admin = scanner.nextLine();
        if (admin.equals("Yes")) {
            System.out.println("Enter key for administrator registration: ");
            String key = scanner.nextLine();
            if (key.equals("geheim1234")) {
                this.requestHandler.registerAsAdministrator(username, email, password);
            } else {
                System.out.println("Wrong key, administrator account generation request denied!");
            }
        } else {
            this.requestHandler.registerAsCostumer(username, email, password);
        }
    }

    /**
     * Meldet einen Benutzer an, basierend auf der eingegebenen E-Mail und dem Passwort.
     *
     * @param scanner Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    private void login(Scanner scanner) {
        System.out.println("\n--- Login ---");
        System.out.println("Enter email: ");
        String email = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        this.person = requestHandler.login(email, password);
    }

    /**
     * Liest den Betrag, der dem Konto des Benutzers hinzugefügt werden soll.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     * @return          Betrag in Euro, der hinzugefügt werden soll.
     */
    private int readBalanceAddition(Scanner scanner) {
        System.out.println("\n--- Add balance ---");
        System.out.println("Enter Euros to add to your account balance: ");
        String money = scanner.nextLine();
        return Integer.parseInt(money);
    }

    /**
     * Verarbeitet Eingaben um dem Benutzer eine Fahrkarte für ein bestimmtes Verkehrsmittel zu kaufen.
     * Der Benutzer wird hierfür aufgefordert, die Transport-ID und die gewünschte Fahrscheinklasse (1 oder 2) einzugeben.
     * Die Methode validiert die Fahrscheinklasse und leitet die Anfrage an den {@link RequestHandler} weiter für Verarbeitung.
     *
     * <p>Optionen werden basierend auf dem Typ von Transport angezeigt:
     * <ul>
     *     <li>Für Bus: nur 2. Klasse für 20 Euro ist verfügbar</li>
     *     <li>Für Zug: Optionen für 1. KLasse (50 Euro) und 2. Klasse (15 Euro) verfügbar</li>
     * </ul>
     *
     * @param scanner                Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    public void buyTicketRequest(Scanner scanner) {
        System.out.println("\n--- Buy ticket ---");
        System.out.println("Enter ID for transport on which you would like to reserve a ticket: ");
        int transportid = Integer.parseInt(scanner.nextLine());
        System.out.println("""
                -------------------------
                For Bus you have the following class options:\s
                \t - 2nd for 20 Euro
                For Train you have the following class options:\s
                \t - 1st for 50 Euro
                \t - 2nd for 15 Euro""");
        System.out.println("Enter class you would like to book (1/2): ");
        try {
            int ticketclass = Integer.parseInt(scanner.nextLine());
            if (ticketclass == 1 || ticketclass == 2) {
                this.requestHandler.buyTicket((Costumer) this.person, transportid, ticketclass);
            } else {
                System.out.println("No correct class was entered, please try again!");
            }
        } catch (NumberFormatException e) {
            System.out.println("No correct class was entered, please try again!");
        }
    }

    /**
     * Liest die Daten eines Tickets, das der Benutzer stornieren möchte.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     * @return          Ticketnummer des zu stornierenden Tickets.
     */
    public int readCancelTicketRequest(Scanner scanner) {
        System.out.println("\n--- Cancel ticket ---");
        System.out.println("Enter ticket number of ticket you would like to cancel: ");
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * Fügt einen neuen Ort in das System ein.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    public void addLocation(Scanner scanner) {
        System.out.println("\n--- Add location ---");
        System.out.println("Enter street address of location: ");
        String street = scanner.nextLine();
        System.out.println("Enter city where the location is: ");
        String city = scanner.nextLine();
        this.requestHandler.addLocation((Administrator) this.person, street, city);
    }

    /**
     * Fügt einen neuen Transport in das System ein.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    public void addTransport(Scanner scanner) {
        System.out.println("\n--- Add transport ---");
        System.out.println("Enter location ID of origin: ");
        int originid = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter location ID of destination: ");
        int destinationid = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter year when the transport will happen: ");
        int year = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter month (as number!) when the transport will happen: ");
        int month = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter day (as number!) when the transport will happen: ");
        int day = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter hour at which the transport will start: ");
        int hourd = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter minute at which the transport will start: ");
        int mind = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter hour at which the transport will arrive: ");
        int houra = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter minute at which the transport will arrive: ");
        int mina = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter means of transport (Bus/Train):");
        String meansoftransport = scanner.nextLine();
        if (meansoftransport.equals("Bus")) {
            System.out.println("Please enter the capacity:");
            int capacity = Integer.parseInt(scanner.nextLine());
            this.requestHandler.addBusTransport((Administrator) this.person, originid, destinationid, year, month, day, hourd, mind, houra, mina, capacity);
        } else if (meansoftransport.equals("Train")) {
            System.out.println("Please enter the first class capacity:");
            int firstcapacity = Integer.parseInt(scanner.nextLine());
            System.out.println("Please enter the second class capacity:");
            int secondcapacity = Integer.parseInt(scanner.nextLine());
            this.requestHandler.addTrainTransport((Administrator) this.person, originid, destinationid, year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
        } else {
            System.out.println("No correct means of transport was entered, please try again!");
        }
    }

    /**
     * Liest die Transport-ID eines Transports, der storniert werden soll.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     * @return          Transport-ID des zu stornierenden Transports.
     */
    public int readCancelTransportRequest(Scanner scanner) {
        System.out.println("\n--- Cancel transport ---");
        System.out.println("Enter transport id of transport you would like to cancel: ");
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * Liest die Transport-ID eines Transports, für den alle gebuchten Tickets angezeigt werden sollen.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     * @return          Transport-ID des Transports.
     */
    public int readTransportId(Scanner scanner) {
        System.out.println("\n--- View all tickets booked on a specific transport ---");
        System.out.println("Enter transport ID of transport you would like to view all tickets of: ");
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * Filtert Transporte basierend auf einem angegebenen Ursprungs- und Zielort.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     */
    public void filterTransportsByLocation(Scanner scanner) {
        System.out.println("\n--- Filter transports by location ---");
        System.out.println("Enter location ID for origin (type -1 to select all available locations as origin): ");
        int origin = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter location ID for destination (type -1 to select all available locations as destination): ");
        int destination = Integer.parseInt(scanner.nextLine());
        this.requestHandler.filterByLocation(origin, destination);
    }

    /**
     * Liest den maximalen Ticketpreis zum Filtern der Transporte.
     *
     * @param scanner   Scanner-Objekt zum Lesen der Benutzereingaben.
     * @return          maximaler Ticketpreis in Euro.
     */
    public int readFilterByPrice(Scanner scanner) {
        System.out.println("\n--- Filter transports by price ---");
        System.out.println("Enter maximum ticket price in Euro:");
        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * Die Hauptmethode zum Starten der App.
     *
     * @param args Argumente der Kommandozeile.
     */
    public static void main(String[] args) {
        if (repositorySource == 0) {
            IRepository<Person> personRepository = createInMemoryPersonRepository();
            IRepository<Transport> transportRepository = createInMemoryTransportRepository();
            IRepository<Location> locationRepository = createInMemoryLocationRepository();
            IRepository<Ticket> ticketRepository = createInMemoryTicketRepository();
            BookingSystem bookingSystem = new BookingSystem(personRepository, transportRepository, ticketRepository, locationRepository);
            RequestHandler requestHandler = new RequestHandler(bookingSystem);
            App application = new App(requestHandler);
            application.start();
        } else if (repositorySource == 1) {
            IRepository<Person> personRepository = new FileRepository<>("src/main/fileresources/persons.db");
            IRepository<Transport> transportRepository = new FileRepository<>("src/main/fileresources/transports.db");
            IRepository<Ticket> ticketRepository = new FileRepository<>("src/main/fileresources/tickets.db");
            IRepository<Location> locationRepository = new FileRepository<>("src/main/fileresources/locations.db");
            BookingSystem bookingSystem = new BookingSystem(personRepository, transportRepository, ticketRepository, locationRepository);
            RequestHandler requestHandler = new RequestHandler(bookingSystem);
            App application = new App(requestHandler);
            application.start();
        }
    }

    /**
     * Erstellt ein In-Memory-Repository für Personen.
     *
     * @return Repository für Personen.
     */
    private static IRepository<Person> createInMemoryPersonRepository() {
        IRepository<Person> personIRepository = new InMemoryRepository<>();
        personIRepository.create(new Administrator("Philipp", "philipp@test.de", "test"));
        personIRepository.create(new Costumer("Daniel", "daniel@test.de", "test"));
        return personIRepository;
    }

    /**
     * Erstellt ein In-Memory-Repository für Transporte.
     *
     * @return Repository für Transporte.
     */
    private static IRepository<Transport> createInMemoryTransportRepository() {
        IRepository<Transport> transportIRepository = new InMemoryRepository<>();
        return transportIRepository;
    }

    /**
     * Erstellt ein In-Memory-Repository für Tickets.
     *
     * @return Repository für Tickets.
     */
    private static IRepository<Ticket> createInMemoryTicketRepository() {
        IRepository<Ticket> ticketIRepository = new InMemoryRepository<>();
        return ticketIRepository;
    }

    /**
     * Erstellt ein In-Memory-Repository für Orte.
     *
     * @return Repository für Orte.
     */
    private static IRepository<Location> createInMemoryLocationRepository() {
        IRepository<Location> locationIRepository = new InMemoryRepository<>();
        locationIRepository.create(new Location(0, "Strada Mihail Kogălniceanu", "Cluj-Napoca"));
        locationIRepository.create(new Location(1, "Strada Mihail Kogălniceanu", "Brasov"));
        locationIRepository.create(new Location(2, "Calea Clujului", "Oradea"));
        return locationIRepository;
    }

}

package main.java;

import main.java.controller.RequestHandler;
import main.java.model.*;
import main.java.repository.IRepository;
import main.java.repository.InMemoryRepository;
import main.java.service.BookingSystem;

import java.util.Scanner;

public class App {

    private final RequestHandler requestHandler;
    private Person person;

    public App(RequestHandler requestHandler) {this.requestHandler = requestHandler;}

    public void start() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println("++ Welcome to the Transport Booking System! ++");
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++");
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            switch (this.person) {
                case null -> {
                    running = startScreen(scanner);
                }
                case Costumer costumer -> {
                    costumerScreen(scanner);
                }
                case Administrator administrator -> {
                    administratorScreen(scanner);
                }
                default -> {
                    System.out.println("Error: User of unexpected class "+this.person.getClass());
                    System.out.println("Shutting down the system.");
                    running = false;
                }
            }

        }
    }

    private boolean startScreen(Scanner scanner) {
        System.out.println("\nPlease select an option:");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. View transports");
        System.out.println("4. Filter transports by location");
        System.out.println("5. Filter transports by price");
        System.out.println("6. View destinations");
        System.out.println("7. Exit");
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
                    filterTransportsByLocation(scanner);
                    break;
                case 5:
                    this.requestHandler.filterByPrice(readFilterByPrice(scanner));
                    break;
                case 6:
                    this.requestHandler.viewAllDestinations();
                    break;
                case 7:
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

    private void costumerScreen(Scanner scanner) {
        System.out.println("\nPlease select an option:");
        System.out.println("1. View transports");
        System.out.println("2. Filter transports by location");
        System.out.println("3. Filter transports by price");
        System.out.println("4. View destinations");
        System.out.println("5. View balance");
        System.out.println("6. Add balance");
        System.out.println("7. View all reserved tickets");
        System.out.println("8. Buy ticket");
        System.out.println("9. Cancel ticket");
        System.out.println("10. Logout");
        System.out.println("++++++++++++++++++++++++++++");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    this.requestHandler.viewAllTransports();
                    break;
                case 2:
                    filterTransportsByLocation(scanner);
                    break;
                case 3:
                    this.requestHandler.filterByPrice(readFilterByPrice(scanner));
                    break;
                case 4:
                    this.requestHandler.viewAllDestinations();
                    break;
                case 5:
                    this.requestHandler.viewBalance((Costumer) this.person);
                    break;
                case 6:
                    this.requestHandler.addBalance((Costumer) this.person, readBalanceAddition(scanner));
                    break;
                case 7:
                    this.requestHandler.viewTickets((Costumer) this.person);
                    break;
                case 8:
                    buyTicketRequest(scanner);
                    break;
                case 9:
                    this.requestHandler.cancelTicket((Costumer) this.person, readCancelTicketRequest(scanner));
                    break;
                case 10:
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

    private void administratorScreen(Scanner scanner) {
        System.out.println("\nPlease select an option:");
        System.out.println("1. View transports");
        System.out.println("2. Filter transports by location");
        System.out.println("3. Filter transports by price");
        System.out.println("4. View destinations");
        System.out.println("5. Add location");
        System.out.println("6. Add transport");
        System.out.println("7. Cancel transport");
        System.out.println("8. View all tickets booked on a specific transport");
        System.out.println("9. Logout");
        System.out.println("++++++++++++++++++++++++++++");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1:
                    this.requestHandler.viewAllTransports();
                    break;
                case 2:
                    filterTransportsByLocation(scanner);
                    break;
                case 3:
                    this.requestHandler.filterByPrice(readFilterByPrice(scanner));
                    break;
                case 4:
                    this.requestHandler.viewAllDestinations();
                    break;
                case 5:
                    addLocation(scanner);
                    break;
                case 6:
                    addTransport(scanner);
                    break;
                case 7:
                    this.requestHandler.cancelTransport((Administrator) this.person, readCancelTransportRequest(scanner));
                    break;
                case 8:
                    this.requestHandler.showAllTickets((Administrator) this.person, readTransportId(scanner));
                    break;
                case 9:
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

    private void login(Scanner scanner) {
        System.out.println("\n--- Login ---");
        System.out.println("Enter email: ");
        String email = scanner.nextLine();
        System.out.println("Enter password: ");
        String password = scanner.nextLine();
        this.person = requestHandler.login(email, password);
    }

    private int readBalanceAddition(Scanner scanner) {
        System.out.println("\n--- Add balance ---");
        System.out.println("Enter Euros to add to your account balance: ");
        String money = scanner.nextLine();
        return Integer.parseInt(money);
    }

    public void buyTicketRequest(Scanner scanner) {
        System.out.println("\n--- Buy ticket ---");
        System.out.println("Enter ID for transport on which you would like to reserve a ticket: ");
        int transportid = Integer.parseInt(scanner.nextLine());
        System.out.println("-------------------------\n" +
                "For Bus you have the following class options: \n\t - 2nd for 20 Euro\n" +
                "For Train you have the following class options: \n\t - 1st for 50 Euro\n\t - 2nd for 15 Euro");
        System.out.println("Enter class you would like to book (1/2): ");
        int ticketclass = Integer.parseInt(scanner.nextLine());
        if (ticketclass == 1 || ticketclass == 2) {
            this.requestHandler.buyTicket((Costumer) this.person, transportid, ticketclass);
        } else {
            System.out.println("No correct class was entered, please try again!");
        }
    }

    public int readCancelTicketRequest(Scanner scanner) {
        System.out.println("\n--- Cancel ticket ---");
        System.out.println("Enter ticket number of ticket you would like to cancel: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public void addLocation(Scanner scanner) {
        System.out.println("\n--- Add location ---");
        System.out.println("Enter street address of location: ");
        String street = scanner.nextLine();
        System.out.println("Enter city where the location is: ");
        String city = scanner.nextLine();
        this.requestHandler.addLocation((Administrator) this.person, street, city);
    }

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

    public int readCancelTransportRequest(Scanner scanner) {
        System.out.println("\n--- Cancel transport ---");
        System.out.println("Enter transport id of transport you would like to cancel: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int readTransportId(Scanner scanner) {
        System.out.println("\n--- View all tickets booked on a specific transport ---");
        System.out.println("Enter transport ID of transport you would like to view all tickets of: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public void filterTransportsByLocation(Scanner scanner) {
        System.out.println("\n--- Filter transports by location ---");
        System.out.println("Enter location ID for origin (type -1 to select all available locations as origin): ");
        int origin = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter location ID for destination (type -1 to select all available locations as destination): ");
        int destination = Integer.parseInt(scanner.nextLine());
        this.requestHandler.filterByLocation(origin, destination);
    }

    public int readFilterByPrice(Scanner scanner) {
        System.out.println("\n--- Filter transports by price ---");
        System.out.println("Enter maximum ticket price in Euro:");
        return Integer.parseInt(scanner.nextLine());
    }

    public static void main(String[] args) {
        IRepository<Person> personRepository = createInMemoryPersonRepository();
        IRepository<Transport> transportRepository = createInMemoryTransportRepository();
        IRepository<Location> locationRepository = createInMemoryLocationRepository();
        IRepository<Ticket> ticketRepository = createInMemoryTicketRepository();

        BookingSystem bookingSystem = new BookingSystem(personRepository, transportRepository, ticketRepository, locationRepository);
        RequestHandler requestHandler = new RequestHandler(bookingSystem);
        App application = new App(requestHandler);
        application.start();
    }

    private static IRepository<Person> createInMemoryPersonRepository() {
        IRepository<Person> personIRepository = new InMemoryRepository<>();
        personIRepository.create(new Administrator("Philipp", "philipp@test.de", "test"));
        personIRepository.create(new Costumer("Daniel", "daniel@test.de", "test"));
        return personIRepository;
    }

    private static IRepository<Transport> createInMemoryTransportRepository() {
        IRepository<Transport> transportIRepository = new InMemoryRepository<>();
        return transportIRepository;
    }

    private static IRepository<Ticket> createInMemoryTicketRepository() {
        IRepository<Ticket> ticketIRepository = new InMemoryRepository<>();
        return ticketIRepository;
    }

    private static IRepository<Location> createInMemoryLocationRepository() {
        IRepository<Location> locationIRepository = new InMemoryRepository<>();
        locationIRepository.create(new Location(0, "Strada Mihail Kogălniceanu", "Cluj-Napoca"));
        locationIRepository.create(new Location(1, "Strada Mihail Kogălniceanu", "Brasov"));
        locationIRepository.create(new Location(2, "Calea Clujului", "Oradea"));
        return locationIRepository;
    }

}

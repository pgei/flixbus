package main.fileresources;

import main.java.model.*;
import main.java.repository.FileRepository;

/**
 * Klasse die dazu dient, FileRepositories mit gewünschten Daten zu initialisieren
 */
public class FileDataInitializer {
    public static void main(String[] args) {
        // Create file-based repositories
        FileRepository<Person> personRepository = new FileRepository<>("src/main/fileresources/persons.db");
        FileRepository<Transport> transportRepo = new FileRepository<>("src/main/fileresources/transports.db");
        FileRepository<Location> locationRepo = new FileRepository<>("src/main/fileresources/locations.db");
        FileRepository<Ticket> ticketRepo = new FileRepository<>("src/main/fileresources/tickets.db");

        // Create instances of Administrator and Costumer, Location and Transport
        Administrator admin1 = new Administrator("Admin_1", "admin1@gmail.com", "Adminpass123!");
        Costumer customer1 = new Costumer("Markus", "markus@yahoo.com", "Pass1234");
        Costumer customer2 = new Costumer("Thomas", "thomas@hotmail.com", "Mypass123");
        Costumer customer3 = new Costumer("Bob", "bob@gmail.com", "Securepass123");
        Location location1= new Location(0,"Aurel Vlaicu","Cluj-Napoca");
        Location location2= new Location(1,"Mihai Viteazul","Sibiu");
        Transport transport1= new Bus(0, location1,location2,2023,12,13,14,0,17,0,20);
        Transport transport2=new Train(1,location1,location2,2023,12,13,10,0,15,0,20,50);
        admin1.getAllAdministeredTransports().add(transport1);
        admin1.getAllAdministeredTransports().add(transport2);

        // Save them in the respective repositories
        personRepository.create(admin1);
        personRepository.create(customer1);
        personRepository.create(customer2);
        personRepository.create(customer3);
        locationRepo.create(location1);
        locationRepo.create(location2);
        transportRepo.create(transport1);
        transportRepo.create(transport2);

        System.out.println("Data initialized and stored in file repositories.");
    }
}

package main.setup;

import main.java.model.*;
import main.java.repository.FileRepository;

public class FileDataInitializer {
    public static void main(String[] args) {
        // Create a file-based repository for Administrator and Costumer
        FileRepository<Administrator> adminRepo = new FileRepository<>("admins.txt");
        FileRepository<Costumer> costumerRepo = new FileRepository<>("costumers.txt");
        FileRepository<Transport> transportRepo = new FileRepository<>("transports.txt");

        // Create instances of Administrator and Costumer
        Administrator admin1 = new Administrator("Admin_1", "admin1@gmail.com", "Adminpass123!");
        Costumer customer1 = new Costumer("Markus", "markus@yahoo.com", "Pass1234");
        Costumer customer2 = new Costumer("Thomas", "thomas@hotmail.com", "Mypass123");
        Costumer customer3 = new Costumer("Bob", "bob@gmail.com", "Securepass123");
        Location location1= new Location(0,"Aurel Vlaicu","Cluj-Napoca");
        Location location2= new Location(1,"Mihai Viteazul","Sibiu");
        Transport transport1= new Bus(0, location1,location2,2023,12,13,14,0,17,0,20);
        Transport transport2=new Train(1,location1,location2,2023,12,13,10,0,15,0,20,50);

        // Save them in the repository
        adminRepo.create(admin1);
        costumerRepo.create(customer1);
        costumerRepo.create(customer2);
        costumerRepo.create(customer3);
        transportRepo.create(transport1);
        transportRepo.create(transport2);

        System.out.println("Data initialized and stored in file repositories.");
    }
}

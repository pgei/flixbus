package test;

import main.java.exceptions.BusinessLogicException;
import main.java.exceptions.EntityNotFoundException;
import main.java.model.*;
import main.java.repository.IRepository;
import main.java.repository.InMemoryRepository;
import main.java.service.BookingSystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    BookingSystem bookingSystem;

    @BeforeEach
    void initializeRepositories() {
        IRepository<Person> personIRepository = new InMemoryRepository<>();
        IRepository<Location> locationIRepository = new InMemoryRepository<>();
        IRepository<Transport> transportIRepository = new InMemoryRepository<>();
        IRepository<Ticket> ticketIRepository = new InMemoryRepository<>();

        Administrator admin0 = new Administrator("Philipp", "philipp@test.de", "test");
        Administrator admin1 = new Administrator("Mihael", "mihael@test.de", "test");
        Costumer costumer = new Costumer("Daniel", "daniel@test.de", "test");
        Costumer costumer1 = new Costumer("Mos Craicun", "mos@craicun.ro", "hoho");
        Location location0 = new Location(0, "Strada Mihail Kogălniceanu", "Cluj-Napoca");
        Location location1 = new Location(1, "Strada Mihail Kogălniceanu", "Brasov");
        Location location2 = new Location(2, "Calea Clujului", "Oradea");
        Bus bus0 = new Bus(0,0, 1, 2024, 12, 12, 12, 0, 14, 0, 2);
        Bus bus1 = new Bus(1,0, 2, 2024, 12, 25, 13, 0, 16, 45, 2);
        Train train2 = new Train(2,0, 2, 2025, 1, 1, 13, 0, 16, 45, 1, 10);
        admin0.getAllAdministeredTransports().add(bus0);
        admin0.getAllAdministeredTransports().add(bus1);
        admin1.getAllAdministeredTransports().add(train2);
        BusTicket ticket0 = new BusTicket(0, "daniel@test.de", 1, 20, 2);
        BusTicket ticket1 = new BusTicket(1, "daniel@test.de", 1, 20, 1);
        TrainTicket ticket2 = new TrainTicket(2, "mos@craicun.ro", 2, 15, 210, 2);
        costumer.getAllTickets().add(ticket0);
        costumer.getAllTickets().add(ticket1);
        costumer1.getAllTickets().add(ticket2);
        costumer.setBalance(40);
        costumer1.setBalance(20);
        bus1.getBookedSeats().put(2, ticket0);
        bus1.getBookedSeats().put(1, ticket0);
        bus1.setCapacity(0);
        train2.getBookedSeats().put(210, ticket2);
        train2.setSecondCapacity(9);
        ticketIRepository.create(ticket0);
        ticketIRepository.create(ticket1);
        ticketIRepository.create(ticket2);
        transportIRepository.create(bus0);
        transportIRepository.create(bus1);
        transportIRepository.create(train2);
        locationIRepository.create(location0);
        locationIRepository.create(location1);
        locationIRepository.create(location2);
        personIRepository.create(admin0);
        personIRepository.create(admin1);
        personIRepository.create(costumer);
        personIRepository.create(costumer1);
        bookingSystem = new BookingSystem(personIRepository, transportIRepository, ticketIRepository, locationIRepository);
    }

    @Test
    void testCRUDOperations() {
        assertDoesNotThrow(() -> {
            IRepository<Person> personRepository = new InMemoryRepository<>();
            personRepository.create(new Costumer("Rainer", "rainer.zufall@test.de", "geheim"));
            assertEquals(1, personRepository.getAll().size());
            assertEquals("Rainer", personRepository.get("rainer.zufall@test.de").getUsername());
            personRepository.update(new Costumer("Zufall", "rainer.zufall@test.de", "geheim"));
            assertEquals(1, personRepository.getAll().size());
            assertEquals("Zufall", personRepository.get("rainer.zufall@test.de").getUsername());
            personRepository.delete("rainer.zufall@test.de");
            assertNull(personRepository.get("rainer.zufall@test.de"));
            assertEquals(0, personRepository.getAll().size());
        });
    }

    @Test
    void testRegisterAdministratorWithNewEmail() {
        assertDoesNotThrow(() -> bookingSystem.registerUser("Rainer", "rainer.zufall@test.de", "geheim", true));
    }

    @Test
    void testRegisterCostumerWithNewEmail() {
        assertDoesNotThrow(() -> bookingSystem.registerUser("Rainer", "rainer.zufall@test.de", "geheim", false));
    }

    @Test
    void testRegisterCostumerWithExistingEmail() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.registerUser("Rainer", "philipp@test.de", "geheim", false));
        assertEquals("Registration failed! ", e.getMessage());
    }

    @Test
    void testLoginWithValidCredentials() throws BusinessLogicException, EntityNotFoundException {
        assertEquals("Daniel", bookingSystem.checkLoginCredentials("daniel@test.de", "test").getUsername());
        assertEquals("daniel@test.de", bookingSystem.checkLoginCredentials("daniel@test.de", "test").getId());
    }

    @Test
    void testLoginWithInvalidPassword() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.checkLoginCredentials("daniel@test.de", "tes"));
        assertEquals("Access denied, invalid combination of email and password provided!", e.getMessage());
    }

    @Test
    void testLoginWithInvalidEmail() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.checkLoginCredentials("daniel@tes.de", "test"));
        assertEquals("Access denied, invalid combination of email and password provided!", e.getMessage());
    }

    @Test
    void testTransportsFilteredByLocationBetweenExistingLocations() {
        //One bus transport fully sold out
        assertEquals(1, bookingSystem.getTransportsFilteredByLocation(0, 2).size());
        assertEquals(0, bookingSystem.getTransportsFilteredByLocation(1, 2).size());
    }

    @Test
    void testTransportsFilteredByLocationFromExistingLocationToAnyLocation() {
        //One bus transport fully sold out
        assertEquals(2, bookingSystem.getTransportsFilteredByLocation(0, -1).size());
    }

    @Test
    void testTransportsFilteredByLocationFromAnyLocationToExistingLocation() {
        //One bus transport fully sold out
        assertEquals(1, bookingSystem.getTransportsFilteredByLocation(-1, 2).size());
    }

    @Test
    void testTransportsFilteredByLocationBetweenNonExistingLocations() {
        assertEquals(0, bookingSystem.getTransportsFilteredByLocation(5, 7).size());
    }

    @Test
    void testTransportsFilteredByHighPrice() {
        //One bus transport fully sold out
        assertEquals(2, bookingSystem.getTransportsFilteredByMaxPrice(100).size());
    }

    @Test
    void testTransportsFilteredByLowPrice() {
        //Bus ticket is 20 Euro
        assertEquals(1, bookingSystem.getTransportsFilteredByMaxPrice(15).size());
    }

    @Test
    void testTransportsFilteredByTooLowPrice() {
        //One bus transport fully sold out
        assertEquals(0, bookingSystem.getTransportsFilteredByMaxPrice(5).size());
    }

    @Test
    void testGetAllTicketsFromExistingCostumer() throws EntityNotFoundException {
        assertEquals(2, bookingSystem.getALlTickets(new Costumer("Daniel", "daniel@test.de", "test")).size());
    }

    @Test
    void testGetAllTicketsFromNonExistingCostumer() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.getALlTickets(new Costumer("Random", "random@test.de", "test")));
        assertEquals("User does not exist!", e.getMessage());
    }

    @Test
    void testGetBalanceFromExistingCostumer() throws EntityNotFoundException {
        assertEquals(40, bookingSystem.getBalance(new Costumer("Daniel", "daniel@test.de", "test")));
    }

    @Test
    void testGetBalanceFromNonExistingCostumer() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.getBalance(new Costumer("Random", "random@test.de", "test")));
        assertEquals("User does not exist!", e.getMessage());
    }

    @Test
    void testAddBalanceFromExistingCostumer() throws EntityNotFoundException {
        assertDoesNotThrow(() -> bookingSystem.addBalance(new Costumer("Daniel", "daniel@test.de", "test"), 20));
        assertEquals(60, bookingSystem.getBalance(new Costumer("Daniel", "daniel@test.de", "test")));
    }

    @Test
    void testAddBalanceFromNonExistingCostumer() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.addBalance(new Costumer("Random", "random@test.de", "test"), 20));
        assertEquals("User does not exist!", e.getMessage());
    }

    @Test
    void testReduceBalanceFromExistingCostumer() throws EntityNotFoundException {
        assertDoesNotThrow(() -> bookingSystem.reduceBalance(new Costumer("Daniel", "daniel@test.de", "test"), 20));
        assertEquals(20, bookingSystem.getBalance(new Costumer("Daniel", "daniel@test.de", "test")));
    }

    @Test
    void testReduceBalanceFromNonExistingCostumer() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.reduceBalance(new Costumer("Random", "random@test.de", "test"), 20));
        assertEquals("User does not exist!", e.getMessage());
    }

    @Test
    void testCreatingTicketWithNoProblems() throws EntityNotFoundException {
        assertDoesNotThrow(() -> bookingSystem.createTicket(new Costumer("Daniel", "daniel@test.de", "test"), 2, 2));
        assertEquals(3, bookingSystem.getALlTickets(new Costumer("Daniel", "daniel@test.de", "test")).size());
    }

    @Test
    void testCreatingTicketWithNonExistingCostumer() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createTicket(new Costumer("Random", "random@test.de", "test"), 0, 2));
        assertEquals("User does not exist!", e.getMessage());
    }

    @Test
    void testCreatingTicketWithNonExistingTransport() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createTicket(new Costumer("Daniel", "daniel@test.de", "test"), 9, 2));
        assertEquals("There does not exist any transport with the entered ID!", e.getMessage());
    }

    @Test
    void testCreatingTicketForSoldOutBusTransport() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.createTicket(new Costumer("Daniel", "daniel@test.de", "test"), 1, 2));
        assertEquals("We are sorry, this bus is sold out. Please try another transport!", e.getMessage());
    }

    @Test
    void testCreatingTicketForSoldOutFirstClassTrainTransport() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> {
            bookingSystem.addBalance(new Costumer("Daniel", "daniel@test.de", "test"), 100);
            bookingSystem.createTicket(new Costumer("Daniel", "daniel@test.de", "test"), 2, 1);
            bookingSystem.createTicket(new Costumer("Daniel", "daniel@test.de", "test"), 2, 1);
        });
        assertEquals("The train transport is sold out in first class, but second class still has seats left!", e.getMessage());
    }

    @Test
    void testCreatingTicketWithTooSmallBalance() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.createTicket(new Costumer("Daniel", "daniel@test.de", "test"), 2, 1));
        assertEquals("You do not have enough money to purchase this ticket!", e.getMessage());
    }

    @Test
    void testCancelTicketWithNoProblems() throws EntityNotFoundException {
        assertDoesNotThrow(() -> bookingSystem.removeTicket(new Costumer("Daniel", "daniel@test.de", "test"), 0));
        assertEquals(1,bookingSystem.getALlTickets(new Costumer("Daniel", "daniel@test.de", "test")).size());
    }

    @Test
    void testCancelTicketWithNonExistingTicket() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.removeTicket(new Costumer("Daniel", "daniel@test.de", "test"), 9));
        assertEquals("No ticket with this TicketNr exists!", e.getMessage());
    }

    @Test
    void testCancelTicketNotOwnedByCostumer() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.removeTicket(new Costumer("Daniel", "daniel@test.de", "test"), 2));
        assertEquals("You do not own a ticket with this TicketNr!", e.getMessage());
    }

    @Test
    void testCreateNewLocationNotYetExisting() {
        assertDoesNotThrow(() -> bookingSystem.createLocation(new Administrator("Philipp", "philipp@test.de", "test"), "Piata 1 Mai", "Cluj-Napoca"));
        assertEquals(4, bookingSystem.getLocations().size());
    }

    @Test
    void testCreatingLocationThatExists() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.createLocation(new Administrator("Philipp", "philipp@test.de", "test"), "Strada Mihail Kogălniceanu", "Cluj-Napoca"));
        assertEquals("Location already exists!", e.getMessage());
    }

    @Test
    void testCreatingBusTransportWithNoProblems() {
        assertDoesNotThrow(() -> bookingSystem.createBusTransport(new Administrator("Philipp", "philipp@test.de", "test"), 1, 2, 2024, 10, 13, 12, 0, 15, 0, 20));
        assertEquals(4, bookingSystem.getAllTransports().size());
    }

    @Test
    void testCreatingBusTransportWithSameLocationForOriginAndDestination() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.createBusTransport(new Administrator("Philipp", "philipp@test.de", "test"), 2, 2, 2024, 10, 13, 12, 0, 15, 0, 20));
        assertEquals("Origin and destination cannot be the same location!", e.getMessage());
    }

    @Test
    void testCreatingBusTransportWithNonExistingOrigin() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createBusTransport(new Administrator("Philipp", "philipp@test.de", "test"), 6, 2, 2024, 10, 13, 12, 0, 15, 0, 20));
        assertEquals("No location with the entered origin ID found!", e.getMessage());
    }

    @Test
    void testCreatingBusTransportWithNonExistingDestination() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createBusTransport(new Administrator("Philipp", "philipp@test.de", "test"), 2, 6, 2024, 10, 13, 12, 0, 15, 0, 20));
        assertEquals("No location with the entered destination ID found!", e.getMessage());
    }

    @Test
    void testCreatingBusTransportWithNonExistingLocations() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createBusTransport(new Administrator("Philipp", "philipp@test.de", "test"), 5, 6, 2024, 10, 13, 12, 0, 15, 0, 20));
        assertEquals("No locations with the entered origin and destination IDs found!", e.getMessage());
    }

    @Test
    void testCreatingTrainTransportWithNoProblems() {
        assertDoesNotThrow(() -> bookingSystem.createTrainTransport(new Administrator("Philipp", "philipp@test.de", "test"), 1, 2, 2024, 10, 13, 12, 0, 15, 0, 2, 10));
        assertEquals(4, bookingSystem.getAllTransports().size());
    }

    @Test
    void testCreatingTrainTransportWithSameLocationForOriginAndDestination() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.createTrainTransport(new Administrator("Philipp", "philipp@test.de", "test"), 2, 2, 2024, 10, 13, 12, 0, 15, 0, 2, 10));
        assertEquals("Warning! Origin and destination cannot be the same location!", e.getMessage());
    }

    @Test
    void testCreatingTrainTransportWithNonExistingOrigin() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createTrainTransport(new Administrator("Philipp", "philipp@test.de", "test"), 6, 2, 2024, 10, 13, 12, 0, 15, 0, 2, 10));
        assertEquals("No location with the entered origin ID found!", e.getMessage());
    }

    @Test
    void testCreatingTrainTransportWithNonExistingDestination() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createTrainTransport(new Administrator("Philipp", "philipp@test.de", "test"), 2, 6, 2024, 10, 13, 12, 0, 15, 0, 2, 10));
        assertEquals("No location with the entered destination ID found!", e.getMessage());
    }

    @Test
    void testCreatingTrainTransportWithNonExistingLocations() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.createTrainTransport(new Administrator("Philipp", "philipp@test.de", "test"), 5, 6, 2024, 10, 13, 12, 0, 15, 0, 2, 10));
        assertEquals("No locations with the entered origin and destination IDs found!", e.getMessage());
    }

    @Test
    void testRemoveTransportWithNoProblems() throws BusinessLogicException, EntityNotFoundException {
        assertDoesNotThrow(() -> bookingSystem.removeTransport(new Administrator("Philipp", "philipp@test.de", "test"), 0));
        Administrator admin = (Administrator) bookingSystem.checkLoginCredentials("philipp@test.de", "test");
        assertEquals(1, admin.getAllAdministeredTransports().size());
        assertEquals(2, bookingSystem.getAllTransports().size());
    }

    @Test
    void testRemoveNonExistingTransport() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.removeTransport(new Administrator("Philipp", "philipp@test.de", "test"), 5));
        assertEquals("No transport with this ID exists in the repository!", e.getMessage());
    }

    @Test
    void testRemoveTransportNotManagedByAdministrator() {
        BusinessLogicException e = assertThrows(BusinessLogicException.class, () -> bookingSystem.removeTransport(new Administrator("Philipp", "philipp@test.de", "test"), 2));
        assertEquals("You are not authorized to remove this transport since you do not manage it!", e.getMessage());
    }

    @Test
    void testGetAllTicketsForTransportWithNoProblems() {
        assertDoesNotThrow(() -> assertEquals(2, bookingSystem.getAllTransportTickets(new Administrator("Philipp", "philipp@test.de", "test"), 1).size()));
    }

    @Test
    void testGetAllTicketsForTransportNotExisting() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> bookingSystem.getAllTransportTickets(new Administrator("Philipp", "philipp@test.de", "test"), 5));
        assertEquals("No transport with this ID exists!", e.getMessage());
    }

}

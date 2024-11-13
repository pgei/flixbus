package main.java.service;

import main.java.model.*;
import main.java.repository.IRepository;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class BookingSystem {

    private final IRepository<Person> personRepository;
    private final IRepository<Transport> transportRepository;
    private final IRepository<Ticket> ticketRepository;
    private final IRepository<Location> locationRepository;

    protected static final int PRICE_BUS = 120;
    protected static final int PRICE_1ST_TRAIN = 180;
    protected static final int PRICE_2ND_TRAIN = 80;

    private int ticketIdCount;
    private int transportIdCount;
    private int locationIdCount;

    public BookingSystem(IRepository<Person> person, IRepository<Transport> transport, IRepository<Ticket> ticket, IRepository<Location> location) {
        this.personRepository = person;
        this.transportRepository = transport;
        this.ticketRepository = ticket;
        this.locationRepository = location;
        this.ticketIdCount = person.getAll().size();
        this.transportIdCount = transport.getAll().size();
        this.locationIdCount = location.getAll().size();

    }

    public boolean registerUser(String name, String email, String password, boolean admin) {
        //First check that ID is unique
        if (!this.personRepository.containsKey(email)) {
            if (admin) {
                Administrator newadmin = new Administrator(name, email, password);
                this.personRepository.create(newadmin);
            } else {
                Costumer newcostumer = new Costumer(name, email, password);
                this.personRepository.create(newcostumer);
            }
            return true;
        } else return false;

    };

    public Person checkLoginCredentials(String email, String password) {
        Person person = this.personRepository.get(email);
        if (person != null && person.isAuthentic(password)) {
            return person;
        } else return null;
    }

    public List<Transport> getAllTransports() {
        return this.transportRepository.getAll();
    }

    public List<Location> getLocations() {
        return this.locationRepository.getAll();
    }

    public List<Ticket> getALlTickets(Costumer costumer) {
        return costumer.getAllTickets();
    }

    public int getBalance(Costumer costumer) {
        return costumer.getBalance();
    }

    private void addBalance(Costumer costumer, int money) {
        costumer.setBalance(costumer.getBalance()+money);
        this.personRepository.update(costumer);
    }

    private void reduceBalance(Costumer costumer, int money) {
        costumer.setBalance(costumer.getBalance()-money);
        this.personRepository.update(costumer);
    }

    public boolean createTicket(Costumer costumer, int transportid, int ticketclass) {
        Transport transport = this.transportRepository.get(transportid);
        if (transport == null) {
            return false;
        } else {
            if (transport instanceof Bus){
                if ((((Bus) transport).getCapacity() > 0 && costumer.getBalance() >= PRICE_BUS)) {
                    //Einzigartige ID für Ticket finden
                    while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                        this.ticketIdCount++;
                    }
                    int seat = ((Bus) transport).getCapacity();
                    //Ticket erstellen und in Repository hinterlegen
                    BusTicket ticket = new BusTicket(this.ticketIdCount, costumer, transport, PRICE_BUS, seat);
                    this.ticketRepository.create(ticket);
                    //Aktualisierung von Transport und Kunde
                    ((Bus) transport).setCapacity(((Bus) transport).getCapacity()-1);
                    ((Bus) transport).getBookedSeats().put(seat, ticket);
                    costumer.getAllTickets().add(ticket);
                    reduceBalance(costumer, PRICE_BUS);
                    this.personRepository.update(costumer);
                    this.transportRepository.update(transport);
                    return true;
                }
                return false;
            } else if (transport instanceof Train && ticketclass == 1) {
                if (((Train) transport).getFirstCapacity() > 0 && costumer.getBalance() >= PRICE_1ST_TRAIN) {
                    //Einzigartige ID für Ticket finden
                    while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                        this.ticketIdCount++;
                    }
                    int seat = Integer.parseInt("1"+((Train) transport).getFirstCapacity());
                    //Ticket erstellen und in Repository hinterlegen
                    TrainTicket ticket = new TrainTicket(this.ticketIdCount, costumer, transport, PRICE_1ST_TRAIN, seat, 1);
                    this.ticketRepository.create(ticket);
                    //Aktualisierung von Transport und Kunde
                    ((Train) transport).setFirstCapacity(((Train) transport).getFirstCapacity()-1);
                    ((Train) transport).getBookedSeats().put(seat, ticket);
                    costumer.getAllTickets().add(ticket);
                    reduceBalance(costumer, PRICE_1ST_TRAIN);
                    this.personRepository.update(costumer);
                    this.transportRepository.update(transport);
                    return true;
                }
                return false;
            } else if (transport instanceof Train && ticketclass == 2) {
                if (((Train) transport).getSecondCapacity() > 0 && costumer.getBalance() >= this.PRICE_2ND_TRAIN) {
                    //Einzigartige ID für Ticket finden
                    while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                        this.ticketIdCount++;
                    }
                    int seat = Integer.parseInt("2"+((Train) transport).getSecondCapacity());
                    //Ticket erstellen und in Repository hinterlegen
                    TrainTicket ticket = new TrainTicket(this.ticketIdCount, costumer, transport, PRICE_2ND_TRAIN, seat, 2);
                    this.ticketRepository.create(ticket);
                    //Aktualisierung von Transport und Kunde
                    ((Train) transport).setSecondCapacity(((Train) transport).getSecondCapacity()-1);
                    ((Train) transport).getBookedSeats().put(seat, ticket);
                    costumer.getAllTickets().add(ticket);
                    reduceBalance(costumer, PRICE_2ND_TRAIN);
                    this.personRepository.update(costumer);
                    this.transportRepository.update(transport);
                    return true;
                }
                return false;
            } else {
                return false;
            }
        }
    }

    public boolean removeTicket(int id) {
        Ticket ticket = this.ticketRepository.get(id);
        if (ticket == null) {
            return false;
        }

        //Transport aktualisieren
        if (ticket.getTransport() instanceof Bus) {
            Bus transport = (Bus) ticket.getTransport();
            HashMap<Integer, BusTicket> mapTickets = transport.getBookedSeats();
            //Ticket entfernen
            mapTickets.remove(ticket.getSeat());
            transport.setCapacity(transport.getCapacity()+1);
            //Ticket abrufen, dass am neusten ist, und an Stelle des entfernten Tickets schreiben
            BusTicket changeticket = mapTickets.get(transport.getCapacity());
            mapTickets.remove(changeticket.getSeat());
            mapTickets.put(ticket.getSeat(),changeticket);
            this.transportRepository.update(transport);
        } else if (ticket.getTransport() instanceof Train) {
            Train transport = (Train) ticket.getTransport();
            HashMap<Integer, TrainTicket> mapTickets = transport.getBookedSeats();
            //Ticket entfernen
            mapTickets.remove(ticket.getSeat());
            if (((TrainTicket) ticket).getTicketClass() == 1) {
                transport.setFirstCapacity(transport.getFirstCapacity()+1);
                //Ticket abrufen, dass am neusten ist, und an Stelle des entfernten Tickets schreiben
                TrainTicket changeticket = mapTickets.get(transport.getFirstCapacity());
                mapTickets.remove(changeticket.getSeat());
                mapTickets.put(ticket.getSeat(),changeticket);
                this.transportRepository.update(transport);
            } else {
                transport.setSecondCapacity(transport.getSecondCapacity()+1);
                //Ticket abrufen, dass am neusten ist, und an Stelle des entfernten Tickets schreiben
                TrainTicket changeticket = mapTickets.get(transport.getSecondCapacity());
                mapTickets.remove(changeticket.getSeat());
                mapTickets.put(ticket.getSeat(),changeticket);
                this.transportRepository.update(transport);
            }
        } else return false;

        //Kunde aktualisieren
        Costumer costumer = ticket.getCostumer();
        costumer.getAllTickets().remove(ticket);
        //Servicegebühr in Höhe von 10% des Ticketpreises wird einbehalten
        addBalance(costumer, (int) (ticket.getPrice()*0.9));
        this.personRepository.update(costumer);
        this.ticketRepository.delete(ticket);
        return true;
    }

    public void createLocation(String street, String city) {
        while (this.locationRepository.containsKey(this.locationIdCount)) {
            this.locationIdCount++;
        }
        //Ticket erstellen und in Repository hinterlegen
        Location loc = new Location(this.locationIdCount, street, city);
        this.locationRepository.create(loc);
    }

    public void createBusTransport(Location origin, Location destination, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) {
        while (this.transportRepository.containsKey(this.transportIdCount)) {
            this.transportIdCount++;
        }
        //Ticket erstellen und in Repository hinterlegen
        Bus transport = new Bus(this.transportIdCount, origin, destination, year, month, day, hourd, mind, houra, mina, capacity);
        this.transportRepository.create(transport);
    }

    public void createTrainTransport(Location origin, Location destination, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) {
        while (this.transportRepository.containsKey(this.transportIdCount)) {
            this.transportIdCount++;
        }
        //Ticket erstellen und in Repository hinterlegen
        Train transport = new Train(this.transportIdCount, origin, destination, year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
        this.transportRepository.create(transport);
    }

    public boolean removeTransport(Administrator admin, int id) {
        Transport transport = this.transportRepository.get(id);
        if (transport == null) {
            return false;
        } else if (admin.getAllAdministeredTransports().contains(transport)){
            admin.getAllAdministeredTransports().remove(transport);
            this.personRepository.update(admin);
            getAllTransportTickets(id).forEach(ticket -> {
                ticket.getCostumer().getAllTickets().remove(ticket);
                //Rückerstattung des vollen Ticketpreises
                addBalance(ticket.getCostumer(),ticket.getPrice());
                this.personRepository.update(ticket.getCostumer());
                this.ticketRepository.delete(ticket);
            });
            this.transportRepository.delete(transport);
            return true;
        } else return false;
    }

    public List<Ticket> getAllTransportTickets(int id) {
        Transport transport = this.transportRepository.get(id);
        if (transport == null) {
            return null;
        } else if (transport instanceof Bus){
            return ((Bus)transport).getBookedSeats().values().stream().collect(Collectors.toUnmodifiableList());
        } else if (transport instanceof Train){
            return ((Train)transport).getBookedSeats().values().stream().collect(Collectors.toUnmodifiableList());
        } else return null;
    }

}

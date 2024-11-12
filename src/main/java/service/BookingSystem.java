package main.java.service;

import main.java.model.*;
import main.java.repository.IRepository;

import java.util.List;

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

    public void registerUser(String name, String email, String password, boolean admin) {
        //First check that ID is unique
        if (!this.personRepository.containsKey(email)) {
            if (admin) {
                Administrator newadmin = new Administrator(name, email, password);
                this.personRepository.create(newadmin);
            } else {
                Costumer newcostumer = new Costumer(name, email, password);
                this.personRepository.create(newcostumer);
            }
        }
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
        Transport transport = ticket.getTransport();
        if (transport instanceof Bus) {
            //todo: entferne Ticket und erhöhe Kapazität
        } else if (transport instanceof Train) {
            //todo: entferne Ticket und erhöhe Kapazität
        } else return false;
        this.transportRepository.update(transport);
        //Kunde aktualisieren
        Costumer costumer = ticket.getCostumer();
        costumer.getAllTickets().remove(ticket);
        //Servicegebühr in Höhe von 10% des Ticketpreises wird einbehalten
        addBalance(costumer, (int) (ticket.getPrice()*0.9));
        this.personRepository.update(costumer);
        this.ticketRepository.delete(ticket);
        return true;
    }

    public void createLocation() {}

    public void createTransport() {}

    public void removeTransport() {}

    public void getAllTransportTickets() {}

}

package main.java.service;

import main.java.exceptions.BusinessLogicException;
import main.java.exceptions.DatabaseException;
import main.java.exceptions.EntityNotFoundException;
import main.java.model.*;
import main.java.repository.IRepository;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Verwaltungsklasse, die die Logik für Verwaltung der Daten in der Anwendung zur Verfügung stellt.
 */
public class BookingSystem {

    private final IRepository<Person> personRepository;
    private final IRepository<Transport> transportRepository;
    private final IRepository<Ticket> ticketRepository;
    private final IRepository<Location> locationRepository;

    /**
     * Fixer Preis für ein Busticket
     */
    protected static final int PRICE_BUS = 20;

    /**
     * Fixer Preis für ein Zugticket in der ersten Klasse
     */
    protected static final int PRICE_1ST_TRAIN = 50;

    /**
     * Fixer Preis für ein Zugticket in der zweiten Klasse
     */
    protected static final int PRICE_2ND_TRAIN = 15;

    /**
     * Speichert die letzte freie, einzigartige ID die für ein Ticket vergeben wurde
     */
    private int ticketIdCount;

    /**
     * Speichert die letzte freie, einzigartige ID die für einen Transport vergeben wurde
     */
    private int transportIdCount;

    /**
     * Speichert die letzte freie, einzigartige ID die für einen Ort vergeben wurde
     */
    private int locationIdCount;

    /**
     * Konstruktor, der neues Buchungssystem mit gegebenen Repositories aufsetzt
     *
     * @param person    Repository das für Personen genutzt werden soll
     * @param transport Repository das für Transporte genutzt werden soll
     * @param ticket    Repository das für Tickets genutzt werden soll
     * @param location  Repository das für Orte genutzt werden soll
     */
    public BookingSystem(IRepository<Person> person, IRepository<Transport> transport, IRepository<Ticket> ticket, IRepository<Location> location) {
        this.personRepository = person;
        this.transportRepository = transport;
        this.ticketRepository = ticket;
        this.locationRepository = location;
        this.ticketIdCount = ticket.getAll().size();
        this.transportIdCount = transport.getAll().size();
        this.locationIdCount = location.getAll().size();
    }

    /**
     * Methode die einen neuen Anwenderaccount erstellt.
     * Dabei wird zuerst kontrolliert, ob die gegebene E-Mail-Adress einzigartig ist, da diese als Schlüssel im Repository genutzt wird
     *
     * @param name                      Anwendername
     * @param email                     E-Mail-Adresse, muss einzigartig sein da sie auch als Schlüssel genutzt werden soll
     * @param password                  Passwort das bei Login verwendet werden soll
     * @param admin                     Administrator-Objekt wird erstellt wenn wahr, sonst wird Kunde erstellt
     * @throws BusinessLogicException   Wenn bereits ein Account mit der gegebenen E-Mail existiert
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void registerUser(String name, String email, String password, boolean admin) throws BusinessLogicException, DatabaseException {
        // Passwort wird gehasht
        String hashedPassword = PasswordHasher.hashPassword(password);
        // Prufen ob ID Unique ist
        if (!this.personRepository.containsKey(email)) {
            if (admin) {
                Administrator newadmin = new Administrator(name, email, hashedPassword);
                this.personRepository.create(newadmin);
            } else {
                Costumer newcostumer = new Costumer(name, email, hashedPassword);
                this.personRepository.create(newcostumer);
            }
        } else {
            throw new BusinessLogicException("Registration failed! ");
        }
    }

    /**
     * Methode die überprüft, ob Person im Repository existiert zu der gegebenen E-Mail-Adresse und, falls dem so ist, auch kontrolliert ob das Passwort zu der Person stimmt
     *
     * @param email                     E-Mail-Adresse die zur Person gehört (Schlüssel)
     * @param hashedPassword                  Passwort der Person
     * @return                          Das Personen-Objekt, falls Passwort authentisch ist
     * @throws BusinessLogicException   Wenn das gegebene Passwort nicht korrekt ist
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt mit der gegebenen E-Mail-Adresse im Repository gefunden wurde
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public Person checkLoginCredentials(String email, String hashedPassword) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        Person person = this.personRepository.get(email);
        if (person != null) {
            if (person.isAuthentic(hashedPassword)) {
                return person;
            } else throw new BusinessLogicException("Access denied, invalid password provided!");
        } else throw new EntityNotFoundException("No user with this email exists!");
    }

    /**
     * Methode die alle Transporte zurückgibt, die im Transport-Repository gespeichert sind
     *
     * @return                      Liste aller Transporte im Repository
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Transport> getAllTransports() throws DatabaseException {
        return this.transportRepository.getAll();
    }

    /**
     * Methode, die alle Transporte zurückgibt, welche an vorgegebenen Orten starten bzw. enden und noch freie Kapazität haben.
     * Dabei ist es auch möglich nur mit dem Abfahrtsort bzw. Ankunftsort zu filtern sowie, wenn keiner der beiden vorgegeben wurde, nur auf verfügbare Kapazität.
     *
     * @param origin                ID des Ortes, an dem der Transport starten muss (-1 bedeutet egal)
     * @param destination           ID des Ortes, an dem der Transport enden muss (-1 bedeutet egal)
     * @return                      Liste aller Transporte die Suchkriterien erfüllen
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Transport> getTransportsFilteredByLocation(int origin, int destination) throws DatabaseException {
        if (origin == -1 && destination == -1) {
            //Fall filtern nur auf Kapazität
            List<Transport> filtered = new ArrayList<>();
            this.transportRepository.getAll().forEach(transport -> {
                if (transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        } else if (origin == -1) {
            //Fall filtern auf Ankunftsort und Kapazität
            List<Transport> filtered = new ArrayList<>();
            this.transportRepository.getAll().forEach(transport -> {
                if (transport.getDestination() == destination && transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        } else if (destination == -1) {
            //Fall filtern auf Abfahrtsort und Kapazität
            List<Transport> filtered = new ArrayList<>();
            this.transportRepository.getAll().forEach(transport -> {
                if (transport.getOrigin() == origin && transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        } else {
            //Fall filtern auf Abfahrtsort und Ankunftsort sowie Kapazität
            List<Transport> filtered = new ArrayList<>();
            this.transportRepository.getAll().forEach(transport -> {
                if ( transport.getOrigin() == origin && transport.getDestination() == destination && transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        }
    }

    /**
     * Methode, die alle Transporte zurückgibt, die noch freie Kapazität haben die zu einem Preis kleiner gleich dem gegebenem verkauft werden.
     *
     * @param price                 Maximaler Preis nach dem Transporte gefiltert werden sollen
     * @return                      Liste aller Transporte, die das Suchkriterium erfüllen
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Transport> getTransportsFilteredByMaxPrice(int price) throws DatabaseException {
        List<Transport> filtered = new ArrayList<>();
        this.transportRepository.getAll().forEach(transport -> {
            if (transport instanceof Bus && transport.getCapacity() > 0 && price >= PRICE_BUS) {
                filtered.add(transport);
            } else if (transport instanceof Train && ((Train) transport).getSecondCapacity() > 0 && price >= PRICE_2ND_TRAIN) {
                filtered.add(transport);
            } else if (transport instanceof Train && ((Train) transport).getFirstCapacity() > 0 && price >= PRICE_1ST_TRAIN) {
                filtered.add(transport);
            }
        });
        return filtered;
    }

    /**
     * Methode die alle Orte zurückgibt, die im Location-Repository gespeichert sind
     *
     * @return                      Liste aller Orte im Repository
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Location> getLocations() throws DatabaseException{
        return this.locationRepository.getAll();
    }

    /**
     * Methode die alle Tickets zurückgibt, die ein spezifischer Kunde gekauft hat
     *
     * @param costumer                  Kunde, von dem alle Tickets zurückgegeben werden sollen
     * @return                          Liste aller Tickets, die von gegebenem Kunden gekauft wurden
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     *
     */
    public List<Ticket> getALlTickets(Costumer costumer) throws EntityNotFoundException, DatabaseException {
        Costumer found = (Costumer) this.personRepository.get(costumer.getId());
        if (found == null) {
            throw new EntityNotFoundException("User does not exist!");
        } else {
            return found.getAllTickets();
        }
    }

    /**
     * Methode die das Guthaben eines gegebenen Kunden zurückgibt
     *
     * @param costumer                  Kunde, für den das Guthaben abgefragt werden soll
     * @return                          Guthaben auf dem Konto des Kunden
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     *
     */
    public int getBalance(Costumer costumer) throws EntityNotFoundException, DatabaseException {
        Costumer found = (Costumer) this.personRepository.get(costumer.getId());
        if (found == null) {
            throw new EntityNotFoundException("User does not exist!");
        } else {
            return found.getBalance();
        }
    }

    /**
     * Methode die das Guthaben eines Kunden um gegebenen Betrag erhöht
     *
     * @param costumer                  Kunde, dessen Guthaben erhöht werden soll
     * @param money                     Euro-Betrag um den das Guthaben steigen soll
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void addBalance(Costumer costumer, int money) throws EntityNotFoundException, DatabaseException {
        Costumer found = (Costumer) this.personRepository.get(costumer.getId());
        if (found == null) {
            throw new EntityNotFoundException("User does not exist!");
        } else {
            found.setBalance(found.getBalance() + money);
            this.personRepository.update(found);
        }
    }

    /**
     * Methode die das Guthaben eines Kunden um gegebenen Betrag reduziert
     *
     * @param costumer                  Kunde, dessen Guthaben reduziert werden soll
     * @param money                     Euro-Betrag mit dem das Guthaben belastet werden soll
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void reduceBalance(Costumer costumer, int money) throws EntityNotFoundException, DatabaseException {
        Costumer found = (Costumer) this.personRepository.get(costumer.getId());
        if (found == null) {
            throw new EntityNotFoundException("User does not exist!");
        } else {
            found.setBalance(found.getBalance() - money);
            this.personRepository.update(found);
        }
    }

    /**
     * Methode die ein Ticket auf einem Transport reserviert, falls dies möglich ist.
     * Zur Überprüfung dessen wird zuerst kontrolliert, ob überhaupt ein Transport mit der angegebenen ID existiert.
     * Ist dies der Fall, wird im nächsten Schritt versucht, das Ticket zu erstellen, wobei die Sitznummer bei Erfolg wie folgt vergeben wird:
     *
     * <ul>
     *   <li>Bus: die Sitzplätze sind nach der Anzahl an Sitzplätzen durchnummeriert und werden in absteigender Reihenfolge vergeben </li>
     *   <li>Zug: die Sitzplätze sind wie bei Bus durchnummeriert für jede Klasse, um die Klasse zu kennzeichen wird der Sitznummer eine 1 respektive 2 voraus gestellt</li>
     * </ul>
     * <p>
     * Beispiel: wenn die erste Klasse im Zug 5 Sitzplätze hat, dann wird der erste vergebene Sitzplatz 15 sein und der letzte 11
     *
     * @param costumer                  Kunde der das Ticket buchen will
     * @param transportid               ID des Transports, auf dem ein Ticket reserviert werden soll
     * @param ticketclass               Klasse, in der ein Ticket reserviert werden soll, bei Bustransporten wird dies ignoriert da Busse nur eine Klasse haben
     * @throws BusinessLogicException   Wenn der Transport keine freien Plätze mehr hat oder der Kunde zu wenig Guthaben hat
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt oder kein Transport mit der gegebenen ID gefunden werden kann
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void createTicket(Costumer costumer, int transportid, int ticketclass) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        Transport transport = this.transportRepository.get(transportid);
        if (transport == null) throw new EntityNotFoundException("There does not exist any transport with the entered ID!");
        Costumer upToDateCostumer = (Costumer) this.personRepository.get(costumer.getId());
        if (upToDateCostumer == null) {
            throw new EntityNotFoundException("User does not exist!");
        } else {
            switch (transport) {
                case Bus bus -> addBusTicket(upToDateCostumer, bus);
                case Train train when ticketclass == 1 -> addFirstClassTrainTicket(upToDateCostumer, train);
                case Train train when ticketclass == 2 -> addSecondClassTrainTicket(upToDateCostumer, train);
                default -> throw new RuntimeException("Unknown class of transport!");
            }
        }
    }

    /**
     * Methode die ein Ticket für einen Bustransport erstellt, falls dies möglich ist.
     * Dabei wird zuerst überprüft, ob es noch freie Sitzplätze auf dem Transport gibt, sowie ob der Kunde noch genug Guthaben hat, um das Ticket zu bezahlen.
     *
     * @param costumer                  Kunde der das Ticket buchen will
     * @param bus                       Bustransport auf dem das Ticket gültig sein soll
     * @throws BusinessLogicException   Wenn der Bus keine freien Plätze mehr hat oder der Kunde zu wenig Guthaben hat
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    private void addBusTicket(Costumer costumer, Bus bus) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        if ((bus.getCapacity() > 0 && costumer.getBalance() >= PRICE_BUS)) {
            //Einzigartige ID für Ticket finden
            while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                this.ticketIdCount++;
            }
            int seat = bus.getCapacity();
            //Ticket erstellen und in Repository hinterlegen
            BusTicket ticket = new BusTicket(this.ticketIdCount, (String) costumer.getId(), (int) bus.getId(), PRICE_BUS, seat);
            this.ticketRepository.create(ticket);
            //Aktualisierung von Transport und Kunde
            bus.setCapacity(bus.getCapacity() - 1);
            bus.getBookedSeats().put(seat, ticket);
            costumer.getAllTickets().add(ticket);
            this.personRepository.update(costumer);
            reduceBalance(costumer, PRICE_BUS);
            this.transportRepository.update(bus);
        } else if (bus.getCapacity() == 0) {
            throw new BusinessLogicException("We are sorry, this bus is sold out. Please try another transport!");
        } else if (costumer.getBalance() < PRICE_BUS) {
            throw new BusinessLogicException("You do not have enough money to purchase this ticket!");
        }
    }

    /**
     * Methode die ein 1. Klasse-Ticket für einen Zugtransport erstellt, falls dies möglich ist.
     * Dabei wird zuerst überprüft, ob es noch freie Sitzplätze auf dem Transport in der 1. Klasse gibt, sowie ob der Kunde noch genug Guthaben hat, um das Ticket zu bezahlen.
     *
     * @param costumer                  Kunde der das Ticket buchen will
     * @param train                     Zugtransport auf dem das Ticket gültig sein soll
     * @throws BusinessLogicException   Wenn der Zug keine freien Plätze mehr hat oder der Kunde zu wenig Guthaben hat
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     *
     */
    private void addFirstClassTrainTicket(Costumer costumer, Train train) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        if (train.getFirstCapacity() > 0 && costumer.getBalance() >= PRICE_1ST_TRAIN) {
            //Einzigartige ID für Ticket finden
            while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                this.ticketIdCount++;
            }
            int seat = Integer.parseInt("1" + train.getFirstCapacity());
            //Ticket erstellen und in Repository hinterlegen
            TrainTicket ticket = new TrainTicket(this.ticketIdCount, (String) costumer.getId(), (int) train.getId(), PRICE_1ST_TRAIN, seat, 1);
            this.ticketRepository.create(ticket);
            //Aktualisierung von Transport und Kunde
            train.setFirstCapacity(train.getFirstCapacity() - 1);
            train.getBookedSeats().put(seat, ticket);
            costumer.getAllTickets().add(ticket);
            this.personRepository.update(costumer);
            reduceBalance(costumer, PRICE_1ST_TRAIN);
            this.transportRepository.update(train);
        } else if (train.getCapacity() == 0) {
            throw new BusinessLogicException("The train transport is sold out. Please try another transport!");
        } else if (train.getFirstCapacity() == 0) {
            throw new BusinessLogicException("The train transport is sold out in first class, but second class still has seats left!");
        } else if (costumer.getBalance() < PRICE_1ST_TRAIN) {
            throw new BusinessLogicException("You do not have enough money to purchase this ticket!");
        }
    }

    /**
     * Methode die ein 2. Klasse-Ticket für einen Zugtransport erstellt, falls dies möglich ist.
     * Dabei wird zuerst überprüft, ob es noch freie Sitzplätze auf dem Transport in der 2. Klasse gibt, sowie ob der Kunde noch genug Guthaben hat, um das Ticket zu bezahlen.
     *
     * @param costumer                  Kunde der das Ticket buchen will
     * @param train                     Zugtransport auf dem das Ticket gültig sein soll
     * @throws BusinessLogicException   Wenn der Zug keine freien Plätze mehr hat oder der Kunde zu wenig Guthaben hat
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    private void addSecondClassTrainTicket(Costumer costumer, Train train) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        if (train.getSecondCapacity() > 0 && costumer.getBalance() >= PRICE_2ND_TRAIN) {
            //Einzigartige ID für Ticket finden
            while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                this.ticketIdCount++;
            }
            int seat = Integer.parseInt("2" + train.getSecondCapacity());
            //Ticket erstellen und in Repository hinterlegen
            TrainTicket ticket = new TrainTicket(this.ticketIdCount, (String) costumer.getId(), (int) train.getId(), PRICE_2ND_TRAIN, seat, 2);
            this.ticketRepository.create(ticket);
            //Aktualisierung von Transport und Kunde
            train.setSecondCapacity(train.getSecondCapacity() - 1);
            train.getBookedSeats().put(seat, ticket);
            costumer.getAllTickets().add(ticket);
            this.personRepository.update(costumer);
            reduceBalance(costumer, PRICE_2ND_TRAIN);
            this.transportRepository.update(train);
        } else if (train.getCapacity() == 0) {
            throw new BusinessLogicException("The train transport is sold out and does not have any capacity left!");
        } else if (train.getSecondCapacity() == 0) {
            throw new BusinessLogicException("The train transport is sold out in second class, but first class still has seats left!");
        } else if (costumer.getBalance() < PRICE_2ND_TRAIN) {
            throw new BusinessLogicException("You do not have enough money left in your account to purchase this ticket!");
        }
    }

    /**
     * Methode die ein Ticket storniert und von dem Transport entfernt.
     * Zuerst wird kontrolliert, ob ein Ticket im Repository unter der Ticket-Nummer gefunden werden kann und ob das Ticket überhaupt von dem gegebenen Kunden gebucht worden ist.
     * Ist dies der Fall, wird das Ticket aus dem Repository gelöscht und ebenfalls beim Kunden und dem Transport aus der Liste entfernt.
     * Der Kunde erhält in diesem Zusammenhang eine Gutschrift des Ticketpreises abzüglich einer Servicegebühr von 10%.
     * Um die Reihenfolge der Vergabe der Sitzplätze bei Buchung neuer Tickets nicht durcheinanderzubringen, wird das neuste Ticket (also kleinste Sitznummer) an die Stelle des gelöschten Tickets geschrieben.
     * Dh. der Sitzplatz des neusten Tickets ändert sich zu dem Sitzplatz des entfernten Tickets.
     *
     * @param costumer                  Kunde der ein Ticket entfernen will
     * @param id                        Ticket-Nummer des Tickets das entfernt werden soll
     * @throws BusinessLogicException   Wenn der Kunde das Ticket nicht besitzt
     * @throws EntityNotFoundException  Wenn kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt oder kein Ticket im Repository mit der gegebenen ID existiert
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void removeTicket(Costumer costumer, int id) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        Ticket ticket = this.ticketRepository.get(id);
        if (ticket == null) throw new EntityNotFoundException("No ticket with this TicketNr exists!");
        //Kontrolle, ob Ticket wirklich dem Kunden gehört
        if (!ticket.getCostumer().equals(costumer.getId())) {
            throw new BusinessLogicException("You do not own a ticket with this TicketNr!");
        }
        //Transport aktualisieren
        if (ticket instanceof BusTicket) {
            removeTicketFromBusTransport(id);
        } else if (ticket instanceof TrainTicket) {
            removeTicketFromTrainTransport(id);
        } else throw new RuntimeException("Ticket has unknown class of transport!");
        //Kunde aktualisieren
        Costumer upToDateCostumer = (Costumer) this.personRepository.get(costumer.getId());
        upToDateCostumer.getAllTickets().removeIf(ticket1 -> ticket1.getId().equals(id));
        //Servicegebühr in Höhe von 10% des Ticketpreises wird einbehalten
        this.personRepository.update(upToDateCostumer);
        addBalance(upToDateCostumer, (int) (ticket.getPrice() * 0.9));
        this.ticketRepository.delete(id);
    }

    /**
     * Methode, die ein gegebenes Ticket von einem Bustransport entfernt.
     * Wenn weitere Tickets auf dem Transport existieren, wird das neueste abgerufen und an Stelle des entfernten Tickets geschrieben.
     *
     * @param id                    ID des Tickets das entfernt werden soll
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    private void removeTicketFromBusTransport(int id) throws DatabaseException {
        Ticket ticket = this.ticketRepository.get(id);
        Bus transport = (Bus) this.transportRepository.get(ticket.getTransport());
        HashMap<Integer, BusTicket> mapTickets = transport.getBookedSeats();
        //Ticket entfernen
        mapTickets.remove(ticket.getSeat());
        transport.setCapacity(transport.getCapacity() + 1);
        //Ticket abrufen, dass am neusten ist, wenn eines existiert, und an Stelle des entfernten Tickets schreiben
        if (!mapTickets.isEmpty()) {
            BusTicket changeticket = mapTickets.get(transport.getCapacity());
            mapTickets.remove(changeticket.getSeat());
            changeticket.setSeat(ticket.getSeat());
            mapTickets.put(ticket.getSeat(), changeticket);
            this.ticketRepository.update(changeticket);
            Costumer costumer = (Costumer) this.personRepository.get(changeticket.getCostumer());
            costumer.getAllTickets().forEach(ticket1 -> {
                if (ticket1.getId().equals(changeticket.getId())) {
                    ticket1.setSeat(ticket.getSeat());
                }
             });
            this.personRepository.update(costumer);
        }
        this.transportRepository.update(transport);
    }

    /**
     * Methode, die ein gegebenes Ticket von einem Zugtransport entfernt.
     * Wenn weitere Tickets auf dem Transport existieren, wird das neueste der aktuellen Klasse abgerufen und an Stelle des entfernten Tickets geschrieben.
     *
     * @param id                    ID des Tickets das entfernt werden soll
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    private void removeTicketFromTrainTransport(int id) throws DatabaseException {
        Ticket ticket = this.ticketRepository.get(id);
        Train transport = (Train) this.transportRepository.get(ticket.getTransport());
        HashMap<Integer, TrainTicket> mapTickets = transport.getBookedSeats();
        //Ticket entfernen
        mapTickets.remove(ticket.getSeat());
        if (((TrainTicket) ticket).getTicketClass() == 1) {
            transport.setFirstCapacity(transport.getFirstCapacity() + 1);
            //Ticket abrufen, dass am neusten ist, wenn eines existiert, und an Stelle des entfernten Tickets schreiben
            if (mapTickets.containsKey(Integer.parseInt("1" + transport.getFirstCapacity()))) {
                TrainTicket changeticket = mapTickets.get(Integer.parseInt("1" + transport.getFirstCapacity()));
                mapTickets.remove(changeticket.getSeat());
                changeticket.setSeat(ticket.getSeat());
                mapTickets.put(ticket.getSeat(), changeticket);
                this.ticketRepository.update(changeticket);
                Costumer costumer = (Costumer) this.personRepository.get(changeticket.getCostumer());
                costumer.getAllTickets().forEach(ticket1 -> {
                    if (ticket1.getId().equals(changeticket.getId())) {
                        ticket1.setSeat(ticket.getSeat());
                    }
                });
                this.personRepository.update(costumer);
            }
            this.transportRepository.update(transport);
        } else if (((TrainTicket) ticket).getTicketClass() == 2) {
            transport.setSecondCapacity(transport.getSecondCapacity() + 1);
            //Ticket abrufen, dass am neusten ist, wenn eines existiert, und an Stelle des entfernten Tickets schreiben
            if (mapTickets.containsKey(Integer.parseInt("2" + transport.getSecondCapacity()))) {
                TrainTicket changeticket = mapTickets.get(Integer.parseInt("2" + transport.getSecondCapacity()));
                mapTickets.remove(changeticket.getSeat());
                changeticket.setSeat(ticket.getSeat());
                mapTickets.put(ticket.getSeat(), changeticket);
                this.ticketRepository.update(changeticket);
                Costumer costumer = (Costumer) this.personRepository.get(changeticket.getCostumer());
                costumer.getAllTickets().forEach(ticket1 -> {
                    if (ticket1.getId().equals(changeticket.getId())) {
                        ticket1.setSeat(ticket.getSeat());
                    }
                });
                this.personRepository.update(costumer);
            }
            this.transportRepository.update(transport);
        }
    }

    /**
     * Methode um einen neuen Ort zu erstellen und ins Repository zu schreiben, dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist der die Methode ausführt
     *
     * @param admin                     Administrator-Objekt, das die Aktion ausführen will, stellt sicher, dass nur Administratoren diese Methode ausführen können
     * @param street                    Straße + eventuell Hausnummer des Ortes
     * @param city                      Stadt, in der der Ort liegt
     * @throws BusinessLogicException   Wenn Ort bereits im Repository existiert oder Nutzer kein Administrator ist
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void createLocation(Administrator admin, String street, String city) throws BusinessLogicException, DatabaseException {
        if (admin.isAdmin()) {
            //Check that location does not already exist
            List<Location> locations = this.locationRepository.getAll();
            AtomicBoolean exists = new AtomicBoolean(false);
            locations.forEach(location -> {
                if (location.getStreet().equals(street) && location.getCity().equals(city)) exists.set(true);
            });
            if (exists.get()) {
                throw new BusinessLogicException("Location already exists!");
            }
            while (this.locationRepository.containsKey(this.locationIdCount)) {
                this.locationIdCount++;
            }
            //Location erstellen und in Repository hinterlegen
            Location location = new Location(this.locationIdCount, street, city);
            this.locationRepository.create(location);
        } else throw new BusinessLogicException("You are not authorized to perform this operation!");
    }

    /**
     * Methode um einen Bustransport zu erstellen und ins Repository zu schreiben, dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist, der die Methode ausführt.
     * Zusätzlich wird geschaut ob Orte mit den gegebenen IDs wirklich existieren.
     *
     * @param admin                     Administrator der die Methode ausführen möchte
     * @param originid                  ID des Ortes an dem der Transport starten soll
     * @param destinationid             ID des Ortes an dem der Transport enden soll
     * @param year                      Jahr in dem der Transport stattfindet
     * @param month                     Monat (als Zahl) in dem der Transport stattfindet
     * @param day                       Tag (als Zahl) an dem der Transport stattfindet
     * @param hourd                     Stunde, zu der der Transport startet
     * @param mind                      Minute, zu der der Transport startet
     * @param houra                     Stunde, zu der der Transport endet
     * @param mina                      Minute, zu der der Transport endet
     * @param capacity                  Anzahl der Sitzplätze die der Bus hat
     * @throws BusinessLogicException   Wenn Nutzer kein Administrator ist oder Start- und Zielort der gleiche sind
     * @throws EntityNotFoundException  Wenn ID eines Ortes nicht im Repository existiert
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void createBusTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        if (admin.isAdmin()) {
            if (originid == destinationid) throw new BusinessLogicException("Error! Origin and destination cannot be the same location!");
            Location origin = this.locationRepository.get(originid);
            Location destination = this.locationRepository.get(destinationid);
            if (origin != null && destination != null) {
                while (this.transportRepository.containsKey(this.transportIdCount)) {
                    this.transportIdCount++;
                }
                //Transport erstellen und in Repository hinterlegen
                Bus transport = new Bus(this.transportIdCount, (int) origin.getId(), (int) destination.getId(), year, month, day, hourd, mind, houra, mina, capacity);
                this.transportRepository.create(transport);
                Administrator upToDateAdmin = (Administrator) this.personRepository.get(admin.getId());
                upToDateAdmin.getAllAdministeredTransports().add(transport);
                this.personRepository.update(upToDateAdmin);
            } else if (origin != null) {
                throw new EntityNotFoundException("No location with the entered destination ID found!");
            } else if (destination != null) {
                throw new EntityNotFoundException("No location with the entered origin ID found!");
            } else throw new EntityNotFoundException("No locations with the entered origin and destination IDs found!");
        } else throw new BusinessLogicException("You are not authorized to perform this operation!");
    }

    /**
     * Methode um einen Zugtransport zu erstellen und ins Repository zu schreiben, dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist, der die Methode ausführt.
     * Zusätzlich wird geschaut ob Orte mit den gegebenen IDs wirklich existieren.
     *
     * @param admin                     Administrator der die Methode ausführen möchte
     * @param originid                  ID des Ortes an dem der Transport starten soll
     * @param destinationid             ID des Ortes an dem der Transport enden soll
     * @param year                      Jahr in dem der Transport stattfindet
     * @param month                     Monat (als Zahl) in dem der Transport stattfindet
     * @param day                       Tag (als Zahl) an dem der Transport stattfindet
     * @param hourd                     Stunde, zu der der Transport startet
     * @param mind                      Minute, zu der der Transport startet
     * @param houra                     Stunde, zu der der Transport endet
     * @param mina                      Minute, zu der der Transport endet
     * @param firstcapacity             Anzahl der Sitzplätze die der Zug in der 1. Klasse hat
     * @param secondcapacity            Anzahl der Sitzplätze die der Zug in der 2. Klasse hat
     * @throws BusinessLogicException   Wenn Nutzer kein Administrator ist
     * @throws EntityNotFoundException  Wenn ID eines Ortes nicht im Repository existiert
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void createTrainTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        if (admin.isAdmin()) {
            if (originid == destinationid) throw new BusinessLogicException("Warning! Origin and destination cannot be the same location!");
            Location origin = this.locationRepository.get(originid);
            Location destination = this.locationRepository.get(destinationid);
            if (origin != null && destination != null) {
                while (this.transportRepository.containsKey(this.transportIdCount)) {
                    this.transportIdCount++;
                }
                //Transport erstellen und in Repository hinterlegen
                Train transport = new Train(this.transportIdCount, (int) origin.getId(), (int) destination.getId(), year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
                this.transportRepository.create(transport);
                Administrator upToDateAdmin = (Administrator) this.personRepository.get(admin.getId());
                upToDateAdmin.getAllAdministeredTransports().add(transport);
                this.personRepository.update(upToDateAdmin);
            } else if (origin != null) {
                throw new EntityNotFoundException("No location with the entered destination ID found!");
            } else if (destination != null) {
                throw new EntityNotFoundException("No location with the entered origin ID found!");
            } else
                throw new EntityNotFoundException("No locations with the entered origin and destination IDs found!");
        } else throw new BusinessLogicException("You are not authorized to perform this operation!");
    }

    /**
     * Methode um einen Transport aus dem Repository zu entfernen, wobei zuerst kontrolliert wird ob es einen Transport unter der gegebenen ID gibt.
     * Zusätzlich wird überprüft, ob der Administrator den Transport auch verwaltet, nur in diesem Fall darf dieser den Transport auch entfernen.
     * Ist dies der Fall werden alle Tickets, die bisher auf dem Transport gebucht waren, gelöscht und der volle Ticketpreis an die betroffenen Kunden zurückerstattet.
     *
     * @param admin                     Administrator der die Methode ausführen möchte
     * @param id                        ID des Transports der entfernt werden soll
     * @throws BusinessLogicException   Wenn Administrator den Transport, der entfernt werden soll, nicht verwaltet
     * @throws EntityNotFoundException  Wenn kein Transport mit der gegebenen ID im Repository existiert oder kein Nutzer-Objekt im Repository mit dem übermittelten übereinstimmt
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public void removeTransport(Administrator admin, int id) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        Transport transport = this.transportRepository.get(id);
        if (transport != null) {
            //Überprüfung, ob Administrator den Transport wirklich verwaltet
            Administrator upToDateAdmin = (Administrator) this.personRepository.get(admin.getId());
            int count = (int) upToDateAdmin.getAllAdministeredTransports().stream()
                    .filter(transport1 -> transport1.getId().equals(id)).count();
            if (count == 1) {
                upToDateAdmin.getAllAdministeredTransports().removeIf(transport1 -> transport1.getId().equals(transport.getId()));
                this.personRepository.update(upToDateAdmin);
                for (Ticket ticket : getAllTransportTickets(admin, id)) {
                    Costumer costumer = (Costumer) this.personRepository.get(ticket.getCostumer());
                    costumer.getAllTickets().removeIf(ticket1 -> ticket1.getId().equals(ticket.getId()));
                    //Rückerstattung des vollen Ticketpreises
                    this.personRepository.update(costumer);
                    addBalance(costumer, ticket.getPrice());
                    this.ticketRepository.delete(ticket.getId());
                }
                this.transportRepository.delete(id);
            } else throw new BusinessLogicException("You are not authorized to remove this transport since you do not manage it!");
        } else throw new EntityNotFoundException("No transport with this ID exists in the repository!");
    }

    /**
     * Methode die alle Tickets zurückgibt, welche auf einem gegebenen Transport bisher gebucht sind.
     * Dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist, der die Methode ausführt und ob es ein Transport mit der ID überhaupt gibt.
     *
     * @param admin                     Administrator der die Methode ausführen möchte
     * @param id                        ID des Transports für den alle Tickets angezeigt werden sollen
     * @return                          Liste die alle Tickets enthält, die zum Zeitpunkt des Methodenaufrufs für gegebenen Transport reserviert sind
     * @throws BusinessLogicException   Wenn Nutzer kein Administrator ist
     * @throws EntityNotFoundException  Wenn kein Transport mit der gegebenen ID im Repository existiert
     * @throws DatabaseException        Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Ticket> getAllTransportTickets(Administrator admin, int id) throws BusinessLogicException, EntityNotFoundException, DatabaseException {
        if (admin.isAdmin()) {
            Transport transport = this.transportRepository.get(id);
            if (transport != null) {
                switch (transport) {
                    case Bus bus -> {
                        List<Ticket> tickets = ((Bus) this.transportRepository.get(bus.getId())).getBookedSeats().values().stream().collect(Collectors.toUnmodifiableList());
                        List<Ticket> ticketList = new ArrayList<>();
                        for (Ticket ticket : tickets) {
                            ticketList.add(this.ticketRepository.get(ticket.getId()));
                        }
                        return ticketList;
                    }
                    case Train train -> {
                        List<Ticket> tickets = ((Train) this.transportRepository.get(train.getId())).getBookedSeats().values().stream().collect(Collectors.toUnmodifiableList());
                        List<Ticket> ticketList = new ArrayList<>();
                        for (Ticket ticket : tickets) {
                            ticketList.add(this.ticketRepository.get(ticket.getId()));
                        }
                        return ticketList;
                    }
                    default -> throw new RuntimeException("Unknown class of transport!");
                }
            } else throw new EntityNotFoundException("No transport with this ID exists!");
        } else throw new BusinessLogicException("You are not authorized to perform this operation!");
    }

    /**
     * Gibt eine Liste von Transporten zurück, sortiert nach Datum in aufsteigender Reihenfolge.
     *
     * @return                      Liste von Transportobjekten sortiert nach dem Abfahrtsdatum.
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Transport> getTransportsSortedByDateAscending() throws DatabaseException {
        return transportRepository.getAll().stream()
                .sorted(Comparator.comparing(Transport::getDate).thenComparing(Transport::getDepartureTime))
                .collect(Collectors.toList());
    }

    /**
     * Gibt eine Liste von Transporten zurück, sortiert nach Dauer in aufsteigender Reihenfolge.
     * Die Dauer wird als die Zeitdifferenz zwischen Abfahrts- und Ankunftszeit berechnet.
     *
     * @return                      Liste von Transportobjekten, sortiert nach der Dauer der Fahrt.
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Transport> getTransportsSortedByDurationAscending() throws DatabaseException {
        return transportRepository.getAll().stream()
                .sorted((t1, t2) -> Long.compare(
                        Duration.between(t1.getDepartureTime(), t1.getArrivalTime()).toMinutes(),
                        Duration.between(t2.getDepartureTime(), t2.getArrivalTime()).toMinutes()
                )).collect(Collectors.toList());
    }

    /**
     * Gibt eine Liste von Orten zurück, sortiert nach der Anzahl der gebuchten Tickets,
     * die für Transporte, die an diesem Ort starten oder enden, reserviert wurden.
     *
     * @return                      Liste von Orten, sortiert nach der Gesamtanzahl der gebuchten Tickets.
     * @throws DatabaseException    Wenn ein Fehler bei einer Datenbankoperation im Repository auftritt
     */
    public List<Location> getLocationsSortedDescendingByTotalTickets() throws DatabaseException {
        //Location-ID als Schlüssel da einzigartig und Anzahl gezählter Tickets als Wert
        Map<Integer, Integer> locationTicketCountMap = new HashMap<>();

        // Zählt die Tickets für alle Transporte, die an einem Ort starten oder enden
        for (Transport transport : transportRepository.getAll()) {
            int ticketCount = (int) ticketRepository.getAll().stream()
                    .filter(ticket -> ticket.getTransport() == (int) transport.getId())
                    .count();
            //Fügt die Summe der für diesen Transport gezählte Tickets zur Summe der für Abfahrts- und Ankunftsort bereits gezählten Tickets
            locationTicketCountMap.merge((int) transport.getOrigin(), ticketCount, Integer::sum);
            locationTicketCountMap.merge((int) transport.getDestination(), ticketCount, Integer::sum);
        }

        // Sortiert die Location-IDs nach der Gesamtanzahl der Tickets und wandelt Liste dann in Orts-Liste um
        List<Integer> locationID = locationTicketCountMap.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey).toList();
        List<Location> sortedLocations = new ArrayList<>();
        for (int id : locationID) {
            sortedLocations.add(locationRepository.get(id));
        }
        return sortedLocations;
    }
}

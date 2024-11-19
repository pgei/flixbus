package main.java.service;

import main.java.model.*;
import main.java.repository.IRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * @param person        Repository das für Personen genutzt werden soll
     * @param transport     Repository das für Transporte genutzt werden soll
     * @param ticket        Repository das für Tickets genutzt werden soll
     * @param location      Repository das für Orte genutzt werden soll
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
     * @param name      Anwendername
     * @param email     E-Mail-Adresse, muss einzigartig sein da sie auch als Schlüssel genutzt werden soll
     * @param password  Passwort das bei Login verwendet werden soll
     * @param admin     Administrator-Objekt wird erstellt wenn wahr, sonst wird Kunde erstellt
     * @return  Wahr wenn erstellen des Personen-Objektes erfolgreich wahr, sonst falsch
     */
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

    }

    /**
     * Methode die überprüft, ob Person im Repository existiert zu der gegebenen E-Mail-Adresse und, falls dem so ist, auch kontrolliert ob das Passwort zu der Person stimmt
     *
     * @param email     E-Mail-Adresse die zur Person gehört (Schlüssel)
     * @param password  Passwort der Person
     * @return          Das Personen-Objekt, falls Passwort authentisch ist
     */
    public Person checkLoginCredentials(String email, String password) {
        Person person = this.personRepository.get(email);
        if (person != null && person.isAuthentic(password)) {
            return person;
        } else return null;
    }

    /**
     * Methode die alle Transporte zurückgibt, die im Transport-Repository gespeichert sind
     *
     * @return  Liste aller Transporte im Repository
     */
    public List<Transport> getAllTransports() {
        return this.transportRepository.getAll();
    }

    /**
     * Methode, die alle Transporte zurückgibt, welche an vorgegebenen Orten starten bzw. enden und noch freie Kapazität haben.
     * Dabei ist es auch möglich nur mit dem Abfahrtsort bzw. Ankunftsort zu filtern sowie, wenn keiner der beiden vorgegeben wurde, nur auf verfügbare Kapazität.
     *
     * @param origin        ID des Ortes, an dem der Transport starten muss (-1 bedeutet egal)
     * @param destination   ID des Ortes, an dem der Transport enden muss (-1 bedeutet egal)
     * @return              Liste aller Transporte die Suchkriterien erfüllen
     */
    public List<Transport> getTransportsFilteredByLocation(int origin, int destination) {
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
                if ((int) transport.getDestination().getId() == destination && transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        } else if (destination == -1) {
            //Fall filtern auf Abfahrtsort und Kapazität
            List<Transport> filtered = new ArrayList<>();
            this.transportRepository.getAll().forEach(transport -> {
                if ((int) transport.getOrigin().getId() == origin && transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        } else {
            //Fall filtern auf Abfahrtsort und Ankunftsort sowie Kapazität
            List<Transport> filtered = new ArrayList<>();
            this.transportRepository.getAll().forEach(transport -> {
                if ((int) transport.getOrigin().getId() == origin && (int) transport.getDestination().getId() == destination && transport.getCapacity() > 0) {
                    filtered.add(transport);
                }
            });
            return filtered;
        }
    }

    /**
     * Methode, die alle Transporte zurückgibt, die noch freie Kapazität haben die zu einem Preis kleiner gleich dem gegebenem verkauft werden.
     *
     * @param price Maximaler Preis nach dem Transporte gefiltert werden sollen
     * @return      Liste aller Transporte, die das Suchkriterium erfüllen
     */
    public List<Transport> getTransportsFilteredByPrice(int price) {
        List<Transport> filtered = new ArrayList<>();
        this.transportRepository.getAll().forEach(transport -> {
            if (transport instanceof Bus && ((Bus) transport).getCapacity() > 0 && price >= PRICE_BUS) {
                filtered.add(transport);
            } else if (transport instanceof Train && ((Train) transport).getSecondCapacity() > 0 && price >= PRICE_2ND_TRAIN ) {
                filtered.add(transport);
            } else if (transport instanceof Train && ((Train) transport).getFirstCapacity() > 0 && price >= PRICE_1ST_TRAIN ) {
                filtered.add(transport);
            }
        });
        return filtered;
    }

    /**
     * Methode die alle Orte zurückgibt, die im Location-Repository gespeichert sind
     *
     * @return  Liste aller Orte im Repository
     */
    public List<Location> getLocations() {
        return this.locationRepository.getAll();
    }

    /**
     * Methode die alle Tickets zurückgibt, die ein spezifischer Kunde gekauft hat
     *
     * @param costumer  Kunde, von dem alle Tickets zurückgegeben werden sollen
     * @return          Liste aller Tickets, die von gegebenem Kunden gekauft wurden
     */
    public List<Ticket> getALlTickets(Costumer costumer) {
        return costumer.getAllTickets();
    }

    /**
     * Methode die das Guthaben eines gegebenen Kunden zurückgibt
     *
     * @param costumer  Kunde, für den das Guthaben abgefragt werden soll
     * @return          Guthaben auf dem Konto des Kunden
     */
    public int getBalance(Costumer costumer) {
        return costumer.getBalance();
    }

    /**
     * Methode die das Guthaben eines Kunden um gegebenen Betrag erhöht
     *
     * @param costumer  Kunde, dessen Guthaben erhöht werden soll
     * @param money     Euro-Betrag um den das Guthaben steigen soll
     */
    public void addBalance(Costumer costumer, int money) {
        costumer.setBalance(costumer.getBalance()+money);
        this.personRepository.update(costumer);
    }

    /**
     * Methode die das Guthaben eines Kunden um gegebenen Betrag reduziert
     *
     * @param costumer  Kunde, dessen Guthaben reduziert werden soll
     * @param money     Euro-Betrag mit dem das Guthaben belastet werden soll
     */
    public void reduceBalance(Costumer costumer, int money) {
        costumer.setBalance(costumer.getBalance()-money);
        this.personRepository.update(costumer);
    }

    /**
     * Methode die ein Ticket auf einem Transport reserviert, falls dies möglich ist.
     * Zur Überprüfung dessen wird zuerst kontrolliert, ob überhaupt ein Transport mit der angegebenen ID existiert.
     * Ist dies der Fall, wird im nächsten Schritt überprüft, ob es noch freie Sitzplätze auf dem Transport in der gewünschten Klasse gibt sowie ob der Kunde noch genug Guthaben hat, um das Ticket zu bezahlen.
     * Wenn alles passt, wird das Ticket erstellt, wobei die Sitznummer wie folgt vergeben wird:
     *
     * <li>Bus: die Sitzplätze sind nach der Anzahl an Sitzplätzen durchnummeriert und werden in absteigender Reihenfolge vergeben <li/>
     * <li>Zug: die Sitzplätze sind wie bei Bus durchnummeriert für jede Klasse, um die Klasse zu kennzeichen wird der Sitznummer eine 1 respektive 2 voraus gestellt<li/>
     *
     * Beispiel: wenn die erste Klasse im Zug 5 Sitzplätze hat, dann wird der erste vergebene Sitzplatz 15 sein und der letzte 11
     *
     * @param costumer      Kunde der das Ticket buchen will
     * @param transportid   ID des Transports, auf dem ein Ticket reserviert werden soll
     * @param ticketclass   Klasse, in der ein Ticket reserviert werden soll, bei Bustransporten wird dies ignoriert da Busse nur eine Klasse haben
     * @return              Wahr falls Ticket erfolgreich erstellt werden konnte, sonst falsch
     */
    public boolean createTicket(Costumer costumer, int transportid, int ticketclass) {
        Transport transport = this.transportRepository.get(transportid);
        if (transport == null) {
            return false;
        } else {
            switch (transport) {
                case Bus bus -> {
                    if ((bus.getCapacity() > 0 && costumer.getBalance() >= PRICE_BUS)) {
                        //Einzigartige ID für Ticket finden
                        while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                            this.ticketIdCount++;
                        }
                        int seat = ((Bus) transport).getCapacity();
                        //Ticket erstellen und in Repository hinterlegen
                        BusTicket ticket = new BusTicket(this.ticketIdCount, costumer, transport, PRICE_BUS, seat);
                        this.ticketRepository.create(ticket);
                        //Aktualisierung von Transport und Kunde
                        ((Bus) transport).setCapacity(((Bus) transport).getCapacity() - 1);
                        ((Bus) transport).getBookedSeats().put(seat, ticket);
                        costumer.getAllTickets().add(ticket);
                        reduceBalance(costumer, PRICE_BUS);
                        this.personRepository.update(costumer);
                        this.transportRepository.update(transport);
                        return true;
                    }
                    return false;
                }
                case Train train when ticketclass == 1 -> {
                    if (train.getFirstCapacity() > 0 && costumer.getBalance() >= PRICE_1ST_TRAIN) {
                        //Einzigartige ID für Ticket finden
                        while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                            this.ticketIdCount++;
                        }
                        int seat = Integer.parseInt("1" + ((Train) transport).getFirstCapacity());
                        //Ticket erstellen und in Repository hinterlegen
                        TrainTicket ticket = new TrainTicket(this.ticketIdCount, costumer, transport, PRICE_1ST_TRAIN, seat, 1);
                        this.ticketRepository.create(ticket);
                        //Aktualisierung von Transport und Kunde
                        ((Train) transport).setFirstCapacity(((Train) transport).getFirstCapacity() - 1);
                        ((Train) transport).getBookedSeats().put(seat, ticket);
                        costumer.getAllTickets().add(ticket);
                        reduceBalance(costumer, PRICE_1ST_TRAIN);
                        this.personRepository.update(costumer);
                        this.transportRepository.update(transport);
                        return true;
                    }
                    return false;
                }
                case Train train when ticketclass == 2 -> {
                    if (train.getSecondCapacity() > 0 && costumer.getBalance() >= PRICE_2ND_TRAIN) {
                        //Einzigartige ID für Ticket finden
                        while (this.ticketRepository.containsKey(this.ticketIdCount)) {
                            this.ticketIdCount++;
                        }
                        int seat = Integer.parseInt("2" + ((Train) transport).getSecondCapacity());
                        //Ticket erstellen und in Repository hinterlegen
                        TrainTicket ticket = new TrainTicket(this.ticketIdCount, costumer, transport, PRICE_2ND_TRAIN, seat, 2);
                        this.ticketRepository.create(ticket);
                        //Aktualisierung von Transport und Kunde
                        ((Train) transport).setSecondCapacity(((Train) transport).getSecondCapacity() - 1);
                        ((Train) transport).getBookedSeats().put(seat, ticket);
                        costumer.getAllTickets().add(ticket);
                        reduceBalance(costumer, PRICE_2ND_TRAIN);
                        this.personRepository.update(costumer);
                        this.transportRepository.update(transport);
                        return true;
                    }
                    return false;
                }
                default -> {
                    return false;
                }
            }
        }
    }

    /**
     * Methode die ein Ticket storniert und von dem Transport entfernt.
     * Zuerst wird kontrolliert, ob ein Ticket im Repository unter der Ticket-Nummer gefunden werden kann und ob das Ticket überhaupt von dem gegebenen Kunden gebucht worden ist.
     * Ist dies der Fall, wird das Ticket aus dem Repository gelöscht und ebenfalls beim Kunden und dem Transport aus der Liste entfernt.
     * Der Kunde erhält in diesem Zusammenhang eine Gutschrift des Ticketpreises abzüglich einer Servicegebühr von 10 %.
     * Um die Reihenfolge der Vergabe der Sitzplätze bei Buchung neuer Tickets nicht durcheinanderzubringen, wird das neuste Ticket (also kleinste Sitznummer) an die Stelle des gelöschten Tickets geschrieben.
     * Dh der Sitzplatz des neusten Tickets ändert sich zu dem Sitzplatz des entfernten Tickets.
     *
     * @param costumer  Kunde der ein Ticket entfernen will
     * @param id        Ticket-Nummer des Tickets das entfernt werden soll
     * @return          Wahr, falls Ticket erfolgreich gelöscht wurde, sonst falsch
     */
    public boolean removeTicket(Costumer costumer, int id) {
        Ticket ticket = this.ticketRepository.get(id);
        if (ticket == null) {
            return false;
        }
        if (ticket.getCostumer().getId() != costumer.getId() ) {
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
            changeticket.setSeat(ticket.getSeat());
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
                TrainTicket changeticket = mapTickets.get(Integer.parseInt("1" + transport.getFirstCapacity()));
                mapTickets.remove(changeticket.getSeat());
                changeticket.setSeat(ticket.getSeat());
                mapTickets.put(ticket.getSeat(),changeticket);
                this.transportRepository.update(transport);
            } else {
                transport.setSecondCapacity(transport.getSecondCapacity()+1);
                //Ticket abrufen, dass am neusten ist, und an Stelle des entfernten Tickets schreiben
                TrainTicket changeticket = mapTickets.get(Integer.parseInt("2" + transport.getSecondCapacity()));
                mapTickets.remove(changeticket.getSeat());
                changeticket.setSeat(ticket.getSeat());
                mapTickets.put(ticket.getSeat(),changeticket);
                this.transportRepository.update(transport);
            }
        } else return false;

        //Kunde aktualisieren
        costumer.getAllTickets().remove(ticket);
        //Servicegebühr in Höhe von 10% des Ticketpreises wird einbehalten
        addBalance(costumer, (int) (ticket.getPrice()*0.9));
        this.personRepository.update(costumer);
        this.ticketRepository.delete(ticket);
        return true;
    }

    /**
     * Methode um einen neuen Ort zu erstellen und ins Repository zu schreiben, dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist der die Methode ausführt
     *
     * @param admin     Administrator-Objekt, das die Aktion ausführen will, stellt sicher, dass nur Administratoren diese Methode ausführen können
     * @param street    Straße + eventuell Hausnummer des Ortes
     * @param city      Stadt, in der der Ort liegt
     * @return          Wahr, falls Ort erfolgreich im Repository hinzugefügt wurde, sonst falsch
     */
    public boolean createLocation(Administrator admin, String street, String city) {
        if (admin.isAdmin()) {
            while (this.locationRepository.containsKey(this.locationIdCount)) {
                this.locationIdCount++;
            }
            //Ticket erstellen und in Repository hinterlegen
            Location loc = new Location(this.locationIdCount, street, city);
            this.locationRepository.create(loc);
            return true;
        }
        return false;
    }

    /**
     * Methode um einen Bustransport zu erstellen und ins Repository zu schreiben, dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist, der die Methode ausführt.
     * Zusätzlich wird geschaut ob Orte mit den gegebenen IDs wirklich existieren.
     *
     * @param admin         Administrator der die Methode ausführen möchte
     * @param originid      ID des Ortes an dem der Transport starten soll
     * @param destinationid ID des Ortes an dem der Transport enden soll
     * @param year          Jahr in dem der Transport stattfindet
     * @param month         Monat (als Zahl) in dem der Transport stattfindet
     * @param day           Tag (als Zahl) an dem der Transport stattfindet
     * @param hourd         Stunde, zu der der Transport startet
     * @param mind          Minute, zu der der Transport startet
     * @param houra         Stunde, zu der der Transport endet
     * @param mina          Minute, zu der der Transport endet
     * @param capacity      Anzahl der Sitzplätze die der Bus hat
     * @return              Wahr, falls Bustransport erfolgreich im Repository hinzugefügt wurde, sonst falsch
     */
    public boolean createBusTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) {
        if (admin.isAdmin()) {
            Location origin = this.locationRepository.get(originid);
            Location destination = this.locationRepository.get(destinationid);
            if (origin != null && destination != null) {
                while (this.transportRepository.containsKey(this.transportIdCount)) {
                    this.transportIdCount++;
                }
                //Ticket erstellen und in Repository hinterlegen
                Bus transport = new Bus(this.transportIdCount, origin, destination, year, month, day, hourd, mind, houra, mina, capacity);
                this.transportRepository.create(transport);
                admin.getAllAdministeredTransports().add(transport);
                this.personRepository.update(admin)
                ;
                return true;
            }
        }
        return false;
    }

    /**
     * Methode um einen Zugtransport zu erstellen und ins Repository zu schreiben, dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist, der die Methode ausführt.
     * Zusätzlich wird geschaut ob Orte mit den gegebenen IDs wirklich existieren.
     *
     * @param admin             Administrator der die Methode ausführen möchte
     * @param originid          ID des Ortes an dem der Transport starten soll
     * @param destinationid     ID des Ortes an dem der Transport enden soll
     * @param year              Jahr in dem der Transport stattfindet
     * @param month             Monat (als Zahl) in dem der Transport stattfindet
     * @param day               Tag (als Zahl) an dem der Transport stattfindet
     * @param hourd             Stunde, zu der der Transport startet
     * @param mind              Minute, zu der der Transport startet
     * @param houra             Stunde, zu der der Transport endet
     * @param mina              Minute, zu der der Transport endet
     * @param firstcapacity     Anzahl der Sitzplätze die der Zug in der 1. Klasse hat
     * @param secondcapacity    Anzahl der Sitzplätze die der Zug in der 2. Klasse hat
     * @return                  Wahr, falls Zugtransport erfolgreich im Repository hinzugefügt wurde, sonst falsch
     */
    public boolean createTrainTransport(Administrator admin, int originid, int destinationid, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) {
        if (admin.isAdmin()) {
            Location origin = this.locationRepository.get(originid);
            Location destination = this.locationRepository.get(destinationid);
            if (origin != null && destination != null) {
                while (this.transportRepository.containsKey(this.transportIdCount)) {
                    this.transportIdCount++;
                }
                //Ticket erstellen und in Repository hinterlegen
                Train transport = new Train(this.transportIdCount, origin, destination, year, month, day, hourd, mind, houra, mina, firstcapacity, secondcapacity);
                this.transportRepository.create(transport);
                admin.getAllAdministeredTransports().add(transport);
                this.personRepository.update(admin);
                return true;
            }
        }
        return false;
    }

    /**
     * Methode um einen Transport aus dem Repository zu entfernen, wobei zuerst kontrolliert wird ob es einen Transport unter der gegebenen ID gibt.
     * Zusätzlich wird überprüft, ob der Administrator den Transport auch verwaltet, nur in diesem Fall darf dieser den Transport auch entfernen.
     * Ist dies der Fall werden alle Tickets, die bisher auf dem Transport gebucht waren, gelöscht und der volle Ticketpreis an die betroffenen Kunden zurückerstattet.
     *
     * @param admin Administrator der die Methode ausführen möchte
     * @param id    ID des Transports der entfernt werden soll
     * @return      Wahr, falls Zugtransport erfolgreich im Repository hinzugefügt wurde, sonst falsch
     */
    public boolean removeTransport(Administrator admin, int id) {
        Transport transport = this.transportRepository.get(id);
        if (transport == null) {
            return false;
        } else if (admin.getAllAdministeredTransports().contains(transport)){
            admin.getAllAdministeredTransports().remove(transport);
            this.personRepository.update(admin);
            getAllTransportTickets(admin, id).forEach(ticket -> {
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

    /**
     * Methode die alle Tickets zurückgibt, welche auf einem gegebenen Transport bisher gebucht sind.
     * Dabei wird zuerst kontrolliert, ob es wirklich ein Administrator ist, der die Methode ausführt und ob es ein Transport mit der ID überhaupt gibt.
     *
     * @param admin Administrator der die Methode ausführen möchte
     * @param id    ID des Transports für den alle Tickets angezeigt werden sollen
     * @return      Liste die alle Tickets enthält, die zum Zeitpunkt des Methodenaufrufs für gegebenen Transport reserviert sind
     */
    public List<Ticket> getAllTransportTickets(Administrator admin, int id) {
        if (admin.isAdmin()) {
            Transport transport = this.transportRepository.get(id);
            return switch (transport) {
                case Bus bus -> bus.getBookedSeats().values().stream().collect(Collectors.toUnmodifiableList());
                case Train train -> train.getBookedSeats().values().stream().collect(Collectors.toUnmodifiableList());
                default -> null;
            };
        }
        return null;
    }
}

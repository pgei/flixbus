package main.java.model;

/**
 * Abstrakte Klasse, die grundlegende Attribute und Methoden für alle Tickets definiert.
 */

public abstract class Ticket implements ID {

    private int ticketId;
    private Costumer costumer;
    private Transport transport;
    private int price;
    private int seat;

    /**
     * Konstruktor für die Erstellung eines Tickets, wobei im System eine einzigartige Id vergeben wird.
     *
     * @param costumer      Kunde der das Ticket erworben hat
     * @param transport     Transport auf dem das Ticket gültig ist
     * @param price         Preis des Tickets
     * @param seat          Sitzplatz welcher unter dem Ticket reserviert ist
     */
    public Ticket(int id, Costumer costumer, Transport transport, int price, int seat) {
        this.ticketId = id;
        this.costumer = costumer;
        this.transport = transport;
        this.price = price;
        this.seat = seat;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getId() {return this.ticketId;}

    /**
     * Getter für Kunde der das Ticket reserviert hat
     *
     * @return  Kunde der das Ticket besitzt
     */
    public Costumer getCostumer() {return this.costumer;}

    /**
     * Getter für Transport auf dem das Ticket gültig ist
     *
     * @return  Transport für welchen das Ticket gültig ist
     */
    public Transport getTransport() {return this.transport;}

    /**
     * Getter für Preis, der beim Kauf des Tickets gezahlt wurde
     *
     * @return  Preis des Tickets
     */
    public int getPrice() {return this.price;}

    /**
     * Getter für Sitzplatz welcher über das Ticket reserviert wurde
     *
     * @return  Sitzplatz für welchen das Ticket gilt
     */
    public int getSeat() {return this.seat;}

}

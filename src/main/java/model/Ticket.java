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

    /**
     * Setter für Sitzplatz welcher über das Ticket reserviert wurde
     *
     * @param seat  Nummer des Sitzplatzes
     */
    public void setSeat(int seat) {
        this.seat = seat;
    }

    /**
     * Wiedergabe des Objektes als String zur besseren Lesbarkeit.
     *
     * @return  String-Repräsentation des Tickets
     */
    @Override
    public String toString() {
        return "+++++++++++++++++++++++++++++++++++++++++" +
                "\n TicketNr: " + this.ticketId +
                "\n Price: " + this.price +
                "\n Seat: " + this.seat +
                "\n+++++++++++++++++++++++++++++++++++++++++\n" +
                this.getTransport().toString() +
                "\n+++++++++++++++++++++++++++++++++++++++++\n";
    }

    /**
     * Wiedergabe des Tickets in verkürzter Form, wobei der Name des Kunden gezeigt wird, aber nicht die Daten des Trasnports.
     * Dieser Modus ist ausschließlich für Administratoren gedacht, um alle Tickets die zu einem Transport gehören, anzuzeigen.
     *
     * @return  String-Repräsentation des Tickets in verkürzter Form
     */
    public String toStringReducedView() {
        return "+++++++++++++++++++++++++++++++++++++++++" +
                "\n TicketNr: " + this.ticketId +
                "\n Price: " + this.price +
                "\n Seat: " + this.seat +
                "\n Name: " + this.costumer.getUsername() +
                "\n+++++++++++++++++++++++++++++++++++++++++\n";
    }



}

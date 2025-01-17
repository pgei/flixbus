package main.java.model;

public class TrainTicket extends Ticket {

    private int ticketclass;

    /**
     * Konstuktor für Erstellung eines Zugtickets, welcher den Konstuktor der erweiterten Klasse Ticket überlädt sowie zusätzlich die Klasse, in der der Sitzplatz reserviert wurde, festlegt.
     *
     * @param costumer      ID des Kunden der das Ticket erworben hat
     * @param transport     ID des Transports auf dem das Ticket gültig ist
     * @param price         Preis des Tickets
     * @param seat          Sitzplatz welcher unter dem Ticket reserviert ist
     * @param ticketclass   Klasse, für die das Ticket gültig ist (1./2. Klasse)
     */
    public TrainTicket(int id, String costumer, int transport, int price, int seat, int ticketclass) {
        super(id, costumer, transport, price, seat);
        this.ticketclass = ticketclass;
    }

    /**
     * Getter für Klasse in der das Ticket gültig ist
     *
     * @return  Klasse in der ein Sitzplatz unter dem Ticket reserviert ist
     */
    public int getTicketClass() {return this.ticketclass;}

}

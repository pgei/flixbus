package main.java.model;

public class BusTicket extends Ticket {

    /**
     * Konstuktor für Erstellung eines Bustickets, welcher den Konstuktor der erweiterten Klasse Ticket überlädt.
     *
     * @param costumer      ID des Kunden der das Ticket erworben hat
     * @param transport     ID des Transports auf dem das Ticket gültig ist
     * @param price         Preis des Tickets
     * @param seat          Sitzplatz welcher unter dem Ticket reserviert ist
     */
    public BusTicket(int id, String costumer, int transport, int price, int seat) {
        super(id, costumer, transport, price, seat);
    }

}

package main.java.model;

public class BusTicket extends Ticket {

    /**
     * Konstuktor für Erstellung eines Bustickets, welcher den Konstuktor der erweiterten Klasse Ticket überlädt.
     *
     * @param costumer      Kunde der das Ticket erworben hat
     * @param transport     Transport auf dem das Ticket gültig ist
     * @param price         Preis des Tickets
     * @param seat          Sitzplatz welcher unter dem Ticket reserviert ist
     */
    public BusTicket(Costumer costumer, Transport transport, int price, int seat) {
        super(costumer, transport, price, seat);
    }

}

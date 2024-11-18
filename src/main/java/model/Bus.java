package main.java.model;

import java.util.HashMap;

/**
 * Klasse die Bustransporte repräsentiert, wobei die abstrakte Klasse Transport um Attribute und dazugehörige getter-Methoden erweitert wird.
 */

public class Bus extends Transport {

    private int capacity;  //Kapazität die noch verfügbar ist (freie Plätze)
    private HashMap<Integer, BusTicket> bookedSeats = new HashMap<>();

    /**
     * Konstuktor für Erstellung eines Bustransports, welcher den Konstuktor der erweiterten Klasse Transport überlädt sowie zusätzlich die anfängliche Kapazität des Buses definiert.
     *
     * @param origin        Ort an dem der Bustransport startet
     * @param destination   Ort an dem der Bustransport endet
     * @param year          Jahr in dem der Bustransport stattfindet
     * @param month         Monat in dem der Bustransport stattfindet
     * @param day           Datum des Tages an dem der Bustransport stattfindet
     * @param hourd         Stunde, zu der der Bustransport startet
     * @param mind          Minute, zu der der Bustransport startet
     * @param houra         Stunde, zu der der Bustransport endet
     * @param mina          Minute, zu der der Bustransport endet
     * @param capacity      Gesamtkapazität des Bustransportes
     */
    public Bus(int id, Location origin, Location destination, int year, int month, int day, int hourd, int mind, int houra, int mina, int capacity) {
        super(id, origin, destination, year, month, day, hourd, mind, houra, mina);
        this.capacity = capacity;
    }

    /**
     * Getter für die zum Zeitpunkt des Methodenaufrufs noch verfügbare Kapazität
     *
     * @return Anzahl noch freier Sitzplätze auf dem Bustransport
     */
    @Override
    public int getCapacity() {return this.capacity;}

    /**
     * Setter für noch verfügbare Kapazität auf Bustransport
     *
     * @param newcapacity   Neue Anzahl noch freier Sitzplätze auf dem Bustransport
     */
    public void setCapacity(int newcapacity) {this.capacity = newcapacity;}

    /**
     * Getter für HashMap welche alle, zum Zeitpunkt des Methodenaufrufs, auf dem Bustransport reservierten Tickets enthält
     *
     * @return HashMap mit allen gebuchten Tickets auf dem Bustransport
     */
    public HashMap<Integer, BusTicket> getBookedSeats() {return this.bookedSeats;}

}

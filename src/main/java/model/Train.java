package main.java.model;

import java.util.HashMap;

/**
 * Klasse die Zugtransporte repräsentiert, wobei die abstrakte Klasse Transport um Attribute und dazugehörige getter-Methoden erweitert wird.
 */

public class Train extends Transport {

    private int firstCapacity;  //Kapazität die noch verfügbar ist (freie Plätze 1. Klasse)
    private int secondCapacity;  //Kapazität die noch verfügbar ist (freie Plätze 2. Klasse)
    private HashMap<Integer, TrainTicket> bookedSeats;

    /**
     * Konstuktor für Erstellung eines Zugtransports, welcher den Konstuktor der erweiterten Klasse Transport überlädt sowie zusätzlich die anfänglichen Kapazitäten des Zuges in der 1. und 2. Klasse definiert.
     *
     * @param origin            Ort an dem der Zugtransport startet
     * @param destination       Ort an dem der Zugtransport endet
     * @param year              Jahr in dem der Zugtransport stattfindet
     * @param month             Monat in dem der Zugtransport stattfindet
     * @param day               Datum des Tages an dem der Zugtransport stattfindet
     * @param hourd             Stunde, zu der der Zugtransport startet
     * @param mind              Minute, zu der der Zugtransport startet
     * @param houra             Stunde, zu der der Zugtransport endet
     * @param mina              Minute, zu der der Zugtransport endet
     * @param firstcapacity     Gesamtkapazität des Zugtransportes in der 1. Klasse
     * @param secondcapacity    Gesamtkapazität des Zugtransportes in der 2. Klasse
     */
    public Train(int id, Location origin, Location destination, int year, int month, int day, int hourd, int mind, int houra, int mina, int firstcapacity, int secondcapacity) {
        super(id, origin, destination, year, month, day, hourd, mind, houra, mina);
        this.firstCapacity = firstcapacity;
        this.secondCapacity = secondcapacity;
    }

    /**
     * Getter für die zum Zeitpunkt des Methodenaufrufs noch verfügbare Kapazität in der 1. Klasse
     *
     * @return Anzahl noch freier Sitzplätze in der 1. Klasse auf dem Zugtransport
     */
    public int getFirstCapacity() {return this.firstCapacity;}

    /**
     * Setter für noch verfügbare Kapazität der 1. Klasse auf Zugtransport
     *
     * @param newcapacity   Neue Anzahl noch freier Sitzplätze in der 1. Klasse
     */
    public void setFirstCapacity(int newcapacity) {this.firstCapacity = newcapacity;}

    /**
     * Getter für die zum Zeitpunkt des Methodenaufrufs noch verfügbare Kapazität in der 2. Klasse
     *
     * @return Anzahl noch freier Sitzplätze in der 2. Klasse auf dem Zugtransport
     */
    public int getSecondCapacity() {return this.secondCapacity;}

    /**
     * Setter für noch verfügbare Kapazität der 2. Klasse auf Zugtransport
     *
     * @param newcapacity   Neue Anzahl noch freier Sitzplätze in der 2. Klasse
     */
    public void setSecondCapacity(int newcapacity) {this.secondCapacity = newcapacity;}

    /**
     * Getter für HashMap welche alle, zum Zeitpunkt des Methodenaufrufs, auf dem Zugtransport reservierten Tickets enthält
     *
     * @return HashMap mit allen gebuchten Tickets auf dem Zugtransport
     */
    public HashMap<Integer, TrainTicket> getBookedSeats() {return this.bookedSeats;}

}

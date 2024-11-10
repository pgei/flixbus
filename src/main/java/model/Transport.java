package main.java.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Abstrakte Klasse, die grundlegende Attribute und Methoden für alle Transporte definiert.
 */

public abstract class Transport {

    private int transportId;
    private Location origin;
    private Location destination;
    private LocalDate date;
    private LocalTime std; //Scheduled time of departure
    private LocalTime sta; //Scheduled time of arrival

    /**
     * Konstruktor für die Erstellung eines Transports, wobei im System eine einzigartige Id vergeben wird.
     *
     * @param origin        Ort an dem der Transport startet
     * @param destination   Ort an dem der Transport endet
     * @param year          Jahr in dem der Transport stattfindet
     * @param month         Monat in dem der Transport stattfindet
     * @param day           Datum des Tages an dem der Transport stattfindet
     * @param hourd         Stunde, zu der der Transport startet
     * @param mind          Minute, zu der der Transport startet
     * @param houra         Stunde, zu der der Transport endet
     * @param mina          Minute, zu der der Transport endet
     */
    public Transport(Location origin, Location destination, int year, int month, int day, int hourd, int mind, int houra, int mina) {
        //todo: add unique transportId
        this.origin = origin;
        this.destination = destination;
        this.date = LocalDate.of(year, month, day);
        this.std = LocalTime.of(hourd, mind);
        this.sta = LocalTime.of(houra, mina);
    }

    /**
     * Getter für einzigartige Id des Transports
     *
     * @return  Einzigartige Id des Transports
     */
    public int getTransportId() {return this.transportId;}

    /**
     * Getter für Ort an dem der Transport startet
     *
     * @return  Ort an dem der Transport startet
     */
    public Location getOrigin() {return this.origin;}

    /**
     * Getter für Ort an dem der Transport endet
     *
     * @return  Ort an dem der Transport endet
     */
    public Location getDestination() {return this.destination;}

    /**
     * Getter für Datum an dem der Transport stattfindet
     *
     * @return  Datum an dem der Transport stattfindet
     */
    public LocalDate getDate() {return this.date;}

    /**
     * Getter für Uhrzeit, zu der der Transport startet
     *
     * @return  Uhrzeit, zu der der Transport startet
     */
    public LocalTime getDepartureTime() {return this.std;}

    /**
     * Getter für Uhrzeit, zu der der Transport endet
     *
     * @return  Uhrzeit, zu der der Transport endet
     */
    public LocalTime getArrivalTime() {return this.sta;}

}

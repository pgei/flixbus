package main.java.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Abstrakte Klasse, die grundlegende Attribute und Methoden für alle Transporte definiert.
 */

public abstract class Transport implements ID {

    private int transportId;
    private int originID;
    private int destinationId;
    private LocalDate date;
    private LocalTime std; //Scheduled time of departure
    private LocalTime sta; //Scheduled time of arrival

    /**
     * Konstruktor für die Erstellung eines Transports, wobei im System eine einzigartige Id vergeben wird.
     *
     * @param origin        ID des Ortes an dem der Transport startet
     * @param destination   ID des Ortes an dem der Transport endet
     * @param year          Jahr in dem der Transport stattfindet
     * @param month         Monat in dem der Transport stattfindet
     * @param day           Datum des Tages an dem der Transport stattfindet
     * @param hourd         Stunde, zu der der Transport startet
     * @param mind          Minute, zu der der Transport startet
     * @param houra         Stunde, zu der der Transport endet
     * @param mina          Minute, zu der der Transport endet
     */
    public Transport(int id, int origin, int destination, int year, int month, int day, int hourd, int mind, int houra, int mina) {
        this.transportId = id;
        this.originID = origin;
        this.destinationId = destination;
        this.date = LocalDate.of(year, month, day);
        this.std = LocalTime.of(hourd, mind);
        this.sta = LocalTime.of(houra, mina);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getId() {return this.transportId;}

    /**
     * Getter für Ort an dem der Transport startet
     *
     * @return  ID des Ortes an dem der Transport startet
     */
    public int getOrigin() {return this.originID;}

    /**
     * Getter für Ort an dem der Transport endet
     *
     * @return  ID des Ortes an dem der Transport endet
     */
    public int getDestination() {return this.destinationId;}

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

    /**
     * Getter für gesamte verfügbare Kapazität des Transportes
     *
     * @return  Summe der freien Sitzplätze in allen Klassen
     */
    public abstract int getCapacity();

    /**
     * Wiedergabe des Objektes als String zur besseren Lesbarkeit
     *
     * @return  String-Repräsentation des Transportes
     */
    @Override
    public String toString() {
        String type = "";
        if (this instanceof Bus) {type = "Bus";} else if (this instanceof Train) {type = "Train";}
        return type +" { \n Transport-ID = " + this.transportId+
                "\n From location with ID "+this.originID+" --> To location with ID " + this.destinationId +
                "\n Date: " + this.date.toString() +
                "\n Departure: " + this.std.toString() +", Arrival: " + this.sta.toString() + " }";
    }
}

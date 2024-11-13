package main.java.model;

/**
 * Klasse die Orte darstellt mit deren Attributen und dazugehörigen getter-Methoden
 */

public class Location implements ID {

    private int locationId;
    private String street;
    private String city;

    /**
     * Konstruktor für Erstellen eines Ortes, der dann als Abfahrts- und Ankunftsort für einen Transport genutzt werden kann.
     * Im System wird dabei eine einzigartige Id vergeben.
     *
     * @param street    Straße + eventuell Hausnummer, also Adresse des Ortes
     * @param city      Stadt in der sich der Ort befindet
     */
    public Location(int id, String street, String city) {
        this.locationId = id;
        this.street = street;
        this.city = city;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getId() {return this.locationId;}

    /**
     * Getter für Adresse des Ortes
     *
     * @return  Straße + eventuell Hausnummer des Ortes
     */
    public String getStreet() {return this.street;}

    /**
     * Getter für Stadt in der sich der Ort befindet
     *
     * @return  Stadt, in der der Ort liegt
     */
    public String getCity() {return this.city;}

    /**
     * Wiedergabe des Objektes als String zur besseren Lesbarkeit
     *
     * @return  String-Repräsentation des Ortes
     */
    @Override
    public String toString() {
        return this.locationId + " : " + this.street + ", " + this.city;
    }

}

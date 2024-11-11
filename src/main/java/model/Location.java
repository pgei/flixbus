package main.java.model;

/**
 * Klasse die Orte darstellt mit deren Attributen und dazugehörigen getter-Methoden
 */

public class Location {

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
    public Location(String street, String city) {
        //todo: add unique locationId
        this.street = street;
        this.city = city;
    }

    /**
     * Getter für einzigartige Id des Ortes
     *
     * @return  Einzigartige Id des Ortes
     */
    public int getLocationId() {return this.locationId;}

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

}

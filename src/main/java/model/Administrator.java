package main.java.model;

import java.util.ArrayList;

/**
 * Klasse für Administratoren, welche die abstrakte Klasse Person um Attribute und dazugehörige getter-Methoden erweitert.
 */
public class Administrator extends Person {

    private final Boolean admin = true;
    private ArrayList<Transport> transports = new ArrayList<>();

    /**
     * Konstuktor für Erstellung eines Administrator-Kontos, welcher den Konstuktor der erweiterten Klasse Person überlädt.
     *
     * @param name      Name des Nutzers
     * @param email     E-Mail-Adresse des Nutzers, muss einzigartig sein
     * @param password  Passwort des Nutzers
     */
    public Administrator (String name, String email, String password) {
        super(name, email, password);
    }

    /**
     * Methode welche dazu dient, zu kontrollieren, ob ein Nutzer-Konto die Berechtigungen des Administrators hat
     *
     * @return  Wahr, da Person welche Administrator ist auch Administratorberechtigungen hat
     */
    public Boolean isAdmin() {return this.admin;}

    /**
     * Getter für alle von diesem Administrator verwalteten Transporte
     *
     * @return  Liste der Transporte, welche von diesem Administrator verwaltet werden
     */
    public ArrayList<Transport> getAllAdministeredTransports() {
        return this.transports;
    }

}

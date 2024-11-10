package main.java.model;

import java.util.List;

/**
 * Klasse für Kunden, welche die abstrakte Klasse Person um zusätzliche Attribute und dazugehörige getter-Methoden erweitert.
 */

public class Costumer extends Person {

    private final Boolean admin = false;
    private int balance;
    private List<Ticket> tickets;

    /**
     * Konstuktor für Erstellung eines Kunden-Kontos, welcher den Konstuktor der erweiterten Klasse Person überlädt sowie um die Möglichkeit erweitert, ein Startguthaben bei Erstellung eines Kontos festzulegen.
     *
     * @param name      Name des Nutzers
     * @param email     E-Mail-Adresse des Nutzers, muss einzigartig sein
     * @param password  Passwort des Nutzers
     */
    public Costumer(String name, String email, String password) {
        super(name, email, password);
        this.balance = 0; //Ist möglich hier ein Startguthaben zu definieren
    }

    /**
     * Methode welche dazu dient, zu kontrollieren, ob ein Nutzer-Konto die Berechtigungen des Administrators hat
     *
     * @return  Falsch, da Person welche Kunde ist keine Administratorberechtigungen hat
     */
    public Boolean isAdmin() {return this.admin;}

    /**
     * Getter für das Guthaben dieses Kunden
     *
     * @return  Guthaben dieses Kunden
     */
    public int getBalance() {return this.balance;}

    /**
     * Getter für alle von diesem Kunden erworbenen, zur Zeit gültigen Tickets
     *
     * @return  Liste von gültigen Tickets, welche dieser Kunde erworben hat
     */
    public List<Ticket> getAllTickets() {return this.tickets;}



}

package main.java.model;

/**
 * Abstrakte Klasse, die grundlegende Attribute und Methoden für alle Nutzer der Anwendung definiert.
 */
public abstract class Person {

    private String name;
    private String email;
    private String password;

    /**
     * Konstruktor für Erstellung eines Nutzers, wobei kontrolliert wird, dass die E-Mail-Adresse als Identifier einzigartig ist.
     *
     * @param name Name des Nutzers
     * @param email E-Mail-Adresse des Nutzers, muss einzigartig sein
     * @param password Passwort des Nutzers
     */
    public Person(String name, String email, String password) {
        this.name = name;
        //todo: check if email is unique
        this.email = email;
        this.password = password;
    }

    /**
     * Methode die kontrolliert, ob ein gegebener String dem Passwort der Person entspricht.
     * Vorgesehen zur Nutzung bei Anmeldung eines Nutzers, um die Authentizität zu überprüfen.
     *
     * @param password Das zu überprüfende Passwort
     * @return Wahr falls gegebener String dem Passwort dieser Person entspricht, sonst falsch
     */
    public boolean isAuthentic(String password) {
        return this.password.equals(password);
    }
}

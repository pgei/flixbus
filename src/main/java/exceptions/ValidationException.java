package main.java.exceptions;

/**
 * Ausnahmeklasse für ungültige Eingaben und Basisvalidierungsfehler
 */
public class ValidationException extends Exception {

    /**
     * Konstruktor der Ausnahmeklasse
     *
     * @param text  Nachricht mit der die Ausnahme geworfen werden soll
     */
    public ValidationException(String text) {super(text);}

}

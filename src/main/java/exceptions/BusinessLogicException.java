package main.java.exceptions;

/**
 * Ausnahmeklasse für Geschäftslogik-Fehler wie Überschreitung der Kapazitätsgrenze eines Transportes
 */
public class BusinessLogicException extends Exception {

    /**
     * Konstruktor der Ausnahmeklasse
     *
     * @param text  Nachricht mit der die Ausnahme geworfen werden soll
     */
    public BusinessLogicException(String text) {super(text);}
}

package main.java.exceptions;

/**
 * Ausnahmeklasse für den Fall, dass Entität anhand der ID nicht gefunden wird
 */
public class EntityNotFoundException extends Exception {

    /**
     * Konstruktor der Ausnahmeklasse
     *
     * @param text  Nachricht mit der die Ausnahme geworfen werden soll
     */
    public EntityNotFoundException(String text) {super(text);}

}

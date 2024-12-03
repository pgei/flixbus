package main.java.exceptions;

/**
 * Ausnahmeklasse für Fehler, die während der Datenbankoperationen auftreten
 */
public class DatabaseException extends Exception {

  /**
   * Konstruktor der Ausnahmeklasse
   *
   * @param text  Nachricht mit der die Ausnahme geworfen werden soll
   */
  public DatabaseException(String text) {super(text);}
}

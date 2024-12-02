package main.java.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Dienstprogrammklasse für die Verwaltung von Datenbankverbindungen.
 */
public class DatabaseConnection {

    /**
     * Die URL der Datenbank.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/your_database";

    /**
     * Der Benutzername der Datenbank.
     */
    private static final String USER = "your_username";

    /**
     * Das Passwort der Datenbank.
     */
    private static final String PASSWORD = "your_password";

    /**
     * Gibt eine {@link Connection} zur Datenbank zurück.
     *
     * @return eine Datenbankverbindung
     * @throws SQLException, falls ein Verbindungsfehler auftritt
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

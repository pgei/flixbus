package main.java.repository.mappers;

import main.java.model.Administrator;
import main.java.model.Costumer;
import main.java.model.Person;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper-Klasse für die Entität {@link Person}.
 * Diese Klasse wird verwendet, um eine {@link ResultSet}-Zeile in ein {@link Person}-Objekt umzuwandeln.
 * Je nach Typ des Nutzers wird entweder ein Administrator oder ein Kunde erstellt.
 */
public class PersonMapper implements EntityMapper<Person> {

    /**
     * Wandelt eine {@link ResultSet}-Zeile in ein {@link Person}-Objekt um.
     * Je nach Typ des Nutzers wird entweder ein Administrator oder ein Kunde erstellt.
     *
     * @param resultSet Das ResultSet, das die Daten der Person enthält.
     * @return Das erstellte Person-Objekt (Administrator oder Kunde).
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    @Override
    public Person map(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("person_id");
        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        boolean isAdmin = resultSet.getBoolean("is_admin");

        if (isAdmin) {
            return new Administrator(username, email, password);
        } else {
            return new Costumer(username, email, password);
        }

    }

    /**
     * Wandelt eine {@link ResultSet}-Zeile in ein {@link Costumer}-Objekt um.
     * Diese Methode wird verwendet, um nur einen Kunden aus dem ResultSet zu erstellen.
     *
     * @param resultSet Das ResultSet, das die Daten des Kunden enthält.
     * @return Das erstellte {@link Costumer}-Objekt.
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    public Costumer mapCustomer(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");

        return new Costumer(name, email, password);
    }
}

package main.java.repository.mappers;

import main.java.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper-Klasse für die Entität {@link Location}.
 * Diese Klasse wird verwendet, um eine {@link ResultSet}-Zeile in ein {@link Location}-Objekt zu konvertieren.
 */
public class LocationMapper {

    /**
     * Wandelt eine {@link ResultSet}-Zeile in ein {@link Location}-Objekt um.
     *
     * @param resultSet Das ResultSet, das die Daten der Location enthält.
     * @param prefix Das Präfix der Spaltennamen für die Location (z.B. "from" oder "to").
     * @return Das erstellte Location-Objekt.
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    public Location map(ResultSet resultSet, String prefix) throws SQLException {
        int id = resultSet.getInt(prefix + "_id");
        String street = resultSet.getString(prefix + "_street");
        String city = resultSet.getString(prefix + "_city");

        return new Location(id, street, city);
    }
}

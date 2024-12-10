package main.java.repository.mappers;

import main.java.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper-Klasse für die Entität {@link Location}.
 * Diese Klasse wird verwendet, um eine {@link ResultSet}-Zeile in ein {@link Location}-Objekt zu konvertieren.
 */
public class LocationMapper implements EntityMapper<Location> {

    /**
     * Wandelt eine {@link ResultSet}-Zeile in ein {@link Location}-Objekt um.
     *
     * @param resultSet Das ResultSet, das die Daten der Location enthält.
     * @return Das erstellte Location-Objekt.
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    @Override
    public Location map(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("location_id");
        String street = resultSet.getString("street");
        String city = resultSet.getString("city");

        return new Location(id, street, city);
    }

    /**
     * Überladene Methode, um eine Location basierend auf einer Fremdschlüssel-Spalte zuzuordnen.
     *
     * @param resultSet  Das ResultSet, das die Daten enthält.
     * @param columnName Der Name der Spalte, die die Location-ID enthält.
     * @return Das erstellte Location-Objekt mit der angegebenen ID.
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    public Location map(ResultSet resultSet, String columnName) throws SQLException {
        int locationId = resultSet.getInt(columnName);
        return new Location(locationId, null, null); // Straße und Stadt können bei Bedarf später abgerufen werden
    }
}

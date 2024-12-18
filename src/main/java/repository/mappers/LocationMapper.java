package main.java.repository.mappers;

import main.java.model.Location;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer Location-Entität.
 */
public class LocationMapper implements EntityMapper<Location> {

    /**
     * Methode die ResultSet in Location-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              Location-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public Location map(ResultSet rs) throws SQLException {
        return new Location(
                rs.getInt("location_id"),
                rs.getString("street"),
                rs.getString("city")
        );
    }
}
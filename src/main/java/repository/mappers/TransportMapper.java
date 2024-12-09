package main.java.repository.mappers;

import main.java.model.Bus;
import main.java.model.Transport;
import main.java.model.Train;
import main.java.model.Location;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper-Klasse für die Entität {@link Transport}.
 * Diese Klasse wird verwendet, um eine {@link ResultSet}-Zeile in ein {@link Transport}-Objekt zu konvertieren.
 * Sie unterstützt die Konvertierung für {@link Bus} und {@link Train}.
 */
public class TransportMapper implements EntityMapper<Transport> {

    private final LocationMapper locationMapper = new LocationMapper();

    /**
     * Wandelt eine {@link ResultSet}-Zeile in ein {@link Transport}-Objekt um.
     * Je nach Art des Transports wird entweder ein Bus oder ein Zug erstellt.
     *
     * @param resultSet Das ResultSet, das die Daten des Transports enthält.
     * @return Das erstellte Transport-Objekt (Bus oder Train).
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    @Override
    public Transport map(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String type = resultSet.getString("type");

        // Locations erstellen
        Location origin = locationMapper.map(resultSet, "from");
        Location destination = locationMapper.map(resultSet, "to");

        // Zeit und Datum des Transports
        int year = resultSet.getInt("year");
        int month = resultSet.getInt("month");
        int day = resultSet.getInt("day");
        int hourd = resultSet.getInt("hour_departure");
        int mind = resultSet.getInt("minute_departure");
        int houra = resultSet.getInt("hour_arrival");
        int mina = resultSet.getInt("minute_arrival");

        // Unterscheidung zwischen Bus und Zug
        if ("bus".equalsIgnoreCase(type)) {
            int capacity = resultSet.getInt("capacity");
            return new Bus(id, origin, destination, year, month, day, hourd, mind, houra, mina, capacity);
        } else if ("train".equalsIgnoreCase(type)) {
            int firstCapacity = resultSet.getInt("first_class_capacity");
            int secondCapacity = resultSet.getInt("second_class_capacity");
            return new Train(id, origin, destination, year, month, day, hourd, mind, houra, mina, firstCapacity, secondCapacity);
        }

        return null;
    }
}

package main.java.repository.mappers;

import main.java.model.Bus;
import main.java.model.Location;
import main.java.model.Train;
import main.java.model.Transport;

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
        int id = resultSet.getInt("transport_id");
        Location origin = locationMapper.map(resultSet, "origin_id");
        Location destination = locationMapper.map(resultSet, "destination_id");

        // Datum aufteilen
        java.sql.Date sqlDate = resultSet.getDate("date");
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(sqlDate);
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1; // Kalender-Monat ist 0-basiert
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        // Abfahrts- und Ankunftszeit aufteilen
        java.sql.Time sqlDepartureTime = resultSet.getTime("departure_time");
        java.sql.Time sqlArrivalTime = resultSet.getTime("arrival_time");
        java.util.Calendar departureCalendar = java.util.Calendar.getInstance();
        departureCalendar.setTime(sqlDepartureTime);
        int hourd = departureCalendar.get(java.util.Calendar.HOUR_OF_DAY);
        int mind = departureCalendar.get(java.util.Calendar.MINUTE);

        java.util.Calendar arrivalCalendar = java.util.Calendar.getInstance();
        arrivalCalendar.setTime(sqlArrivalTime);
        int houra = arrivalCalendar.get(java.util.Calendar.HOUR_OF_DAY);
        int mina = arrivalCalendar.get(java.util.Calendar.MINUTE);

        // Transporttyp ermitteln
        String type = resultSet.getString("transport_type");

        if ("BUS".equalsIgnoreCase(type)) {
            int capacity = resultSet.getInt("capacity");
            return new Bus(id, origin, destination, year, month, day, hourd, mind, houra, mina, capacity);
        } else if ("TRAIN".equalsIgnoreCase(type)) {
            int firstClassCapacity = resultSet.getInt("first_class_capacity");
            int secondClassCapacity = resultSet.getInt("second_class_capacity");
            return new Train(id, origin, destination, year, month, day, hourd, mind, houra, mina, firstClassCapacity, secondClassCapacity);
        }

        return null;
    }
}
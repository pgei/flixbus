package main.java.repository.mappers;

import main.java.model.Bus;
import main.java.model.Location;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer Bus-Entität.
 */
public class BusMapper implements EntityMapper<Bus> {

    /**
     * Methode die ResultSet in Bus-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              Bus-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public Bus map(ResultSet rs) throws SQLException {
        return new Bus(
                rs.getInt("transport_id"),
                rs.getInt("origin_id"),
                rs.getInt("destination_id"),
                rs.getDate("date").toLocalDate().getYear(),
                rs.getDate("date").toLocalDate().getMonthValue(),
                rs.getDate("date").toLocalDate().getDayOfMonth(),
                rs.getTime("departure_time").toLocalTime().getHour(),
                rs.getTime("departure_time").toLocalTime().getMinute(),
                rs.getTime("arrival_time").toLocalTime().getHour(),
                rs.getTime("arrival_time").toLocalTime().getMinute(),
                rs.getInt("capacity")
        );
    }
}
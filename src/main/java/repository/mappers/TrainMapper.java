package main.java.repository.mappers;

import main.java.model.Train;
import main.java.model.Location;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer Train-Entität.
 */
public class TrainMapper implements EntityMapper<Train> {

    /**
     * Methode die ResultSet in Train-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              Train-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public Train map(ResultSet rs) throws SQLException {
        return new Train(
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
                rs.getInt("first_class_capacity"),
                rs.getInt("second_class_capacity")
        );
    }
}
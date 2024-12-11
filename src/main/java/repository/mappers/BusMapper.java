package main.java.repository.mappers;

import main.java.model.Bus;
import main.java.model.Location;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class BusMapper implements EntityMapper<Bus> {
    @Override
    public Bus map(ResultSet rs) throws SQLException {
        Location origin = new Location(rs.getInt("origin_id"), rs.getString("origin_street"), rs.getString("origin_city"));
        Location destination = new Location(rs.getInt("destination_id"), rs.getString("destination_street"), rs.getString("destination_city"));

        return new Bus(
                rs.getInt("bus_id"),
                origin,
                destination,
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
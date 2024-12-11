package main.java.repository.mappers;

import main.java.model.Location;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationMapper implements EntityMapper<Location> {
    @Override
    public Location map(ResultSet rs) throws SQLException {
        return new Location(
                rs.getInt("id"),
                rs.getString("street"),
                rs.getString("city")
        );
    }
}
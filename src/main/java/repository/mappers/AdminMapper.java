package main.java.repository.mappers;

import main.java.model.Administrator;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminMapper implements EntityMapper<Administrator> {
    @Override
    public Administrator map(ResultSet rs) throws SQLException {
        return new Administrator(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
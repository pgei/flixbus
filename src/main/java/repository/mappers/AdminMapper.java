package main.java.repository.mappers;

import main.java.model.Administrator;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer Administrator-Entität.
 */
public class AdminMapper implements EntityMapper<Administrator> {

    /**
     * Methode die ResultSet in Administrator-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              Administrator-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public Administrator map(ResultSet rs) throws SQLException {
        return new Administrator(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
        );
    }
}
package main.java.repository.mappers;

import main.java.model.Costumer;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer Costumer-Entität.
 */
public class CustomerMapper implements EntityMapper<Costumer> {

    /**
     * Methode die ResultSet in Costumer-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              Costumer-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public Costumer map(ResultSet rs) throws SQLException {
        Costumer costumer = new Costumer(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
        );
        costumer.setBalance(rs.getInt("balance"));
        return costumer;
    }
}
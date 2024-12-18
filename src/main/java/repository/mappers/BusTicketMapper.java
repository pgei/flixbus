package main.java.repository.mappers;

import main.java.model.BusTicket;
import main.java.model.Bus;
import main.java.model.Costumer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer BusTicket-Entität.
 */
public class BusTicketMapper implements EntityMapper<BusTicket> {

    /**
     * Methode die ResultSet in BusTicket-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              BusTicket-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public BusTicket map(ResultSet rs) throws SQLException {
        return new BusTicket(
                rs.getInt("ticket_id"),
                rs.getString("email"),
                rs.getInt("transport_id"),
                rs.getInt("price"),
                rs.getInt("seat_number")
        );
    }
}

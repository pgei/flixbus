package main.java.repository.mappers;

import main.java.model.TrainTicket;
import main.java.model.Train;
import main.java.model.Costumer;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Klasse zum Zuordnen von ResultSet-Daten zu einer TrainTicket-Entität.
 */
public class TrainTicketMapper implements EntityMapper<TrainTicket> {

    /**
     * Methode die ResultSet in TrainTicket-Objekt überträgt
     *
     * @param rs            ResultSet das umgewandelt werden soll
     * @return              TrainTicket-Objekt
     * @throws SQLException Wenn ein Fehler auftritt
     */
    @Override
    public TrainTicket map(ResultSet rs) throws SQLException {
        return new TrainTicket(
                rs.getInt("ticket_id"),
                rs.getString("email"),
                rs.getInt("transport_id"),
                rs.getInt("price"),
                rs.getInt("seat_number"),
                Integer.parseInt(rs.getString("class"))
        );
    }
}

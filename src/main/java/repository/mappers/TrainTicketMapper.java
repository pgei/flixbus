package main.java.repository.mappers;

import main.java.model.TrainTicket;
import main.java.model.Train;
import main.java.model.Costumer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TrainTicketMapper implements EntityMapper<TrainTicket> {
    @Override
    public TrainTicket map(ResultSet rs) throws SQLException {
        Costumer costumer = new Costumer(
                rs.getString("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_email")
        );

        Train train = new Train(
                rs.getInt("train_id"),
                // Include required arguments for Train here, e.g., origin, destination, etc.
                // Assuming you'll fetch these fields or have relevant joins in the SQL query
                null, null, 0, 0, 0, 0, 0, 0, 0, 0,0
        );

        return new TrainTicket(
                rs.getInt("ticket_id"),
                costumer,
                train,
                rs.getInt("price"),
                rs.getInt("seat_number"),
                rs.getInt("class")
        );
    }
}

package main.java.repository.mappers;

import main.java.model.BusTicket;
import main.java.model.Bus;
import main.java.model.Costumer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BusTicketMapper implements EntityMapper<BusTicket> {
    @Override
    public BusTicket map(ResultSet rs) throws SQLException {
        Costumer costumer = new Costumer(
                rs.getString("customer_id"),
                rs.getString("customer_name"),
                rs.getString("customer_email")
        );

        Bus bus = new Bus(
                rs.getInt("bus_id"),
                // Include required arguments for Bus here, e.g., origin, destination, etc.
                // Assuming you'll fetch these fields or have relevant joins in the SQL query
                null, null, 0, 0, 0, 0, 0, 0, 0, 0
        );

        return new BusTicket(
                rs.getInt("ticket_id"),
                costumer,
                bus,
                rs.getInt("price"),
                rs.getInt("seat_number")
        );
    }
}

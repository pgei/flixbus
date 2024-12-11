package main.java.repository.mappers;

import com.mysql.cj.conf.ConnectionUrlParser;
import main.java.model.Costumer;
import main.java.model.Ticket;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerTicketHelper {
    public ConnectionUrlParser.Pair<Costumer, Ticket> map(ResultSet rs) throws SQLException {
        Costumer customer = new CustomerMapper().map(rs);
        Ticket ticket = rs.getString("ticket_type").equalsIgnoreCase("BUS")
                ? new BusTicketMapper().map(rs)
                : new TrainTicketMapper().map(rs);
        return new ConnectionUrlParser.Pair<>(customer, ticket);
    }
}

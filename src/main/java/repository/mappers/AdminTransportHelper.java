package main.java.repository.mappers;

import com.mysql.cj.conf.ConnectionUrlParser;
import main.java.model.Administrator;
import main.java.model.Transport;


import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminTransportHelper {
    public ConnectionUrlParser.Pair<Administrator, Transport> map(ResultSet rs) throws SQLException {
        Administrator admin = new AdminMapper().map(rs);
        Transport transport = rs.getString("transport_type").equalsIgnoreCase("BUS")
                ? new BusMapper().map(rs)
                : new TrainMapper().map(rs);
        return new ConnectionUrlParser.Pair<>(admin, transport);
    }
}

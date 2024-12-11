package main.java.repository.mappers;

import main.java.model.Costumer;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerMapper implements EntityMapper<Costumer> {
    @Override
    public Costumer map(ResultSet rs) throws SQLException {
        return new Costumer(
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password")
                //rs.getInt("balance")
                //Balance wurde vielleicht als der 4te Attribut des Customer-Klasse, um es hier zu verwenden
        );
    }
}
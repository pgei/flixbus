package main.java.repository.mappers;

import main.java.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Mapper-Klasse für die Entität {@link Ticket}.
 * Diese Klasse wird verwendet, um eine {@link ResultSet}-Zeile in ein {@link Ticket}-Objekt umzuwandeln.
 * Sie unterstützt sowohl Bus- als auch Zugtickets.
 */
public class TicketMapper implements EntityMapper<Ticket> {

    /**
     * Wandelt eine {@link ResultSet}-Zeile in ein {@link Ticket}-Objekt um.
     * Je nach Typ des Tickets wird entweder ein {@link BusTicket} oder ein {@link TrainTicket} erstellt.
     *
     * @param resultSet Das ResultSet, das die Ticket-Daten enthält.
     * @return Das erstellte {@link Ticket}-Objekt (Bus- oder Zugticket).
     * @throws SQLException Wenn ein Fehler beim Zugriff auf die Datenbank auftritt.
     */
    private final PersonMapper personMapper = new PersonMapper();
    private final TransportMapper transportMapper = new TransportMapper();

    @Override
    public Ticket map(ResultSet resultSet) throws SQLException {
        int ticketId = resultSet.getInt("ticket_id");
        Costumer customer = (Costumer) personMapper.map(resultSet);
        Transport transport = transportMapper.map(resultSet);
        int price = resultSet.getInt("price");
        int seatNumber = resultSet.getInt("seat_number");

        if (transport instanceof Bus) {
            return new BusTicket(ticketId, customer, transport, price, seatNumber);
        } else if (transport instanceof Train) {
            int ticketClass = resultSet.getInt("ticket_class"); // Needs an additional column in schema for class.
            return new TrainTicket(ticketId, customer, transport, price, seatNumber, ticketClass);
        }

        return null;
    }
}

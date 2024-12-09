package main.java.repository.mappers;

import main.java.model.BusTicket;
import main.java.model.Costumer;
import main.java.model.Ticket;
import main.java.model.Transport;
import main.java.model.TrainTicket;

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
    @Override
    public Ticket map(ResultSet resultSet) throws SQLException {
        int ticketId = resultSet.getInt("id");
        int price = resultSet.getInt("price");
        int seat = resultSet.getInt("seat");
        int transportId = resultSet.getInt("transport_id");
        int customerId = resultSet.getInt("customer_id");

        Transport transport = new TransportMapper().map(resultSet);
        Costumer customer = new PersonMapper().mapCustomer(resultSet); // Spezifische Methode für Kunden

        String type = resultSet.getString("type");

        if ("bus".equalsIgnoreCase(type)) {
            return new BusTicket(ticketId, customer, transport, price, seat);
        } else if ("train".equalsIgnoreCase(type)) {
            int ticketClass = resultSet.getInt("ticket_class");
            return new TrainTicket(ticketId, customer, transport, price, seat, ticketClass);
        }

        return null;
    }
}

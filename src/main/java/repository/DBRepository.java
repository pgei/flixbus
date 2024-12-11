package main.java.repository;

import main.java.database.DatabaseConnection;
import main.java.exceptions.DatabaseException;
import main.java.model.*;
import main.java.repository.mappers.EntityMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository-Klasse zur Verwaltung von Datenspeicherung und -abfrage mit einer relationalen Datenbank.
 *
 * @param <T> Der Typ der zu speichernden Objekte, die das {@link ID}-Interface für eine eindeutige Identifikation implementieren müssen.
 */
public class DBRepository<T extends ID> implements IRepository<T> {

    private final String tableName;
    private final EntityMapper<T> mapper;

    /**
     * Konstruktor zur Erstellung eines Datenbank-Repositories.
     *
     * @param tableName Der Name der Datenbanktabelle, auf der operiert werden soll.
     * @param mapper    Ein Mapper zur Umwandlung von Datenbankzeilen in Objekte vom Typ T.
     */
    public DBRepository(String tableName, EntityMapper<T> mapper) {
        this.tableName = tableName;
        this.mapper = mapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(T object) throws DatabaseException {
        String sql = generateInsertQuery();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(stmt, object);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Fehler beim Erstellen des Eintrags in der Tabelle " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Object id) throws DatabaseException {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fehler beim Abrufen des Eintrags mit der ID " + id + " aus der Tabelle " + tableName);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T object) throws DatabaseException {
        String sql = generateUpdateQuery();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, object);
            stmt.setObject(getParameterCount() + 1, object.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Fehler beim Aktualisieren des Eintrags in der Tabelle " + tableName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object id) throws DatabaseException {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Fehler beim Löschen des Eintrags mit der ID " + id + " aus der Tabelle " + tableName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() throws DatabaseException {
        String sql = "SELECT * FROM " + tableName;
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fehler beim Abrufen aller Einträge aus der Tabelle " + tableName);
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        try {
            return get(key) != null;
        } catch (DatabaseException e) {
            return false;
        }
    }

    /**
     * Generiert eine SQL-Insert-Abfrage basierend auf der Tabelle.
     *
     * @return Die generierte SQL-Insert-Abfrage.
     */
    private String generateInsertQuery() {
        switch (tableName.toLowerCase()) {
            case "person":
                return "INSERT INTO Person (username, email, password, is_admin) VALUES (?, ?, ?, ?)";
            case "location":
                return "INSERT INTO Location (street, city) VALUES (?, ?)";
            case "transport":
                return "INSERT INTO Transport (origin_id, destination_id, date, departure_time, arrival_time, transport_type) VALUES (?, ?, ?, ?, ?, ?)";
            case "bus":
                return "INSERT INTO Bus (bus_id, capacity) VALUES (?, ?)";
            case "train":
                return "INSERT INTO Train (train_id, first_class_capacity, second_class_capacity) VALUES (?, ?, ?)";
            case "ticket":
                return "INSERT INTO Ticket (customer_id, transport_id, price, seat_number) VALUES (?, ?, ?, ?)";
            default:
                throw new IllegalArgumentException("Unbekannte Tabelle: " + tableName);
        }
    }

    /**
     * Generiert eine SQL-Update-Abfrage basierend auf der Tabelle.
     *
     * @return Die generierte SQL-Update-Abfrage.
     */
    private String generateUpdateQuery() {
        switch (tableName.toLowerCase()) {
            case "person":
                return "UPDATE Person SET username = ?, email = ?, password = ?, is_admin = ? WHERE id = ?";
            case "location":
                return "UPDATE Location SET street = ?, city = ? WHERE id = ?";
            case "transport":
                return "UPDATE Transport SET origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, transport_type = ? WHERE id = ?";
            case "bus":
                return "UPDATE Bus SET capacity = ? WHERE bus_id = ?";
            case "train":
                return "UPDATE Train SET first_class_capacity = ?, second_class_capacity = ? WHERE train_id = ?";
            case "ticket":
                return "UPDATE Ticket SET customer_id = ?, transport_id = ?, price = ?, seat_number = ? WHERE ticket_id = ?";
            default:
                throw new IllegalArgumentException("Unbekannte Tabelle: " + tableName);
        }
    }

    /**
     * Setzt die Parameter eines PreparedStatements basierend auf dem Objekt.
     *
     * @param stmt   Das PreparedStatement.
     * @param object Das Objekt, dessen Felder in die Parameter eingesetzt werden sollen.
     * @throws SQLException Wenn ein Fehler auftritt.
     */
    private void setStatementParameters(PreparedStatement stmt, T object) throws SQLException {
        if (object instanceof Administrator) {
            Administrator admin = (Administrator) object;
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, (String) admin.getId());
            stmt.setString(3, admin.isAuthentic("password") ? "password" : "");
            stmt.setBoolean(4, true);
        } else if (object instanceof Costumer) {
            Costumer customer = (Costumer) object;
            stmt.setString(1, customer.getUsername());
            stmt.setString(2, (String) customer.getId());
            stmt.setString(3, customer.isAuthentic("password") ? "password" : "");
            stmt.setBoolean(4, false);
        }else if (object instanceof Location) {
            Location location = (Location) object;
            stmt.setString(1, location.getStreet());
            stmt.setString(2, location.getCity());
        } else if (object instanceof Transport) {
            Transport transport = (Transport) object;
            stmt.setInt(1, (int) transport.getOrigin().getId());
            stmt.setInt(2, (int) transport.getDestination().getId());
            stmt.setDate(3, java.sql.Date.valueOf(transport.getDate()));
            stmt.setTime(4, java.sql.Time.valueOf(transport.getDepartureTime()));
            stmt.setTime(5, java.sql.Time.valueOf(transport.getArrivalTime()));
            stmt.setString(6, transport instanceof Bus ? "BUS" : "TRAIN");
        } else if (object instanceof Bus) {
            Bus bus = (Bus) object;
            stmt.setInt(1, (int) bus.getId());
            stmt.setInt(2, bus.getCapacity());
        } else if (object instanceof Train) {
            Train train = (Train) object;
            stmt.setInt(1, (int) train.getId());
            stmt.setInt(2, train.getFirstCapacity());
            stmt.setInt(3, train.getSecondCapacity());
        } else if (object instanceof Ticket) {
            Ticket ticket = (Ticket) object;
            stmt.setInt(1, (int) ticket.getCostumer().getId());
            stmt.setInt(2, (int) ticket.getTransport().getId());
            stmt.setDouble(3, ticket.getPrice());
            stmt.setInt(4, ticket.getSeat());
        } else {
            throw new SQLException("Unbekanntes Objekt für das Statement: " + object.getClass().getName());
        }
    }


    /**
     * Liefert die Anzahl der Parameter für die Abfrage.
     *
     * @return Anzahl der Parameter.
     */
    private int getParameterCount() {
        if ("person".equalsIgnoreCase(tableName)) {
            return 4;
        } else if ("location".equalsIgnoreCase(tableName)) {
            return 2;
        } else if ("transport".equalsIgnoreCase(tableName)) {
            return 6;
        } else if ("bus".equalsIgnoreCase(tableName)) {
            return 2;
        } else if ("train".equalsIgnoreCase(tableName)) {
            return 3;
        } else if ("ticket".equalsIgnoreCase(tableName)) {
            return 4;
        }
        return 0;
    }
}

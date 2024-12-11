package main.java.repository;

import com.mysql.cj.conf.ConnectionUrlParser;
import main.java.database.DatabaseConnection;
import main.java.exceptions.DatabaseException;
import main.java.model.*;
import main.java.repository.mappers.*;

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

    // Deklaration von Mappern
    private final AdminMapper adminMapper = new AdminMapper();
    private final CustomerMapper customerMapper = new CustomerMapper();
    private final LocationMapper locationMapper = new LocationMapper();
    private final BusMapper busMapper = new BusMapper();
    private final TrainMapper trainMapper = new TrainMapper();
    private final BusTicketMapper busTicketMapper = new BusTicketMapper();
    private final TrainTicketMapper trainTicketMapper = new TrainTicketMapper();

    /**
     * Konstruktor zur Erstellung eines Datenbank-Repositories.
     *
     * @param tableName Der Name der Datenbanktabelle, auf der operiert werden soll.
     * @param mapper    Ein Mapper zur Umwandlung von Datenbankzeilen in Objekte vom Typ T.
     */
    public DBRepository(String tableName, EntityMapper<T> mapper) {
        this.tableName = tableName;
    }

    /**
     * Ruft eine Liste der Bus-Ticket-Paare aus der BusHashMap-Tabelle auf.
     */
    public List<ConnectionUrlParser.Pair<Bus, Ticket>> getBusHashMap() throws DatabaseException {
        String sql = "SELECT * FROM BusHashMap";
        List<ConnectionUrlParser.Pair<Bus, Ticket>> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Bus bus = new BusMapper().map(rs);
                Ticket ticket = new BusTicketMapper().map(rs);
                results.add(new ConnectionUrlParser.Pair<>(bus, ticket));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching BusHashMap records.");
        }
        return results;
    }

    /**
     * Ruft eine Liste der Zug-Ticket-Paare aus der TrainHashMap-Tabelle auf.
     */
    public List<ConnectionUrlParser.Pair<Train, Ticket>> getTrainHashMap() throws DatabaseException {
        String sql = "SELECT * FROM TrainHashMap";
        List<ConnectionUrlParser.Pair<Train, Ticket>> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Train train = new TrainMapper().map(rs);
                Ticket ticket = new TrainTicketMapper().map(rs);
                results.add(new ConnectionUrlParser.Pair<>(train, ticket));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching TrainHashMap records.");
        }
        return results;
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
                    return mapResultSetToEntity(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table " + tableName);
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
            stmt.setObject(getParameterCount() + 1, object.getId());  // Assuming the ID is the last parameter
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
                results.add(mapResultSetToEntity(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all entries from table " + tableName);
        }
        return results;
    }

    /**
     * Mappt eine Datenbankzeile (ResultSet) zu einem Objekt des Typs T.
     *
     * @param rs Das ResultSet der Datenbankabfrage.
     * @return Das gemappte Objekt.
     * @throws SQLException Wenn ein Fehler beim Mapping auftritt.
     */
    private T mapResultSetToEntity(ResultSet rs) throws SQLException, DatabaseException {
        switch (tableName.toLowerCase()) {
            case "admin":
                return (T) adminMapper.map(rs);
            case "customer":
                return (T) customerMapper.map(rs);
            case "location":
                return (T) locationMapper.map(rs);
            case "bus":
                return (T) busMapper.map(rs);
            case "train":
                return (T) trainMapper.map(rs);
            case "busticket":
                return (T) busTicketMapper.map(rs);
            case "trainticket":
                return (T) trainTicketMapper.map(rs);
            default:
                throw new DatabaseException("Unknown table: " + tableName);
        }
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
            case "admin":
                return "INSERT INTO Admin (username, email, password) VALUES (?, ?, ?)";
            case "customer":
                return "INSERT INTO Customer (username, email, password, balance) VALUES (?, ?, ?, ?)";
            case "location":
                return "INSERT INTO Location (street, city) VALUES (?, ?)";
            case "bus":
                return "INSERT INTO Bus (origin_id, destination_id, date, departure_time, arrival_time, capacity) VALUES (?, ?, ?, ?, ?, ?)";
            case "train":
                return "INSERT INTO Train (origin_id, destination_id, date, departure_time, arrival_time, first_class_capacity, second_class_capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case "busticket":
                return "INSERT INTO BusTicket (customer_id, bus_id, price, seat_number) VALUES (?, ?, ?, ?)";
            case "trainticket":
                return "INSERT INTO TrainTicket (customer_id, train_id, price, seat_number, class) VALUES (?, ?, ?, ?, ?)";
            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }

    /**
     * Generiert eine SQL-Update-Abfrage basierend auf der Tabelle.
     *
     * @return Die generierte SQL-Update-Abfrage.
     */
    private String generateUpdateQuery() {
        switch (tableName.toLowerCase()) {
            case "admin":
                return "UPDATE Admin SET username = ?, email = ?, password = ? WHERE admin_id = ?";
            case "customer":
                return "UPDATE Customer SET username = ?, email = ?, password = ?, balance = ? WHERE customer_id = ?";
            case "location":
                return "UPDATE Location SET street = ?, city = ? WHERE location_id = ?";
            case "bus":
                return "UPDATE Bus SET origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, capacity = ? WHERE bus_id = ?";
            case "train":
                return "UPDATE Train SET origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, first_class_capacity = ?, second_class_capacity = ? WHERE train_id = ?";
            case "busticket":
                return "UPDATE BusTicket SET customer_id = ?, bus_id = ?, price = ?, seat_number = ? WHERE ticket_id = ?";
            case "trainticket":
                return "UPDATE TrainTicket SET customer_id = ?, train_id = ?, price = ?, seat_number = ?, class = ? WHERE ticket_id = ?";
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
            stmt.setString(3, admin.getPassword());
            stmt.setBoolean(4, true);
        } else if (object instanceof Costumer) {
            Costumer customer = (Costumer) object;
            stmt.setString(1, customer.getUsername());
            stmt.setString(2, (String) customer.getId());
            stmt.setString(3, customer.getPassword());
            stmt.setBoolean(4, false);
        }else if (object instanceof Location) {
            Location location = (Location) object;
            stmt.setString(1, location.getStreet());
            stmt.setString(2, location.getCity());
        } else if (object instanceof Bus) {
            Bus bus = (Bus) object;
            stmt.setInt(1, (int) bus.getId());
            stmt.setInt(2, (int) bus.getOrigin().getId());
            stmt.setInt(3, (int) bus.getDestination().getId());
            stmt.setDate(4, java.sql.Date.valueOf(bus.getDate()));
            stmt.setTime(5, java.sql.Time.valueOf(bus.getDepartureTime()));
            stmt.setTime(6, java.sql.Time.valueOf(bus.getArrivalTime()));
            stmt.setInt(7, bus.getCapacity());
        } else if (object instanceof Train) {
            Train train = (Train) object;
            stmt.setInt(1, (int) train.getId());
            stmt.setInt(2, (int) train.getOrigin().getId());
            stmt.setInt(3, (int) train.getDestination().getId());
            stmt.setDate(4, java.sql.Date.valueOf(train.getDate()));
            stmt.setTime(5, java.sql.Time.valueOf(train.getDepartureTime()));
            stmt.setTime(6, java.sql.Time.valueOf(train.getArrivalTime()));
            stmt.setInt(7, train.getFirstCapacity());
            stmt.setInt(8, train.getSecondCapacity());
        } else if (object instanceof BusTicket) {
            BusTicket ticket = (BusTicket) object;
            stmt.setInt(1, (int) ticket.getId());   // ticket.customer_id
            stmt.setInt(2, (int) ticket.getId());        // ticket.bus_id
            stmt.setInt(3, ticket.getPrice()); // ticket.price (DECIMAL)
            stmt.setInt(4, ticket.getSeat());   // ticket.seat_number (INT)
        } else if (object instanceof TrainTicket) {
            TrainTicket ticket = (TrainTicket) object;
            stmt.setInt(1, (int) ticket.getId());     // ticket.customer_id
            stmt.setInt(2, (int) ticket.getId());        // ticket.train_id
            stmt.setInt(3, ticket.getPrice());   // ticket.price (DECIMAL)
            stmt.setInt(4, ticket.getSeat());
            if(ticket.getTicketClass()==1)
                stmt.setInt(5, 1);
            else
                stmt.setInt(5, 2);
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
        if ("Admin".equalsIgnoreCase(tableName)) {
            return 3;  // admin.username, admin.email, admin.password
        } else if ("Customer".equalsIgnoreCase(tableName)) {
            return 4;  // customer.username, customer.email, customer.password, customer.balance
        } else if ("Location".equalsIgnoreCase(tableName)) {
            return 2;  // location.street, location.city
        } else if ("Bus".equalsIgnoreCase(tableName)) {
            return 6;  // bus.origin_id, bus.destination_id, bus.date, bus.departure_time, bus.arrival_time, bus.capacity
        } else if ("Train".equalsIgnoreCase(tableName)) {
            return 7;  // train.origin_id, train.destination_id, train.date, train.departure_time, train.arrival_time, train.first_class_capacity, train.second_class_capacity
        } else if ("BusTicket".equalsIgnoreCase(tableName)) {
            return 4;  // ticket.customer_id, ticket.bus_id, ticket.price, ticket.seat_number
        } else if ("TrainTicket".equalsIgnoreCase(tableName)) {
            return 5;  // ticket.customer_id, ticket.train_id, ticket.price, ticket.seat_number, ticket.class
        }
        return 0;
    }

}

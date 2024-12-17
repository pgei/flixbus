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
        switch (object.getClass().getName()) {
            case "Administrator":
                Administrator administrator = (Administrator) object;
                executeInsert(generateInsertQuery("Person"), object, "Person");
                executeInsert(generateInsertQuery("Admin"), object, "Admin");
                administrator.getAllAdministeredTransports().forEach(transport -> {
                    executeInsert(generateInsertQuery("AdminTransport"), (T) transport, "AdminTransport", (String) administrator.getId());
                });
            case "Costumer":
                Costumer costumer = (Costumer) object;
                executeInsert(generateInsertQuery("Person"), object, "Person");
                executeInsert(generateInsertQuery("Costumer"), object, "Costumer");
                costumer.getAllTickets().forEach(ticket -> {
                    executeInsert(generateInsertQuery("CostumerTicket"), (T) ticket, "CostumerTicket", (String) costumer.getId());
                });
            case "Bus":
                Bus bus = (Bus) object;
                executeInsert(generateInsertQuery("Transport"), object, "Transport");
                executeInsert(generateInsertQuery("Bus"), object, "Bus");
                bus.getBookedSeats().values().forEach(busTicket -> {
                    executeInsert(generateInsertQuery("BusHashMap"), (T) busTicket, "BusHashMap");
                });
            case "Train":
                Train train = (Train) object;
                executeInsert(generateInsertQuery("Transport"), object, "Transport");
                executeInsert(generateInsertQuery("Train"), object, "Train");
                train.getBookedSeats().values().forEach(trainTicket -> {
                    executeInsert(generateInsertQuery("TrainHashMap"), (T) trainTicket, "TrainHashMap");
                });
            case "BusTicket":
                executeInsert(generateInsertQuery("Ticket"), object, "Ticket");
                executeInsert(generateInsertQuery("BusTicket"), object, "BusTicket");
            case "TrainTicket":
                executeInsert(generateInsertQuery("Ticket"), object, "Ticket");
                executeInsert(generateInsertQuery("TrainTicket"), object, "TrainTicket");
            case "Location":
                executeInsert(generateInsertQuery("Location"), object, "Location");
            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }

    private void executeInsert(String sql, T object, String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(stmt, object, tableName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during insert into table " + tableName + ": " + e.getMessage());
        }
    }

    private void executeInsert(String sql, T object, String tableName, String adminID) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (tableName.equals("AdminTransport") || tableName.equals("CostumerTicket")) {
                 stmt.setString(1, adminID);
                 stmt.setInt(2, (int) object.getId());
            } else {
                throw new SQLException("Unknown table: "+tableName);
             }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during insert into table " + tableName + ": " + e.getMessage());
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
        switch (object.getClass().getName()) {
            case "Administrator":
                Administrator administrator = (Administrator) object;
                executeUpdate(generateUpdateQuery("Admin"), object, "Admin");
                executeDelete(object, "AdminTransport");
                administrator.getAllAdministeredTransports().forEach(transport -> {
                    executeInsert(generateInsertQuery("AdminTransport"), (T) transport, "AdminTransport", (String) administrator.getId());
                });
            case "Costumer":
                Costumer costumer = (Costumer) object;
                executeUpdate(generateUpdateQuery("Costumer"), object, "Costumer");
                executeDelete(object, "CostumerTicket");
                costumer.getAllTickets().forEach(ticket -> {
                    executeInsert(generateInsertQuery("CostumerTicket"), (T) ticket, "CostumerTicket", (String) costumer.getId());
                });
            case "Bus":
                Bus bus = (Bus) object;
                executeUpdate(generateUpdateQuery("Bus"), object, "Bus");
                executeDelete(object, "BusHashMap");
                bus.getBookedSeats().values().forEach(busTicket -> {
                    executeInsert(generateInsertQuery("BusHashMap"), (T) busTicket, "BusHashMap");
                });
            case "Train":
                Train train = (Train) object;
                executeUpdate(generateUpdateQuery("Train"), object, "Train");
                executeDelete(object, "TrainHashMap");
                train.getBookedSeats().values().forEach(trainTicket -> {
                    executeInsert(generateInsertQuery("TrainHashMap"), (T) trainTicket, "TrainHashMap");
                });
            case "BusTicket":
                executeUpdate(generateUpdateQuery("BusTicket"), object, "BusTicket");
            case "TrainTicket":
                executeUpdate(generateUpdateQuery("TrainTicket"), object, "TrainTicket");
            case "Location":
                executeUpdate(generateUpdateQuery("Location"), object, "Location");
            default:
                throw new DatabaseException("Unknown object class: " + object.getClass().getName());
        }

    }

    private void executeUpdate(String sql, T object, String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, object, tableName);
            stmt.setObject(getParameterCount() + 1, object.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during update of entry in table " + tableName+": "+e.getMessage());
        }
    }

    /**
     * Only used for AdminTransport, CostumerTicket and Bus/TrainHashMap
     *
     * @param object
     * @param tableName
     */
    private void executeDelete(T object, String tableName) {
        String sql = "";
        if (object instanceof Person) {
            sql = "DELETE FROM " + tableName + " WHERE email = ?";
        } else if (object instanceof Transport) {
            sql = "DELETE FROM " + tableName + " WHERE transport_id = ?";
        } else throw new DatabaseException("Object of unexpected class: "+object.getClass().getName());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, object.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during removal of entry with ID " + object.getId() + " from table " + tableName+": "+e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object id) throws DatabaseException {
        //Due to "cascade on delete" in database the linked entries in other tables are also removed
        String sql = "";
        if (tableName.equals("Person")) {
            sql = "DELETE FROM " + tableName + " WHERE email = ?";
        } else if (tableName.equals("Transport")) {
            sql = "DELETE FROM " + tableName + " WHERE transport_id = ?";
        } else if (tableName.equals("Ticket")) {
            sql = "DELETE FROM " + tableName + " WHERE ticket_id = ?";
        } else if (tableName.equals("Location")) {
            sql = "DELETE FROM " + tableName + " WHERE location_id = ?";
        } else throw new DatabaseException("Unexpected table name for repository: "+this.tableName);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during deletion of entry with ID " + id + " from table " + tableName);
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
    private String generateInsertQuery(String tablename) {
        switch (tablename) {
            case "Person":
                return "INSERT INTO Person (email, person_table) VALUES (?, ?)";
            case "Admin":
                return "INSERT INTO Admin (email,username, password) VALUES (?, ?, ?)";
            case "AdminTransport":
                return "INSERT INTO AdminTransport (email, transport_id) VALUES (?, ?)";
            case "Costumer":
                return "INSERT INTO Customer (email,username, password, balance) VALUES (?, ?, ?, ?)";
            case "CostumerTicket":
                return "INSERT INTO CostumerTicket (email, ticket_id) VALUES (?, ?)";
            case "Transport":
                return "INSERT INTO Transport (transport_id, transport_table) VALUES (?, ?)";
            case "Bus":
                return "INSERT INTO Bus (transport_id, origin_id, destination_id, date, departure_time, arrival_time, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case "BusHashMap":
                return "INSERT INTO BusHashMap (transport_id, seat_number, ticket_id) VALUES (?, ?, ?)";
            case "Train":
                return "INSERT INTO Train (transport_id, origin_id, destination_id, date, departure_time, arrival_time, first_class_capacity, second_class_capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            case "TrainHashMap":
                return "INSERT INTO TrainHashMap (transport_id, seat_number, ticket_id) VALUES (?, ?, ?)";
            case "Ticket":
                return "INSERT INTO Ticket (ticket_id, ticket_table) VALUES (?, ?)";
            case "BusTicket":
                return "INSERT INTO BusTicket (ticket_id, email, transport_id, price, seat_number) VALUES (?, ?, ?, ?, ?)";
            case "TrainTicket":
                return "INSERT INTO TrainTicket (ticket_id, email, transport_id, price, seat_number, class) VALUES (?, ?, ?, ?, ?, ?)";
            case "Location":
                return "INSERT INTO Location (location_id, street, city) VALUES (?, ?, ?)";
            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }

    /**
     * Generiert eine SQL-Update-Abfrage basierend auf der Tabelle.
     *
     * @return Die generierte SQL-Update-Abfrage.
     */
    private String generateUpdateQuery(String tableName) {
        switch (tableName) {
            case "Admin":
                return "UPDATE Admin SET email = ?, username = ?, password = ? WHERE email = ?";
            case "Costumer":
                return "UPDATE Customer SET email = ?,username = ?, password = ?, balance = ? WHERE email = ?";
            case "Bus":
                return "UPDATE Bus SET transport_id = ?, origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, capacity = ? WHERE transport_id = ?";
            case "Train":
                return "UPDATE Train SET transport_id = ?, origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, first_class_capacity = ?, second_class_capacity = ? WHERE transport_id = ?";
            case "BusTicket":
                return "UPDATE BusTicket SET ticket_id = ?, email = ?, transport_id = ?, price = ?, seat_number = ? WHERE ticket_id = ?";
            case "TrainTicket":
                return "UPDATE TrainTicket SET ticket_id = ?, email = ?, transport_id = ?, price = ?, seat_number = ?, class = ? WHERE ticket_id = ?";
            case "Location":
                return "UPDATE Location SET location_id = ?, street = ?, city = ? WHERE location_id = ?";
            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }


    /**
     * Setzt die Parameter eines PreparedStatements basierend auf dem Objekt.
     *
     * @param stmt   Das PreparedStatement.
     * @param object Das Objekt, dessen Felder in die Parameter eingesetzt werden sollen.
     * @throws SQLException Wenn ein Fehler auftritt.
     */
    private void setStatementParameters(PreparedStatement stmt, T object, String tablename) throws SQLException {
        switch (tablename) {
            case "Person":
                Person person = (Person) object;
                stmt.setString(1, (String) person.getId());
                if (person instanceof Administrator) {
                    stmt.setString(2, "Admin");
                } else if (person instanceof Costumer) {
                    stmt.setString(2, "Costumer");
                }
            case "Admin":
                Administrator admin = (Administrator) object;
                stmt.setString(1, (String) admin.getId());
                stmt.setString(2, admin.getUsername());
                stmt.setString(3, admin.getPassword());
            case "Costumer":
                Costumer costumer = (Costumer) object;
                stmt.setString(1, (String) costumer.getId());
                stmt.setString(2, costumer.getUsername());
                stmt.setString(3, costumer.getPassword());
                stmt.setInt(4, costumer.getBalance());
            case "Transport":
                Transport transport = (Transport) object;
                stmt.setInt(1, (int) transport.getId());
                if (transport instanceof Bus) {
                    stmt.setString(2, "Bus");
                } else if (transport instanceof Train) {
                    stmt.setString(2, "Train");
                }
            case "Bus":
                Bus bus = (Bus) object;
                stmt.setInt(1, (int) bus.getId());
                stmt.setInt(2, (int) bus.getOrigin().getId());
                stmt.setInt(3, (int) bus.getDestination().getId());
                stmt.setDate(4, java.sql.Date.valueOf(bus.getDate()));
                stmt.setTime(5, java.sql.Time.valueOf(bus.getDepartureTime()));
                stmt.setTime(6, java.sql.Time.valueOf(bus.getArrivalTime()));
                stmt.setInt(7, bus.getCapacity());
            case "BusHashMap":
                BusTicket ticket1 = (BusTicket) object;
                stmt.setInt(1, (int) ticket1.getTransport().getId());
                stmt.setInt(2, ticket1.getSeat());
                stmt.setInt(3, (int) ticket1.getId());
            case "Train":
                Train train = (Train) object;
                stmt.setInt(1, (int) train.getId());
                stmt.setInt(2, (int) train.getOrigin().getId());
                stmt.setInt(3, (int) train.getDestination().getId());
                stmt.setDate(4, java.sql.Date.valueOf(train.getDate()));
                stmt.setTime(5, java.sql.Time.valueOf(train.getDepartureTime()));
                stmt.setTime(6, java.sql.Time.valueOf(train.getArrivalTime()));
                stmt.setInt(7, train.getFirstCapacity());
                stmt.setInt(8, train.getSecondCapacity());
            case "TrainHashMap":
                TrainTicket ticket2 = (TrainTicket) object;
                stmt.setInt(1, (int) ticket2.getTransport().getId());
                stmt.setInt(2, ticket2.getSeat());
                stmt.setInt(3, (int) ticket2.getId());
            case "Ticket":
                Ticket ticket = (Ticket) object;
                stmt.setInt(1, (int) ticket.getId());
                if (ticket instanceof BusTicket) {
                    stmt.setString(2, "BusTicket");
                } else if (ticket instanceof TrainTicket) {
                    stmt.setString(2, "TrainTicket");
                }
            case "BusTicket":
                BusTicket busTicket = (BusTicket) object;
                stmt.setInt(1, (int) busTicket.getId());
                stmt.setString(2, (String) busTicket.getCostumer().getId());
                stmt.setInt(3,(int) busTicket.getTransport().getId());
                stmt.setInt(4,busTicket.getPrice());
                stmt.setInt(5,busTicket.getSeat());
            case "TrainTicket":
                TrainTicket trainTicket = (TrainTicket) object;
                stmt.setInt(1, (int) trainTicket.getId());
                stmt.setString(2, (String) trainTicket.getCostumer().getId());
                stmt.setInt(3,(int) trainTicket.getTransport().getId());
                stmt.setInt(4,trainTicket.getPrice());
                stmt.setInt(5,trainTicket.getSeat());
                if (trainTicket.getTicketClass() == 1) {
                    stmt.setString(6, "1");
                } else if (trainTicket.getTicketClass() == 2) {
                    stmt.setString(6, "2");
                }
            case "Location":
                Location location = (Location) object;
                stmt.setInt(1, (int) location.getId());
                stmt.setString(2, location.getStreet());
                stmt.setString(3, location.getCity());
            default:
                throw new DatabaseException("Unknown table encountered when setting statement parameters: " + tableName);
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

    public Costumer getCostumerByEmail(String email) throws SQLException {
        String sql = "SELECT email, username, password, balance FROM Costumer WHERE email = ?";

        Costumer result = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = new Costumer(
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Bus getBusById(int transportId) throws SQLException {
        String sql = "SELECT transport_id, origin_id, destination_id, date, departure_time, arrival_time, capacity " +
                "FROM Bus WHERE transport_id = ?";

        Bus result = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Location origin = getLocationById(rs.getInt("origin_id"));
                    Location destination = getLocationById(rs.getInt("destination_id"));
                    result = new Bus(
                            rs.getInt("transport_id"),
                            origin,
                            destination,
                            rs.getDate("date").getYear(),
                            rs.getDate("date").getMonth() + 1,
                            rs.getDate("date").getDate(),
                            rs.getTime("departure_time").getHours(),
                            rs.getTime("departure_time").getMinutes(),
                            rs.getTime("arrival_time").getHours(),
                            rs.getTime("arrival_time").getMinutes(),
                            rs.getInt("capacity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Train getTrainById(int transportId) throws SQLException {
        String sql = "SELECT transport_id, origin_id, destination_id, date, departure_time, arrival_time, first_class_capacity, second_class_capacity " +
                "FROM Train WHERE transport_id = ?";

        Train result = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transportId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Location origin = getLocationById(rs.getInt("origin_id"));
                    Location destination = getLocationById(rs.getInt("destination_id"));
                    result = new Train(
                            rs.getInt("transport_id"),
                            origin,
                            destination,
                            rs.getDate("date").getYear(),
                            rs.getDate("date").getMonth() + 1,
                            rs.getDate("date").getDate(),
                            rs.getTime("departure_time").getHours(),
                            rs.getTime("departure_time").getMinutes(),
                            rs.getTime("arrival_time").getHours(),
                            rs.getTime("arrival_time").getMinutes(),
                            rs.getInt("first_class_capacity"),
                            rs.getInt("second_class_capacity")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Ticket getTicketById(int ticketId) throws SQLException {
        String sql = "SELECT t.ticket_id, t.ticket_table, c.email AS customer_email, c.username, c.password, c.balance, " +
                "bt.seat_number AS bus_seat, bt.price AS bus_price, tt.seat_number AS train_seat, tt.price AS train_price, " +
                "bt.transport_id AS bus_transport_id, tt.transport_id AS train_transport_id " +
                "FROM Ticket t " +
                "LEFT JOIN BusTicket bt ON t.ticket_id = bt.ticket_id " +
                "LEFT JOIN TrainTicket tt ON t.ticket_id = tt.ticket_id " +
                "JOIN Costumer c ON t.ticket_id = c.email " +
                "WHERE t.ticket_id = ?";

        Ticket result = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Costumer customer = new Costumer(
                            rs.getString("customer_email"),
                            rs.getString("username"),
                            rs.getString("password")
                    );

                    if ("BusTicket".equals(rs.getString("ticket_table"))) {
                        // For BusTicket
                        Bus bus = getBusById(rs.getInt("bus_transport_id"));
                        result = new BusTicket(
                                rs.getInt("ticket_id"),
                                customer,
                                bus,
                                rs.getInt("bus_price"),
                                rs.getInt("bus_seat")
                        );
                    } else if ("TrainTicket".equals(rs.getString("ticket_table"))) {
                        // For TrainTicket
                        Train train = getTrainById(rs.getInt("train_transport_id"));
                        result = new TrainTicket(
                                rs.getInt("ticket_id"),
                                customer,
                                train,
                                rs.getInt("train_price"),
                                rs.getInt("train_seat"),
                                rs.getInt("ticket_class")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Location getLocationById(int locationId) throws SQLException {
        String sql = "SELECT location_id, street, city FROM Location WHERE location_id = ?";

        Location result = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, locationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = new Location(
                            rs.getInt("location_id"),
                            rs.getString("street"),
                            rs.getString("city")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Administrator getAdminByEmail(String email) throws SQLException {
        String sql = "SELECT email, username, password FROM Admin WHERE email = ?";

        Administrator result = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    result = new Administrator(
                            rs.getString("email"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Administrator getAdminTransportByEmailAndId(String email, int transportId) throws SQLException {
        // SQL query to retrieve administrator and transport details (Bus or Train) based on email and transport ID
        String sql = "SELECT a.email AS admin_email, a.username AS admin_username, " +
                "b.transport_id AS bus_transport_id, b.origin_id AS bus_origin_id, b.destination_id AS bus_destination_id, " +
                "b.year AS bus_year, b.month AS bus_month, b.day AS bus_day, b.hourd AS bus_hourd, b.mind AS bus_mind, " +
                "b.houra AS bus_houra, b.mina AS bus_mina, b.capacity AS bus_capacity, " +
                "tr.transport_id AS train_transport_id, tr.origin_id AS train_origin_id, tr.destination_id AS train_destination_id, " +
                "tr.year AS train_year, tr.month AS train_month, tr.day AS train_day, tr.hourd AS train_hourd, tr.mind AS train_mind, " +
                "tr.houra AS train_houra, tr.mina AS train_mina, tr.capacity AS train_capacity " +
                "FROM Administrator a " +
                "LEFT JOIN Bus b ON b.admin_id = a.admin_id " +
                "LEFT JOIN Train tr ON tr.admin_id = a.admin_id " +
                "WHERE a.email = ? AND (b.transport_id = ? OR tr.transport_id = ?)";

        Administrator admin = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setInt(2, transportId);
            stmt.setInt(3, transportId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Creating Administrator object based on result set
                    admin = new Administrator(
                            rs.getString("admin_username"),
                            rs.getString("admin_email"),
                            "" // Optionally handle password or other attributes
                    );

                    // Now, let's create the transport (Bus or Train)
                    if (rs.getInt("bus_transport_id") != 0) {
                        // If it's a Bus
                        Location busOrigin = new Location(rs.getInt("bus_origin_id"), null, null);
                        Location busDestination = new Location(rs.getInt("bus_destination_id"), null, null);
                        Bus bus = new Bus(
                                rs.getInt("bus_transport_id"),
                                busOrigin,
                                busDestination,
                                rs.getInt("bus_year"),
                                rs.getInt("bus_month"),
                                rs.getInt("bus_day"),
                                rs.getInt("bus_hourd"),
                                rs.getInt("bus_mind"),
                                rs.getInt("bus_houra"),
                                rs.getInt("bus_mina"),
                                rs.getInt("bus_capacity")
                        );
                        admin.getAllAdministeredTransports().add(bus);
                    } else if (rs.getInt("train_transport_id") != 0) {
                        // If it's a Train
                        Location trainOrigin = new Location(rs.getInt("train_origin_id"), null, null);
                        Location trainDestination = new Location(rs.getInt("train_destination_id"), null, null);
                        Train train = new Train(
                                rs.getInt("train_transport_id"),
                                trainOrigin,
                                trainDestination,
                                rs.getInt("train_year"),
                                rs.getInt("train_month"),
                                rs.getInt("train_day"),
                                rs.getInt("train_hourd"),
                                rs.getInt("train_mind"),
                                rs.getInt("train_houra"),
                                rs.getInt("train_mina"),
                                rs.getInt("first_capacity"),
                                rs.getInt("second_capacity")
                        );
                        admin.getAllAdministeredTransports().add(train);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admin;
    }

}

package main.java.repository;

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
     */
    public DBRepository(String tableName) {
        this.tableName = tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(T object) throws DatabaseException {
        switch (object.getClass().getSimpleName()) {
            case "Administrator":
                Administrator administrator = (Administrator) object;
                executeInsert(generateInsertQuery("Person"), object, "Person");
                executeInsert(generateInsertQuery("Admin"), object, "Admin");
                administrator.getAllAdministeredTransports().forEach(transport -> executeInsert(generateInsertQuery("AdminTransport"), (T) transport, "AdminTransport", (String) administrator.getId()));
                break;
            case "Costumer":
                Costumer costumer = (Costumer) object;
                executeInsert(generateInsertQuery("Person"), object, "Person");
                executeInsert(generateInsertQuery("Costumer"), object, "Costumer");
                costumer.getAllTickets().forEach(ticket -> executeInsert(generateInsertQuery("CostumerTicket"), (T) ticket, "CostumerTicket", (String) costumer.getId()));
                break;
            case "Bus":
                Bus bus = (Bus) object;
                executeInsert(generateInsertQuery("Transport"), object, "Transport");
                executeInsert(generateInsertQuery("Bus"), object, "Bus");
                bus.getBookedSeats().values().forEach(busTicket -> executeInsert(generateInsertQuery("BusHashMap"), (T) busTicket, "BusHashMap"));
                break;
            case "Train":
                Train train = (Train) object;
                executeInsert(generateInsertQuery("Transport"), object, "Transport");
                executeInsert(generateInsertQuery("Train"), object, "Train");
                train.getBookedSeats().values().forEach(trainTicket -> executeInsert(generateInsertQuery("TrainHashMap"), (T) trainTicket, "TrainHashMap"));
                break;
            case "BusTicket":
                executeInsert(generateInsertQuery("Ticket"), object, "Ticket");
                executeInsert(generateInsertQuery("BusTicket"), object, "BusTicket");
                break;
            case "TrainTicket":
                executeInsert(generateInsertQuery("Ticket"), object, "Ticket");
                executeInsert(generateInsertQuery("TrainTicket"), object, "TrainTicket");
                break;
            case "Location":
                executeInsert(generateInsertQuery("Location"), object, "Location");
                break;
            default:
                throw new DatabaseException("Unknown class of object: " + object.getClass().getSimpleName());
        }
    }

    /**
     * Führt SQL-Befehle aus für Insert-Operation.
     *
     * @param sql           SQL-Befehl
     * @param object        Objekt das in die Datenbank eingefügt werden soll
     * @param tableName     Name der Tabelle in der Datenbank wo Objekt eingefügt werden soll
     */
    private void executeInsert(String sql, T object, String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStatementParameters(stmt, object, tableName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during insert into table " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * Führt SQL-Befehle aus für Insert-Operation, spezifische Methode für AdminTransport und CostumerTicket welche Information über den Administrator/Kunden benötigen
     *
     * @param sql           SQL-Befehl
     * @param object        Objekt das in die Datenbank eingefügt werden soll
     * @param tableName     Name der Tabelle in der Datenbank wo Objekt eingefügt werden soll
     * @param adminID       ID des Administrators
     */
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
        return switch (this.tableName) {
            case "Person" -> (T) executeGetPerson((String) id);
            case "Transport" -> (T) executeGetTransport((Integer) id);
            case "Ticket" -> (T) executeGetTicket((Integer) id);
            case "Location" -> (T) executeGetLocation((Integer) id);
            default -> throw new DatabaseException("Repository with table of unknown type: " + this.tableName);
        };
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von Orten
     *
     * @param id    ID des Ortes
     * @return      Ort-Objekt, falls eins gefunden wurde, sonst null
     */
    private Location executeGetLocation(int id) {
        String sql = "SELECT * FROM Location WHERE location_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return locationMapper.map(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table Location: "+e.getMessage());
        }
        return null;
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von Tickets
     *
     * @param id    ID des Tickets
     * @return      Ticket-Objekt, falls eins gefunden wurde, sonst null
     */
    private Ticket executeGetTicket(int id) {
        String sql = "SELECT ticket_table FROM Ticket WHERE ticket_id = ?";
        String type = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    type = rs.getString("ticket_table");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table Ticket: "+e.getMessage());
        }
        sql = "SELECT * FROM "+type+" WHERE ticket_id = ?";
        if (type.equals("BusTicket")) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return busTicketMapper.map(rs);
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error fetching entry with ID " + id + " from table " + type);
            }
        } else if (type.equals("TrainTicket")) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return trainTicketMapper.map(rs);
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error fetching entry with ID " + id + " from table " + type);
            }
        } else throw new DatabaseException("Ticket of unknown type: "+type);
        return null;
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von Transporten
     *
     * @param id    ID des Transports
     * @return      Transport-Objekt, falls eins gefunden wurde, sonst null
     */
    private Transport executeGetTransport(int id) {
        String sql = "SELECT transport_table FROM Transport WHERE transport_id = ?";
        String type = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    type = rs.getString("transport_table");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table Transport: "+e.getMessage());
        }
        sql = "SELECT * FROM "+type+" WHERE transport_id = ?";
        if (type.equals("Bus")) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Bus bus = busMapper.map(rs);
                        executeGetBusHashMapTickets(id).forEach(ticketID -> bus.getBookedSeats().put(ticketID[0], (BusTicket) executeGetTicket(ticketID[1])));
                        return bus;
                    }
                }
            } catch (SQLException e) {
                System.err.println(e.getMessage());
                throw new DatabaseException("Error fetching entry with ID " + id + " from table " + type);
            }
        } else if (type.equals("Train")) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Train train = trainMapper.map(rs);
                        executeGetTrainHashMapTickets(id).forEach(ticketID -> train.getBookedSeats().put(ticketID[0], (TrainTicket) executeGetTicket(ticketID[1])));
                        return train;
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error fetching entry with ID " + id + " from table " + type);
            }
        } else if (type.isEmpty()) {
            return null;
        } else throw new DatabaseException("Transport of unknown type: "+type);
        return null;
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von BusHashMap
     *
     * @param id    ID des Transportes
     * @return      Liste mit Wertepaaren bestehend aus Sitznummer und TicketID
     */
    private ArrayList<int[]> executeGetBusHashMapTickets(int id) {
        String sql = "SELECT * FROM BusHashMap WHERE transport_id = ?";
        ArrayList<int[]> tickets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new int[]{rs.getInt("seat_number"), rs.getInt("ticket_id")});
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table BusHashMap: "+e.getMessage());
        }
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von TrainHashMap
     *
     * @param id    ID des Transportes
     * @return      Liste mit Wertepaaren bestehend aus Sitznummer und TicketID
     */
    private ArrayList<int[]> executeGetTrainHashMapTickets(int id) {
        String sql = "SELECT * FROM TrainHashMap WHERE transport_id = ?";
        ArrayList<int[]> tickets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(new int[]{rs.getInt("seat_number"), rs.getInt("ticket_id")});
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table TrainHashMap:"+e.getMessage());
        }
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von Personen
     *
     * @param id    ID der Person
     * @return      Person-Objekt, falls eins gefunden wurde, sonst null
     */
    private Person executeGetPerson(String id) {
        String sql = "SELECT person_table FROM Person WHERE email = ?";
        String type = "";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    type = rs.getString("person_table");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table Person: "+e.getMessage());
        }
        sql = "SELECT * FROM "+type+" WHERE email = ?";
        if (type.equals("Admin")) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Administrator admin = adminMapper.map(rs);
                        executeGetAdminTransports(id).forEach(transportID -> admin.getAllAdministeredTransports().add(executeGetTransport(transportID)));
                        return admin;
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error fetching entry with ID " + id + " from table " + type);
            }
        } else if (type.equals("Costumer")) {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Costumer costumer = customerMapper.map(rs);
                        executeGetCostumerTickets(id).forEach(ticketID -> costumer.getAllTickets().add(executeGetTicket(ticketID)));
                        return costumer;
                    }
                }
            } catch (SQLException e) {
                throw new DatabaseException("Error fetching entry with ID " + id + " from table " + type);
            }
        }
        return null;
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von AdminTransport
     *
     * @param id    ID des Administrators
     * @return      Liste aller verwalteten TransportIDs
     */
    private ArrayList<Integer> executeGetAdminTransports(String id) {
        String sql = "SELECT * FROM AdminTransport WHERE email = ?";
        ArrayList<Integer> transports = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    transports.add(rs.getInt("transport_id"));
                }
                return transports;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table AdminTransport: "+e.getMessage());
        }
    }

    /**
     * Führt SQL-Befehle aus für Get-Operation von CostumerTicket
     *
     * @param id    ID des Kunden
     * @return      Liste aller gekauften Tickets in Form von deren ID
     */
    private ArrayList<Integer> executeGetCostumerTickets(String id) {
        String sql = "SELECT * FROM CostumerTicket WHERE email = ?";
        ArrayList<Integer> tickets = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    tickets.add(rs.getInt("ticket_id"));
                }
                return tickets;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching entry with ID " + id + " from table CostumerTicket: "+e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T object) throws DatabaseException {
        switch (object.getClass().getSimpleName()) {
            case "Administrator":
                Administrator administrator = (Administrator) object;
                executeUpdate(generateUpdateQuery("Admin"), object, "Admin");
                executeDelete(object, "AdminTransport");
                administrator.getAllAdministeredTransports().forEach(transport -> executeInsert(generateInsertQuery("AdminTransport"), (T) transport, "AdminTransport", (String) administrator.getId()));
                break;
            case "Costumer":
                Costumer costumer = (Costumer) object;
                executeUpdate(generateUpdateQuery("Costumer"), object, "Costumer");
                executeDelete(object, "CostumerTicket");
                costumer.getAllTickets().forEach(ticket -> executeInsert(generateInsertQuery("CostumerTicket"), (T) ticket, "CostumerTicket", (String) costumer.getId()));
                break;
            case "Bus":
                Bus bus = (Bus) object;
                executeUpdate(generateUpdateQuery("Bus"), object, "Bus");
                executeDelete(object, "BusHashMap");
                bus.getBookedSeats().values().forEach(busTicket -> executeInsert(generateInsertQuery("BusHashMap"), (T) busTicket, "BusHashMap"));
                break;
            case "Train":
                Train train = (Train) object;
                executeUpdate(generateUpdateQuery("Train"), object, "Train");
                executeDelete(object, "TrainHashMap");
                train.getBookedSeats().values().forEach(trainTicket -> executeInsert(generateInsertQuery("TrainHashMap"), (T) trainTicket, "TrainHashMap"));
                break;
            case "BusTicket":
                executeUpdate(generateUpdateQuery("BusTicket"), object, "BusTicket");
                break;
            case "TrainTicket":
                executeUpdate(generateUpdateQuery("TrainTicket"), object, "TrainTicket");
                break;
            case "Location":
                executeUpdate(generateUpdateQuery("Location"), object, "Location");
                break;
            default:
                throw new DatabaseException("Unknown object class: " + object.getClass().getSimpleName());
        }
    }

    /**
     * Führt SQL-Befehle aus für Update-Operation
     *
     * @param sql           SQL-Befehl
     * @param object        Objekt das in die Datenbank aktualisiert werden soll
     * @param tableName     Name der Tabelle in der Datenbank wo Objekt aktualisiert werden soll
     */
    private void executeUpdate(String sql, T object, String tableName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, object, tableName);
            stmt.setObject(getParameterCount(tableName) + 1, object.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error during update of entry in table " + tableName+": "+e.getMessage());
        }
    }

    /**
     * Führt SQL-Befehle aus für Update-Operation, nur für AdminTransport, CostumerTicket, BusHashMap and TrainHashMap genutzt
     *
     * @param object        Objekt das in die Datenbank entfernt werden soll
     * @param tableName     Name der Tabelle in der Datenbank wo Objekt entfernt werden soll
     */
    private void executeDelete(T object, String tableName) {
        String sql;
        if (object instanceof Person) {
            sql = "DELETE FROM " + tableName + " WHERE email = ?";
        } else if (object instanceof Transport) {
            sql = "DELETE FROM " + tableName + " WHERE transport_id = ?";
        } else throw new DatabaseException("Object of unexpected class: "+object.getClass().getSimpleName());
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
        String sql = switch (tableName) {
            case "Person" -> "DELETE FROM " + tableName + " WHERE email = ?";
            case "Transport" -> "DELETE FROM " + tableName + " WHERE transport_id = ?";
            case "Ticket" -> "DELETE FROM " + tableName + " WHERE ticket_id = ?";
            case "Location" -> "DELETE FROM " + tableName + " WHERE location_id = ?";
            default -> throw new DatabaseException("Unexpected table name for repository: " + this.tableName);
        };
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
        List<Object> idList = new ArrayList<>();
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            switch (this.tableName) {
                case "Person": {
                    while (rs.next()) {
                        idList.add((rs.getString("email")));
                    }
                    idList.forEach(personId -> results.add((T) executeGetPerson((String) personId)));
                    break;
                }
                case "Transport": {
                    while (rs.next()) {
                        idList.add((rs.getInt("transport_id")));
                    }
                    idList.forEach(transportId -> results.add((T) executeGetTransport((int) transportId)));
                    break;
                }
                case "Ticket": {
                    while (rs.next()) {
                        idList.add((rs.getInt("ticket_id")));
                    }
                    idList.forEach(ticketId -> results.add((T) executeGetTicket((int) ticketId)));
                    break;
                }
                case "Location": {
                    while (rs.next()) {
                        idList.add((rs.getInt("location_id")));
                    }
                    idList.forEach(locationId -> results.add((T) executeGetLocation((int) locationId)));
                    break;
                }
                default: throw new DatabaseException("Table of unexpected type: "+this.tableName);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all entries from table " + tableName);
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
     * @param tableName     Name der Tabelle in der Datenbank wo SQL-Operation ausgeführt werden soll
     * @return              Die generierte SQL-Insert-Abfrage.
     */
    private String generateInsertQuery(String tableName) {
        return switch (tableName) {
            case "Person" -> "INSERT INTO Person (email, person_table) VALUES (?, ?)";
            case "Admin" -> "INSERT INTO Admin (email, username, password) VALUES (?, ?, ?)";
            case "AdminTransport" -> "INSERT INTO AdminTransport (email, transport_id) VALUES (?, ?)";
            case "Costumer" -> "INSERT INTO Costumer (email, username, password, balance) VALUES (?, ?, ?, ?)";
            case "CostumerTicket" -> "INSERT INTO CostumerTicket (email, ticket_id) VALUES (?, ?)";
            case "Transport" -> "INSERT INTO Transport (transport_id, transport_table) VALUES (?, ?)";
            case "Bus" ->
                    "INSERT INTO Bus (transport_id, origin_id, destination_id, date, departure_time, arrival_time, capacity) VALUES (?, ?, ?, ?, ?, ?, ?)";
            case "BusHashMap" -> "INSERT INTO BusHashMap (transport_id, seat_number, ticket_id) VALUES (?, ?, ?)";
            case "Train" ->
                    "INSERT INTO Train (transport_id, origin_id, destination_id, date, departure_time, arrival_time, first_class_capacity, second_class_capacity) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            case "TrainHashMap" -> "INSERT INTO TrainHashMap (transport_id, seat_number, ticket_id) VALUES (?, ?, ?)";
            case "Ticket" -> "INSERT INTO Ticket (ticket_id, ticket_table) VALUES (?, ?)";
            case "BusTicket" ->
                    "INSERT INTO BusTicket (ticket_id, email, transport_id, price, seat_number) VALUES (?, ?, ?, ?, ?)";
            case "TrainTicket" ->
                    "INSERT INTO TrainTicket (ticket_id, email, transport_id, price, seat_number, class) VALUES (?, ?, ?, ?, ?, ?)";
            case "Location" -> "INSERT INTO Location (location_id, street, city) VALUES (?, ?, ?)";
            default -> throw new IllegalArgumentException("Unknown table: " + tableName);
        };
    }

    /**
     * Generiert eine SQL-Update-Abfrage basierend auf der Tabelle.
     *
     * @param tableName     Name der Tabelle in der Datenbank wo SQL-Operation ausgeführt werden soll
     * @return              Die generierte SQL-Update-Abfrage.
     */
    private String generateUpdateQuery(String tableName) {
        return switch (tableName) {
            case "Admin" -> "UPDATE Admin SET email = ?, username = ?, password = ? WHERE email = ?";
            case "Costumer" -> "UPDATE Costumer SET email = ?,username = ?, password = ?, balance = ? WHERE email = ?";
            case "Bus" ->
                    "UPDATE Bus SET transport_id = ?, origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, capacity = ? WHERE transport_id = ?";
            case "Train" ->
                    "UPDATE Train SET transport_id = ?, origin_id = ?, destination_id = ?, date = ?, departure_time = ?, arrival_time = ?, first_class_capacity = ?, second_class_capacity = ? WHERE transport_id = ?";
            case "BusTicket" ->
                    "UPDATE BusTicket SET ticket_id = ?, email = ?, transport_id = ?, price = ?, seat_number = ? WHERE ticket_id = ?";
            case "TrainTicket" ->
                    "UPDATE TrainTicket SET ticket_id = ?, email = ?, transport_id = ?, price = ?, seat_number = ?, class = ? WHERE ticket_id = ?";
            case "Location" -> "UPDATE Location SET location_id = ?, street = ?, city = ? WHERE location_id = ?";
            default -> throw new IllegalArgumentException("Unknown table: " + tableName);
        };
    }

    /**
     * Setzt die Parameter eines PreparedStatements basierend auf dem Objekt.
     *
     * @param stmt                  PreparedStatement das befüllt werden soll
     * @param object                Objekt, dessen Felder in die Parameter eingesetzt werden sollen.
     * @param tableName             Name der Tabelle in der Datenbank wo SQL-Operation ausgeführt werden soll
     * @throws DatabaseException    Wenn Tabellenname unbekannt ist
     * @throws SQLException         Wenn ein Fehler auftritt
     */
    private void setStatementParameters(PreparedStatement stmt, T object, String tableName) throws DatabaseException, SQLException {
        switch (tableName) {
            case "Person":
                Person person = (Person) object;
                stmt.setString(1, (String) person.getId());
                if (person instanceof Administrator) {
                    stmt.setString(2, "Admin");
                } else if (person instanceof Costumer) {
                    stmt.setString(2, "Costumer");
                }
                break;
            case "Admin":
                Administrator admin = (Administrator) object;
                stmt.setString(1, (String) admin.getId());
                stmt.setString(2, admin.getUsername());
                stmt.setString(3, admin.getPassword());
                break;
            case "Costumer":
                Costumer costumer = (Costumer) object;
                stmt.setString(1, (String) costumer.getId());
                stmt.setString(2, costumer.getUsername());
                stmt.setString(3, costumer.getPassword());
                stmt.setInt(4, costumer.getBalance());
                break;
            case "Transport":
                Transport transport = (Transport) object;
                stmt.setInt(1, (int) transport.getId());
                if (transport instanceof Bus) {
                    stmt.setString(2, "Bus");
                } else if (transport instanceof Train) {
                    stmt.setString(2, "Train");
                }
                break;
            case "Bus":
                Bus bus = (Bus) object;
                stmt.setInt(1, (int) bus.getId());
                stmt.setInt(2, bus.getOrigin());
                stmt.setInt(3, bus.getDestination());
                stmt.setDate(4, java.sql.Date.valueOf(bus.getDate()));
                stmt.setTime(5, java.sql.Time.valueOf(bus.getDepartureTime()));
                stmt.setTime(6, java.sql.Time.valueOf(bus.getArrivalTime()));
                stmt.setInt(7, bus.getCapacity());
                break;
            case "BusHashMap":
                BusTicket ticket1 = (BusTicket) object;
                stmt.setInt(1, ticket1.getTransport());
                stmt.setInt(2, ticket1.getSeat());
                stmt.setInt(3, (int) ticket1.getId());
                break;
            case "Train":
                Train train = (Train) object;
                stmt.setInt(1, (int) train.getId());
                stmt.setInt(2, train.getOrigin());
                stmt.setInt(3, train.getDestination());
                stmt.setDate(4, java.sql.Date.valueOf(train.getDate()));
                stmt.setTime(5, java.sql.Time.valueOf(train.getDepartureTime()));
                stmt.setTime(6, java.sql.Time.valueOf(train.getArrivalTime()));
                stmt.setInt(7, train.getFirstCapacity());
                stmt.setInt(8, train.getSecondCapacity());
                break;
            case "TrainHashMap":
                TrainTicket ticket2 = (TrainTicket) object;
                stmt.setInt(1, ticket2.getTransport());
                stmt.setInt(2, ticket2.getSeat());
                stmt.setInt(3, (int) ticket2.getId());
                break;
            case "Ticket":
                Ticket ticket = (Ticket) object;
                stmt.setInt(1, (int) ticket.getId());
                if (ticket instanceof BusTicket) {
                    stmt.setString(2, "BusTicket");
                } else if (ticket instanceof TrainTicket) {
                    stmt.setString(2, "TrainTicket");
                }
                break;
            case "BusTicket":
                BusTicket busTicket = (BusTicket) object;
                stmt.setInt(1, (int) busTicket.getId());
                stmt.setString(2, busTicket.getCostumer());
                stmt.setInt(3, busTicket.getTransport());
                stmt.setInt(4,busTicket.getPrice());
                stmt.setInt(5,busTicket.getSeat());
                break;
            case "TrainTicket":
                TrainTicket trainTicket = (TrainTicket) object;
                stmt.setInt(1, (int) trainTicket.getId());
                stmt.setString(2, trainTicket.getCostumer());
                stmt.setInt(3,trainTicket.getTransport());
                stmt.setInt(4,trainTicket.getPrice());
                stmt.setInt(5,trainTicket.getSeat());
                if (trainTicket.getTicketClass() == 1) {
                    stmt.setString(6, "1");
                } else if (trainTicket.getTicketClass() == 2) {
                    stmt.setString(6, "2");
                }
                break;
            case "Location":
                Location location = (Location) object;
                stmt.setInt(1, (int) location.getId());
                stmt.setString(2, location.getStreet());
                stmt.setString(3, location.getCity());
                break;
            default:
                throw new DatabaseException("Unknown table encountered when setting statement parameters: " + tableName);
        }
    }

    /**
     * Liefert die Anzahl der Parameter für die Abfrage.
     *
     * @return Anzahl der Parameter.
     */
    private int getParameterCount(String tableName) {
        if ("Admin".equalsIgnoreCase(tableName)) {
            return 3;  // admin.username, admin.email, admin.password
        } else if ("Costumer".equalsIgnoreCase(tableName)) {
            return 4;  // customer.username, customer.email, customer.password, customer.balance
        } else if ("Location".equalsIgnoreCase(tableName)) {
            return 3;  // location.street, location.city
        } else if ("Bus".equalsIgnoreCase(tableName)) {
            return 7;  // bus.origin_id, bus.destination_id, bus.date, bus.departure_time, bus.arrival_time, bus.capacity
        } else if ("Train".equalsIgnoreCase(tableName)) {
            return 8;  // train.origin_id, train.destination_id, train.date, train.departure_time, train.arrival_time, train.first_class_capacity, train.second_class_capacity
        } else if ("BusTicket".equalsIgnoreCase(tableName)) {
            return 5;  // ticket.customer_id, ticket.bus_id, ticket.price, ticket.seat_number
        } else if ("TrainTicket".equalsIgnoreCase(tableName)) {
            return 6;  // ticket.customer_id, ticket.train_id, ticket.price, ticket.seat_number, ticket.class
        }
        return 0;
    }


}
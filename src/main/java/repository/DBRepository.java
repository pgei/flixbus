package main.java.repository;

import main.java.database.DatabaseConnection;
import main.java.exceptions.DatabaseException;
import main.java.model.ID;
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
        String sql = "INSERT INTO " + tableName + " VALUES (?, ?, ?, ?)"; // Adjust columns and parameters as needed
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, object);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DatabaseException: Error creating entry in " + tableName + ": " + e.getMessage());
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
            throw new DatabaseException("DatabaseException: Error fetching entry with ID " + id + " from " + tableName + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T object) throws DatabaseException {
        String sql = "UPDATE " + tableName + " SET column1 = ?, column2 = ?, column3 = ? WHERE id = ?"; // Adjust columns
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setStatementParameters(stmt, object);
            stmt.setObject(4, object.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("DatabaseException: Error updating entry with ID " + object.getId() + " in " + tableName + ": " + e.getMessage());
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
            throw new DatabaseException("Error deleting entry with ID " + id + " from " + tableName + ": " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() throws DatabaseException {
        String sql = "SELECT * FROM " + tableName;
        List<T> resultList = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                resultList.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching all entries from " + tableName + ": " + e.getMessage());
        }
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return get(key) != null;
    }

    /**
     * Setzt die Parameter eines PreparedStatements basierend auf den Feldern des Objekts.
     *
     * @param stmt   Das vorbereitete SQL-Statement.
     * @param object Das Objekt, das die einzufügenden Werte enthält.
     */
    private void setStatementParameters(PreparedStatement stmt, T object) throws DatabaseException {
        // Beispiel: Anpassung basierend auf der Struktur des Objekts
        // stmt.setObject(1, object.getField1());
        // stmt.setObject(2, object.getField2());
        // Fügen Sie so viele Parameter hinzu, wie es das Tabellenschema erfordert.
    }
}

package main.java.repository.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Funktionales Interface zum Zuordnen von ResultSet-Daten zu einer Entität.
 *
 * @param <T> Der Typ der Entität.
 */
@FunctionalInterface
public interface EntityMapper<T> {
    T map(ResultSet resultSet) throws SQLException;
}

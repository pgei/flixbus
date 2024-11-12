package main.java.repository;

import main.java.model.ID;
import java.util.List;

/**
 * Interface, das die grundlegenden CRUD-Methoden festlegt für die Verwaltung von Daten in Repositories.
 */
public interface IRepository<T extends ID> {

    /**
     * Erzeugt ein neues Objekt im Repository.
     *
     * @param object    Objekt das im Repository erzeugt werden soll
     */
    void create(T object);

    /**
     * Abruf eines Objektes das im Repository gespeichert ist über die ID
     *
     * @param id    Einzigartige ID des Objektes das abgerufen werden soll
     * @return      Objekt aus dem Repository oder null, wenn kein Objekt mit der gegebenen ID gefunden wurde
     */
    T get(Object id);

    /**
     * Aktualisiert ein bereits existierendes Objekt im Repository
     *
     * @param object    Objekt das aktualisiert werden soll
     */
    void update(T object);

    /**
     * Entfernt ein Objekt aus dem Repository, falls es unter der gegebenen ID gefunden werden kann
     *
     * @param id    Einzigartige ID des Objektes das gelöscht werden soll
     */
    void delete(Object id);

    /**
     * Abruf aller im Repository gespeicherten Objekte
     *
     * @return  Liste aller Objekte im Repository
     */
    List<T> getAll();

}

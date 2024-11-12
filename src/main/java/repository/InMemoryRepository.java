package main.java.repository;

import main.java.model.ID;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Klasse, die ein Repository abbildet, welches Daten im Speicher lagert, wobei die CRUD-Methoden des Interfaces IRepository implementiert werden.
 *
 * @param <T>   Typ der Objekte, die in diesem Repository gespeichert werden sollen, wobei dieser eine einzigartige ID zur Verfügung stellen muss (siehe {@link ID})
 */
public class InMemoryRepository<T extends ID> implements IRepository<T> {

    private final Map<Object,T> repository =new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(T object) {
        this.repository.putIfAbsent(object.getId(), object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Object id) {
        return this.repository.get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T object) {
        this.repository.replace(object.getId(),object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object id) {
        this.repository.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() {
        return this.repository.values().stream().toList();
    }
}

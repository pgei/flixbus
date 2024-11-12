package main.java.model;

import java.io.Serializable;

/**
 * Interface, das ein Objekt mit einzigartiger ID definiert
 */
public interface ID<T> extends Serializable {

    /**
     *  Getter für einzigartige ID des Objektes
     *
     * @return Einzigartige ID
     */
    T getId();

}

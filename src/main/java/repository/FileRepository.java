package main.java.repository;

import main.java.model.ID;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *  Klasse, die ein Repository abbildet, welches Daten in einer Datei lagert, wobei die CRUD-Methoden des Interfaces IRepository implementiert werden.
 *
 * @param <T>   Typ der Objekte, die in diesem Repository gespeichert werden sollen, wobei dieser eine einzigartige ID zur Verfügung stellen muss (siehe {@link ID})
 */
public class FileRepository<T extends ID & Serializable>implements IRepository<T> {

    /**
     * Dateipfad für Repository-Datei
     */
    private final String filePath;

    /**
     * Konstruktor für Erstellung eines Datei-Repositories.
     * Wenn noch keine Datei auf dem Dateipfad existiert, wird eine neue unter diesem Pfad erstellt.
     *
     * @param filePath  Dateipfad für Datei auf der dieses Repository basieren soll
     */
    public FileRepository(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);
        try{
            if(!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(T object) {
        if(!containsKey(object.getId())){
            List<T> objects = readFromFile();
            objects.add(object);
            writeToFile(objects);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get(Object id){
        return readFromFile().stream()
                .filter(obj->obj.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(T object) {
        List<T> objects=readFromFile();
        for(int i=0; i<objects.size(); i++){
            if(objects.get(i).getId().equals(object.getId())){
                objects.set(i, object);
                writeToFile(objects);
                return;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(Object id) {
        List<T> objects=readFromFile();
        objects.removeIf(obj->obj.getId().equals(id));
        writeToFile(objects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> getAll() {
        return readFromFile();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return readFromFile().stream().anyMatch(obj->obj.getId().equals(key));
    }

    /**
     * Liest Objekte aus Datei aus
     *
     * @return  Liste aller Objekte in der Datei
     */
    private List<T> readFromFile(){
        try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(filePath))){
            return (List<T>) ois.readObject();
        }catch (EOFException e){
            return new ArrayList<>();
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read file",e);
        }
    }

    /**
     * Schreibt gegebene Objekte in Datei
     *
     * @param objects   Liste der Objekte, die in Datei geschrieben werden sollen
     */
    private void writeToFile(List<T> objects){
        try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(filePath))){
            oos.writeObject(objects);
        }catch (IOException e){
            throw new RuntimeException("Failed to write file",e);
        }
    }
}

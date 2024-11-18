package main.java.repository;

import main.java.model.ID;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileRepository<T extends ID & Serializable>implements IRepository<T> {
    private final String filePath;
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

    @Override
    public void create(T object) {
        if(!containsKey(object.getId())){
            List<T> objects = readFromFile();
            objects.add(object);
            writeToFile(objects);
        }
    }

    @Override
    public T get(Object id){
        return readFromFile().stream()
                .filter(obj->obj.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

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

    @Override
    public void delete(Object id) {
        List<T> objects=readFromFile();
        objects.removeIf(obj->obj.getId().equals(id));
        writeToFile(objects);
    }

    @Override
    public List<T> getAll() {
        return readFromFile();
    }

    @Override
    public boolean containsKey(Object key) {
        return readFromFile().stream().anyMatch(obj->obj.getId().equals(key));
    }

    private List<T> readFromFile(){
        try(ObjectInputStream ois=new ObjectInputStream(new FileInputStream(filePath))){
            return (List<T>) ois.readObject();
        }catch (EOFException e){
            return new ArrayList<>();
        }catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to read file",e);
        }
    }

    private void writeToFile(List<T> objects){
        try (ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(filePath))){
            oos.writeObject(objects);
        }catch (IOException e){
            throw new RuntimeException("Failed to write file",e);
        }
    }
}

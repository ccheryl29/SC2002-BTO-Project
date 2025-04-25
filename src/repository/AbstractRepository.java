package repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract repository implementation that provides common functionality
 * for serializing and deserializing objects to/from files.
 * 
 * @param <T> The type of entity this repository works with
 * @param <ID> The type of the entity's identifier
 */
public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
    
    protected List<T> entities;
    protected String filePath;
    
    /**
     * Constructor for AbstractRepository
     * 
     * @param filePath Path to the file where entities are stored
     */
    public AbstractRepository(String filePath) {
        this.entities = new ArrayList<>();
        this.filePath = filePath;
    }
    
    @Override
    public T save(T entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
        } else {
            // If entity exists, update it
            int index = entities.indexOf(entity);
            entities.set(index, entity);
        }
        saveData();
        return entity;
    }
    
    @Override
    public void delete(T entity) {
        entities.remove(entity);
        saveData();
    }
    
    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities);
    }
    
    @Override
    public void loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            entities = new ArrayList<>();
            return;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                entities = (List<T>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + filePath + ": " + e.getMessage());
            entities = new ArrayList<>();
        }
    }
    
    @Override
    public void saveData() {
        try {
            File file = new File(filePath);
            
            // Create parent directories if they don't exist
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(entities);
            }
        } catch (IOException e) {
            System.err.println("Error saving data to " + filePath + ": " + e.getMessage());
        }
    }
    
    /**
     * Abstract method to extract the ID from an entity
     * 
     * @param entity The entity to extract the ID from
     * @return The ID of the entity
     */
    protected abstract ID getEntityId(T entity);
} 
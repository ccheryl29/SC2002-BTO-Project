package repository;

import java.util.List;

/**
 * Generic repository interface for data access operations
 * 
 * @param <T> The type of entity this repository works with
 * @param <ID> The type of the entity's identifier
 */
public interface Repository<T, ID> {
    
    /**
     * Saves an entity to the repository
     * 
     * @param entity The entity to save
     * @return The saved entity
     */
    T save(T entity);
    
    /**
     * Deletes an entity from the repository
     * 
     * @param entity The entity to delete
     */
    void delete(T entity);
    
    /**
     * Finds an entity by its ID
     * 
     * @param id The ID to search for
     * @return The found entity, or null if not found
     */
    T findById(ID id);
    
    /**
     * Finds all entities in the repository
     * 
     * @return A list of all entities
     */
    List<T> findAll();
    
    /**
     * Loads data from the data source
     */
    void loadData();
    
    /**
     * Saves data to the data source
     */
    void saveData();
} 
package repository;

import model.HDBManager;
import model.Project;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing HDBManager entities
 */
public class ManagerRepository extends AbstractRepository<HDBManager, String> {
    
    /**
     * Constructor for ManagerRepository
     * 
     * @param filePath Path to the file where managers are stored
     */
    public ManagerRepository(String filePath) {
        super(filePath);
    }
    
    @Override
    public HDBManager findById(String nric) {
        return entities.stream()
                      .filter(manager -> manager.getNRIC().equals(nric))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    protected String getEntityId(HDBManager manager) {
        return manager.getNRIC();
    }
    
    /**
     * Finds a manager by name
     * 
     * @param name The name to search for
     * @return The manager with the matching name, or null if not found
     */
    public HDBManager findByName(String name) {
        return entities.stream()
                      .filter(manager -> manager.getName().equalsIgnoreCase(name))
                      .findFirst()
                      .orElse(null);
    }
    
    /**
     * Finds managers who created a specific project
     * 
     * @param project The project to search for
     * @return A list of managers who created the project
     */
    public List<HDBManager> findByCreatedProject(Project project) {
        return entities.stream()
                      .filter(manager -> 
                          manager.getCreatedProjects().stream()
                                .anyMatch(p -> p.getName().equals(project.getName()))
                      )
                      .collect(Collectors.toList());
    }
    
    /**
     * Authenticates a manager using NRIC and password
     * 
     * @param nric The NRIC to authenticate
     * @param password The password to authenticate
     * @return The authenticated manager, or null if authentication fails
     */
    public HDBManager authenticate(String nric, String password) {
        HDBManager manager = findById(nric);
        if (manager != null && manager.validatePassword(password)) {
            return manager;
        }
        return null;
    }
} 
package repository;

import model.HDBOfficer;
import model.Project;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing HDBOfficer entities
 */
public class OfficerRepository extends AbstractRepository<HDBOfficer, String> {
    
    /**
     * Constructor for OfficerRepository
     * 
     * @param filePath Path to the file where officers are stored
     */
    public OfficerRepository(String filePath) {
        super(filePath);
    }
    
    @Override
    public HDBOfficer findById(String nric) {
        return entities.stream()
                      .filter(officer -> officer.getNRIC().equals(nric))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    protected String getEntityId(HDBOfficer officer) {
        return officer.getNRIC();
    }
    
    /**
     * Finds officers by their registration status
     * 
     * @param status The registration status to search for
     * @return A list of matching officers
     */
    public List<HDBOfficer> findByRegistrationStatus(HDBOfficer.RegistrationStatus status) {
        return entities.stream()
                      .filter(officer -> officer.getRegistrationStatus() == status)
                      .collect(Collectors.toList());
    }
    
    /**
     * Finds officers handling a specific project
     * 
     * @param project The project to search for
     * @return A list of officers handling the project
     */
    public List<HDBOfficer> findByHandlingProject(Project project) {
        return entities.stream()
                      .filter(officer -> {
                          Project handlingProject = officer.getHandlingProject();
                          return handlingProject != null && handlingProject.getName().equals(project.getName());
                      })
                      .collect(Collectors.toList());
    }
    
    /**
     * Authenticates an officer using NRIC and password
     * 
     * @param nric The NRIC to authenticate
     * @param password The password to authenticate
     * @return The authenticated officer, or null if authentication fails
     */
    public HDBOfficer authenticate(String nric, String password) {
        HDBOfficer officer = findById(nric);
        if (officer != null && officer.validatePassword(password)) {
            return officer;
        }
        return null;
    }
    
    /**
     * Finds officers that are not currently handling any project
     * 
     * @return A list of available officers
     */
    public List<HDBOfficer> findAvailableOfficers() {
        return entities.stream()
                      .filter(officer -> officer.getHandlingProject() == null)
                      .collect(Collectors.toList());
    }
} 
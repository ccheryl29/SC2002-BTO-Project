package repository;

import model.Applicant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing Applicant entities
 */
public class ApplicantRepository extends AbstractRepository<Applicant, String> {
    
    /**
     * Constructor for ApplicantRepository
     * 
     * @param filePath Path to the file where applicants are stored
     */
    public ApplicantRepository(String filePath) {
        super(filePath);
    }
    
    @Override
    public Applicant findById(String nric) {
        return entities.stream()
                      .filter(applicant -> applicant.getNRIC().equals(nric))
                      .findFirst()
                      .orElse(null);
    }
    
    @Override
    protected String getEntityId(Applicant applicant) {
        return applicant.getNRIC();
    }
    
    /**
     * Finds an applicant by their name
     * 
     * @param name The name to search for
     * @return The matching applicant, or null if not found
     */
    public Applicant findByName(String name) {
        return entities.stream()
                      .filter(applicant -> applicant.getName().equalsIgnoreCase(name))
                      .findFirst()
                      .orElse(null);
    }
    
    /**
     * Finds applicants by their marital status
     * 
     * @param maritalStatus The marital status to search for
     * @return A list of matching applicants
     */
    public List<Applicant> findByMaritalStatus(Applicant.MaritalStatus maritalStatus) {
        return entities.stream()
                      .filter(applicant -> applicant.getMaritalStatus() == maritalStatus)
                      .collect(Collectors.toList());
    }
    
    /**
     * Authenticates an applicant using NRIC and password
     * 
     * @param nric The NRIC to authenticate
     * @param password The password to authenticate
     * @return The authenticated applicant, or null if authentication fails
     */
    public Applicant authenticate(String nric, String password) {
        Applicant applicant = findById(nric);
        if (applicant != null && applicant.validatePassword(password)) {
            return applicant;
        }
        return null;
    }
} 
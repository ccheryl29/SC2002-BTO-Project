package repository;

import model.Applicant;
import model.Enquiry;
import model.Project;
import model.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository for managing Enquiry entities
 */
public class EnquiryRepository extends AbstractRepository<Enquiry, String> {
    
    /**
     * Constructor for EnquiryRepository
     * 
     * @param filePath Path to the file where enquiries are stored
     */
    public EnquiryRepository(String filePath) {
        super(filePath);
    }
    
    /**
     * Default constructor that sets a default file path
     */
    public EnquiryRepository() {
        super("data/enquiries.dat");
    }
    
    /**
     * Generates a unique ID for a new enquiry
     * 
     * @return A unique enquiry ID
     */
    public String generateEnquiryId() {
        return "ENQ-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @Override
    public Enquiry findById(String id) {
        return entities.stream()
            .filter(enquiry -> enquiry.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    @Override
    protected String getEntityId(Enquiry enquiry) {
        return enquiry.getId();
    }
    
    /**
     * Finds enquiries submitted by a specific applicant
     * 
     * @param applicant The applicant
     * @return List of enquiries by the applicant
     */
    public List<Enquiry> findByApplicant(Applicant applicant) {
        return entities.stream()
            .filter(enquiry -> enquiry.getApplicant().getNRIC().equals(applicant.getNRIC()))
            .collect(Collectors.toList());
    }
    
    /**
     * Finds enquiries related to a specific project
     * 
     * @param project The project
     * @return List of enquiries for the project
     */
    public List<Enquiry> findByProject(Project project) {
        return entities.stream()
            .filter(enquiry -> enquiry.getProject().getName().equals(project.getName()))
            .collect(Collectors.toList());
    }
    
    /**
     * Finds pending enquiries (without responses)
     * 
     * @return List of pending enquiries
     */
    public List<Enquiry> findPendingEnquiries() {
        return entities.stream()
            .filter(enquiry -> enquiry.getStatus() == Enquiry.EnquiryStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    /**
     * Finds pending enquiries for a specific project
     * 
     * @param project The project
     * @return List of pending enquiries for the project
     */
    public List<Enquiry> findPendingEnquiriesByProject(Project project) {
        return entities.stream()
            .filter(enquiry -> enquiry.getProject().getName().equals(project.getName()))
            .filter(enquiry -> enquiry.getStatus() == Enquiry.EnquiryStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    /**
     * Finds responded enquiries (with responses)
     * 
     * @return List of responded enquiries
     */
    public List<Enquiry> findRespondedEnquiries() {
        return entities.stream()
            .filter(enquiry -> enquiry.getStatus() == Enquiry.EnquiryStatus.RESPONDED)
            .collect(Collectors.toList());
    }
    
    /**
     * Finds enquiries responded to by a specific user
     * 
     * @param user The user who responded
     * @return List of enquiries responded to by the user
     */

} 
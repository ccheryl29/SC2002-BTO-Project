package service;

import model.Enquiry;
import model.HDBManager;
import model.Project;
import repository.EnquiryRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling HDB manager enquiry operations
 */
public class HDBManagerEnquiryService {
    
    private final EnquiryRepository enquiryRepository;
    
    /**
     * Constructor for HDBManagerEnquiryService
     * 
     * @param enquiryRepository The enquiry repository
     */
    public HDBManagerEnquiryService(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }
    
    /**
     * Gets all enquiries for a specific project
     * 
     * @param projectName The name of the project
     * @return List of enquiries for the project
     */
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        return enquiryRepository.findAll().stream()
            .filter(enquiry -> enquiry.getProject().getName().equals(projectName))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets pending enquiries (with no replies) for a project
     * 
     * @param projectName The name of the project
     * @return List of pending enquiries
     */
    public List<Enquiry> getPendingEnquiries(String projectName) {
        return getEnquiriesByProject(projectName).stream()
            .filter(enquiry -> !enquiry.isResponded())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets an enquiry by its ID
     * 
     * @param enquiryId The ID of the enquiry
     * @return The enquiry, or null if not found
     */
    public Enquiry getEnquiryById(String enquiryId) {
        return enquiryRepository.findById(enquiryId);
    }
    
    /**
     * Adds a reply to an enquiry
     * 
     * @param enquiryId The ID of the enquiry
     * @param manager The HDB manager adding the reply
     * @param replyContent The content of the reply
     * @return true if reply added successfully, false otherwise
     */
    public boolean addReply(String enquiryId, HDBManager manager, String replyContent) {
        if (replyContent == null || replyContent.trim().isEmpty()) {
            return false;
        }
        
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        if (enquiry == null) {
            return false;
        }
        
        // Check if this manager is responsible for the project
        Project project = enquiry.getProject();
        if (!manager.getCreatedProjects().contains(project)) {
            return false;
        }
        
        // Add the reply
        enquiry.addReply(replyContent, manager);
        
        // Save the updated enquiry
        enquiryRepository.save(enquiry);
        
        return true;
    }
    
    /**
     * Gets the total number of enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The total number of enquiries
     */
    public int getEnquiryCount(String projectName) {
        return getEnquiriesByProject(projectName).size();
    }
    
    /**
     * Gets the number of pending enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The number of pending enquiries
     */
    public int getPendingEnquiryCount(String projectName) {
        return getPendingEnquiries(projectName).size();
    }
} 
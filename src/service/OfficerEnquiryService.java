package service;

import model.Enquiry;
import model.HDBOfficer;
import model.Project;
import repository.EnquiryRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for handling HDB officer enquiry operations
 */
public class OfficerEnquiryService {
    
    private final EnquiryRepository enquiryRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * Constructor for OfficerEnquiryService
     * 
     * @param enquiryRepository The enquiry repository
     * @param projectRepository The project repository
     */
    public OfficerEnquiryService(EnquiryRepository enquiryRepository, ProjectRepository projectRepository) {
        this.enquiryRepository = enquiryRepository;
        this.projectRepository = projectRepository;
    }
    
    /**
     * Gets all enquiries for the project the officer is handling
     * 
     * @param officer The HDB officer
     * @return List of enquiries for the officer's handling project
     */
    public List<Enquiry> getEnquiriesByOfficerProject(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            return new ArrayList<>();
        }
        
        Project handlingProject = officer.getHandlingProject();
        return enquiryRepository.findAll().stream()
            .filter(enquiry -> enquiry.getProject().getName().equals(handlingProject.getName()))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets pending enquiries (with no replies) for the project the officer is handling
     * 
     * @param officer The HDB officer
     * @return List of pending enquiries for the officer's handling project
     */
    public List<Enquiry> getPendingEnquiriesByOfficerProject(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            return new ArrayList<>();
        }
        
        return getEnquiriesByOfficerProject(officer).stream()
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
        if (enquiryId == null || enquiryId.trim().isEmpty()) {
            return null;
        }
        
        return enquiryRepository.findById(enquiryId);
    }
    
    /**
     * Adds a reply to an enquiry
     * 
     * @param enquiryId The ID of the enquiry
     * @param officer The HDB officer adding the reply
     * @param replyContent The content of the reply
     * @return true if reply added successfully, false otherwise
     */
    public boolean addReply(String enquiryId, HDBOfficer officer, String replyContent) {
        if (enquiryId == null || officer == null || replyContent == null || replyContent.trim().isEmpty()) {
            return false;
        }
        
        // Get the enquiry
        Enquiry enquiry = enquiryRepository.findById(enquiryId);
        if (enquiry == null) {
            return false;
        }
        
        // Check if the officer is handling the project related to this enquiry
        Project handlingProject = officer.getHandlingProject();
        if (handlingProject == null || !handlingProject.getName().equals(enquiry.getProject().getName())) {
            return false;
        }
        
        // Add the reply
        enquiry.addReply(replyContent, officer);
        
        // Save the updated enquiry
        enquiryRepository.save(enquiry);
        
        return true;
    }
    
    /**
     * Gets the total number of enquiries for the officer's handling project
     * 
     * @param officer The HDB officer
     * @return The total number of enquiries
     */
    public int getEnquiryCount(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            return 0;
        }
        
        return getEnquiriesByOfficerProject(officer).size();
    }
    
    /**
     * Gets the number of pending enquiries for the officer's handling project
     * 
     * @param officer The HDB officer
     * @return The number of pending enquiries
     */
    public int getPendingEnquiryCount(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            return 0;
        }
        
        return getPendingEnquiriesByOfficerProject(officer).size();
    }
} 
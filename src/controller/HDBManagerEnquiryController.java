package controller;

import controller.interfaces.IEnquiryController;
import model.Enquiry;
import model.HDBManager;
import model.User;
import service.HDBManagerEnquiryService;

import java.util.List;

/**
 * Controller for handling enquiry-related operations for HDB managers
 */
public class HDBManagerEnquiryController implements IEnquiryController {
    private final HDBManagerEnquiryService enquiryService;
    
    /**
     * Constructor for HDBManagerEnquiryController
     * 
     * @param enquiryService The service for handling enquiry operations
     */
    public HDBManagerEnquiryController(HDBManagerEnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }
    
    /**
     * Gets all enquiries for a specific project
     * 
     * @param projectName The name of the project
     * @return A list of enquiries for the specified project
     */
    @Override
    public List<Enquiry> getEnquiriesForProject(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return List.of();
        }
        return enquiryService.getEnquiriesByProject(projectName);
    }
    
    /**
     * Gets all pending enquiries for a specific project
     * 
     * @param projectName The name of the project
     * @return A list of pending enquiries for the specified project
     */
    @Override
    public List<Enquiry> getPendingEnquiriesForProject(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return List.of();
        }
        return enquiryService.getPendingEnquiries(projectName);
    }
    
    /**
     * Gets all enquiries for a specific project - keeps original method name for backward compatibility
     * 
     * @param projectName The name of the project
     * @return A list of enquiries for the specified project
     */
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        return getEnquiriesForProject(projectName);
    }
    
    /**
     * Gets all pending enquiries for a specific project - keeps original method name for backward compatibility
     * 
     * @param projectName The name of the project
     * @return A list of pending enquiries for the specified project
     */
    public List<Enquiry> getPendingEnquiries(String projectName) {
        return getPendingEnquiriesForProject(projectName);
    }
    
    /**
     * Gets an enquiry by its ID
     * 
     * @param enquiryId The ID of the enquiry
     * @return The enquiry with the specified ID, or null if not found
     */
    @Override
    public Enquiry getEnquiryById(String enquiryId) {
        if (enquiryId == null || enquiryId.isEmpty()) {
            return null;
        }
        return enquiryService.getEnquiryById(enquiryId);
    }
    
    /**
     * Adds a reply to an enquiry
     * 
     * @param enquiryId The ID of the enquiry
     * @param respondent The user adding the reply
     * @param replyContent The content of the reply
     * @return true if the reply was added successfully, false otherwise
     */
    @Override
    public boolean addReply(String enquiryId, User respondent, String replyContent) {
        if (enquiryId == null || enquiryId.isEmpty() || 
            respondent == null || 
            replyContent == null || replyContent.isEmpty()) {
            return false;
        }
        
        try {
            if (respondent instanceof HDBManager) {
                return enquiryService.addReply(enquiryId, (HDBManager) respondent, replyContent);
            } else {
                return false; // Only HDBManager can add replies in this controller
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Adds a reply to an enquiry - keeps original method signature for backward compatibility
     * 
     * @param enquiryId The ID of the enquiry
     * @param manager The manager adding the reply
     * @param replyContent The content of the reply
     * @return true if the reply was added successfully, false otherwise
     */
    public boolean addReply(String enquiryId, HDBManager manager, String replyContent) {
        return addReply(enquiryId, (User) manager, replyContent);
    }
    
    /**
     * Gets the total number of enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The total number of enquiries for the specified project
     */
    @Override
    public int getEnquiryCount(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return 0;
        }
        return enquiryService.getEnquiryCount(projectName);
    }
    
    /**
     * Gets the number of pending enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The number of pending enquiries for the specified project
     */
    @Override
    public int getPendingEnquiryCount(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return 0;
        }
        return enquiryService.getPendingEnquiryCount(projectName);
    }
} 
package controller.interfaces;

import model.Enquiry;
import model.Applicant;
import model.User;

import java.util.List;

/**
 * Generic interface for EnquiryController defining common methods for enquiry operations
 * This interface follows the Dependency Inversion Principle by allowing views to depend on
 * this abstraction rather than concrete implementations.
 */
public interface IEnquiryController {
    
    /**
     * Gets an enquiry by ID
     * 
     * @param enquiryId The ID of the enquiry
     * @return The enquiry, or null if not found
     */
    Enquiry getEnquiryById(String enquiryId);
    
    /**
     * Gets all enquiries for a project
     * 
     * @param projectName The name of the project
     * @return List of enquiries for the project
     */
    List<Enquiry> getEnquiriesForProject(String projectName);
    
    /**
     * Gets pending enquiries for a project
     * 
     * @param projectName The name of the project
     * @return List of pending enquiries for the project
     */
    List<Enquiry> getPendingEnquiriesForProject(String projectName);
    
    /**
     * Adds a reply to an enquiry
     * 
     * @param enquiryId The ID of the enquiry
     * @param respondent The user responding to the enquiry
     * @param content The content of the reply
     * @return True if successful, false otherwise
     */
    boolean addReply(String enquiryId, User respondent, String content);
    
    /**
     * Gets the count of enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The number of enquiries
     */
    int getEnquiryCount(String projectName);
    
    /**
     * Gets the count of pending enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The number of pending enquiries
     */
    int getPendingEnquiryCount(String projectName);
} 
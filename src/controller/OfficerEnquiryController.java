package controller;

import controller.interfaces.IEnquiryController;
import model.Enquiry;
import model.HDBOfficer;
import model.User;
import service.OfficerEnquiryService;

import java.util.List;

/**
 * Controller for handling enquiry operations for HDB Officers
 */
public class OfficerEnquiryController implements IEnquiryController {
    
    private final OfficerEnquiryService enquiryService;
    
    /**
     * Constructor for OfficerEnquiryController
     * 
     * @param enquiryService The enquiry service to use
     */
    public OfficerEnquiryController(OfficerEnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }
    
    /**
     * Gets all enquiries for a project
     * 
     * @param projectName The name of the project
     * @return List of enquiries for the project
     */
    @Override
    public List<Enquiry> getEnquiriesForProject(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return List.of();
        }
        
        HDBOfficer dummyOfficer = new HDBOfficer("", "", "", 0, null);
        dummyOfficer.setHandlingProject(new model.Project(projectName, "", null, null, 0));
        return enquiryService.getEnquiriesByOfficerProject(dummyOfficer);
    }
    
    /**
     * Gets all enquiries for the project being handled by the officer
     * 
     * @param officer The HDB officer handling the project
     * @return List of enquiries for the officer's project
     */
    public List<Enquiry> getEnquiriesForHandlingProject(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            throw new IllegalStateException("Officer is not handling any project");
        }
        
        return enquiryService.getEnquiriesByOfficerProject(officer);
    }
    
    /**
     * Gets all pending enquiries for a project
     * 
     * @param projectName The name of the project
     * @return List of pending enquiries for the project
     */
    @Override
    public List<Enquiry> getPendingEnquiriesForProject(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return List.of();
        }
        
        HDBOfficer dummyOfficer = new HDBOfficer("", "", "", 0, null);
        dummyOfficer.setHandlingProject(new model.Project(projectName, "", null, null, 0));
        return enquiryService.getPendingEnquiriesByOfficerProject(dummyOfficer);
    }
    
    /**
     * Gets all pending enquiries for the project being handled by the officer
     * 
     * @param officer The HDB officer handling the project
     * @return List of pending enquiries for the officer's project
     */
    public List<Enquiry> getPendingEnquiriesForHandlingProject(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            throw new IllegalStateException("Officer is not handling any project");
        }
        
        return enquiryService.getPendingEnquiriesByOfficerProject(officer);
    }
    
    /**
     * Gets an enquiry by ID
     * 
     * @param enquiryId The ID of the enquiry
     * @return The enquiry, or null if not found
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
     * @param respondent The user responding to the enquiry
     * @param content The content of the reply
     * @return True if successful, false otherwise
     */
    @Override
    public boolean addReply(String enquiryId, User respondent, String content) {
        if (enquiryId == null || enquiryId.isEmpty() || 
            respondent == null || 
            content == null || content.isEmpty()) {
            return false;
        }
        
        if (respondent instanceof HDBOfficer) {
            try {
                return enquiryService.addReply(enquiryId, (HDBOfficer) respondent, content);
            } catch (Exception e) {
                return false;
            }
        } else {
            return false; // Only HDBOfficer can add replies in this controller
        }
    }
    
    /**
     * Gets the count of enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The number of enquiries
     */
    @Override
    public int getEnquiryCount(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return 0;
        }
        
        HDBOfficer dummyOfficer = new HDBOfficer("", "", "", 0, null);
        dummyOfficer.setHandlingProject(new model.Project(projectName, "", null, null, 0));
        return enquiryService.getEnquiryCount(dummyOfficer);
    }
    
    /**
     * Gets the count of enquiries for the project being handled by the officer
     * 
     * @param officer The HDB officer handling the project
     * @return The number of enquiries for the officer's project
     */
    public int getEnquiryCount(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            return 0;
        }
        
        return enquiryService.getEnquiryCount(officer);
    }
    
    /**
     * Gets the count of pending enquiries for a project
     * 
     * @param projectName The name of the project
     * @return The number of pending enquiries
     */
    @Override
    public int getPendingEnquiryCount(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return 0;
        }
        
        HDBOfficer dummyOfficer = new HDBOfficer("", "", "", 0, null);
        dummyOfficer.setHandlingProject(new model.Project(projectName, "", null, null, 0));
        return enquiryService.getPendingEnquiryCount(dummyOfficer);
    }
    
    /**
     * Gets the count of pending enquiries for the project being handled by the officer
     * 
     * @param officer The HDB officer handling the project
     * @return The number of pending enquiries for the officer's project
     */
    public int getPendingEnquiryCount(HDBOfficer officer) {
        if (officer == null || officer.getHandlingProject() == null) {
            return 0;
        }
        
        return enquiryService.getPendingEnquiryCount(officer);
    }
} 
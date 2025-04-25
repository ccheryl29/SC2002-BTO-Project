package controller;

import controller.interfaces.IEnquiryController;
import model.Applicant;
import model.Enquiry;
import model.Project;
import model.User;
import service.ApplicantEnquiryService;

import java.util.List;

/**
 * Controller for handling enquiry operations for Applicants
 */
public class ApplicantEnquiryController implements IEnquiryController {
    
    private final ApplicantEnquiryService enquiryService;
    private Applicant currentApplicant;
    
    /**
     * Constructor for ApplicantEnquiryController
     * 
     * @param enquiryService The enquiry service
     * @param currentApplicant The current logged-in applicant
     */
    public ApplicantEnquiryController(ApplicantEnquiryService enquiryService, Applicant currentApplicant) {
        this.enquiryService = enquiryService;
        this.currentApplicant = currentApplicant;
    }
    
    /**
     * Submits a new enquiry
     * 
     * @param projectName The name of the project
     * @param question The enquiry question
     * @return The created enquiry, or null if failed
     * @throws IllegalArgumentException if parameters are invalid
     */
    public Enquiry submitEnquiry(Applicant applicant, String projectName, String question) {
        validateInputString(projectName, "Project name");
        validateInputString(question, "Question");
        return enquiryService.submitEnquiry(applicant, projectName, question);
    }
    public void setCurrentApplicant(Applicant applicant) {
        this.currentApplicant = applicant;
    }
    
    /**
     * Gets all enquiries submitted by the current applicant
     * 
     * @return List of enquiries submitted by the applicant
     */
    public List<Enquiry> getMyEnquiries(Applicant applicant) {
        return enquiryService.getEnquiriesByApplicant(applicant);
    }
    
    /**
     * Gets a specific enquiry by its ID
     * 
     * @param enquiryId The ID of the enquiry
     * @return The enquiry, or null if not found or doesn't belong to the applicant
     * @throws IllegalArgumentException if enquiryId is invalid
     */
    @Override
    public Enquiry getEnquiryById(String enquiryId) {
        validateInputString(enquiryId, "Enquiry ID");

        return enquiryService.getEnquiryById(enquiryId, currentApplicant);
    }
    
    /**
     * Updates an enquiry's question
     * 
     * @param enquiryId The ID of the enquiry
     * @param newQuestion The new question
     * @return true if updated successfully, false otherwise
     * @throws IllegalArgumentException if parameters are invalid
     */
    public boolean updateEnquiry(String enquiryId, String newQuestion) {
        validateInputString(enquiryId, "Enquiry ID");
        validateInputString(newQuestion, "New question");
        
        return enquiryService.updateEnquiry(enquiryId, currentApplicant, newQuestion);
    }
    
    /**
     * Deletes an enquiry
     * 
     * @param enquiryId The ID of the enquiry
     * @return true if deleted successfully, false otherwise
     * @throws IllegalArgumentException if enquiryId is invalid
     */
    public boolean deleteEnquiry(String enquiryId) {
        validateInputString(enquiryId, "Enquiry ID");
        
        return enquiryService.deleteEnquiry(enquiryId, currentApplicant);
    }
    
    /**
     * Gets all projects that accept enquiries
     * 
     * @return List of available projects
     */
    public List<Project> getAvailableProjects() {
        return enquiryService.getAvailableProjects();
    }
    
    /**
     * Validates that a string input is not null or empty
     * 
     * @param input The input string to validate
     * @param fieldName The name of the field (for error message)
     * @throws IllegalArgumentException if input is null or empty
     */
    private void validateInputString(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    @Override
    public List<Enquiry> getEnquiriesForProject(String projectName) {
        validateInputString(projectName, "Project name");
        // Applicants can only see their own enquiries for a project
        return enquiryService.getEnquiriesByApplicantAndProject(currentApplicant, projectName);
    }
    
    @Override
    public List<Enquiry> getPendingEnquiriesForProject(String projectName) {
        validateInputString(projectName, "Project name");
        // Applicants can only see their own pending enquiries for a project
        return enquiryService.getPendingEnquiriesByApplicantAndProject(currentApplicant, projectName);
    }
    
    @Override
    public boolean addReply(String enquiryId, User respondent, String content) {
        // Applicants are not allowed to reply to enquiries
        return false;
    }
    
    @Override
    public int getEnquiryCount(String projectName) {
        validateInputString(projectName, "Project name");
        // For applicants, this returns their own enquiry count for a project
            return enquiryService.getEnquiryCountByApplicantAndProject(currentApplicant, projectName);
    }
    
    @Override
    public int getPendingEnquiryCount(String projectName) {
        validateInputString(projectName, "Project name");
        // For applicants, this returns their own pending enquiry count for a project
        return enquiryService.getPendingEnquiryCountByApplicantAndProject(currentApplicant, projectName);
    }
} 
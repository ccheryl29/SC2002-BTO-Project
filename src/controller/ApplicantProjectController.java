package controller;

import controller.interfaces.IApplicantProjectController;
import model.Applicant;
import model.Application;
import model.Flat;
import model.Project;
import model.User;
import service.ApplicantProjectService;

import java.util.Date;
import java.util.List;

/**
 * Controller for handling project-related operations for applicants
 */
public class ApplicantProjectController implements IApplicantProjectController {
    
    private final ApplicantProjectService projectService;
    
    /**
     * Constructor for ApplicantProjectController
     * 
     * @param projectService The project service to use
     */
    public ApplicantProjectController(ApplicantProjectService projectService) {
        this.projectService = projectService;
    }
    
    /**
     * Gets all visible projects for an applicant based on marital status
     * 
     * @param applicant The applicant
     * @return List of projects
     */
    public List<Project> getVisibleProjects(Applicant applicant) {
        if (applicant == null) {
            return List.of();
        }
        
        return projectService.getVisibleProjects(applicant);
    }
    public List<Project> getOpeningProjects(Applicant applicant) {
        if (applicant == null) {
            return List.of();
        }
        
        return projectService.getOpeningProjects(applicant);
    }
    
    /**
     * Get a project by name
     * 
     * @param projectName The name of the project
     * @return The project, or null if not found
     */
    public Project getProjectByName(String projectName) {
        if (projectName == null || projectName.isEmpty()) {
            return null;
        }
        
        return projectService.getProjectByName(projectName);
    }
    
    /**
     * Apply for a project
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @param flatTypeStr The flat type
     * @return Result message
     */
    public String applyForProject(Applicant applicant, String projectName, String flatTypeStr) {
        try {
            // Validate inputs
            if (applicant == null) {
                return "Error: No applicant information provided";
            }
            
            if (projectName == null || projectName.isEmpty()) {
                return "Error: Invalid project name provided";
            }
            
            if (flatTypeStr == null || flatTypeStr.isEmpty()) {
                return "Error: Invalid flat type provided";
            }
            
            // Parse the flat type
            Flat.FlatType flatType;
            try {
                flatType = Flat.FlatType.valueOf(flatTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                return "Error: Invalid flat type: " + flatTypeStr;
            }
            
            // Check if applicant already has an application
            if (projectService.hasExistingApplication(applicant)) {
                return "Error: You already have an existing application. You cannot apply for multiple projects.";
            }
            
            // Validate eligibility based on age and marital status
            if (!isEligibleForFlatType(applicant, flatType)) {
                if (applicant.getMaritalStatus() == User.MaritalStatus.SINGLE) {
                    return "Error: Singles, 35 years old and above, can ONLY apply for 2-Room flats";
                } else {
                    return "Error: Married applicants must be 21 years old or above to apply";
                }
            }
            
            // Check if project exists and is visible to the applicant
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                return "Error: Project not found";
            }
            
            if (!projectService.isProjectVisibleToApplicant(project, applicant)) {
                return "Error: This project is not available for your application type";
            }
            
            // Check if flat type is available in the project
            if (!projectService.isFlatTypeAvailable(project, flatType)) {
                return "Error: This flat type is not available in the selected project";
            }
            
            // Check if units of this flat type are available
            if (!projectService.hasAvailableUnits(project, flatType)) {
                return "Error: No available units for this flat type";
            }
            
            // Create application
            boolean result = projectService.createApplication(applicant, projectName, flatType);
            
            if (result) {
                return "Your application for " + projectName + " has been submitted successfully!";
            } else {
                return "Failed to submit application. Please try again later.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Withdraw an application
     * 
     * @param applicant The applicant
     * @return Result message
     */
    public String withdrawApplication(Applicant applicant) {
        try {
            // Validate input
            if (applicant == null) {
                return "Error: No applicant information provided";
            }
            
            // Check if applicant has an application
            if (!projectService.hasExistingApplication(applicant)) {
                return "Error: You don't have any active applications to withdraw";
            }
            
            // Get the current application
            Application application = projectService.getApplicantApplication(applicant);
            
            if (application.getStatus() == Application.ApplicationStatus.BOOKED) {
                return "Error: Cannot withdraw an application that has already been booked";
            }
            
            boolean result = projectService.requestWithdrawal(applicant);
            
            if (result) {
                return "Your withdrawal request has been submitted and is pending approval by HDB Manager";
            } else {
                return "Failed to submit withdrawal request. Please try again later.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Check application status
     * 
     * @param applicant The applicant
     * @return Application status message
     */
    public String checkApplicationStatus(Applicant applicant) {
        try {
            // Validate input
            if (applicant == null) {
                return "Error: No applicant information provided";
            }
            
            // Check if applicant has an application
            if (!projectService.hasExistingApplication(applicant)) {
                return "You don't have any active applications";
            }
            
            // Get the current application
            Application application = projectService.getApplicantApplication(applicant);
            
            StringBuilder result = new StringBuilder();
            result.append("Application Status: ").append(application.getStatus().toString()).append("\n");
            result.append("Project: ").append(application.getProject().getName()).append("\n");
            result.append("Flat Type: ").append(application.getFlatType().getDisplayName()).append("\n");
            
            if (application.isWithdrawalRequested()) {
                result.append("Withdrawal Request Status: ").append(application.getWithdrawalRequestStatus().toString()).append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Check if the applicant is eligible for the given flat type
     *
     * @param applicant The applicant
     * @param flatType The flat type
     * @return true if eligible, false otherwise
     */
    private boolean isEligibleForFlatType(Applicant applicant, Flat.FlatType flatType) {
        // Singles, 35 years old and above, can ONLY apply for 2-Room
        if (applicant.getMaritalStatus() == User.MaritalStatus.SINGLE) {
            if (applicant.getAge() < 35) {
                return false; // Singles must be 35 or above
            }
            return flatType == Flat.FlatType.TWO_ROOM; // Singles can only apply for 2-Room
        } 
        // Married, 21 years old and above, can apply for any flat types (2-Room or 3-Room)
        else if (applicant.getMaritalStatus() == User.MaritalStatus.MARRIED) {
            if (applicant.getAge() < 21) {
                return false; // Married must be 21 or above
            }
            return true; // Married can apply for any flat type
        }
        
        return false; // Other marital statuses are not eligible (if any)
    }
    
    /**
     * Initiates a booking request for an applicant
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return A message indicating the result of the operation
     */
    public String initiateBooking(String applicantNRIC, String projectName) {
        // Input validation
        if (applicantNRIC == null || applicantNRIC.trim().isEmpty()) {
            return "Applicant NRIC is required.";
        }
        
        if (projectName == null || projectName.trim().isEmpty()) {
            return "Project name is required.";
        }
        
        // Call the service to initiate the booking
        return projectService.initiateBooking(applicantNRIC, projectName);
    }
}
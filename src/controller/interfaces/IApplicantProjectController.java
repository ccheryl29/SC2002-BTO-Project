package controller.interfaces;

import model.Applicant;
import model.Project;

import java.util.List;

/**
 * Interface for ApplicantProjectController defining methods for applicant project operations
 * This interface follows the Dependency Inversion Principle by allowing views to depend on
 * this abstraction rather than concrete implementations.
 */
public interface IApplicantProjectController {
    
    /**
     * Gets all visible projects for an applicant based on marital status
     * 
     * @param applicant The applicant
     * @return List of projects
     */
    List<Project> getVisibleProjects(Applicant applicant);
    
    /**
     * Gets all currently open projects for an applicant
     * 
     * @param applicant The applicant
     * @return List of projects
     */
    List<Project> getOpeningProjects(Applicant applicant);
    
    /**
     * Get a project by name
     * 
     * @param projectName The name of the project
     * @return The project, or null if not found
     */
    Project getProjectByName(String projectName);
    
    /**
     * Apply for a project
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @param flatType The flat type
     * @return Result message
     */
    String applyForProject(Applicant applicant, String projectName, String flatType);
    
    /**
     * Withdraw an application
     * 
     * @param applicant The applicant
     * @return Result message
     */
    String withdrawApplication(Applicant applicant);
    
    /**
     * Check application status
     * 
     * @param applicant The applicant
     * @return Application status message
     */
    String checkApplicationStatus(Applicant applicant);
    
    /**
     * Initiates a booking request for an applicant
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return A message indicating the result of the operation
     */
    String initiateBooking(String applicantNRIC, String projectName);
} 
package controller.interfaces;

import model.Application;
import model.HDBManager;

import java.util.List;

/**
 * Interface for HDBManagerApplicationController defining methods for HDB manager application operations
 * This interface follows the Dependency Inversion Principle by allowing views to depend on
 * this abstraction rather than concrete implementations.
 */
public interface IHDBManagerApplicationController {
    
    /**
     * Gets pending applications for a project
     * 
     * @param projectName The name of the project
     * @return List of pending applications
     */
    List<Application> getPendingApplications(String projectName);
    
    /**
     * Gets applications with a specific status for a project
     * 
     * @param projectName The name of the project
     * @param status The application status
     * @return List of applications with the specified status
     */
    List<Application> getApplicationsByStatus(String projectName, String status);
    
    /**
     * Gets withdrawal requests for a project
     * 
     * @param projectName The name of the project
     * @return List of applications with withdrawal requests
     */
    List<Application> getWithdrawalRequests(String projectName);
    
    /**
     * Approves an application
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager approving the application
     * @return Result message
     */
    String approveApplication(String applicantNRIC, String projectName, HDBManager manager);
    
    /**
     * Rejects an application
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager rejecting the application
     * @return Result message
     */
    String rejectApplication(String applicantNRIC, String projectName, HDBManager manager);
    
    /**
     * Approves a withdrawal request
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager approving the withdrawal
     * @return Result message
     */
    String approveWithdrawal(String applicantNRIC, String projectName, HDBManager manager);
    
    /**
     * Rejects a withdrawal request
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager rejecting the withdrawal
     * @return Result message
     */
    String rejectWithdrawal(String applicantNRIC, String projectName, HDBManager manager);
} 
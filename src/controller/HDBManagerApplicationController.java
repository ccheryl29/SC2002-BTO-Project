package controller;

import controller.interfaces.IHDBManagerApplicationController;
import model.Application;
import model.HDBManager;
import service.HDBManagerApplicationService;

import java.util.List;

/**
 * Controller for handling HDB Manager's application approval operations
 */
public class HDBManagerApplicationController implements IHDBManagerApplicationController {
    
    private final HDBManagerApplicationService applicationService;
    
    /**
     * Constructor for HDBManagerApplicationController
     * 
     * @param applicationService The application service to use
     */
    public HDBManagerApplicationController(HDBManagerApplicationService applicationService) {
        this.applicationService = applicationService;
    }
    
    /**
     * Get all pending applications for a project
     * 
     * @param projectName The name of the project
     * @return A list of pending applications
     */
    public List<Application> getPendingApplications(String projectName) {
        return applicationService.getPendingApplications(projectName);
    }
    
    /**
     * Get all applications with pending withdrawal requests for a project
     * 
     * @param projectName The name of the project
     * @return A list of applications with withdrawal requests
     */
    public List<Application> getWithdrawalRequests(String projectName) {
        return applicationService.getWithdrawalRequests(projectName);
    }
    
    /**
     * Get applications by status for a project
     * 
     * @param projectName The name of the project
     * @param statusStr The status string to filter by (PENDING, SUCCESSFUL, UNSUCCESSFUL, BOOKED)
     * @return A list of applications with the given status
     */
    public List<Application> getApplicationsByStatus(String projectName, String statusStr) {
        try {
            Application.ApplicationStatus status = Application.ApplicationStatus.valueOf(statusStr.toUpperCase());
            return applicationService.getApplicationsByStatus(projectName, status);
        } catch (IllegalArgumentException e) {
            return List.of(); // Return empty list if status is invalid
        }
    }
    
    /**
     * Approve an application (changing status from PENDING to SUCCESSFUL)
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager approving the application
     * @return A message indicating success or failure
     */
    public String approveApplication(String applicantNRIC, String projectName, HDBManager manager) {
        try {
            // Validate inputs
            if (applicantNRIC == null || applicantNRIC.isEmpty()) {
                return "Error: Invalid NRIC provided";
            }
            
            if (projectName == null || projectName.isEmpty()) {
                return "Error: Invalid project name provided";
            }
            
            if (manager == null) {
                return "Error: No manager provided";
            }
            
            // Verify manager is authorized to approve (implementation may vary)
            if (!isManagerAuthorized(manager, projectName)) {
                return "Error: You are not authorized to manage this project";
            }
            
            boolean result = applicationService.approveApplication(applicantNRIC, projectName);
            
            if (result) {
                return "Application approved successfully. The applicant can proceed with flat booking through an HDB Officer.";
            } else {
                return "Failed to approve application. Check if the application is pending and flat type has available units.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Reject an application (changing status from PENDING to UNSUCCESSFUL)
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager rejecting the application
     * @return A message indicating success or failure
     */
    public String rejectApplication(String applicantNRIC, String projectName, HDBManager manager) {
        try {
            // Validate inputs
            if (applicantNRIC == null || applicantNRIC.isEmpty()) {
                return "Error: Invalid NRIC provided";
            }
            
            if (projectName == null || projectName.isEmpty()) {
                return "Error: Invalid project name provided";
            }
            
            if (manager == null) {
                return "Error: No manager provided";
            }
            
            // Verify manager is authorized to reject
            if (!isManagerAuthorized(manager, projectName)) {
                return "Error: You are not authorized to manage this project";
            }
            
            boolean result = applicationService.rejectApplication(applicantNRIC, projectName);
            
            if (result) {
                return "Application rejected successfully. The applicant cannot proceed with this project.";
            } else {
                return "Failed to reject application. Check if the application is pending.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Approve a withdrawal request (changing application status to UNSUCCESSFUL)
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager approving the withdrawal
     * @return A message indicating success or failure
     */
    public String approveWithdrawal(String applicantNRIC, String projectName, HDBManager manager) {
        try {
            // Validate inputs
            if (applicantNRIC == null || applicantNRIC.isEmpty()) {
                return "Error: Invalid NRIC provided";
            }
            
            if (projectName == null || projectName.isEmpty()) {
                return "Error: Invalid project name provided";
            }
            
            if (manager == null) {
                return "Error: No manager provided";
            }
            
            // Verify manager is authorized to approve withdrawal
            if (!isManagerAuthorized(manager, projectName)) {
                return "Error: You are not authorized to manage this project";
            }
            
            boolean result = applicationService.approveWithdrawal(applicantNRIC, projectName);
            
            if (result) {
                return "Withdrawal request approved successfully. Application has been marked as unsuccessful.";
            } else {
                return "Failed to approve withdrawal request. Check if a withdrawal was requested.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Reject a withdrawal request (keeping the original application status)
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param manager The manager rejecting the withdrawal
     * @return A message indicating success or failure
     */
    public String rejectWithdrawal(String applicantNRIC, String projectName, HDBManager manager) {
        try {
            // Validate inputs
            if (applicantNRIC == null || applicantNRIC.isEmpty()) {
                return "Error: Invalid NRIC provided";
            }
            
            if (projectName == null || projectName.isEmpty()) {
                return "Error: Invalid project name provided";
            }
            
            if (manager == null) {
                return "Error: No manager provided";
            }
            
            // Verify manager is authorized to reject withdrawal
            if (!isManagerAuthorized(manager, projectName)) {
                return "Error: You are not authorized to manage this project";
            }
            
            boolean result = applicationService.rejectWithdrawal(applicantNRIC, projectName);
            
            if (result) {
                return "Withdrawal request rejected successfully. Application will maintain its current status.";
            } else {
                return "Failed to reject withdrawal request. Check if a withdrawal was requested.";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Check if the manager is authorized to manage the project
     * 
     * @param manager The manager to check
     * @param projectName The name of the project
     * @return True if authorized, false otherwise
     */
    private boolean isManagerAuthorized(HDBManager manager, String projectName) {
        // Simplified implementation - check if the manager created the project
        return manager.getCreatedProjects().stream()
            .anyMatch(project -> project.getName().equals(projectName));
    }
} 
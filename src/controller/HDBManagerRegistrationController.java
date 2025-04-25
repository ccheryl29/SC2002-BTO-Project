package controller;

import model.HDBManager;
import model.HDBOfficer;
import service.HDBManagerRegistrationService;
import service.HDBManagerProjectService;

import java.util.List;

/**
 * Controller for handling HDB manager officer registration operations
 */
public class HDBManagerRegistrationController {
    
    private final HDBManagerRegistrationService registrationService;
    private final HDBManagerProjectService projectService;
    
    /**
     * Constructor for HDBManagerRegistrationController
     * 
     * @param registrationService Service for officer registration management
     * @param projectService Service for project management
     */
    public HDBManagerRegistrationController(
            HDBManagerRegistrationService registrationService, 
            HDBManagerProjectService projectService) {
        this.registrationService = registrationService;
        this.projectService = projectService;
    }
    
    /**
     * Gets pending officer registrations for a project
     * 
     * @param projectName The name of the project
     * @return List of pending officers
     */
    public List<HDBOfficer> getPendingOfficerRegistrations(String projectName) {
        try {
            validateProjectName(projectName);
            return registrationService.getPendingOfficerRegistrations(projectName);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    /**
     * Gets approved officer registrations for a project
     * 
     * @param projectName The name of the project
     * @return List of approved officers
     */
    public List<HDBOfficer> getApprovedOfficerRegistrations(String projectName) {
        try {
            validateProjectName(projectName);
            return registrationService.getApprovedOfficerRegistrations(projectName);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
    
    /**
     * Approves an officer registration
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @param manager The manager approving the registration
     * @return Result message
     */
    public String approveOfficerRegistration(String officerNRIC, String projectName, HDBManager manager) {
        try {
            validateInputs(officerNRIC, projectName);
            validateManager(manager, projectName);
            
            boolean success = registrationService.approveOfficerRegistration(officerNRIC, projectName);
            if (success) {
                return "Officer registration approved successfully.";
            } else {
                return "Error: Unable to approve officer registration. Check if the project has available slots.";
            }
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Rejects an officer registration
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @param manager The manager rejecting the registration
     * @return Result message
     */
    public String rejectOfficerRegistration(String officerNRIC, String projectName, HDBManager manager) {
        try {
            validateInputs(officerNRIC, projectName);
            validateManager(manager, projectName);
            
            boolean success = registrationService.rejectOfficerRegistration(officerNRIC, projectName);
            if (success) {
                return "Officer registration rejected successfully.";
            } else {
                return "Error: Unable to reject officer registration.";
            }
        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Validates that the manager is the creator of the project
     * 
     * @param manager The manager to validate
     * @param projectName The name of the project
     * @throws IllegalArgumentException if validation fails
     */
    private void validateManager(HDBManager manager, String projectName) {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null.");
        }
        
        // Check if the manager is the creator of the project
        boolean isCreator = manager.getCreatedProjects().stream()
                              .anyMatch(p -> p.getName().equals(projectName));
        
        if (!isCreator) {
            throw new IllegalArgumentException("You are not authorized to manage this project.");
        }
    }
    
    /**
     * Validates that the inputs are not null or empty
     * 
     * @param id The ID to validate
     * @param projectName The project name to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateInputs(String id, String projectName) {
        validateId(id);
        validateProjectName(projectName);
    }
    
    /**
     * Validates that the ID is not null or empty
     * 
     * @param id The ID to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID cannot be empty.");
        }
    }
    
    /**
     * Validates that the project name is not null or empty
     * 
     * @param projectName The project name to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProjectName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name cannot be empty.");
        }
    }
} 
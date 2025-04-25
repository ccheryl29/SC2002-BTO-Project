package controller;

import controller.interfaces.IOfficerRegistrationController;
import model.HDBOfficer;
import model.Project;
import service.OfficerRegistrationService;

import java.util.List;

/**
 * Controller for handling HDB Officer registration operations
 */
public class OfficerRegistrationController implements IOfficerRegistrationController {
    
    private final OfficerRegistrationService officerRegistrationService;
    
    /**
     * Constructor for OfficerRegistrationController
     * 
     * @param officerRegistrationService The officer registration service to use
     */
    public OfficerRegistrationController(OfficerRegistrationService officerRegistrationService) {
        this.officerRegistrationService = officerRegistrationService;
    }
    
    /**
     * Registers an officer for a project
     * 
     * @param officer The officer to register
     * @param projectName The name of the project
     * @return Result message
     */
    public String registerForProject(HDBOfficer officer, String projectName) {
        try {
            // Validate inputs
            if (officer == null) {
                return "Error: Officer not found.";
            }
            
            if (projectName == null || projectName.trim().isEmpty()) {
                return "Error: Project name cannot be empty.";
            }
            
            // Check if officer is already handling a project
            // if (officer.getHandlingProject() != null &&
            //     officer.getRegistrationStatus() == HDBOfficer.RegistrationStatus.APPROVED) {
            //     return "Error: You are already handling the project '" + 
            //         officer.getHandlingProject().getName() + "'.";
            // }
            
            // // Check if officer has a pending registration
            // if (officer.getRegistrationStatus() == HDBOfficer.RegistrationStatus.PENDING) {
            //     return "Error: You already have a pending registration.";
            // }
            
            // Register the officer
            boolean success = officerRegistrationService.registerForProject(officer, projectName);
            
            if (success) {
                return "Registration submitted successfully. Please wait for approval.";
            } else {
                return "Error: Unable to register for the project. Please try again later.";
            }
        } catch (Exception e) {
            return "Error registering for project: " + e.getMessage();
        }
    }
    
    /**
     * Checks if an officer has applied for a project
     * 
     * @param officer The officer to check
     * @param project The project to check
     * @return True if the officer has applied for the project, false otherwise
     */
    public boolean checkAppliedForProject(HDBOfficer officer, Project project) {
        return officerRegistrationService.hasAppliedForProject(officer, project);
    }
    
    /**
     * Gets all available projects for officer registration
     * 
     * @return List of available projects
     */
    public List<Project> getAvailableProjects() {
        return officerRegistrationService.getAvailableProjects();
    }
    
    /**
     * Gets the registration status of an officer
     * 
     * @param officer The officer
     * @return The registration status message
     */
    public String getRegistrationStatus(HDBOfficer officer) {
        if (officer == null) {
            return "Error: Officer not found.";
        }
        
        HDBOfficer.RegistrationStatus status = officer.getRegistrationStatus();
        Project handlingProject = officer.getHandlingProject();
        
        StringBuilder statusMsg = new StringBuilder();
        
        if (status == null) {
            statusMsg.append("You have not registered for any projects yet.");
        } else {
            statusMsg.append("Registration Status: ").append(status);
            
            if (handlingProject != null) {
                statusMsg.append("\nHandling Project: ").append(handlingProject.getName());
                statusMsg.append("\nNeighborhood: ").append(handlingProject.getNeighborhood());
                statusMsg.append("\nApplication Period: ").append(handlingProject.getApplicationOpenDate())
                         .append(" to ").append(handlingProject.getApplicationCloseDate());
            }
        }
        
        return statusMsg.toString();
    }
} 
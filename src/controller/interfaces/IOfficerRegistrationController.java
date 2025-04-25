package controller.interfaces;

import model.HDBOfficer;
import model.Project;

import java.util.List;

/**
 * Interface for OfficerRegistrationController defining methods for HDB officer registration operations
 * This interface follows the Dependency Inversion Principle by allowing views to depend on
 * this abstraction rather than concrete implementations.
 */
public interface IOfficerRegistrationController {
    
    /**
     * Registers an officer for a project
     * 
     * @param officer The officer to register
     * @param projectName The name of the project
     * @return Result message
     */
    String registerForProject(HDBOfficer officer, String projectName);
    
    /**
     * Checks if an officer has applied for a project
     * 
     * @param officer The officer to check
     * @param project The project to check
     * @return True if the officer has applied for the project, false otherwise
     */
    boolean checkAppliedForProject(HDBOfficer officer, Project project);
    
    /**
     * Gets all available projects for officer registration
     * 
     * @return List of available projects
     */
    List<Project> getAvailableProjects();
    
    /**
     * Gets the registration status of an officer
     * 
     * @param officer The officer
     * @return The registration status message
     */
    String getRegistrationStatus(HDBOfficer officer);
} 
package controller.interfaces;

import model.HDBManager;
import model.Project;

import java.util.List;
import java.util.Map;

/**
 * Interface for HDBManagerProjectController defining methods for HDB manager project operations
 * This interface follows the Dependency Inversion Principle by allowing views to depend on
 * this abstraction rather than concrete implementations.
 */
public interface IHDBManagerProjectController {
    
    /**
     * Creates a new project
     * 
     * @param name Project name
     * @param neighborhood Project neighborhood
     * @param applicationOpenDateStr Application opening date (dd/MM/yyyy)
     * @param applicationCloseDateStr Application closing date (dd/MM/yyyy)
     * @param availableOfficerSlotsStr Number of available officer slots
     * @param manager The manager creating the project
     * @return Result message indicating success or failure
     */
    String createProject(String name, String neighborhood, 
                      String applicationOpenDateStr, String applicationCloseDateStr,
                      String availableOfficerSlotsStr, HDBManager manager);
    
    /**
     * Adds a flat type to a project
     * 
     * @param projectName Name of the project
     * @param flatType Type of flat to add
     * @param totalUnitsStr Total number of units
     * @param sellingPriceStr Selling price
     * @return Result message indicating success or failure
     */
    String addFlatToProject(String projectName, String flatType, 
                         String totalUnitsStr, String sellingPriceStr);
    
    /**
     * Updates an existing project
     * 
     * @param projectName Name of the project to update
     * @param neighborhood Updated neighborhood
     * @param applicationOpenDateStr Updated application opening date (dd/MM/yyyy)
     * @param applicationCloseDateStr Updated application closing date (dd/MM/yyyy)
     * @param availableOfficerSlotsStr Updated number of available officer slots
     * @param manager The manager updating the project
     * @return Result message indicating success or failure
     */
    String updateProject(String projectName, String neighborhood, 
                      String applicationOpenDateStr, String applicationCloseDateStr,
                      String availableOfficerSlotsStr, HDBManager manager);
    
    /**
     * Toggles the visibility of a project
     * 
     * @param projectName Name of the project
     * @param visibilityStr Visibility state ("on" or "off")
     * @param manager The manager toggling the visibility
     * @return Result message indicating success or failure
     */
    String toggleProjectVisibility(String projectName, String visibilityStr, HDBManager manager);
    
    /**
     * Gets projects created by a specific manager
     * 
     * @param manager The manager
     * @return List of projects created by the manager
     */
    List<Project> getProjectsByManager(HDBManager manager);
    
    /**
     * Gets all projects in the system
     * 
     * @return List of all projects
     */
    List<Project> getAllProjects();
    
    /**
     * Get a project by name
     * 
     * @param name The name of the project
     * @return The project, or null if not found
     */
    Project getProjectByName(String name);
    
    /**
     * Generates a report of all applicants for a project
     * 
     * @param projectName The name of the project
     * @return List of applicant data for the report
     */
    List<Map<String, Object>> generateProjectApplicantsReport(String projectName);
    
    /**
     * Generates a filtered report of applicants for a project
     * 
     * @param projectName The name of the project
     * @param flatType The flat type filter
     * @param maritalStatus The marital status filter
     * @param minAgeStr The minimum age filter
     * @param maxAgeStr The maximum age filter
     * @return List of filtered applicant data for the report
     */
    List<Map<String, Object>> generateFilteredApplicantsReport(
        String projectName, String flatType, String maritalStatus, 
        String minAgeStr, String maxAgeStr);
} 
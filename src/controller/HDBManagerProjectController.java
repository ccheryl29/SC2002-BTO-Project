package controller;

import controller.interfaces.IHDBManagerProjectController;
import model.HDBManager;
import model.Project;
import model.Flat;
import service.HDBManagerProjectService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class for handling HDB Manager project management interactions
 */
public class HDBManagerProjectController implements IHDBManagerProjectController {
    
    private final HDBManagerProjectService projectService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructor for HDBManagerProjectController
     * 
     * @param projectService Service for project management
     */
    public HDBManagerProjectController(HDBManagerProjectService projectService) {
        this.projectService = projectService;
    }
    
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
    public String createProject(String name, String neighborhood, 
                             String applicationOpenDateStr, String applicationCloseDateStr,
                             String availableOfficerSlotsStr, HDBManager manager) {
        try {
            // Validate input strings
            validateInputString(name, "Project name");
            validateInputString(neighborhood, "Neighborhood");
            
            // Parse and validate dates
            Date applicationOpenDate = parseDateString(applicationOpenDateStr);
            Date applicationCloseDate = parseDateString(applicationCloseDateStr);
            validateDates(applicationOpenDate, applicationCloseDate);
            
            // Parse and validate available officer slots
            int availableOfficerSlots = parseIntString(availableOfficerSlotsStr);
            validateOfficerSlots(availableOfficerSlots);
            
            // Check if a project with the same name already exists
            if (projectService.getProjectByName(name) != null) {
                return "Error: A project with the name '" + name + "' already exists.";
            }
            
            // Check if manager is already handling a project during the same period
            if (isManagerHandlingProjectInPeriod(manager, applicationOpenDate, applicationCloseDate)) {
                return "Error: You are already handling a project during this period.";
            }
            
            // Create the project
            Project project = projectService.createProject(
                name, neighborhood, applicationOpenDate, applicationCloseDate, availableOfficerSlots, manager
            );
            
            return "Project '" + project.getName() + "' created successfully.";
        } catch (IllegalArgumentException e) {
            return "Error creating project: " + e.getMessage();
        } catch (Exception e) {
            return "Error creating project: An unexpected error occurred.";
        }
    }
    
    /**
     * Adds a flat type to a project
     * 
     * @param projectName Name of the project
     * @param flatType Type of flat to add
     * @param totalUnitsStr Total number of units
     * @param sellingPriceStr Selling price
     * @return Result message indicating success or failure
     */
    public String addFlatToProject(String projectName, String flatType, 
                                String totalUnitsStr, String sellingPriceStr) {
        try {
            // Validate inputs
            validateInputString(projectName, "Project name");
            validateInputString(flatType, "Flat type");
            
            // Get the project
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                return "Error: Project '" + projectName + "' not found.";
            }
            
            // Parse and validate input values
            int totalUnits = parseIntString(totalUnitsStr);
            if (totalUnits <= 0) {
                return "Error: Total units must be greater than 0.";
            }
            
            long sellingPrice = parseLongString(sellingPriceStr);
            if (sellingPrice <= 0) {
                return "Error: Selling price must be greater than 0.";
            }
            
            // Validate flat type (only "2-Room" and "3-Room" are allowed)
            if (Flat.FlatType.fromDisplayName(flatType) == null) {
                return "Error: Flat type must be either '2-Room' or '3-Room'.";
            }
            
            // Check if this flat type already exists in the project
            boolean flatTypeExists = project.getFlats().stream()
                                         .anyMatch(flat -> flat.getFlatType().getDisplayName().equalsIgnoreCase(flatType));
            
            if (flatTypeExists) {
                return "Error: Flat type '" + flatType + "' already exists in this project.";
            }
            
            // Add the flat to the project
            projectService.addFlatToProject(project, flatType, totalUnits, sellingPrice);
            
            return "Flat type '" + flatType + "' added to project '" + projectName + "' successfully.";
        } catch (IllegalArgumentException e) {
            return "Error adding flat to project: " + e.getMessage();
        } catch (Exception e) {
            return "Error adding flat to project: An unexpected error occurred.";
        }
    }
    
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
    public String updateProject(String projectName, String neighborhood, 
                             String applicationOpenDateStr, String applicationCloseDateStr,
                             String availableOfficerSlotsStr, HDBManager manager) {
        try {
            // Validate inputs
            validateInputString(projectName, "Project name");
            validateInputString(neighborhood, "Neighborhood");
            
            // Get the project
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                return "Error: Project '" + projectName + "' not found.";
            }
            
            // Check if project is deleted
            if (project.isDeleted()) {
                return "Error: Project '" + projectName + "' is archived and cannot be modified.";
            }
            
            // Check if manager is the creator of the project
            boolean isCreator = manager.getCreatedProjects().stream()
                                   .anyMatch(p -> p.getName().equals(project.getName()));
            
            if (!isCreator) {
                return "Error: Only the creator manager can update this project.";
            }
            
            // Parse and validate dates
            Date applicationOpenDate = parseDateString(applicationOpenDateStr);
            Date applicationCloseDate = parseDateString(applicationCloseDateStr);
            validateDates(applicationOpenDate, applicationCloseDate);
            
            // Parse and validate available officer slots
            int availableOfficerSlots = parseIntString(availableOfficerSlotsStr);
            validateOfficerSlots(availableOfficerSlots);
            
            // Update the project
            projectService.updateProject(
                project, neighborhood, applicationOpenDate, applicationCloseDate, availableOfficerSlots, manager
            );
            
            return "Project '" + projectName + "' updated successfully.";
        } catch (IllegalArgumentException e) {
            return "Error updating project: " + e.getMessage();
        } catch (Exception e) {
            return "Error updating project: An unexpected error occurred.";
        }
    }
    
    /**
     * Toggles the visibility of a project
     * 
     * @param projectName Name of the project
     * @param visibilityStr Visibility state ("on" or "off")
     * @param manager The manager toggling the visibility
     * @return Result message indicating success or failure
     */
    public String toggleProjectVisibility(String projectName, String visibilityStr, HDBManager manager) {
        try {
            // Validate inputs
            validateInputString(projectName, "Project name");
            validateInputString(visibilityStr, "Visibility");
            
            // Get the project
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                return "Error: Project '" + projectName + "' not found.";
            }
            
            // Check if project is deleted
            if (project.isDeleted()) {
                return "Error: Project '" + projectName + "' is archived and cannot be modified.";
            }
            
            // Check if manager is the creator of the project
            boolean isCreator = manager.getCreatedProjects().stream()
                                   .anyMatch(p -> p.getName().equals(project.getName()));
            
            if (!isCreator) {
                return "Error: Only the creator manager can toggle visibility for this project.";
            }
            
            // Parse visibility
            boolean visibility = parseVisibility(visibilityStr);
            
            // Toggle the visibility
            projectService.toggleProjectVisibility(project, visibility, manager);
            
            return "Project '" + projectName + "' visibility set to " + (visibility ? "ON" : "OFF") + ".";
        } catch (IllegalArgumentException e) {
            return "Error toggling project visibility: " + e.getMessage();
        } catch (Exception e) {
            return "Error toggling project visibility: An unexpected error occurred.";
        }
    }
    
    /**
     * Gets all projects created by a manager
     * 
     * @param manager The manager
     * @return List of projects created by the manager
     */
    public List<Project> getProjectsByManager(HDBManager manager) {
        return projectService.getProjectsByManager(manager);
    }
    
    /**
     * Gets all projects in the system regardless of creator or visibility
     * 
     * @return List of all projects
     */
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }
    
    /**
     * Gets a project by name
     * 
     * @param name Project name
     * @return The project with the specified name, or null if not found
     */
    public Project getProjectByName(String name) {
        return projectService.getProjectByName(name);
    }
    
    /**
     * Generates a report of applicants for a specific project
     * 
     * @param projectName Name of the project
     * @return List of applicant reports
     */
    public List<Map<String, Object>> generateProjectApplicantsReport(String projectName) {
        try {
            validateInputString(projectName, "Project name");
            return projectService.generateProjectApplicantsReport(projectName);
        } catch (IllegalArgumentException e) {
            // Log the error and return empty list
            System.err.println("Error generating project applicants report: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Generates a report of applicants filtered by various criteria
     * 
     * @param projectName Optional project name filter
     * @param flatType Optional flat type filter
     * @param maritalStatus Optional marital status filter
     * @param minAgeStr Optional minimum age filter
     * @param maxAgeStr Optional maximum age filter
     * @return List of applicant reports matching the filters
     */
    public List<Map<String, Object>> generateFilteredApplicantsReport(
            String projectName, String flatType, String maritalStatus, 
            String minAgeStr, String maxAgeStr) {
        
        Map<String, String> filters = new HashMap<>();
        
        try {
            // Add filters if provided
            if (projectName != null && !projectName.trim().isEmpty()) {
                validateInputString(projectName, "Project name");
                filters.put("projectName", projectName);
            }
            
            if (flatType != null && !flatType.trim().isEmpty()) {
                validateInputString(flatType, "Flat type");
                // Validate that it's a valid flat type
                if (Flat.FlatType.fromDisplayName(flatType) == null) {
                    throw new IllegalArgumentException("Flat type must be either '2-Room' or '3-Room'");
                }
                filters.put("flatType", flatType);
            }
            
            if (maritalStatus != null && !maritalStatus.trim().isEmpty()) {
                if (!maritalStatus.equalsIgnoreCase("married") && !maritalStatus.equalsIgnoreCase("single")) {
                    throw new IllegalArgumentException("Marital status must be 'married' or 'single'");
                }
                boolean isMarried = maritalStatus.equalsIgnoreCase("married");
                filters.put("maritalStatus", Boolean.toString(isMarried));
            }
            
            if (minAgeStr != null && !minAgeStr.trim().isEmpty()) {
                int minAge = parseIntString(minAgeStr);
                if (minAge < 0) {
                    throw new IllegalArgumentException("Minimum age cannot be negative");
                }
                filters.put("minAge", Integer.toString(minAge));
                
                // If max age is not provided, set it to a high value
                if (maxAgeStr == null || maxAgeStr.trim().isEmpty()) {
                    filters.put("maxAge", "150");
                }
            }
            
            if (maxAgeStr != null && !maxAgeStr.trim().isEmpty()) {
                int maxAge = parseIntString(maxAgeStr);
                if (maxAge < 0) {
                    throw new IllegalArgumentException("Maximum age cannot be negative");
                }
                filters.put("maxAge", Integer.toString(maxAge));
                
                // If min age is not provided, set it to 0
                if (minAgeStr == null || minAgeStr.trim().isEmpty()) {
                    filters.put("minAge", "0");
                }
            }
            
            return projectService.generateFilteredApplicantsReport(filters);
        } catch (IllegalArgumentException e) {
            // Log the error and return empty list
            System.err.println("Error generating filtered applicants report: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Soft deletes a project
     * 
     * @param projectName Name of the project to delete
     * @param manager The manager deleting the project
     * @return Result message indicating success or failure
     */
    public String softDeleteProject(String projectName, HDBManager manager) {
        try {
            // Validate inputs
            validateInputString(projectName, "Project name");
            
            // Get the project
            Project project = projectService.getProjectByName(projectName);
            if (project == null) {
                return "Error: Project '" + projectName + "' not found.";
            }
            
            // Check if project is already deleted
            if (project.isDeleted()) {
                return "Error: Project '" + projectName + "' is already deleted.";
            }
            
            // Check if manager is the creator of the project
            boolean isCreator = manager.getCreatedProjects().stream()
                                   .anyMatch(p -> p.getName().equals(project.getName()));
            
            if (!isCreator) {
                return "Error: Only the creator manager can delete this project.";
            }
            
            // Check if project has applications
            if (!project.getApplications().isEmpty()) {
                return "Error: Project '" + projectName + "' has applications and cannot be deleted.";
            }
            
            // Soft delete the project
            projectService.softDeleteProject(project, manager);
            
            return "Project '" + projectName + "' has been archived.";
        } catch (IllegalArgumentException e) {
            return "Error deleting project: " + e.getMessage();
        } catch (Exception e) {
            return "Error deleting project: An unexpected error occurred.";
        }
    }
    
    /**
     * Gets all deleted projects created by a manager
     * 
     * @param manager The manager
     * @return List of deleted projects created by the manager
     */
    public List<Project> getDeletedProjectsByManager(HDBManager manager) {
        return projectService.getDeletedProjectsByManager(manager);
    }
    
    /**
     * Gets all deleted projects in the system regardless of creator
     * 
     * @return List of all deleted projects
     */
    public List<Project> getAllDeletedProjects() {
        return projectService.getAllDeletedProjects();
    }
    
    /**
     * Validates that a string is not null or empty
     * 
     * @param str The string to validate
     * @param fieldName The name of the field being validated
     * @throws IllegalArgumentException if the string is null or empty
     */
    private void validateInputString(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
    
    /**
     * Validates dates for a project
     * 
     * @param openDate Opening date
     * @param closeDate Closing date
     * @throws IllegalArgumentException if validation fails
     */
    private void validateDates(Date openDate, Date closeDate) {
        if (openDate == null) {
            throw new IllegalArgumentException("Application open date cannot be null.");
        }
        
        if (closeDate == null) {
            throw new IllegalArgumentException("Application close date cannot be null.");
        }
        
        if (closeDate.before(openDate)) {
            throw new IllegalArgumentException("Application close date cannot be before application open date.");
        }
        
        Date currentDate = new Date();
        if (openDate.before(currentDate)) {
            throw new IllegalArgumentException("Application open date cannot be in the past.");
        }
    }
    
    /**
     * Validates the number of officer slots
     * 
     * @param slots Number of slots
     * @throws IllegalArgumentException if validation fails
     */
    private void validateOfficerSlots(int slots) {
        if (slots <= 0) {
            throw new IllegalArgumentException("Available officer slots must be greater than 0.");
        }
        
        if (slots > 10) {
            throw new IllegalArgumentException("Available officer slots cannot exceed 10.");
        }
    }
    
    /**
     * Checks if a manager is handling a project during a specific period
     * 
     * @param manager The manager to check
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return true if the manager is handling a project during the period, false otherwise
     */
    private boolean isManagerHandlingProjectInPeriod(HDBManager manager, Date startDate, Date endDate) {
        return manager.getCreatedProjects().stream()
                     .anyMatch(project -> {
                         Date projectStartDate = project.getApplicationOpenDate();
                         Date projectEndDate = project.getApplicationCloseDate();
                         
                         // Check for overlap between periods
                         return !(projectEndDate.before(startDate) || projectStartDate.after(endDate));
                     });
    }
    
    /**
     * Parses a date string into a Date object
     * 
     * @param dateStr Date string in format dd/MM/yyyy
     * @return Parsed Date object
     * @throws IllegalArgumentException if parsing fails
     */
    private Date parseDateString(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be empty.");
        }
        
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use dd/MM/yyyy.");
        }
    }
    
    /**
     * Parses an integer string
     * 
     * @param intStr Integer string
     * @return Parsed integer
     * @throws IllegalArgumentException if parsing fails
     */
    private int parseIntString(String intStr) {
        if (intStr == null || intStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Number cannot be empty.");
        }
        
        try {
            return Integer.parseInt(intStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format.");
        }
    }
    
    /**
     * Parses a long string
     * 
     * @param longStr Long string
     * @return Parsed long
     * @throws IllegalArgumentException if parsing fails
     */
    private long parseLongString(String longStr) {
        if (longStr == null || longStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Number cannot be empty.");
        }
        
        try {
            return Long.parseLong(longStr.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format.");
        }
    }
    
    /**
     * Parses a visibility string
     * 
     * @param visibilityStr Visibility string ("on" or "off")
     * @return true for "on", false for "off"
     * @throws IllegalArgumentException if the string is neither "on" nor "off"
     */
    private boolean parseVisibility(String visibilityStr) {
        if (visibilityStr == null || visibilityStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Visibility cannot be empty.");
        }
        
        String trimmed = visibilityStr.trim().toLowerCase();
        if (trimmed.equals("on")) {
            return true;
        } else if (trimmed.equals("off")) {
            return false;
        } else {
            throw new IllegalArgumentException("Visibility must be 'on' or 'off'.");
        }
    }
} 
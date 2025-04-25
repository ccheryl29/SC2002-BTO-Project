package service;

import model.Applicant;
import model.Application;
import model.Flat;
import model.HDBManager;
import model.Project;
import model.User;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for HDB Manager's project management
 */
public class HDBManagerProjectService {
    
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;
    
    /**
     * Constructor for HDBManagerProjectService
     * 
     * @param projectRepository Repository for managing projects
     * @param applicationRepository Repository for managing applications
     */
    public HDBManagerProjectService(ProjectRepository projectRepository, ApplicationRepository applicationRepository) {
        this.projectRepository = projectRepository;
        this.applicationRepository = applicationRepository;
    }
    
    /**
     * Creates a new project
     * 
     * @param name Project name
     * @param neighborhood Project neighborhood
     * @param applicationOpenDate Application opening date
     * @param applicationCloseDate Application closing date
     * @param availableOfficerSlots Number of available officer slots
     * @param manager The manager creating the project
     * @return The created project
     */
    public Project createProject(String name, String neighborhood, 
                              Date applicationOpenDate, Date applicationCloseDate,
                              int availableOfficerSlots, HDBManager manager) {
        // Create the project
        Project project = new Project(name, neighborhood, applicationOpenDate, applicationCloseDate, availableOfficerSlots);
        project.setManager(manager);
        // Add the project to the manager's created projects
        manager.addCreatedProject(project);
        
        // Save the project to the repository
        projectRepository.save(project);
        
        return project;
    }
    
    /**
     * Adds a flat type to a project
     * 
     * @param project The project to add the flat to
     * @param flatTypeStr The type of flat
     * @param totalUnits The total number of units
     * @param sellingPrice The selling price
     * @return The updated project
     */
    public Project addFlatToProject(Project project, String flatTypeStr, int totalUnits, long sellingPrice) {
        // Convert string to FlatType enum
        Flat.FlatType flatType = Flat.FlatType.fromDisplayName(flatTypeStr);
        if (flatType == null) {
            throw new IllegalArgumentException("Invalid flat type. Only '2-Room' and '3-Room' are supported.");
        }
        
        // Create the flat and add it to the project
        Flat flat = new Flat(flatType, totalUnits, sellingPrice);
        project.addFlat(flat);
        
        // Update the project in the repository
        projectRepository.save(project);
        
        return project;
    }
    
    /**
     * Updates an existing project
     * 
     * @param project The project to update
     * @param neighborhood Updated neighborhood
     * @param applicationOpenDate Updated application opening date
     * @param applicationCloseDate Updated application closing date
     * @param availableOfficerSlots Updated number of available officer slots
     * @param manager The manager updating the project
     * @return The updated project
     */
    public Project updateProject(Project project, String neighborhood, 
                              Date applicationOpenDate, Date applicationCloseDate,
                              int availableOfficerSlots, HDBManager manager) {
        // Update project details
        project.setNeighborhood(neighborhood);
        project.setApplicationOpenDate(applicationOpenDate);
        project.setApplicationCloseDate(applicationCloseDate);
        project.setAvailableOfficerSlots(availableOfficerSlots);
        
        // Update the project in the repository
        projectRepository.save(project);
        
        return project;
    }
    
    /**
     * Toggles the visibility of a project
     * 
     * @param project The project to toggle
     * @param visibility The new visibility state
     * @param manager The manager toggling the visibility
     * @return The updated project
     */
    public Project toggleProjectVisibility(Project project, boolean visibility, HDBManager manager) {
        // Toggle the visibility
        project.toggleVisibility(visibility);
        
        // Update the project in the repository
        projectRepository.save(project);
        
        return project;
    }
    
    /**
     * Soft deletes a project making it invisible but still accessible for reference
     * 
     * @param project The project to delete
     * @param manager The manager deleting the project
     * @return The updated project
     * @throws IllegalArgumentException if manager is not the creator of the project
     */
    public Project softDeleteProject(Project project, HDBManager manager) {
        // Check if manager is the creator of the project
        if (!project.getManager().getNRIC().equals(manager.getNRIC())) {
            throw new IllegalArgumentException("Only the creator manager can delete this project.");
        }
        
        // Set project as deleted and hide it
        project.setDeleted(true);
        project.toggleVisibility(false);
        
        // Update the project in the repository
        projectRepository.save(project);
        
        return project;
    }
    
    /**
     * Gets all non-deleted projects created by a manager
     * 
     * @param manager The manager
     * @return A list of active projects created by the manager
     */
    public List<Project> getProjectsByManager(HDBManager manager) {
        return manager.getCreatedProjects().stream()
            .filter(project -> !project.isDeleted())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all non-deleted projects regardless of creator or visibility
     * 
     * @return A list of all active projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll().stream()
            .filter(project -> !project.isDeleted())
            .collect(Collectors.toList());
    }
    
    /**
     * Gets a project by name
     * 
     * @param name Project name
     * @return The project with the specified name, or null if not found
     */
    public Project getProjectByName(String name) {
        return projectRepository.findById(name);
    }
    
    /**
     * Generates a report of applicants for a specific project
     * 
     * @param projectName The name of the project
     * @return A list of applicant reports
     */
    public List<Map<String, Object>> generateProjectApplicantsReport(String projectName) {
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return new ArrayList<>();
        }
        
        List<Application> applications = applicationRepository.findAll().stream()
            .filter(app -> app.getProject().getName().equals(projectName))
            .collect(Collectors.toList());
        
        return createApplicantReportEntries(applications);
    }
    
    /**
     * Generates a filtered report of applicants
     * 
     * @param filters Map of filters to apply (e.g., "flatType", "maritalStatus", "ageRange")
     * @return A list of applicant reports matching the filters
     */
    public List<Map<String, Object>> generateFilteredApplicantsReport(Map<String, String> filters) {
        List<Application> allApplications = applicationRepository.findAll();
        List<Application> filteredApplications = allApplications;
        
        // Apply filters
        if (filters.containsKey("projectName")) {
            String projectName = filters.get("projectName");
            filteredApplications = filteredApplications.stream()
                .filter(app -> app.getProject().getName().equals(projectName))
                .collect(Collectors.toList());
        }
        
        if (filters.containsKey("flatType")) {
            final String flatTypeStr = filters.get("flatType");
            Flat.FlatType flatType = Flat.FlatType.fromDisplayName(flatTypeStr);
            if (flatType != null) {
                filteredApplications = filteredApplications.stream()
                    .filter(app -> app.getFlatType() == flatType)
                    .collect(Collectors.toList());
            }
        }
        
        if (filters.containsKey("maritalStatus")) {
            String maritalStatus = filters.get("maritalStatus");
            User.MaritalStatus maritalStatusEnum = maritalStatus == "married" ? User.MaritalStatus.MARRIED : User.MaritalStatus.SINGLE;
            filteredApplications = filteredApplications.stream()
                .filter(app -> app.getApplicant().getMaritalStatus() == maritalStatusEnum)
                .collect(Collectors.toList());
        }
        
        if (filters.containsKey("minAge") && filters.containsKey("maxAge")) {
            int minAge = Integer.parseInt(filters.get("minAge"));
            int maxAge = Integer.parseInt(filters.get("maxAge"));
            
            filteredApplications = filteredApplications.stream()
                .filter(app -> {
                    int age = app.getApplicant().getAge();
                    return age >= minAge && age <= maxAge;
                })
                .collect(Collectors.toList());
        }
        
        return createApplicantReportEntries(filteredApplications);
    }
    
    /**
     * Helper method to create report entries from applications
     * 
     * @param applications List of applications
     * @return List of report entries
     */
    private List<Map<String, Object>> createApplicantReportEntries(List<Application> applications) {
        List<Map<String, Object>> reportEntries = new ArrayList<>();
        
        for (Application app : applications) {
            Map<String, Object> entry = new HashMap<>();
            Applicant applicant = app.getApplicant();
            
            entry.put("applicantName", applicant.getName());
            entry.put("age", applicant.getAge());
            entry.put("married", applicant.getMaritalStatus() == User.MaritalStatus.MARRIED);
            entry.put("projectName", app.getProject().getName());
            entry.put("flatType", app.getFlatType().getDisplayName());
            entry.put("applicationDate", app.getApplicationDate());
            entry.put("status", app.getStatus());
            reportEntries.add(entry);
        }
        
        return reportEntries;
    }
    
    /**
     * Gets all deleted projects created by a manager
     * 
     * @param manager The manager
     * @return A list of deleted projects created by the manager
     */
    public List<Project> getDeletedProjectsByManager(HDBManager manager) {
        return manager.getCreatedProjects().stream()
            .filter(Project::isDeleted)
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all deleted projects in the system
     * 
     * @return A list of all deleted projects
     */
    public List<Project> getAllDeletedProjects() {
        return projectRepository.findAll().stream()
            .filter(Project::isDeleted)
            .collect(Collectors.toList());
    }
} 
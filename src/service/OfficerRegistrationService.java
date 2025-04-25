package service;

import model.HDBOfficer;
import model.Project;
import repository.OfficerRepository;
import repository.ProjectRepository;
import repository.ApplicationRepository;

import java.util.List;

/**
 * Service class for handling HDBOfficer operations
 */
public class OfficerRegistrationService {
    
    private final OfficerRepository officerRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;
    
    /**
     * Constructor for OfficerService
     * 
     * @param officerRepository Repository for managing officers
     * @param projectRepository Repository for managing projects
     * @param applicationRepository Repository for managing applications
     */
    public OfficerRegistrationService(
            OfficerRepository officerRepository, 
            ProjectRepository projectRepository,
            ApplicationRepository applicationRepository) {
        this.officerRepository = officerRepository;
        this.projectRepository = projectRepository;
        this.applicationRepository = applicationRepository;
    }
    
    /**
     * Checks if an officer has applied for a project as an applicant
     * 
     * @param officer The officer to check
     * @param project The project to check
     * @return true if the officer has applied for the project, false otherwise
     */
    public boolean hasAppliedForProject(HDBOfficer officer, Project project) {
        if (officer == null || project == null) {
            return false;
        }
        
        // Check if the officer has a current application for this project
        return applicationRepository.findByApplicantAndProject(officer, project) != null;
    }
    
    /**
     * Registers an officer to handle a project
     * 
     * @param officer The officer to register
     * @param projectName The name of the project
     * @return true if registration succeeded, false otherwise
     */
    public boolean registerForProject(HDBOfficer officer, String projectName) {
        // Validate inputs
        if (officer == null || projectName == null || projectName.trim().isEmpty()) {
            return false;
        }
        
        // Get the project
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return false;
        }
        
        // Check if project is full
        if (project.isFull()) {
            return false;
        }
        
        // Check if officer has applied for this project as an applicant
        if (hasAppliedForProject(officer, project)) {
            return false;
        }
        
        // // Check if officer can register for this project
        // if (!officer.canRegisterForProject(project)) {
        //     return false;
        // }
        
        // Set the project for the officer
        officer.setHandlingProject(project);
        
        // Set registration status to pending
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatus.PENDING);
        
        // Save the officer
        officerRepository.save(officer);
        
        return true;
    }
    
    /**
     * Gets all projects available for registration
     * 
     * @return List of available projects
     */
    public List<Project> getAvailableProjects() {
        return projectRepository.findOpenProjects();
    }
    
    /**
     * Finds an officer by NRIC
     * 
     * @param nric The NRIC to search for
     * @return The officer with the specified NRIC, or null if not found
     */
    public HDBOfficer findOfficerByNRIC(String nric) {
        return officerRepository.findById(nric);
    }
    
    /**
     * Updates an officer's information
     * 
     * @param officer The officer to update
     * @return The updated officer
     */
    public HDBOfficer updateOfficer(HDBOfficer officer) {
        if (officer == null) {
            return null;
        }
        
        return officerRepository.save(officer);
    }
    
    /**
     * Authenticates an officer using NRIC and password
     * 
     * @param nric The NRIC to authenticate
     * @param password The password to authenticate
     * @return The authenticated officer, or null if authentication fails
     */
    public HDBOfficer authenticate(String nric, String password) {
        return officerRepository.authenticate(nric, password);
    }
} 
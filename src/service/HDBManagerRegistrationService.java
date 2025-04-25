package service;

import model.HDBManager;
import model.HDBOfficer;
import model.Project;
import repository.OfficerRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for HDB Manager's officer registration management
 */
public class HDBManagerRegistrationService {
    
    private final OfficerRepository officerRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * Constructor for HDBManagerRegistrationService
     * 
     * @param officerRepository Repository for managing officers
     * @param projectRepository Repository for managing projects
     */
    public HDBManagerRegistrationService(OfficerRepository officerRepository, ProjectRepository projectRepository) {
        this.officerRepository = officerRepository;
        this.projectRepository = projectRepository;
    }
    
    /**
     * Gets all pending officer registrations for a specific project
     * 
     * @param projectName The name of the project
     * @return List of pending officer registrations
     */
    public List<HDBOfficer> getPendingOfficerRegistrations(String projectName) {
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return new ArrayList<>();
        }
        
        return officerRepository.findAll().stream()
            .filter(officer -> officer.getRegistrationStatus() == HDBOfficer.RegistrationStatus.PENDING)
            .filter(officer -> {
                if (officer.getHandlingProject() == null) {
                    return false;
                }
                return officer.getHandlingProject().getName().equals(projectName);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all approved officer registrations for a specific project
     * 
     * @param projectName The name of the project
     * @return List of approved officer registrations
     */
    public List<HDBOfficer> getApprovedOfficerRegistrations(String projectName) {
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return new ArrayList<>();
        }
        
        return officerRepository.findAll().stream()
            .filter(officer -> officer.getRegistrationStatus() == HDBOfficer.RegistrationStatus.APPROVED)
            .filter(officer -> {
                if (officer.getHandlingProject() == null) {
                    return false;
                }
                return officer.getHandlingProject().getName().equals(projectName);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * Approves an officer's registration for a project
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @return true if successful, false otherwise
     */
    public boolean approveOfficerRegistration(String officerNRIC, String projectName) {
        HDBOfficer officer = officerRepository.findById(officerNRIC);
        if (officer == null) {
            return false;
        }
        
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return false;
        }
        
        // Check if officer's current project matches the given project
        if (officer.getHandlingProject() == null || 
            !officer.getHandlingProject().getName().equals(projectName)) {
            return false;
        }
        
        // Check if officer is pending
        if (officer.getRegistrationStatus() != HDBOfficer.RegistrationStatus.PENDING) {
            return false;
        }
        
        // Check if project has available slots
        if (project.isFull()) {
            return false;
        }
        
        // Update officer status
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatus.APPROVED);
        officerRepository.save(officer);
        
        // Update project's officer list
        project.registerOfficer(officer);
        projectRepository.save(project);
        
        return true;
    }
    
    /**
     * Rejects an officer's registration for a project
     * 
     * @param officerNRIC The NRIC of the officer
     * @param projectName The name of the project
     * @return true if successful, false otherwise
     */
    public boolean rejectOfficerRegistration(String officerNRIC, String projectName) {
        HDBOfficer officer = officerRepository.findById(officerNRIC);
        if (officer == null) {
            return false;
        }
        
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return false;
        }
        
        // Check if officer's current project matches the given project
        if (officer.getHandlingProject() == null || 
            !officer.getHandlingProject().getName().equals(projectName)) {
            return false;
        }
        
        // Check if officer is pending
        if (officer.getRegistrationStatus() != HDBOfficer.RegistrationStatus.PENDING) {
            return false;
        }
        
        // Update officer status
        officer.setRegistrationStatus(HDBOfficer.RegistrationStatus.REJECTED);
        officerRepository.save(officer);
        
        return true;
    }
} 
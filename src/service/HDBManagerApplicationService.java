package service;

import model.Applicant;
import model.Application;
import model.Application.ApplicationStatus;
import model.Flat;
import model.HDBManager;
import model.Project;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for HDB Manager's application management
 */
public class HDBManagerApplicationService {
    
    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * Constructor for HDBManagerApplicationService
     * 
     * @param applicationRepository Repository for managing applications
     * @param projectRepository Repository for managing projects
     */
    public HDBManagerApplicationService(ApplicationRepository applicationRepository, ProjectRepository projectRepository) {
        this.applicationRepository = applicationRepository;
        this.projectRepository = projectRepository;
    }
    
    /**
     * Gets all pending applications for a specific project
     * 
     * @param projectName The name of the project
     * @return List of pending applications
     */
    public List<Application> getPendingApplications(String projectName) {
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return new ArrayList<>();
        }
        
        return applicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == Application.ApplicationStatus.PENDING)
            .filter(app -> !app.isWithdrawalRequested()) // Exclude applications with withdrawal requests
            .filter(app -> app.getProject().getName().equals(projectName))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all applications with a specific status for a project
     * 
     * @param projectName The name of the project
     * @param status The status to filter by
     * @return List of applications with the specified status
     */
    public List<Application> getApplicationsByStatus(String projectName, Application.ApplicationStatus status) {
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return new ArrayList<>();
        }
        
        return applicationRepository.findAll().stream()
            .filter(app -> app.getStatus() == status)
            .filter(app -> !app.isWithdrawalRequested()) // Exclude applications with withdrawal requests
            .filter(app -> app.getProject().getName().equals(projectName))
            .collect(Collectors.toList());
    }
    
    /**
     * Gets all applications with withdrawal requests for a project
     * 
     * @param projectName The name of the project
     * @return List of applications with withdrawal requests
     */
    public List<Application> getWithdrawalRequests(String projectName) {
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return new ArrayList<>();
        }
        
        return applicationRepository.findAll().stream()
            .filter(app -> app.isWithdrawalRequested())
            .filter(app -> app.getWithdrawalRequestStatus() == Application.WithdrawalRequestStatus.PENDING)
            .filter(app -> app.getProject().getName().equals(projectName))
            .collect(Collectors.toList());
    }
    
    /**
     * Approves an application (changes status from PENDING to SUCCESSFUL)
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return true if successful, false otherwise
     */
    public boolean approveApplication(String applicantNRIC, String projectName) {
        Application application = findApplication(applicantNRIC, projectName);
        if (application == null) {
            return false;
        }
        
        // Check if application is pending
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            return false;
        }
        
        // Check if withdrawal is requested
        if (application.isWithdrawalRequested()) {
            return false;
        }
        
        // Check if the project has available units of the requested flat type
        Project project = application.getProject();
        Flat.FlatType flatType = application.getFlatType();
        
        boolean hasAvailableUnits = false;
        for (Flat flat : project.getFlats()) {
            if (flat.getFlatType() == flatType && flat.getAvailableUnits() > 0) {
                hasAvailableUnits = true;
                break;
            }
        }
        
        if (!hasAvailableUnits) {
            return false;
        }
        
        // Update application status to SUCCESSFUL
        application.updateStatus(Application.ApplicationStatus.SUCCESSFUL);
        applicationRepository.save(application);
        
        return true;
    }
    
    /**
     * Rejects an application
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return true if successful, false otherwise
     */
    public boolean rejectApplication(String applicantNRIC, String projectName) {
        Application application = findApplication(applicantNRIC, projectName);
        if (application == null) {
            return false;
        }
        
        // Check if application is pending
        if (application.getStatus() != Application.ApplicationStatus.PENDING) {
            return false;
        }
        
        // Check if withdrawal is requested
        if (application.isWithdrawalRequested()) {
            return false;
        }
        
        // Update application status to UNSUCCESSFUL
        application.updateStatus(Application.ApplicationStatus.UNSUCCESSFUL);
        applicationRepository.save(application);
        
        return true;
    }
    
    /**
     * Approves a withdrawal request (changes application status to UNSUCCESSFUL)
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return true if successful, false otherwise
     */
    public boolean approveWithdrawal(String applicantNRIC, String projectName) {
        Application application = findApplication(applicantNRIC, projectName);
        if (application == null) {
            return false;
        }
        
        // Check if withdrawal is requested
        if (!application.isWithdrawalRequested()) {
            return false;
        }
        
        // Check if withdrawal request is pending
        if (application.getWithdrawalRequestStatus() != Application.WithdrawalRequestStatus.PENDING) {
            return false;
        }
        
        // If the application was approved, increase available units
        if (application.getStatus() == Application.ApplicationStatus.SUCCESSFUL) {
            Project project = application.getProject();
            Flat.FlatType flatType = application.getFlatType();
            
            for (Flat flat : project.getFlats()) {
                if (flat.getFlatType() == flatType) {
                    flat.setAvailableUnits(flat.getAvailableUnits() + 1);
                    break;
                }
            }
            
            projectRepository.save(project);
        }
        
        // Update withdrawal request status
        application.setWithdrawalRequestStatus(Application.WithdrawalRequestStatus.APPROVED);
        
        // Mark application as unsuccessful
        application.processWithdrawal();
        applicationRepository.save(application);
        
        return true;
    }
    
    /**
     * Rejects a withdrawal request
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return true if successful, false otherwise
     */
    public boolean rejectWithdrawal(String applicantNRIC, String projectName) {
        Application application = findApplication(applicantNRIC, projectName);
        if (application == null) {
            return false;
        }
        
        // Check if withdrawal is requested
        if (!application.isWithdrawalRequested()) {
            return false;
        }
        
        // Check if withdrawal request is pending
        if (application.getWithdrawalRequestStatus() != Application.WithdrawalRequestStatus.PENDING) {
            return false;
        }
        
        // Update withdrawal request status
        application.setWithdrawalRequestStatus(Application.WithdrawalRequestStatus.REJECTED);
        applicationRepository.save(application);
        
        return true;
    }
    
    /**
     * Helper method to find an application by applicant NRIC and project name
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return The application, or null if not found
     */
    private Application findApplication(String applicantNRIC, String projectName) {
        return applicationRepository.findAll().stream()
            .filter(app -> app.getApplicant().getNRIC().equals(applicantNRIC))
            .filter(app -> app.getProject().getName().equals(projectName))
            .findFirst()
            .orElse(null);
    }
} 
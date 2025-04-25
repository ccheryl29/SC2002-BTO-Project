package service;

import model.Applicant;
import model.Application;
import model.Flat;
import model.Project;
import model.User;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for applicant project interactions
 */
public class ApplicantProjectService {
    
    private final ProjectRepository projectRepository;
    private final ApplicationRepository applicationRepository;
    
    /**
     * Constructor for ApplicantProjectService
     * 
     * @param projectRepository The project repository
     * @param applicationRepository The application repository
     */
    public ApplicantProjectService(ProjectRepository projectRepository, ApplicationRepository applicationRepository) {
        this.projectRepository = projectRepository;
        this.applicationRepository = applicationRepository;
    }
    
    /**
     * Gets all visible projects for an applicant based on marital status
     * 
     * @param applicant The applicant
     * @return List of visible projects
     */
    public List<Project> getVisibleProjects(Applicant applicant) {
        // Get all projects
        List<Project> allProjects = projectRepository.findVisibleProjects();
        
        // Filter projects that are visible and match the applicant's marital status
        return allProjects.stream()
            .filter(Project::isVisible)
            .filter(project -> isProjectVisibleToApplicant(project, applicant)) // Based on marital status
            .collect(Collectors.toList());
    }
    public List<Project> getOpeningProjects(Applicant applicant) {
        // Get all projects
        List<Project> allProjects = projectRepository.findOpeningProjects();
        
        // Filter projects that are visible and match the applicant's marital status
        return allProjects.stream()
            .filter(Project::isVisible)
            .filter(project -> isProjectVisibleToApplicant(project, applicant)) // Based on marital status
            .collect(Collectors.toList());
    }
    
    
    /**
     * Check if a project is visible to an applicant based on marital status
     * 
     * @param project The project
     * @param applicant The applicant
     * @return true if visible, false otherwise
     */
    public boolean isProjectVisibleToApplicant(Project project, Applicant applicant) {
        if (!project.isVisible()) {
            // If project is not visible in general, check if applicant has an application for it
            if (hasExistingApplication(applicant)) {
                Application application = getApplicantApplication(applicant);
                if (application != null && application.getProject().getName().equals(project.getName())) {
                    return true; // Applicant can view the project they applied for even if visibility is turned off
                }
            }
            return false;
        }
        
        // Check marital status eligibility
        if (applicant.getMaritalStatus() == User.MaritalStatus.SINGLE) {
            return project.isEligibleForSingles();
        } else if (applicant.getMaritalStatus() == User.MaritalStatus.MARRIED) {
            return project.isEligibleForMarried();
        }
        
        return false;
    }
    
    /**
     * Get a project by name
     * 
     * @param projectName The name of the project
     * @return The project, or null if not found
     */
    public Project getProjectByName(String projectName) {
        return projectRepository.findById(projectName);
    }
    
    /**
     * Create a new application
     * 
     * @param applicant The applicant
     * @param projectName The name of the project
     * @param flatType The flat type
     * @return true if successful, false otherwise
     */
    public boolean createApplication(Applicant applicant, String projectName, Flat.FlatType flatType) {
        // Get the project
        Project project = projectRepository.findById(projectName);
        if (project == null) {
            return false;
        }
        
        // Check if there are available units of this flat type
        if (!hasAvailableUnits(project, flatType)) {
            return false;
        }
        
        // Create a new application
        Application application = new Application(applicant, project, flatType);
        project.addApplication(application);
        // Save the application
        applicationRepository.save(application);
        
        return true;
    }
    
    /**
     * Request withdrawal of an application
     * 
     * @param applicant The applicant
     * @return true if successful, false otherwise
     */
    public boolean requestWithdrawal(Applicant applicant) {
        // Get the application
        Application application = getApplicantApplication(applicant);
        if (application == null) {
            return false;
        }
        
        // Request withdrawal
        application.requestWithdrawal();
        
        // Save the updated application
        applicationRepository.save(application);
        
        return true;
    }
    
    /**
     * Check if a flat type is available in a project
     * 
     * @param project The project
     * @param flatType The flat type
     * @return true if available, false otherwise
     */
    public boolean isFlatTypeAvailable(Project project, Flat.FlatType flatType) {
        return project.getFlats().stream()
            .anyMatch(flat -> flat.getFlatType() == flatType);
    }
    
    /**
     * Check if there are available units of a flat type in a project
     * 
     * @param project The project
     * @param flatType The flat type
     * @return true if there are available units, false otherwise
     */
    public boolean hasAvailableUnits(Project project, Flat.FlatType flatType) {
        return project.getFlats().stream()
            .filter(flat -> flat.getFlatType() == flatType)
            .anyMatch(flat -> flat.getAvailableUnits() > 0);
    }
    
    /**
     * Check if an applicant has an existing application
     * 
     * @param applicant The applicant
     * @return true if an application exists, false otherwise
     */
    public boolean hasExistingApplication(Applicant applicant) {
        return applicationRepository.findAll().stream()
            .anyMatch(app -> app.getApplicant().getNRIC().equals(applicant.getNRIC()));
    }
    
    /**
     * Get an applicant's current application
     * 
     * @param applicant The applicant
     * @return The application, or null if not found
     */
    public Application getApplicantApplication(Applicant applicant) {
        return applicationRepository.findAll().stream()
            .filter(app -> app.getApplicant().getNRIC().equals(applicant.getNRIC()))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Initiates a booking request for an applicant
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return A message indicating the result of the operation
     */
    public String initiateBooking(String applicantNRIC, String projectName) {
        // Find the application
        Application application = findApplication(applicantNRIC, projectName);
        
        if (application == null) {
            return "No application found for this applicant and project.";
        }
        
        // Check if the application is successful
        if (application.getStatus() != Application.ApplicationStatus.SUCCESSFUL) {
            return "Only successful applications can initiate a booking.";
        }
        
        // Check if the application already has a booking
        if (application.getBookingStatus() != Application.BookingFlatStatus.NONE) {
            return "This application already has a booking request.";
        }
        
        // Update the booking status to pending
        application.updateBookingStatus(Application.BookingFlatStatus.PENDING);
        
        // Save the application
        applicationRepository.save(application);
        
        return "Booking request initiated successfully. Please contact an HDB Officer to complete the booking process.";
    }
    
    /**
     * Helper method to find an application by applicant NRIC and project name
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return The application or null if not found
     */
    private Application findApplication(String applicantNRIC, String projectName) {
        return applicationRepository.findAll().stream()
            .filter(app -> app.getApplicant().getNRIC().equals(applicantNRIC) &&
                   app.getProject().getName().equals(projectName))
            .findFirst()
            .orElse(null);
    }
} 
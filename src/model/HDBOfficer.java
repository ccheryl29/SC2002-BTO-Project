package model;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an HDB Officer who can handle applications for projects
 * HDB Officers also inherit from Applicant, so they can apply for projects they are not handling
 */
public class HDBOfficer extends Applicant {
    private static final long serialVersionUID = 1L;
    
    /**
     * Enum representing the registration status of an officer for a project
     */
    public enum RegistrationStatus {
        PENDING,   // Application to be an officer is pending approval
        APPROVED,  // Application to be an officer has been approved
        REJECTED   // Application to be an officer has been rejected
    }
    
    private Project handlingProject;           // The project this officer is currently handling
    private RegistrationStatus registrationStatus; // The registration status of this officer
    private List<Project> registeredProjects;  // Projects this officer has been registered to handle
    
    /**
     * Constructor for HDBOfficer
     * 
     * @param nric NRIC of the officer
     * @param name Name of the officer
     * @param password Password for login
     * @param age Age of the officer
     * @param maritalStatus Marital status of the officer
     */
    public HDBOfficer(String nric, String name, String password, int age, MaritalStatus maritalStatus) {
        super(nric, name, password, age, maritalStatus);
        this.handlingProject = null;
        this.registrationStatus = null; // No registration status initially
        this.registeredProjects = new ArrayList<>();
    }
    
    /**
     * Gets the project this officer is currently handling
     * 
     * @return The project, or null if not handling any project
     */
    public Project getHandlingProject() {
        return handlingProject;
    }
    
    /**
     * Sets the project this officer is handling
     * 
     * @param project The project to handle
     */
    public void setHandlingProject(Project project) {
        this.handlingProject = project;
        if (project != null && !registeredProjects.contains(project)) {
            registeredProjects.add(project);
        }
    }
    
    /**
     * Gets the registration status of this officer
     * 
     * @return The registration status
     */
    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }
    
    /**
     * Sets the registration status of this officer
     * 
     * @param status The new registration status
     */
    public void setRegistrationStatus(RegistrationStatus status) {
        this.registrationStatus = status;
    }
    
    /**
     * Gets the list of projects this officer has been registered to handle
     * 
     * @return The list of registered projects
     */
    public List<Project> getRegisteredProjects() {
        return registeredProjects;
    }
    
    /**
     * Registers to handle a project
     * 
     * @param project The project to register for
     * @return true if registered successfully, false otherwise
     */
    public boolean registerForProject(Project project) {
        if (project == null) {
            return false;
        }
        
        // Cannot register for a project the officer has applied for as an applicant
        if (hasAppliedForProject(project)) {
            return false;
        }
        
        // Initialize registration
        this.registrationStatus = RegistrationStatus.PENDING;
        return true;
    }
    
    public boolean hasAppliedForProject(Project project) {
        return currentApplication != null && currentApplication.getProject().equals(project);
    }
    
    /**
     * Checks if this officer has availability to handle a project
     * 
     * @return true if the officer has availability, false otherwise
     */
    public boolean hasAvailability() {
        return handlingProject == null || isHandlingProjectClosed();
    }
    
    /**
     * Checks if the project this officer is handling is closed
     * 
     * @return true if the project is closed, false otherwise
     */
    private boolean isHandlingProjectClosed() {
        if (handlingProject == null) {
            return true;
        }
        
        // Check if the application period has ended
        return new java.util.Date().after(handlingProject.getApplicationCloseDate());
    }
    
    /**
     * Validates if this officer can register for a project
     * 
     * @param project The project to validate
     * @return true if eligible to register, false otherwise
     */
    public boolean canRegisterForProject(Project project) {
        // Cannot register if already registered for this project
        if (registeredProjects.contains(project)) {
            return false;
        }
        
        // Cannot register if already handling a project within its application period
        if (handlingProject != null && !isHandlingProjectClosed()) {
            return false;
        }
        
        
        return true;
    }
    
    /**
     * Checks if the officer is handling a project in a specific period
     * 
     * @param startDate Start date of the period
     * @param endDate End date of the period
     * @return true if the officer is handling a project in the period, false otherwise
     */
    public boolean isHandlingProjectInPeriod(Date startDate, Date endDate) {
        if (handlingProject == null) {
            return false;
        }
        
        Date projectStartDate = handlingProject.getApplicationOpenDate();
        Date projectEndDate = handlingProject.getApplicationCloseDate();
        
        // Check if there's any overlap between the two periods
        return !((projectEndDate.before(startDate)) || (projectStartDate.after(endDate)));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("\nRole: HDB Officer");
        
        if (handlingProject != null) {
            sb.append("\nHandling Project: ").append(handlingProject.getName());
        } else {
            sb.append("\nHandling Project: None");
        }
        
        if (registrationStatus != null) {
            sb.append("\nRegistration Status: ").append(registrationStatus);
        }
        
        return sb.toString();
    }
} 
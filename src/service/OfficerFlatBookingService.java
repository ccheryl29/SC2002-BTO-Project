package service;

import model.Applicant;
import model.Application;
import model.Flat;
import model.Project;
import model.HDBOfficer;
import repository.ApplicationRepository;
import repository.ProjectRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for handling flat booking operations by HDB Officers
 */
public class OfficerFlatBookingService {
    
    private final ApplicationRepository applicationRepository;
    private final ProjectRepository projectRepository;
    
    /**
     * Constructor for OfficerFlatBookingService
     * 
     * @param applicationRepository The application repository
     * @param projectRepository The project repository
     */
    public OfficerFlatBookingService(ApplicationRepository applicationRepository, ProjectRepository projectRepository) {
        this.applicationRepository = applicationRepository;
        this.projectRepository = projectRepository;
    }
    
    /**
     * Gets pending booking requests for a specific project
     * 
     * @param projectName The name of the project
     * @return A list of applications with pending booking requests
     */
    public List<Application> getPendingBookingRequests(String projectName) {
        // Find the project
        Project project = findProjectByName(projectName);
        
        if (project == null) {
            return new ArrayList<>();
        }
        
        // Filter applications by project and booking status
        return project.getApplications().stream()
            .filter(app -> app.getBookingStatus() == Application.BookingFlatStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    /**
     * Helper method to find a project by name
     * 
     * @param projectName The name of the project
     * @return The project or null if not found
     */
    private Project findProjectByName(String projectName) {
        return projectRepository.findAll().stream()
            .filter(project -> project.getName().equals(projectName))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Completes a booking process by an HDB Officer
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param officer The officer handling the booking
     * @return A message indicating the result of the operation
     */
    public String completeBooking(String applicantNRIC, String projectName, HDBOfficer officer) {
        // Find the application
        Application application = findApplication(applicantNRIC, projectName);
        
        if (application == null) {
            return "No application found for this applicant and project.";
        }
        
        // Check if the application has a pending booking
        if (application.getBookingStatus() != Application.BookingFlatStatus.PENDING) {
            return "This application does not have a pending booking request.";
        }
        
        // Check if the application is successful
        if (application.getStatus() != Application.ApplicationStatus.SUCCESSFUL) {
            return "Only successful applications can complete a booking.";
        }
        
        // Get the project
        Project project = application.getProject();
        
        // Check if the officer is handling this project
        if (officer.getHandlingProject() == null || 
            !officer.getHandlingProject().getName().equals(project.getName())) {
            return "You are not authorized to handle bookings for this project.";
        }
        
        // Check if the flat type is available
        Flat.FlatType flatType = application.getFlatType();
        Flat flat = project.getFlatByType(flatType);
        
        if (flat == null) {
            return "The requested flat type does not exist in this project.";
        }
        
        if (flat.getAvailableUnits() <= 0) {
            return "No units of this flat type are currently available.";
        }
        
        // Reduce the available units
        flat.reduceAvailableUnits();
        
        // Update the application status
        application.updateBookingStatus(Application.BookingFlatStatus.COMPLETED);
        
        // Save the changes
        applicationRepository.save(application);
        projectRepository.save(project);
        
        return "Booking completed successfully for " + application.getApplicant().getName() +
               " for a " + flatType.getDisplayName() + " flat in " + project.getName() + ".";
    }
    
    /**
     * Generates a receipt for a completed booking
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return A map containing the receipt details or null if not found
     */
    public Map<String, Object> generateBookingReceipt(String applicantNRIC, String projectName) {
        // Find the application
        Application application = findApplication(applicantNRIC, projectName);
        
        if (application == null || application.getBookingStatus() != Application.BookingFlatStatus.COMPLETED) {
            return null;
        }
        
        Map<String, Object> receipt = new HashMap<>();
        Applicant applicant = application.getApplicant();
        Project project = application.getProject();
        
        // Applicant details
        receipt.put("applicantName", applicant.getName());
        receipt.put("nric", applicant.getNRIC());
        receipt.put("age", applicant.getAge());
        receipt.put("maritalStatus", applicant.getMaritalStatus().toString());
        
        // Booking details
        receipt.put("flatType", application.getFlatType().getDisplayName());
        receipt.put("projectName", project.getName());
        receipt.put("neighborhood", project.getNeighborhood());
        receipt.put("bookingDate", application.getBookingDate());
        
        // Get the price of the flat
        Flat flat = project.getFlatByType(application.getFlatType());
        receipt.put("price", flat != null ? flat.getSellingPrice() : "Unknown");
        
        return receipt;
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
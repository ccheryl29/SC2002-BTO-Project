package controller;

import controller.interfaces.IOfficerFlatBookingController;
import model.Application;
import model.HDBOfficer;
import service.OfficerFlatBookingService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for handling flat booking operations by HDB Officers
 */
public class OfficerFlatBookingController implements IOfficerFlatBookingController {
    
    private final OfficerFlatBookingService bookingService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Constructor for OfficerFlatBookingController
     * 
     * @param bookingService The booking service
     */
    public OfficerFlatBookingController(OfficerFlatBookingService bookingService) {
        this.bookingService = bookingService;
    }
    
    /**
     * Gets pending booking requests for a specific project
     * 
     * @param projectName The name of the project
     * @return A list of applications with pending booking requests
     */
    public List<Application> getPendingBookingRequests(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        return bookingService.getPendingBookingRequests(projectName);
    }
    
    /**
     * Completes a booking process
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param officer The officer handling the booking
     * @return A message indicating the result of the operation
     */
    public String completeBooking(String applicantNRIC, String projectName, HDBOfficer officer) {
        // Validate inputs
        if (applicantNRIC == null || applicantNRIC.trim().isEmpty()) {
            return "Applicant NRIC is required.";
        }
        
        if (projectName == null || projectName.trim().isEmpty()) {
            return "Project name is required.";
        }
        
        if (officer == null) {
            return "Officer information is required.";
        }
        
        // Call the service
        return bookingService.completeBooking(applicantNRIC, projectName, officer);
    }
    
    /**
     * Generates a receipt for a completed booking
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return A map containing the receipt details or error message
     */
    public Map<String, Object> generateBookingReceipt(String applicantNRIC, String projectName) {
        // Validate inputs
        if (applicantNRIC == null || applicantNRIC.trim().isEmpty() ||
            projectName == null || projectName.trim().isEmpty()) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "Applicant NRIC and project name are required.");
            return errorResult;
        }
        
        // Call the service
        Map<String, Object> receipt = bookingService.generateBookingReceipt(applicantNRIC, projectName);
        
        if (receipt == null) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "No completed booking found for this applicant and project.");
            return errorResult;
        }
        
        return receipt;
    }
} 
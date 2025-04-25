package controller.interfaces;

import model.Application;
import model.HDBOfficer;

import java.util.List;
import java.util.Map;

/**
 * Interface for OfficerFlatBookingController defining methods for flat booking operations
 * This interface follows the Dependency Inversion Principle by allowing views to depend on
 * this abstraction rather than concrete implementations.
 */
public interface IOfficerFlatBookingController {
    
    /**
     * Gets pending booking requests for a project
     * 
     * @param projectName The name of the project
     * @return List of pending booking requests
     */
    List<Application> getPendingBookingRequests(String projectName);
    
    /**
     * Completes a booking process
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @param officer The officer completing the booking
     * @return Result message
     */
    String completeBooking(String applicantNRIC, String projectName, HDBOfficer officer);
    
    /**
     * Generates a booking receipt for a completed booking
     * 
     * @param applicantNRIC The NRIC of the applicant
     * @param projectName The name of the project
     * @return Map containing receipt details, or an error message
     */
    Map<String, Object> generateBookingReceipt(String applicantNRIC, String projectName);
} 
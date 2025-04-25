package model;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an application submitted by a user for a project.
 */
public class Application implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Applicant applicant;
    private Project project;
    private ApplicationStatus status;
    private Date applicationDate;
    private Flat.FlatType flatType;
    private boolean withdrawalRequested;
    private WithdrawalRequestStatus withdrawalRequestStatus;
    private BookingFlatStatus bookingStatus;
    private Date bookingDate;
    
    /**
     * Enum representing the status of an application
     */
    public enum ApplicationStatus {
        PENDING,      // Entry status upon application - No conclusive decision made
        SUCCESSFUL,   // Application is successful, invited to make flat booking
        UNSUCCESSFUL, // Application is unsuccessful, cannot make flat booking
        BOOKED        // Secured a unit after successful application and completed booking
    }
    
    /**
     * Enum representing the status of a withdrawal request
     */
    public enum WithdrawalRequestStatus {
        NONE,
        PENDING,
        APPROVED,
        REJECTED
    }

    /**
     * Enum representing the status of flat booking
     */
    public enum BookingFlatStatus {
        NONE,           // No booking has been attempted
        PENDING,        // Booking is pending officer assistance
        COMPLETED,      // Booking has been completed successfully
    }

    /**
     * Constructor for Application
     * 
     * @param applicant The applicant submitting the application
     * @param project The project being applied for
     * @param flatType The type of flat being applied for
     */
    public Application(Applicant applicant, Project project, Flat.FlatType flatType) {
        this.applicant = applicant;
        this.project = project;
        this.status = ApplicationStatus.PENDING;
        this.applicationDate = new Date();
        this.flatType = flatType;
        this.withdrawalRequested = false;
        this.withdrawalRequestStatus = WithdrawalRequestStatus.NONE;
        this.bookingStatus = BookingFlatStatus.NONE;
        this.bookingDate = null;
    }
    
    /**
     * Gets the applicant associated with this application
     * 
     * @return The applicant
     */
    public Applicant getApplicant() {
        return applicant;
    }
    
    /**
     * Gets the project associated with this application
     * 
     * @return The project
     */
    public Project getProject() {
        return project;
    }
    
    /**
     * Gets the status of this application
     * 
     * @return The application status
     */
    public ApplicationStatus getStatus() {
        return status;
    }
    
    /**
     * Updates the status of this application
     * 
     * @param status The new application status
     */
    public void updateStatus(ApplicationStatus status) {
        this.status = status;
        
    }
    
    /**
     * Gets the application date
     * 
     * @return The application date
     */
    public Date getApplicationDate() {
        return applicationDate;
    }
    
    /**
     * Gets the flat type for this application
     * 
     * @return The flat type
     */
    public Flat.FlatType getFlatType() {
        return flatType;
    }
    
    /**
     * Sets the flat type for this application
     * 
     * @param flatType The new flat type
     */
    public void setFlatType(Flat.FlatType flatType) {
        this.flatType = flatType;
    }
        /**
     * Checks if a withdrawal has been requested for this application
     * 
     * @return true if withdrawal has been requested, false otherwise
     */
    public boolean isWithdrawalRequested() {
        return withdrawalRequested;
    }
    
    /**
     * Gets the withdrawal request status
     * 
     * @return The withdrawal request status
     */
    public WithdrawalRequestStatus getWithdrawalRequestStatus() {
        return withdrawalRequestStatus;
    }
    
    /**
     * Sets the withdrawal request status
     * 
     * @param status The new withdrawal request status
     */
    public void setWithdrawalRequestStatus(WithdrawalRequestStatus status) {
        this.withdrawalRequestStatus = status;
        
        // Update the withdrawal requested flag based on the status
        this.withdrawalRequested = (status == WithdrawalRequestStatus.PENDING);
    }
    
    /**
     * Gets the booking status for this application
     * 
     * @return The booking status
     */
    public BookingFlatStatus getBookingStatus() {
        return bookingStatus;
    }
    
    /**
     * Gets the booking date
     * 
     * @return The booking date
     */
    public Date getBookingDate() {
        return bookingDate;
    }
    
    /**
     * Updates the booking status for this application
     * 
     * @param status The new booking status
     */
    public void updateBookingStatus(BookingFlatStatus status) {
        this.bookingStatus = status;
        
        // If booking is completed, update application status and set booking date
        if (status == BookingFlatStatus.COMPLETED) {
            this.status = ApplicationStatus.BOOKED;
            this.bookingDate = new Date();
        }
    }
    
    /**
     * Checks if the application is eligible for booking
     * 
     * @return true if eligible for booking, false otherwise
     */
    public boolean isEligibleForBooking() {
        // Can only book if application is successful and not already booked
        return this.status == ApplicationStatus.SUCCESSFUL && 
               this.bookingStatus != BookingFlatStatus.COMPLETED;
    }
    
    /**
     * Requests withdrawal of this application
     */
    public void requestWithdrawal() {
        // Can only request withdrawal for PENDING or SUCCESSFUL applications
        if (this.status == ApplicationStatus.PENDING || 
            this.status == ApplicationStatus.SUCCESSFUL) {
            this.withdrawalRequested = true;
            this.withdrawalRequestStatus = WithdrawalRequestStatus.PENDING;
        }
    }
    
    /**
     * Cancels a withdrawal request for this application
     */
    public void cancelWithdrawalRequest() {
        this.withdrawalRequested = false;
        this.withdrawalRequestStatus = WithdrawalRequestStatus.NONE;
    }
    
    /**
     * Process the withdrawal of this application when approved
     */
    public void processWithdrawal() {
        this.status = ApplicationStatus.UNSUCCESSFUL;
        this.withdrawalRequested = false;
        this.withdrawalRequestStatus = WithdrawalRequestStatus.NONE;
    }
    
    @Override
    public String toString() {
        String result = "Application for: " + project.getName() +
               "\nApplicant: " + applicant.getName() +
               "\nFlat Type: " + flatType.getDisplayName() +
               "\nStatus: " + status +
               "\nApplication Date: " + applicationDate;
        
        if (withdrawalRequested) {
            result += "\nWithdrawal Requested: Yes";
        }
        
        if (bookingStatus != BookingFlatStatus.NONE) {
            result += "\nBooking Status: " + bookingStatus;
            if (bookingDate != null) {
                result += "\nBooking Date: " + bookingDate;
            }
        }
        
        return result;
    }
} 
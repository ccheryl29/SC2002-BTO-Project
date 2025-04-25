package view;

import controller.OfficerRegistrationController;
import controller.AccountManagementController;
import controller.ApplicantProjectController;
import controller.OfficerEnquiryController;
import model.HDBOfficer;
import model.Project;
import service.AccountManagementService;

import java.util.Scanner;

/**
 * Dashboard for HDB Officers with options to register for projects and view profile
 */
public class OfficerDashboard extends Dashboard {
    private final Scanner scanner;
    private final HDBOfficer currentOfficer;
    private final OfficerRegistrationView registrationView;
    private final OfficerApplicationView applicationView;
    private final OfficerFlatBookingView flatBookingView;
    private final OfficerEnquiryView enquiryView;
    private final AccountManagementView accountManagementView;
    
    /**
     * Constructor for OfficerDashboard
     * 
     * @param officer The logged-in officer
     * @param scanner Scanner for reading user input
     * @param registrationView View for registration operations
     * @param applicationView View for application operations
     * @param flatBookingView View for flat booking operations
     * @param enquiryView View for enquiry operations
     */
    public OfficerDashboard(
            HDBOfficer officer,
            Scanner scanner, 
            OfficerRegistrationView registrationView,
            OfficerApplicationView applicationView,
            OfficerFlatBookingView flatBookingView,
            OfficerEnquiryView enquiryView, AccountManagementView accountManagementView) {
        super(officer);
        this.currentOfficer = officer;
        this.scanner = scanner;
        this.registrationView = registrationView;
        this.applicationView = applicationView;
        this.flatBookingView = flatBookingView;
        this.enquiryView = enquiryView;
        
        // Set the current officer for the views
        this.registrationView.setCurrentOfficer(officer);
        this.applicationView.setCurrentOfficer(officer);
        this.flatBookingView.setCurrentOfficer(officer);
        this.enquiryView.setCurrentOfficer(officer);
        this.accountManagementView = accountManagementView;
        
    
    }
    
    /**
     * Displays the dashboard and handles user choices
     * 
     * @return true if relogin is required, false otherwise
     */
    @Override
    public boolean displayDashboard() {
        boolean exit = false;
        boolean reloginRequired = false;
        
        while (!exit) {
            System.out.println("\n==== HDB Officer Dashboard ====");
            System.out.println("Welcome, " + currentOfficer.getName());
            System.out.println("1. Register for Projects");
            System.out.println("2. View My Profile");
            System.out.println("3. Apply for Housing (as Applicant)");
            System.out.println("4. Manage Flat Bookings");
            System.out.println("5. Manage Project Enquiries");
            System.out.println("6. Account Management");
            System.out.println("7. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    registrationView.displayMenu(currentOfficer);
                    break;
                case 2:
                    displayProfile();
                    break;
                case 3:
                    applicationView.displayMenu();
                    break;
                case 4:
                    flatBookingView.displayMenu();
                    break;
                case 5:
                    enquiryView.displayMenu();
                    break;
                case 6:
                    reloginRequired = accountManagementView.displayMenu();
                    if (reloginRequired) {
                        return true;
                    }
                    break;
                case 7:
                    exit = true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        return false;
    }
    
    /**
     * Displays the officer profile
     */
    private void displayProfile() {
        System.out.println("\n==== My Profile ====");
        System.out.println(currentOfficer.toString());
        
        Project handlingProject = currentOfficer.getHandlingProject();
        HDBOfficer.RegistrationStatus status = currentOfficer.getRegistrationStatus();
        
        System.out.println("\n--- Registration Status ---");
        if (status == null) {
            System.out.println("You have not registered for any project yet.");
        } else {
            System.out.println("Status: " + status);
            
            if (handlingProject != null) {
                System.out.println("Handling Project: " + handlingProject.getName());
                System.out.println("Neighborhood: " + handlingProject.getNeighborhood());
                System.out.println("Application Period: " + handlingProject.getApplicationOpenDate() + 
                                  " to " + handlingProject.getApplicationCloseDate());
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine(); // Wait for user to press Enter
    }
    
    /**
     * Helper method to read integer input
     * 
     * @return The integer input
     */
    private int readIntInput() {
        try {
            int input = Integer.parseInt(scanner.nextLine().trim());
            return input;
        } catch (NumberFormatException e) {
            return -1; // Invalid input
        }
    }
} 
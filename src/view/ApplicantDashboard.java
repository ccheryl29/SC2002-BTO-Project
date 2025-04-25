package view;

import controller.AccountManagementController;
import controller.ApplicantEnquiryController;
import controller.ApplicantProjectController;
import model.Applicant;
import model.User;
import service.AccountManagementService;

import java.util.Scanner;

/**
 * Dashboard view class for Applicant interactions
 */
public class ApplicantDashboard extends Dashboard {
    
    private final Scanner scanner;
    private final ApplicantProjectView projectView;
    private final ApplicantEnquiryView enquiryView;
    private final Applicant currentApplicant;
    private final AccountManagementView accountManagementView;
    
    /**
     * Constructor for ApplicantDashboard
     * 
     * @param applicant The currently logged-in applicant
     * @param projectController The project controller
     * @param enquiryController The enquiry controller
     */
    public ApplicantDashboard(
            Applicant applicant,
            ApplicantProjectController projectController, 
            ApplicantEnquiryController enquiryController,
            AccountManagementController accountController) {
        super(applicant);
        this.currentApplicant = applicant;
        this.scanner = new Scanner(System.in);
        this.projectView = new ApplicantProjectView(projectController);
        this.enquiryView = new ApplicantEnquiryView(enquiryController, projectController);
        
        // Initialize views with applicant
        this.projectView.setCurrentApplicant(applicant);
        this.enquiryView.setCurrentApplicant(applicant);
        this.accountManagementView = new AccountManagementView(accountController, applicant);
    }
    
    /**
     * Displays the dashboard and handles user interactions
     * 
     * @return true if relogin is required, false otherwise
     */
    @Override
    public boolean displayDashboard() {
        if (currentApplicant == null) {
            System.out.println("Error: No applicant logged in.");
            return false;
        }
        
        boolean exit = false;
        boolean reloginRequired = false;
        
        while (!exit) {
            System.out.println("\n===== APPLICANT DASHBOARD =====");
            System.out.println("Welcome, " + currentApplicant.getName() + "!");
            System.out.println("1. Project & Application Management");
            System.out.println("2. Enquiry Management");
            System.out.println("3. View Profile");
            System.out.println("4. Account Management");
            System.out.println("5. Logout");
            System.out.print("Enter your choice: ");
            
            int choice = readIntInput();
            
            switch (choice) {
                case 1:
                    projectView.displayMenu();
                    break;
                case 2:
                    enquiryView.displayEnquiryMenu();
                    break;
                case 3:
                    displayProfileInfo();
                    break;
                case 4:
                    reloginRequired = accountManagementView.displayMenu();
                    if (reloginRequired) {
                        return true;
                    }
                    break;
                case 5:
                    exit = true;
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
        
        return false;
    }
    
    /**
     * Displays the applicant's profile information
     */
    private void displayProfileInfo() {
        System.out.println("\n===== PROFILE INFORMATION =====");
        System.out.println("Name: " + currentApplicant.getName());
        System.out.println("NRIC: " + currentApplicant.getNRIC());
        System.out.println("Age: " + currentApplicant.getAge());
        System.out.println("Marital Status: " + currentApplicant.getMaritalStatus());
        
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }
    
    /**
     * Read integer input from the user
     * 
     * @return The integer input
     */
    private int readIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
} 